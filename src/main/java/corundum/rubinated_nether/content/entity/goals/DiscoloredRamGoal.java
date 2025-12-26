package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
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
        if(!entity.getTarnishLevel().equals(TarnishStage.DISCOLORED)) stop();

        switch (phase) {
            case 1:
                entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
                phaseTicks++;
                if (phaseTicks >= CHARGE_TIME) {
                    dashDirection = target.position().subtract(entity.position()).normalize();
                    phase = 2;
                    phaseTicks = 0;
                    // Start ram animation when beginning the dash
                    if (!entity.level().isClientSide) {
                        entity.level().broadcastEntityEvent(entity, BronzeEntity.RAM_START);
                    }
                }
                break;

            case 2:
                entity.setDeltaMovement(dashDirection.scale(RAM_SPEED));
                entity.setYRot((float) (Mth.atan2(dashDirection.z, dashDirection.x) * (180F / Math.PI)) - 90F);
                entity.yBodyRot = entity.getYRot();

                if (entity.distanceTo(target) < 1.5) {
                    boolean hasShield = target.isBlocking();
                    if (hasShield) {
                        Vec3 attackDir = entity.position().subtract(target.position()).normalize();
                        Vec3 lookVec = target.getLookAngle().normalize();
                        double dot = attackDir.dot(lookVec);

                        if (dot > 0.3) {
                            triggerStun();
                            return;
                        }
                    }

                    target.hurt(entity.damageSources().mobAttack(entity), 6.0F);
                    stop();
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
        // Stop ram animation and start stun animation
        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, BronzeEntity.RAM_STOP); // Stop ram
            entity.level().broadcastEntityEvent(entity, BronzeEntity.STUN_START); // Start stun
            // Play loud metallic crash sound
            entity.level().playSound(null, entity.blockPosition(), net.minecraft.sounds.SoundEvents.ANVIL_LAND, net.minecraft.sounds.SoundSource.HOSTILE, 1.5F, 0.5F);
        }
        phase = 0;
    }

    @Override
    public void stop() {
        if (!isStunned) {
            entity.setRamCooldown(COOLDOWN);
            // Stop ram animation when goal ends normally
            if (!entity.level().isClientSide && phase == 2) {
                entity.level().broadcastEntityEvent(entity, BronzeEntity.RAM_STOP); // Stop ram
            }
        }
        entity.setDeltaMovement(Vec3.ZERO);
        phase = 0;
        target = null;
    }
}