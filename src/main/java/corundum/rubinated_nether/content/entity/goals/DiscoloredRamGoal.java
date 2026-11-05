package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DiscoloredRamGoal extends Goal {
    private final BronzeEntity entity;
    private Player target;

    private int phase = 0;
    private int phaseTicks = 0;
    private Vec3 dashDirection = Vec3.ZERO;
    // How long it stands still with its rod out, tracking the player, before ramming (30 ticks = 1.5s)
    private static final int AIM_TIME = 30;
    // Max degrees per tick it can turn to keep facing the player while aiming
    private static final float AIM_TURN_SPEED = 30.0F;
    private static final int DASH_TIME = 10;
    private static final int MAX_DASH_TIME = 24;
    private static final double ATTACK_RANGE = 25.0;
    private static final double JUMP_IMPULSE = 0.42;
    private int dashTime = DASH_TIME;
    private boolean hopping = false;
    private static final int COOLDOWN = 80;
    private static final double RAM_SPEED = 1.2;
    private boolean isStunned = false;
    private int stunTicks = 0;
    private static final int STUN_DURATION = 40;
    private static final double ROD_ORIGIN_HEIGHT = 1.0625;
    private static final double ROD_REACH = 3.0;
    private static final double PITCHED_RAM = 0.3;
    private static final double DIVE_RAM = -0.3;

    public DiscoloredRamGoal(BronzeEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean isStunned() {
        return isStunned;
    }

    private Vec3 rodOrigin() {
        return entity.position().add(0.0, ROD_ORIGIN_HEIGHT, 0.0);
    }

    private Vec3 targetCenter() {
        return target.getBoundingBox().getCenter();
    }

    public boolean isDivingRam() {
        return phase == 2 && dashDirection.y < DIVE_RAM;
    }

    private void lookAt(Vec3 point, float yawSpeed, float pitchSpeed) {
        entity.getLookControl().setLookAt(point.x, point.y, point.z, yawSpeed, pitchSpeed);
    }

    @Override
    public boolean canUse() {
        if (!entity.getTarnishLevel().equals(TarnishStage.DISCOLORED)) return false;
        if (entity.getRamCooldown() > 0) return false;

        Player player = entity.level().getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), ATTACK_RANGE, e -> !e.isSpectator() && !BronzeEntity.isSoothed(e));
        if (player != null && entity.hasLineOfSight(player) && !player.isCreative()) {
            target = player;
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        phase = 1;
        phaseTicks = 0;
        entity.getNavigation().stop();
        entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
        // Put the rod into ram position right away so the whole aiming phase is visibly a wind-up
        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, BronzeEntity.RAM_START);
        }
    }

    @Override
    public boolean canContinueToUse() {
        return (phase > 0 || isStunned) && target != null && target.isAlive() && !BronzeEntity.isSoothed(target) && entity.getTarnishLevel().equals(TarnishStage.DISCOLORED);
    }

    @Override
    public void tick() {
        if (isStunned) {
            stunTicks--;
            entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
            if (stunTicks <= 0) {
                isStunned = false;
                entity.setRamCooldown(COOLDOWN);
                if (!entity.level().isClientSide) {
                    entity.level().broadcastEntityEvent(entity, (byte) 73);
                }
                stop();
            }
            return;
        }

        if (target == null) return;
        if (!entity.getTarnishLevel().equals(TarnishStage.DISCOLORED)) {
            stop();
            return;
        }

        switch (phase) {
            case 1:
                // Stay still, keep the whole body (not just the head) turned toward the player
                entity.getNavigation().stop();
                entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
                faceTarget(AIM_TURN_SPEED);
                lookAt(targetCenter(), AIM_TURN_SPEED, AIM_TURN_SPEED);

                phaseTicks++;
                if (phaseTicks >= AIM_TIME) {
                    dashDirection = targetCenter().subtract(rodOrigin()).normalize();
                    float yaw = (float) (Mth.atan2(dashDirection.z, dashDirection.x) * (180F / Math.PI)) - 90F;
                    entity.setYRot(yaw);
                    entity.yBodyRot = yaw;
                    entity.yHeadRot = yaw;
                    lookAt(rodOrigin().add(dashDirection.scale(16.0)), 360.0F, 90.0F);

                    double dashDistance = targetCenter().distanceTo(rodOrigin());
                    dashTime = Mth.clamp(Mth.ceil(dashDistance / RAM_SPEED) + 3, DASH_TIME, MAX_DASH_TIME);
                    hopping = false;
                    phase = 2;
                    phaseTicks = 0;
                }
                break;

            case 2:
                lookAt(rodOrigin().add(dashDirection.scale(16.0)), 360.0F, 90.0F);

                Vec3 dashVelocity = dashDirection.scale(RAM_SPEED);
                boolean grounded = entity.onGround();
                double dashY = dashVelocity.y;
                if (grounded && !isDivingRam() && (gapAhead() || entity.horizontalCollision)) {
                    dashY = JUMP_IMPULSE;
                    hopping = true;
                } else if (hopping && !grounded) {
                    dashY = entity.horizontalCollision ? JUMP_IMPULSE : entity.getDeltaMovement().y;
                } else {
                    if (grounded) hopping = false;
                    if (dashY > 0.0) dashY = entity.getDeltaMovement().y;
                }
                dashVelocity = new Vec3(dashVelocity.x, dashY, dashVelocity.z);
                entity.setDeltaMovement(dashVelocity);

                boolean pitched = Math.abs(dashDirection.y) >= PITCHED_RAM;
                Vec3 rodTip = rodOrigin().add(dashDirection.scale(ROD_REACH));
                AABB hitArea = entity.getBoundingBox().inflate(1.2);
                if (pitched) {
                    hitArea = hitArea.minmax(new AABB(rodTip, rodTip).inflate(1.2));
                }

                java.util.List<net.minecraft.world.entity.LivingEntity> nearbyEntities = entity.level().getEntitiesOfClass(
                        net.minecraft.world.entity.LivingEntity.class,
                        hitArea,
                        e -> e != entity && e.isAlive()
                );

                for (net.minecraft.world.entity.LivingEntity hitEntity : nearbyEntities) {
                    double distance = entity.distanceTo(hitEntity);
                    if (pitched) {
                        distance = Math.min(distance, hitEntity.getBoundingBox().getCenter().distanceTo(rodTip));
                    }

                    if (distance > 2.0) continue;

                    boolean shouldStun = false;
                    if (hitEntity instanceof Player player && player.isBlocking()) {
                        Vec3 attackDir = entity.position().subtract(player.position()).normalize();
                        Vec3 lookVec = player.getLookAngle().normalize();
                        double dot = attackDir.dot(lookVec);

                        if (dot > 0.3) {
                            shouldStun = true;
                        }
                    }

                    boolean damageDealt = hitEntity.hurt(entity.damageSources().mobAttack(entity), 6.0F);

                    if (hitEntity instanceof BronzeEntity && !hitEntity.isAlive() && !entity.level().isClientSide) {
                        ItemStack disc = new ItemStack(RNItems.MUSIC_DISC_SINNER.get());
                        ItemEntity itemEntity = new ItemEntity(
                                entity.level(),
                                hitEntity.getX(), hitEntity.getY(), hitEntity.getZ(),
                                disc
                        );
                        entity.level().addFreshEntity(itemEntity);
                    }

                    if (shouldStun) {
                        if (hitEntity instanceof Player player) {
                            player.getCooldowns().addCooldown(player.getUseItem().getItem(), 50);
                            player.stopUsingItem();
                        }
                        triggerStun();
                        return;
                    }

                    if (damageDealt || !shouldStun) {
                        double knockbackStrength = 0.5;
                        hitEntity.knockback(knockbackStrength,
                                -dashDirection.x,
                                -dashDirection.z);
                    }

                    stop();
                    return;
                }

                phaseTicks++;
                if (phaseTicks >= dashTime) {
                    stop();
                }
                break;
        }
    }

    private boolean solidAt(double x, double y, double z) {
        BlockPos pos = BlockPos.containing(x, y, z);
        return !entity.level().getBlockState(pos).getCollisionShape(entity.level(), pos).isEmpty();
    }

    private boolean floorBelow(double x, double z) {
        return solidAt(x, entity.getY() - 0.5, z) || solidAt(x, entity.getY() - 1.5, z);
    }

    private boolean gapAhead() {
        double length = Math.sqrt(dashDirection.x * dashDirection.x + dashDirection.z * dashDirection.z);
        if (length < 1.0E-4) return false;
        double hx = dashDirection.x / length;
        double hz = dashDirection.z / length;

        boolean gap = false;
        for (double d = 0.8; d <= 1.6; d += 0.8) {
            if (!floorBelow(entity.getX() + hx * d, entity.getZ() + hz * d)) {
                gap = true;
                break;
            }
        }
        if (!gap) return false;

        for (double d = 2.4; d <= 6.4; d += 0.8) {
            if (floorBelow(entity.getX() + hx * d, entity.getZ() + hz * d)) return true;
        }
        return false;
    }

    private void faceTarget(float maxTurn) {
        double dx = target.getX() - entity.getX();
        double dz = target.getZ() - entity.getZ();
        float targetYaw = (float) (Mth.atan2(dz, dx) * (180F / Math.PI)) - 90F;
        float yaw = Mth.approachDegrees(entity.getYRot(), targetYaw, maxTurn);
        entity.setYRot(yaw);
        entity.yBodyRot = yaw;
        entity.yHeadRot = yaw;
    }

    public boolean isDashing() {
        return phase == 2;
    }

    private void triggerStun() {
        isStunned = true;
        stunTicks = STUN_DURATION;
        entity.setDeltaMovement(Vec3.ZERO);
        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, BronzeEntity.RAM_STOP); // Stop ram
            entity.level().broadcastEntityEvent(entity, BronzeEntity.STUN_START); // Start stun
            entity.level().playSound(null, entity.blockPosition(), net.minecraft.sounds.SoundEvents.BEACON_DEACTIVATE, net.minecraft.sounds.SoundSource.HOSTILE, 1.2F, 0.8F);
            entity.level().playSound(null, entity.blockPosition(), net.minecraft.sounds.SoundEvents.REDSTONE_TORCH_BURNOUT, net.minecraft.sounds.SoundSource.HOSTILE, 1.5F, 0.5F);
        }
        phase = 0;
    }

    @Override
    public void stop() {
        if (!isStunned) {
            entity.setRamCooldown(COOLDOWN);
            if (!entity.level().isClientSide && phase != 0) {
                entity.level().broadcastEntityEvent(entity, BronzeEntity.RAM_STOP); // Stop ram
            }
        }
        entity.setDeltaMovement(Vec3.ZERO);
        phase = 0;
        hopping = false;
        target = null;
    }
}