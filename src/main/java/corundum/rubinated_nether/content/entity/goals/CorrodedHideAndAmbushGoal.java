package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CorrodedHideAndAmbushGoal extends Goal {
    private final BronzeEntity entity;
    private Player target;

    private int state = 0;
    private int stateTicks = 0;
    private int repicks = 0;
    private int undergroundTicks = 0;
    private Vec3 trapPos;
    private final Deque<Vec3> samples = new ArrayDeque<>();
    private final Set<UUID> alreadyHit = new HashSet<>();
    private final List<BlockPos> route = new ArrayList<>();
    private int routeIndex = 0;
    private final Set<Integer> tunnel = new HashSet<>();
    private Vec3 aimVelocity = Vec3.ZERO;

    private static final double MOVE_SPEED = 0.4;
    private static final double ACQUIRE_RANGE = 25.0;
    private static final double HIT_RADIUS = 2.0;
    private static final double VERTICAL_REACH = 2.0;
    private static final float PLAYER_DAMAGE = 10.0F;
    private static final float MOB_DAMAGE = 6.0F;

    private static final int DRILL_TICKS = 40;
    private static final int HIT_START = 26;
    private static final int HIT_END = 44;
    private static final int END_TICKS = 48;
    private static final int MIN_AGE = 40;

    private static final int FIRE_LEAD = 28;
    private static final int ARRIVE_BUFFER = 6;
    private static final int MAX_HORIZON = 100;
    private static final double MAX_LEAD_DIST = 12.0;
    private static final double FIRE_CLOSEST = 1.8;
    private static final double NEAR_FIRE = 2.2;
    private static final double REPICK_DIST = 2.8;
    private static final int MAX_REPICKS = 8;
    private static final int MAX_TRAVEL = 160;
    private static final int MAX_LURK = 100;
    private static final int MAX_UNDERGROUND = 200;
    private static final double[] LEAD_STEPS = {1.0, 0.75, 0.5, 0.25, 0.0};

    private static final int ROUTE_REFRESH = 10;
    private static final int ROUTE_RADIUS = 32;
    private static final int ROUTE_MAX_NODES = 6000;
    private static final double WAYPOINT_REACHED = 0.6;
    private static final int MAX_FLAT_HUMP = 3;
    private static final int MAX_HUMP_HEIGHT = 2;
    private static final double STEP_UP_IMPULSE = 0.42;
    private static final int[][] ROUTE_DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    private static final int SAMPLE_WINDOW = 12;
    private static final int RECENT_WINDOW = 4;
    private static final double RECENT_WEIGHT = 0.65;
    private static final double TURN_REPICK_DOT = 0.5;
    private static final double MAX_READ_SPEED = 0.45;

    public CorrodedHideAndAmbushGoal(BronzeEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (entity.tickCount < MIN_AGE) return false;
        if (entity.getAmbushCooldown() > 0 || entity.isBurrowed()) return false;
        if (entity.getTarget() instanceof Player player && entity.getTarnishLevel().equals(TarnishStage.CORRODED)) {
            if (entity.distanceTo(player) < ACQUIRE_RANGE && entity.hasLineOfSight(player)) {
                this.target = player;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return state != 0 && entity.getTarnishLevel().equals(TarnishStage.CORRODED) && target != null && !entity.isInWater() && target.isAlive();
    }

    @Override
    public void start() {
        state = 1;
        stateTicks = 0;
        repicks = 0;
        undergroundTicks = 0;
        trapPos = null;
        route.clear();
        tunnel.clear();
        routeIndex = 0;
        aimVelocity = Vec3.ZERO;
        samples.clear();
        alreadyHit.clear();
        recordSample();
        entity.setBurrowed(true);
        entity.setAmbushPhase(1);
        entity.getNavigation().stop();

        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, (byte) 76);
        }
    }

    @Override
    public void tick() {
        if (!entity.getTarnishLevel().equals(TarnishStage.CORRODED) || target == null) {
            stop();
            return;
        }
        recordSample();
        switch (state) {
            case 1 -> {
                stateTicks++;
                entity.setDeltaMovement(Vec3.ZERO);
                entity.getNavigation().stop();
                faceToward(target.position());
                if (stateTicks >= DRILL_TICKS) {
                    state = 2;
                    stateTicks = 0;
                    trapPos = pickTrapPos();
                    spawnDust(4, 0.2, 0.05, 0.02);
                    entity.setAmbushPhase(2);

                    if (!entity.level().isClientSide) {
                        entity.level().broadcastEntityEvent(entity, (byte) 79);
                    }
                }
            }

            case 2 -> {
                stateTicks++;
                if (++undergroundTicks >= MAX_UNDERGROUND && !entity.noPhysics) {
                    fire();
                    return;
                }
                if (stateTicks % ROUTE_REFRESH == 0) trapPos = pickTrapPos();
                if ((stateTicks == 1 || stateTicks % ROUTE_REFRESH == 0) && !entity.noPhysics) buildRoute();

                while (routeIndex < route.size() && reachedWaypoint(route.get(routeIndex))) {
                    routeIndex++;
                }

                if (routeIndex >= route.size() || (stateTicks >= MAX_TRAVEL && !entity.noPhysics)) {
                    releasePhysics();
                    entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
                    state = 4;
                    stateTicks = 0;
                    return;
                }

                updatePhysics();
                walkTo(route.get(routeIndex));
                spawnDust(4, 0.2, 0.05, 0.02);
            }

            case 3 -> {
                stateTicks++;
                entity.setDeltaMovement(Vec3.ZERO);
                entity.getNavigation().stop();
                faceToward(target.position());

                if (!entity.level().isClientSide) {
                    entity.level().broadcastEntityEvent(entity, (byte) 81);
                }

                if (stateTicks == HIT_START) {
                    spawnDust(16, 0.3, 0.1, 0.05);
                }

                if (stateTicks >= HIT_START && stateTicks <= HIT_END) {
                    applyHits();
                }

                if (stateTicks >= END_TICKS) {
                    stop();
                }
            }

            case 4 -> {
                stateTicks++;
                if (++undergroundTicks >= MAX_UNDERGROUND) {
                    fire();
                    return;
                }
                entity.setDeltaMovement(Vec3.ZERO);
                entity.getNavigation().stop();
                faceToward(target.position());

                if (stateTicks % 6 == 0) {
                    spawnDust(2, 0.25, 0.05, 0.02);
                }

                double[] approach = closestApproach();
                boolean onCue = approach[1] <= FIRE_CLOSEST && approach[0] <= FIRE_LEAD;
                boolean tooClose = flatDistance(entity.position(), target.position()) <= NEAR_FIRE;
                if (onCue || tooClose || stateTicks >= MAX_LURK) {
                    fire();
                    return;
                }

                boolean offTarget = approach[1] > REPICK_DIST && stateTicks >= 5;
                boolean turned = stateTicks >= 3 && velocityShifted();
                if ((offTarget || turned) && repicks < MAX_REPICKS) {
                    repicks++;
                    trapPos = pickTrapPos();
                    state = 2;
                    stateTicks = 0;
                }
            }
        }
    }

    private void fire() {
        releasePhysics();
        state = 3;
        stateTicks = 0;
        entity.setDeltaMovement(Vec3.ZERO);
        spawnDust(4, 0.2, 0.05, 0.02);
        entity.setAmbushPhase(3);

        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, (byte) 81);
        }
    }

    private void applyHits() {
        if (!(entity.level() instanceof ServerLevel server)) return;

        List<LivingEntity> nearby = server.getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(HIT_RADIUS, VERTICAL_REACH, HIT_RADIUS),
                e -> e != entity && e.isAlive()
        );

        for (LivingEntity hit : nearby) {
            if (hit instanceof Player && hit != target) continue;
            if (alreadyHit.contains(hit.getUUID())) continue;
            if (flatDistance(entity.position(), hit.position()) > HIT_RADIUS) continue;
            if (Math.abs(hit.getY() - entity.getY()) > VERTICAL_REACH) continue;

            alreadyHit.add(hit.getUUID());

            if (hit instanceof Player) {
                server.sendParticles(ParticleTypes.CRIT, hit.getX(), hit.getY() + 1, hit.getZ(), 10, 0.3, 0.2, 0.3, 0.1);
                hit.hurt(entity.damageSources().mobAttack(entity), PLAYER_DAMAGE);
                continue;
            }

            hit.hurt(entity.damageSources().mobAttack(entity), MOB_DAMAGE);

            if (hit instanceof BronzeEntity && !hit.isAlive()) {
                ItemStack disc = new ItemStack(RNItems.MUSIC_DISC_SINNER.get());
                ItemEntity itemEntity = new ItemEntity(
                        entity.level(),
                        hit.getX(), hit.getY(), hit.getZ(),
                        disc
                );
                entity.level().addFreshEntity(itemEntity);
            }
        }
    }

    private void recordSample() {
        if (target == null) return;
        samples.addLast(target.position());
        while (samples.size() > SAMPLE_WINDOW) {
            samples.removeFirst();
        }
    }

    private Vec3 averageVelocity(List<Vec3> list, int steps) {
        Vec3 newest = list.get(list.size() - 1);
        Vec3 oldest = list.get(list.size() - 1 - steps);
        return new Vec3((newest.x - oldest.x) / steps, 0.0, (newest.z - oldest.z) / steps);
    }

    private Vec3 estimateVelocity() {
        if (samples.size() < 4) return Vec3.ZERO;

        List<Vec3> list = new ArrayList<>(samples);
        Vec3 recent = averageVelocity(list, Math.min(RECENT_WINDOW, list.size() - 1));
        if (recent.length() < 0.02) return Vec3.ZERO;

        Vec3 overall = averageVelocity(list, list.size() - 1);
        Vec3 velocity = recent.scale(RECENT_WEIGHT).add(overall.scale(1.0 - RECENT_WEIGHT));

        double speed = velocity.length();
        if (speed < 0.02) return Vec3.ZERO;
        if (speed > MAX_READ_SPEED) velocity = velocity.scale(MAX_READ_SPEED / speed);
        return velocity;
    }

    private boolean velocityShifted() {
        Vec3 now = estimateVelocity();
        double a = aimVelocity.length();
        double b = now.length();
        if (a < 0.02 && b < 0.02) return false;
        if (a < 0.02) return b > 0.08;
        if (b < 0.02) return a > 0.08;
        return aimVelocity.dot(now) / (a * b) < TURN_REPICK_DOT;
    }

    private Vec3 pickTrapPos() {
        Vec3 pos = target.position();
        Vec3 velocity = estimateVelocity();
        aimVelocity = velocity;

        double horizon = FIRE_LEAD;
        for (int i = 0; i < 6; i++) {
            Vec3 aim = new Vec3(pos.x + velocity.x * horizon, pos.y, pos.z + velocity.z * horizon);
            double travelTicks = flatDistance(entity.position(), aim) / MOVE_SPEED;
            horizon = Mth.clamp(travelTicks + FIRE_LEAD + ARRIVE_BUFFER, FIRE_LEAD, MAX_HORIZON);
        }

        double leadX = velocity.x * horizon;
        double leadZ = velocity.z * horizon;
        double length = Math.sqrt(leadX * leadX + leadZ * leadZ);
        if (length > MAX_LEAD_DIST) {
            leadX *= MAX_LEAD_DIST / length;
            leadZ *= MAX_LEAD_DIST / length;
            length = MAX_LEAD_DIST;
        }

        int baseY = entity.blockPosition().getY();
        BlockPos first = findFeet(pos.x, pos.z, baseY);
        if (first != null) {
            double bestX = pos.x;
            double bestZ = pos.z;
            int bestY = first.getY();
            int steps = (int) Math.ceil(length / 0.5);
            for (int i = 1; i <= steps; i++) {
                double f = Math.min(1.0, i * 0.5 / length);
                double cx = pos.x + leadX * f;
                double cz = pos.z + leadZ * f;
                BlockPos feet = findFeet(cx, cz, bestY);
                if (feet == null) break;
                bestX = cx;
                bestZ = cz;
                bestY = feet.getY();
            }
            return new Vec3(bestX, bestY, bestZ);
        }

        for (double step : LEAD_STEPS) {
            double cx = pos.x + leadX * step;
            double cz = pos.z + leadZ * step;
            BlockPos feet = findFeet(cx, cz, baseY);
            if (feet != null) return new Vec3(cx, feet.getY(), cz);
        }
        return new Vec3(pos.x, entity.getY(), pos.z);
    }

    private boolean passable(BlockPos pos) {
        Level level = entity.level();
        if (!level.hasChunkAt(pos)) return false;
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && (level.getFluidState(pos).isEmpty() || level.getFluidState(pos).is(FluidTags.LAVA));
    }

    private boolean standableFeet(BlockPos feet) {
        Level level = entity.level();
        if (!passable(feet) || !passable(feet.above())) return false;
        BlockPos below = feet.below();
        if (!level.hasChunkAt(below)) return false;
        BlockState floor = level.getBlockState(below);
        if (floor.getFluidState().is(FluidTags.LAVA)) return true;
        return !floor.getCollisionShape(level, below).isEmpty() && floor.getFluidState().isEmpty();
    }

    private BlockPos findFeet(double x, double z, int aroundY) {
        int[] offsets = {0, 1, -1, 2, -2};
        for (int dy : offsets) {
            BlockPos feet = BlockPos.containing(x, aroundY + dy, z);
            if (standableFeet(feet)) return feet;
        }
        return null;
    }

    private BlockPos stepTo(BlockPos cur, int dx, int dz) {
        int[] dys = {0, 1, -1};
        for (int dy : dys) {
            BlockPos next = new BlockPos(cur.getX() + dx, cur.getY() + dy, cur.getZ() + dz);
            if (!standableFeet(next)) continue;
            if (dy == 1 && !passable(cur.above(2))) continue;
            return next;
        }
        return null;
    }

    private void buildRoute() {
        route.clear();
        tunnel.clear();
        routeIndex = 0;
        if (trapPos == null) return;

        BlockPos start = findFeet(entity.getX(), entity.getZ(), entity.blockPosition().getY());
        if (start == null) start = entity.blockPosition();

        double goalX = trapPos.x;
        double goalZ = trapPos.z;

        Map<BlockPos, BlockPos> parent = new HashMap<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        parent.put(start, start);
        queue.add(start);

        BlockPos best = start;
        double bestDist = distSqr(start, goalX, goalZ);

        while (!queue.isEmpty() && parent.size() < ROUTE_MAX_NODES) {
            BlockPos cur = queue.poll();
            double d = distSqr(cur, goalX, goalZ);
            if (d < bestDist) {
                bestDist = d;
                best = cur;
            }
            if (d <= 0.5) break;

            for (int[] dir : ROUTE_DIRS) {
                int dx = dir[0];
                int dz = dir[1];
                if (Math.abs(cur.getX() + dx - start.getX()) > ROUTE_RADIUS || Math.abs(cur.getZ() + dz - start.getZ()) > ROUTE_RADIUS) continue;

                BlockPos next = stepTo(cur, dx, dz);
                if (next == null || parent.containsKey(next)) continue;
                if (dx != 0 && dz != 0 && (stepTo(cur, dx, 0) == null || stepTo(cur, 0, dz) == null)) continue;

                parent.put(next, cur);
                queue.add(next);
            }
        }

        List<BlockPos> reversed = new ArrayList<>();
        BlockPos node = best;
        while (!node.equals(start)) {
            reversed.add(node);
            node = parent.get(node);
        }
        Collections.reverse(reversed);
        route.addAll(reversed);
        flattenHumps(start.getY());
    }

    private void flattenHumps(int startY) {
        int i = 0;
        while (i < route.size()) {
            int baseY = i == 0 ? startY : route.get(i - 1).getY();
            if (route.get(i).getY() <= baseY) {
                i++;
                continue;
            }

            int j = i;
            int maxY = baseY;
            while (j < route.size() && route.get(j).getY() > baseY) {
                maxY = Math.max(maxY, route.get(j).getY());
                j++;
            }

            boolean returns = j < route.size() && route.get(j).getY() == baseY;
            if (returns && j - i <= MAX_FLAT_HUMP && maxY - baseY <= MAX_HUMP_HEIGHT) {
                for (int k = i; k < j; k++) {
                    BlockPos cell = route.get(k);
                    route.set(k, new BlockPos(cell.getX(), baseY, cell.getZ()));
                    tunnel.add(k);
                }
            }
            i = j;
        }
    }

    private void updatePhysics() {
        if (tunnel.contains(routeIndex) || tunnel.contains(routeIndex - 1)) {
            entity.noPhysics = true;
        } else if (entity.noPhysics && entity.level().noCollision(entity)) {
            entity.noPhysics = false;
        }
    }

    private void releasePhysics() {
        if (!entity.noPhysics) return;
        entity.noPhysics = false;
        for (int i = 0; i < MAX_HUMP_HEIGHT + 1 && !entity.level().noCollision(entity); i++) {
            entity.setPos(entity.getX(), entity.getY() + 1.0, entity.getZ());
        }
    }

    private static double distSqr(BlockPos cell, double x, double z) {
        double dx = cell.getX() + 0.5 - x;
        double dz = cell.getZ() + 0.5 - z;
        return dx * dx + dz * dz;
    }

    private boolean reachedWaypoint(BlockPos cell) {
        double dx = cell.getX() + 0.5 - entity.getX();
        double dz = cell.getZ() + 0.5 - entity.getZ();
        return dx * dx + dz * dz < WAYPOINT_REACHED * WAYPOINT_REACHED && Math.abs(cell.getY() - entity.getY()) < 1.2;
    }

    private void walkTo(BlockPos cell) {
        Vec3 waypoint = Vec3.atBottomCenterOf(cell);
        double dx = waypoint.x - entity.getX();
        double dz = waypoint.z - entity.getZ();
        double flat = Math.sqrt(dx * dx + dz * dz);

        double vy = entity.getDeltaMovement().y;
        boolean grounded = entity.onGround();
        boolean tunneling = entity.noPhysics;
        if (tunneling) {
            vy = Mth.clamp(waypoint.y - entity.getY(), -0.3, 0.3);
        } else if (grounded && waypoint.y > entity.getY() + 0.3) {
            vy = STEP_UP_IMPULSE;
        }

        double vx = 0.0;
        double vz = 0.0;
        if (flat > 1.0E-4 && (grounded || vy > 0.0 || tunneling)) {
            vx = dx / flat * MOVE_SPEED;
            vz = dz / flat * MOVE_SPEED;
        }

        faceToward(waypoint);
        entity.setDeltaMovement(vx, vy, vz);
    }

    private double[] closestApproach() {
        Vec3 pos = target.position();
        Vec3 velocity = estimateVelocity();
        double relX = trapPos.x - pos.x;
        double relZ = trapPos.z - pos.z;
        double speedSqr = velocity.x * velocity.x + velocity.z * velocity.z;

        double ticks = 0.0;
        if (speedSqr > 1.0E-4) {
            ticks = Mth.clamp((relX * velocity.x + relZ * velocity.z) / speedSqr, 0.0, 200.0);
        }

        double missX = relX - velocity.x * ticks;
        double missZ = relZ - velocity.z * ticks;
        return new double[]{ticks, Math.sqrt(missX * missX + missZ * missZ)};
    }

    private static double flatDistance(Vec3 a, Vec3 b) {
        double dx = a.x - b.x;
        double dz = a.z - b.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    private void spawnDust(int count, double spreadXZ, double spreadY, double speed) {
        if (entity.level() instanceof ServerLevel server) {
            BlockPos below = entity.blockPosition().below();
            BlockState blockstate = entity.level().getBlockState(below);
            server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockstate),
                    entity.getX(), entity.getY() + 0.1, entity.getZ(), count, spreadXZ, spreadY, spreadXZ, speed);
        }
    }

    private void faceToward(Vec3 pos) {
        double dx = pos.x - entity.getX();
        double dz = pos.z - entity.getZ();
        if (dx * dx + dz * dz < 1.0E-4) return;
        float yaw = (float) (Mth.atan2(dz, dx) * (180F / Math.PI)) - 90F;
        entity.setYRot(yaw);
        entity.yBodyRot = yaw;
        entity.yHeadRot = yaw;
    }

    @Override
    public void stop() {
        state = 0;
        stateTicks = 0;
        repicks = 0;
        undergroundTicks = 0;
        target = null;
        trapPos = null;
        releasePhysics();
        route.clear();
        tunnel.clear();
        routeIndex = 0;
        aimVelocity = Vec3.ZERO;
        samples.clear();
        alreadyHit.clear();
        entity.setBurrowed(false);
        entity.setAmbushPhase(0);
        entity.setAmbushCooldown(30);

        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, (byte) 87);
        }
    }
}
