package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class DiscoloredRamGoal extends Goal {
    private final BronzeEntity entity;
    private Player target;

    private int phase = 0;
    private int phaseTicks = 0;
    private Vec3 dashDirection = Vec3.ZERO;
    private static final int CHARGE_TIME = 20;
    private static final int DASH_TIME = 10;
    private static final int COOLDOWN = 80;
    private static final double RAM_SPEED = 1.2;
    private boolean isStunned = false;
    private int stunTicks = 0;
    private static final int STUN_DURATION = 40;


    public DiscoloredRamGoal(BronzeEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean isStunned() {
        return isStunned;
    }

    @Override
    public boolean canUse() {
        if (!entity.getTarnishLevel().equals(TarnishStage.DISCOLORED)) return false;
        if (entity.getRamCooldown() > 0) return false;

        Player player = entity.level().getNearestPlayer(entity, 15);
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
    }

    @Override
    public boolean canContinueToUse() {
        return (phase > 0 || isStunned) && target != null && target.isAlive() && entity.getTarnishLevel().equals(TarnishStage.DISCOLORED);
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
        if (!entity.getTarnishLevel().equals(TarnishStage.DISCOLORED)) stop();

        switch (phase) {
            case 1:
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
                phaseTicks++;
                if (phaseTicks >= CHARGE_TIME) {
                    dashDirection = target.position().subtract(entity.position()).normalize();
                    entity.setYRot((float) (Mth.atan2(dashDirection.z, dashDirection.x) * (180F / Math.PI)) - 90F);
                    entity.yBodyRot = entity.getYRot();
                    entity.yHeadRot = entity.getYRot();

                    phase = 2;
                    phaseTicks = 0;
                    if (!entity.level().isClientSide) {
                        entity.level().broadcastEntityEvent(entity, BronzeEntity.RAM_START);
                    }
                }
                break;

            case 2:
                entity.setDeltaMovement(dashDirection.scale(RAM_SPEED));

                java.util.List<net.minecraft.world.entity.LivingEntity> nearbyEntities = entity.level().getEntitiesOfClass(
                        net.minecraft.world.entity.LivingEntity.class,
                        entity.getBoundingBox().inflate(1.2),
                        e -> e != entity && e.isAlive()
                );

                for (net.minecraft.world.entity.LivingEntity hitEntity : nearbyEntities) {
                    double distance = entity.distanceTo(hitEntity);

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
                if (phaseTicks >= DASH_TIME) {
                    stop();
                }
                break;
        }
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
            if (!entity.level().isClientSide && phase == 2) {
                entity.level().broadcastEntityEvent(entity, BronzeEntity.RAM_STOP); // Stop ram
            }
        }
        entity.setDeltaMovement(Vec3.ZERO);
        phase = 0;
        target = null;
    }
}