package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
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
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
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

    private static final double MOVE_SPEED = 0.3;
    private static final double HIT_RADIUS = 2.0;
    private static final double VERTICAL_REACH = 2.0;
    private static final float PLAYER_DAMAGE = 10.0F;
    private static final float MOB_DAMAGE = 6.0F;

    private static final int DRILL_TICKS = 40;
    private static final int HIT_START = 26;
    private static final int HIT_END = 44;
    private static final int END_TICKS = 48;
    private static final int MIN_AGE = 40;

    private static final int FIRE_LEAD = 34;
    private static final int ARRIVE_BUFFER = 6;
    private static final int MAX_HORIZON = 80;
    private static final double MAX_LEAD_DIST = 9.0;
    private static final double FIRE_CLOSEST = 1.8;
    private static final double NEAR_FIRE = 2.2;
    private static final double REPICK_DIST = 2.8;
    private static final int MAX_REPICKS = 6;
    private static final int MAX_TRAVEL = 120;
    private static final int MAX_LURK = 100;
    private static final int MAX_UNDERGROUND = 160;
    private static final double[] LEAD_STEPS = {1.0, 0.75, 0.5, 0.25, 0.0};

    private static final int SAMPLE_WINDOW = 10;
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
            if (entity.distanceTo(player) < 10 && entity.hasLineOfSight(player)) {
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
                if (++undergroundTicks >= MAX_UNDERGROUND) {
                    fire();
                    return;
                }
                if (stateTicks % 10 == 0) trapPos = pickTrapPos();

                Vec3 toTrap = new Vec3(trapPos.x - entity.getX(), 0, trapPos.z - entity.getZ());
                if (toTrap.lengthSqr() < 0.25 || stateTicks >= MAX_TRAVEL) {
                    entity.setDeltaMovement(Vec3.ZERO);
                    state = 4;
                    stateTicks = 0;
                    return;
                }

                Vec3 direction = toTrap.normalize();
                faceToward(trapPos);
                entity.setDeltaMovement(direction.x * MOVE_SPEED, 0, direction.z * MOVE_SPEED);
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

                if (stateTicks % 2 == 0) {
                    spawnDust(3, 0.25, 0.05, 0.02);
                }

                double[] approach = closestApproach();
                boolean onCue = approach[1] <= FIRE_CLOSEST && approach[0] <= FIRE_LEAD;
                boolean tooClose = flatDistance(entity.position(), target.position()) <= NEAR_FIRE;
                if (onCue || tooClose || stateTicks >= MAX_LURK) {
                    fire();
                    return;
                }

                if (approach[1] > REPICK_DIST && stateTicks >= 5 && repicks < MAX_REPICKS) {
                    repicks++;
                    trapPos = pickTrapPos();
                    state = 2;
                    stateTicks = 0;
                }
            }
        }
    }

    private void fire() {
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

    private Vec3 estimateVelocity() {
        if (samples.size() < 4) return Vec3.ZERO;

        Vec3 oldest = samples.peekFirst();
        Vec3 newest = samples.peekLast();
        if (oldest == null || newest == null) return Vec3.ZERO;

        double steps = samples.size() - 1;
        Vec3 velocity = new Vec3((newest.x - oldest.x) / steps, 0.0, (newest.z - oldest.z) / steps);

        double speed = velocity.length();
        if (speed < 0.02) return Vec3.ZERO;
        if (speed > MAX_READ_SPEED) velocity = velocity.scale(MAX_READ_SPEED / speed);
        return velocity;
    }

    private Vec3 pickTrapPos() {
        Vec3 pos = target.position();
        Vec3 velocity = estimateVelocity();

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
        }

        for (double step : LEAD_STEPS) {
            Vec3 candidate = new Vec3(pos.x + leadX * step, entity.getY(), pos.z + leadZ * step);
            if (isStandable(candidate)) return candidate;
        }
        return new Vec3(pos.x, entity.getY(), pos.z);
    }

    private boolean isStandable(Vec3 point) {
        Level level = entity.level();
        BlockPos feet = BlockPos.containing(point.x, entity.getY() + 0.1, point.z);
        if (!level.hasChunkAt(feet)) return false;

        BlockPos above = feet.above();
        BlockPos below = feet.below();
        return level.getBlockState(feet).getCollisionShape(level, feet).isEmpty()
                && level.getBlockState(above).getCollisionShape(level, above).isEmpty()
                && !level.getBlockState(below).getCollisionShape(level, below).isEmpty();
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
