package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.List;

public class TarnishedPounceGoal extends Goal {
    private enum Phase { IDLE, CROUCH, AIRBORNE, LANDING, RECOVER }

    private static final double TRIGGER_RANGE = 25.0;
    private static final int COOLDOWN_BASE = 80;
    private static final int COOLDOWN_SPREAD = 60;

    private static final int CROUCH_TICKS = 24;
    private static final float CROUCH_TURN_SPEED = 25.0F;
    private static final int SAMPLE_WINDOW = 12;
    private static final double MAX_READ_SPEED = 0.36;
    private static final double MIN_LEAD = 0.70;
    private static final double MAX_LEAD = 0.95;
    private static final double AIM_ERROR = 0.6;
    private static final double MAX_LEAP = 25.0;

    private static final int MIN_AIR_TICKS = 12;
    private static final int MAX_AIR_TICKS = 56;
    private static final double MIN_USABLE_ARC = 1.0;
    private static final double LAND_DIP = 0.2;

    private static final double SLAM_RADIUS = 1.5;
    private static final float SLAM_EDGE_FRACTION = 0.7F;
    private static final double EXPLOSION_RADIUS = 3.5;
    private static final float EXPLOSION_MAX_FRACTION = 0.5F;
    private static final float EXPLOSION_MIN_FRACTION = 0.2F;
    private static final float MIN_KNOCKBACK_FRACTION = 0.35F;
    private static final int SHIELD_DISABLE_TICKS = 100;

    private static final int SHIELD_STUN = 50;
    private static final int INTERRUPT_STUN = 60;
    private static final int SLAM_PAUSE_TICKS = 12;

    private final BronzeEntity entity;
    private final ArrayDeque<Vec3> samples = new ArrayDeque<>();

    private Phase phase = Phase.IDLE;
    private int phaseTicks = 0;
    @Nullable
    private Player target;

    private Vec3 launchPos = Vec3.ZERO;
    private Vec3 landingPos = Vec3.ZERO;
    private int flightTicks = 1;
    private double arcHeight = 2.5;

    public boolean isStunned = false;
    private int stunTicks = 0;
    private int stunVisualDelay = 0;
    private boolean diving = false;
    private boolean primed = false;

    public TarnishedPounceGoal(BronzeEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    public boolean isCrouching() {
        return phase == Phase.CROUCH;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private boolean isTarnished() {
        return entity.getTarnishLevel().equals(TarnishStage.TARNISHED);
    }

    private static boolean isValidPlayer(Player player) {
        return player.isAlive() && !player.isCreative() && !player.isSpectator() && !BronzeEntity.isSoothed(player);
    }

    @Override
    public boolean canUse() {
        if (!isTarnished()) return false;
        if (!primed) {
            primed = true;
            entity.setPounceCooldown(entity.getRandom().nextInt(COOLDOWN_SPREAD));
        }
        if (isStunned || entity.getPounceCooldown() > 0) return false;
        if (!entity.onGround() || entity.isInWater() || entity.isBurrowed()) return false;

        LivingEntity current = entity.getTarget();
        if (!(current instanceof Player player) || !isValidPlayer(player)) return false;
        if (entity.distanceTo(player) > TRIGGER_RANGE || !entity.hasLineOfSight(player)) return false;

        target = player;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return isTarnished() && (phase != Phase.IDLE || isStunned);
    }

    @Override
    public void start() {
        phase = Phase.CROUCH;
        phaseTicks = 0;
        diving = false;
        samples.clear();
        entity.getNavigation().stop();
        entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
        entity.setDefending(true);

        broadcast(BronzeEntity.CROUCH_START);
        playSound(SoundEvents.PISTON_CONTRACT, 1.2F, 0.6F);
    }

    @Override
    public void tick() {
        if (!isTarnished()) {
            stop();
            return;
        }

        if (isStunned) {
            tickStun();
            return;
        }

        switch (phase) {
            case CROUCH -> tickCrouch();
            case AIRBORNE -> tickAirborne();
            case LANDING -> tickLanding();
            case RECOVER -> tickRecover();
            default -> { }
        }
    }

    @Override
    public void stop() {
        if (phase == Phase.CROUCH) {
            entity.setDefending(false);
            broadcast(BronzeEntity.CROUCH_STOP);
        } else if (phase == Phase.AIRBORNE || phase == Phase.LANDING) {
            broadcast(BronzeEntity.LEAP_STOP);
        }
        if (isStunned) {
            broadcast(BronzeEntity.STUN_STOP);
        }
        broadcast(BronzeEntity.SLAM_STOP);

        phase = Phase.IDLE;
        phaseTicks = 0;
        isStunned = false;
        stunTicks = 0;
        stunVisualDelay = 0;
        diving = false;
        target = null;
        samples.clear();
    }

    private void tickCrouch() {
        entity.getNavigation().stop();
        entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);

        if (target == null || !isValidPlayer(target) || entity.distanceTo(target) > TRIGGER_RANGE + 6.0) {
            abortCrouch();
            return;
        }

        faceToward(target.position(), CROUCH_TURN_SPEED);
        recordSample();
        phaseTicks++;

        if (phaseTicks % 3 == 0 && entity.level() instanceof ServerLevel server) {
            int count = 1 + phaseTicks / 8;
            server.sendParticles(ParticleTypes.ELECTRIC_SPARK, entity.getX(), entity.getY() + 0.2, entity.getZ(),
                    count, 0.4, 0.1, 0.4, 0.4);
        }

        if (phaseTicks >= CROUCH_TICKS) {
            launch();
        }
    }

    private void abortCrouch() {
        entity.setPounceCooldown(20 + entity.getRandom().nextInt(30));
        stop();
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

    private static int airTicksFor(double horizontalDistance) {
        return Mth.clamp(Mth.floor(10.0 + horizontalDistance * 1.8), MIN_AIR_TICKS, MAX_AIR_TICKS);
    }

    private static double horizontalDistance(Vec3 a, Vec3 b) {
        double dx = a.x - b.x;
        double dz = a.z - b.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    private Vec3 predictLanding() {
        Vec3 pos = target.position();
        Vec3 velocity = estimateVelocity();
        double lead = Mth.lerp(entity.getRandom().nextDouble(), MIN_LEAD, MAX_LEAD);

        Vec3 aim = pos;
        for (int i = 0; i < 3; i++) {
            int airTicks = airTicksFor(horizontalDistance(entity.position(), aim));
            aim = new Vec3(pos.x + velocity.x * airTicks * lead, pos.y, pos.z + velocity.z * airTicks * lead);
        }

        aim = aim.add(
                (entity.getRandom().nextDouble() - 0.5) * 2.0 * AIM_ERROR,
                0.0,
                (entity.getRandom().nextDouble() - 0.5) * 2.0 * AIM_ERROR);

        double dx = aim.x - entity.getX();
        double dz = aim.z - entity.getZ();
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length > MAX_LEAP) {
            double f = MAX_LEAP / length;
            aim = new Vec3(entity.getX() + dx * f, aim.y, entity.getZ() + dz * f);
        }
        return aim;
    }

    @Nullable
    private Vec3 findLanding(Vec3 aim) {
        Level level = entity.level();
        int x = Mth.floor(aim.x);
        int z = Mth.floor(aim.z);
        int top = Mth.floor(Math.max(entity.getY(), aim.y)) + 1;
        int bottom = Mth.floor(Math.min(entity.getY(), aim.y)) - 8;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int y = top; y >= bottom; y--) {
            pos.set(x, y, z);
            BlockState state = level.getBlockState(pos);
            VoxelShape shape = state.getCollisionShape(level, pos);
            FluidState fluid = level.getFluidState(pos);

            double surface;
            if (!shape.isEmpty()) {
                surface = y + shape.max(Direction.Axis.Y);
            } else if (fluid.is(FluidTags.LAVA)) {
                surface = y + fluid.getHeight(level, pos);
            } else {
                continue;
            }

            AABB standing = entity.getBoundingBox().move(
                    aim.x - entity.getX(),
                    surface + 0.01 - entity.getY(),
                    aim.z - entity.getZ());
            if (level.noCollision(entity, standing)) {
                return new Vec3(aim.x, surface, aim.z);
            }
        }
        return null;
    }

    private double clearanceAbove(double wanted) {
        double step = 0.25;
        double clear = 0.0;
        while (clear + step <= wanted) {
            AABB box = entity.getBoundingBox().move(0.0, clear + step, 0.0);
            if (!entity.level().noCollision(entity, box)) break;
            clear += step;
        }
        return clear;
    }

    private void launch() {
        if (target == null) {
            abortCrouch();
            return;
        }

        Vec3 landing = findLanding(predictLanding());
        if (landing == null) landing = findLanding(target.position());
        if (landing == null) {
            abortCrouch();
            return;
        }

        double distance = horizontalDistance(entity.position(), landing);
        double wantedArc = Mth.clamp(2.0 + distance * 0.3, 2.5, 5.0);
        double arc = Math.min(wantedArc, clearanceAbove(wantedArc + 0.5) - 0.3);
        if (arc < MIN_USABLE_ARC) {
            abortCrouch();
            return;
        }

        launchPos = entity.position();
        landingPos = landing;
        flightTicks = airTicksFor(distance);
        arcHeight = arc;
        phase = Phase.AIRBORNE;
        phaseTicks = 0;

        entity.setDefending(false);
        entity.getNavigation().stop();
        entity.fallDistance = 0;

        broadcast(BronzeEntity.CROUCH_STOP);
        broadcast(BronzeEntity.LEAP_START);
        playSound(SoundEvents.PISTON_EXTEND, 1.5F, 0.5F);
        playSound(SoundEvents.ANVIL_LAND, 0.6F, 1.6F);

        if (entity.level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.CRIT, entity.getX(), entity.getY() + 0.8, entity.getZ(),
                    14, 0.3, 0.5, 0.3, 0.3);
        }
    }

    private Vec3 flightPoint(double s) {
        double x = Mth.lerp(s, launchPos.x, landingPos.x);
        double z = Mth.lerp(s, launchPos.z, landingPos.z);
        double y = Mth.lerp(s, launchPos.y, landingPos.y - LAND_DIP) + 4.0 * arcHeight * s * (1.0 - s);
        return new Vec3(x, y, z);
    }

    private void tickAirborne() {
        phaseTicks++;
        entity.fallDistance = 0;
        faceToward(landingPos, 40.0F);

        double s = Math.min(1.0, phaseTicks / (double) flightTicks);

        if (s >= 0.5) startDive();

        Vec3 delta = flightPoint(s).subtract(entity.position());
        double length = delta.length();
        if (length > 1.5) delta = delta.scale(1.5 / length);
        entity.setDeltaMovement(delta);

        if (phaseTicks >= 4) {
            if (s > 0.35 && entity.onGround()) {
                slam();
                return;
            }
            boolean hitWall = entity.horizontalCollision;
            boolean hitCeiling = entity.verticalCollision && s < 0.5;
            if (hitWall || hitCeiling) {
                phase = Phase.LANDING;
                phaseTicks = 0;
                return;
            }
        }

        if (phaseTicks >= flightTicks) {
            phase = Phase.LANDING;
            phaseTicks = 0;
        }
    }

    private void startDive() {
        if (diving) return;
        diving = true;
        broadcast(BronzeEntity.DIVE_START);
    }

    private int randomCooldown() {
        return COOLDOWN_BASE + entity.getRandom().nextInt(COOLDOWN_SPREAD);
    }

    private void tickRecover() {
        entity.getNavigation().stop();
        entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);

        if (++phaseTicks >= SLAM_PAUSE_TICKS) {
            stop();
        }
    }

    private void tickLanding() {
        startDive();
        phaseTicks++;
        entity.fallDistance = 0;
        entity.setDeltaMovement(0.0, -0.9, 0.0);

        if (entity.onGround() || phaseTicks > 25) {
            slam();
        }
    }

    private void slam() {
        Vec3 center = entity.position();
        entity.setDeltaMovement(Vec3.ZERO);
        entity.fallDistance = 0;
        diving = false;
        phase = Phase.IDLE;
        phaseTicks = 0;

        boolean shieldHit = false;
        if (entity.level() instanceof ServerLevel server) {
            broadcast(BronzeEntity.LEAP_STOP);
            broadcast(BronzeEntity.SLAM_START);
            spawnSlamEffects(server, center);
            shieldHit = damageNearby(center);
        }

        entity.setPounceCooldown(randomCooldown());

        if (shieldHit) {
            beginStun(SHIELD_STUN, SLAM_PAUSE_TICKS);
        } else {
            phase = Phase.RECOVER;
            phaseTicks = 0;
        }
    }

    private boolean damageNearby(Vec3 center) {
        float attack = (float) entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        boolean shieldHit = false;

        List<LivingEntity> victims = entity.level().getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(EXPLOSION_RADIUS, 1.5, EXPLOSION_RADIUS),
                e -> e != entity && e.isAlive()
                        && !(e instanceof Player p && (p.isCreative() || p.isSpectator())));

        for (LivingEntity victim : victims) {
            double distance = horizontalDistance(victim.position(), center);
            if (distance > EXPLOSION_RADIUS) continue;

            if (distance <= SLAM_RADIUS) {
                float falloff = (float) Mth.lerp(distance / SLAM_RADIUS, 1.0, SLAM_EDGE_FRACTION);
                boolean wasBlocking = victim instanceof Player player && player.isBlocking() && isFacing(player, center);
                Item shieldItem = victim instanceof Player player ? player.getUseItem().getItem() : null;

                BronzeEntity.hurtWithSinnerDrop(victim, entity.damageSources().mobAttack(entity), attack * falloff);

                if (wasBlocking && victim instanceof Player player) {
                    shieldHit = true;
                    player.getCooldowns().addCooldown(shieldItem, SHIELD_DISABLE_TICKS);
                    player.stopUsingItem();
                    player.level().broadcastEntityEvent(player, (byte) 30);
                }
            } else {
                double t = (distance - SLAM_RADIUS) / (EXPLOSION_RADIUS - SLAM_RADIUS);
                float falloff = (float) Mth.lerp(t, EXPLOSION_MAX_FRACTION, EXPLOSION_MIN_FRACTION);
                BronzeEntity.hurtWithSinnerDrop(victim, entity.damageSources().explosion(entity, entity), attack * falloff);
            }

            float knockback = (float) Mth.lerp(distance / EXPLOSION_RADIUS, 1.0, MIN_KNOCKBACK_FRACTION);
            if (!(victim instanceof BronzeEntity)) {
                launchVictim(victim, center, knockback);
            }
        }
        return shieldHit;
    }

    private void launchVictim(LivingEntity victim, Vec3 center, float falloff) {
        double resistance = victim.getAttributes().hasAttribute(Attributes.KNOCKBACK_RESISTANCE)
                ? victim.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) : 0.0;

        double dx = victim.getX() - center.x;
        double dz = victim.getZ() - center.z;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < 1.0E-4) {
            double angle = entity.getRandom().nextDouble() * Math.PI * 2.0;
            dx = Math.cos(angle);
            dz = Math.sin(angle);
        } else {
            dx /= length;
            dz /= length;
        }

        double push = (0.3 + 0.9 * falloff) * (1.0 - resistance);
        double lift = (0.45 + 0.25 * falloff) * (1.0 - resistance);
        victim.setDeltaMovement(victim.getDeltaMovement().add(dx * push, lift, dz * push));
        victim.hurtMarked = true;
    }

    private static boolean isFacing(Player player, Vec3 point) {
        Vec3 toPoint = new Vec3(point.x - player.getX(), 0.0, point.z - player.getZ());
        if (toPoint.lengthSqr() < 1.0E-4) return true;
        Vec3 look = new Vec3(player.getLookAngle().x, 0.0, player.getLookAngle().z);
        if (look.lengthSqr() < 1.0E-4) return false;
        return look.normalize().dot(toPoint.normalize()) > 0.0;
    }

    private void spawnSlamEffects(ServerLevel server, Vec3 center) {
        server.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y + 0.3, center.z, 6, 1.2, 0.2, 1.2, 0.0);

        BlockState below = server.getBlockState(entity.blockPosition().below());
        if (below.isAir()) below = Blocks.NETHERRACK.defaultBlockState();
        ParticleOptions dust = new BlockParticleOption(ParticleTypes.BLOCK, below);

        double[] radii = {SLAM_RADIUS, EXPLOSION_RADIUS};
        for (double radius : radii) {
            int points = (int) (radius * 8);
            for (int i = 0; i < points; i++) {
                double angle = (Math.PI * 2.0 * i) / points;
                server.sendParticles(dust,
                        center.x + Math.cos(angle) * radius, center.y + 0.1, center.z + Math.sin(angle) * radius,
                        2, 0.1, 0.05, 0.1, 0.05);
            }
        }

        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 2.0F, 0.7F);
        playSound(SoundEvents.ANVIL_LAND, 1.4F, 0.6F);
    }

    public void triggerStunFromKeyHit() {
        if (phase != Phase.CROUCH || entity.level().isClientSide) return;

        entity.setDefending(false);
        broadcast(BronzeEntity.CROUCH_STOP);
        phase = Phase.IDLE;
        phaseTicks = 0;
        samples.clear();
        entity.setPounceCooldown(randomCooldown());
        beginStun(INTERRUPT_STUN, 0);
    }

    private void beginStun(int ticks, int visualDelay) {
        isStunned = true;
        stunTicks = ticks;
        stunVisualDelay = visualDelay;
        entity.getNavigation().stop();
        if (visualDelay <= 0) {
            startStunVisuals();
        }
    }

    private void startStunVisuals() {
        broadcast(BronzeEntity.STUN_START);
        playSound(SoundEvents.BEACON_DEACTIVATE, 1.2F, 0.8F);
        playSound(SoundEvents.REDSTONE_TORCH_BURNOUT, 1.5F, 0.5F);
    }

    private void tickStun() {
        entity.getNavigation().stop();
        entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);

        if (stunVisualDelay > 0 && --stunVisualDelay == 0) {
            startStunVisuals();
        }

        if (--stunTicks <= 0) {
            stop();
        }
    }

    private void faceToward(Vec3 pos, float maxTurn) {
        double dx = pos.x - entity.getX();
        double dz = pos.z - entity.getZ();
        float targetYaw = (float) (Mth.atan2(dz, dx) * (180F / Math.PI)) - 90F;
        float yaw = Mth.approachDegrees(entity.getYRot(), targetYaw, maxTurn);
        entity.setYRot(yaw);
        entity.yBodyRot = yaw;
        entity.yHeadRot = yaw;
    }

    private void broadcast(byte event) {
        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, event);
        }
    }

    private void playSound(net.minecraft.sounds.SoundEvent sound, float volume, float pitch) {
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }
}
