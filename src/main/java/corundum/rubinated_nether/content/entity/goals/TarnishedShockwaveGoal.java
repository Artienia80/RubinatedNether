package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class TarnishedShockwaveGoal extends Goal {
    private final BronzeEntity entity;
    private int defenseTicks = 0;
    private int hitCount = 0;
    public boolean isDefending = false;

    public boolean isStunned = false;
    private int stunTicks = 0;

    private static final int MAX_DEFENSE_TICKS = 150;
    private static final int MAX_HITS_ALLOWED = 5;
    private static final int STUN_DURATION = 80;
    private static final int COOLDOWN_DURATION = 300;

    public TarnishedShockwaveGoal(BronzeEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return entity.getShockwaveCooldown() <= 0
                && entity.getTarnishLevel() == 3
                && entity.getTarget() instanceof Player;
    }

    @Override
    public void start() {
        defenseTicks = 0;
        hitCount = 0;
        isDefending = true;
        isStunned = false;
        stunTicks = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return (isDefending || isStunned) && entity.getTarnishLevel() == 3;
    }

    public boolean isDefending() {
        return isDefending;
    }

    @Override
    public void tick() {
        if(entity.getTarnishLevel() != 3) stop();
        if (isDefending) {
            if (!entity.level().isClientSide) {
                entity.level().broadcastEntityEvent(entity, (byte) 68);
            }
            defenseTicks++;

            if (defenseTicks >= MAX_DEFENSE_TICKS) {
                isDefending = false;
                if (!entity.level().isClientSide) {
                    entity.level().broadcastEntityEvent(entity, (byte) 69);
                }

                if (hitCount < MAX_HITS_ALLOWED) {
                    if (!entity.level().isClientSide) {
                        entity.level().broadcastEntityEvent(entity, (byte) 89);
                    }
                    spawnShockwaveParticles();
                    buffNearbyBronzes();
                    entity.setShockwaveCooldown(COOLDOWN_DURATION);
                    this.stop();
                } else {
                    isStunned = true;
                    stunTicks = STUN_DURATION;

                    if (!entity.level().isClientSide) {
                        entity.level().broadcastEntityEvent(entity, (byte) 71);
                    }
                }
            }

            if (hitCount >= MAX_HITS_ALLOWED) {
                isDefending = false;
                isStunned = true;
                stunTicks = STUN_DURATION;

                if (!entity.level().isClientSide) {
                    entity.level().broadcastEntityEvent(entity, (byte) 69);
                    entity.level().broadcastEntityEvent(entity, (byte) 71);
                }
            }
        }

        if (isStunned) {
            stunTicks--;
            if (stunTicks <= 0) {
                isStunned = false;
                entity.setShockwaveCooldown(COOLDOWN_DURATION);

                this.stop();
            }
        }

        if (entity.getShockwaveCooldown() > 0) {
            entity.setShockwaveCooldown(COOLDOWN_DURATION);
        }

        System.out.println(defenseTicks);
        System.out.println(hitCount);
        System.out.println(isDefending());
        System.out.println(isStunned);
    }

    @Override
    public void stop() {
        isDefending = false;
        isStunned = false;
        stunTicks = 0;

        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, (byte) 69);
            entity.level().broadcastEntityEvent(entity, (byte) 73);
        }
    }

    public void onHitWhileDefending() {
        if (isDefending()) {
            hitCount++;
        }
    }

    private void spawnShockwaveParticles() {
        ((ServerLevel) entity.level()).sendParticles(ParticleTypes.EXPLOSION, entity.getX(), entity.getY(), entity.getZ(), 20, 1, 1, 1, 0.2);
    }

    private void buffNearbyBronzes() {
        List<BronzeEntity> allies = entity.level().getEntitiesOfClass(BronzeEntity.class, entity.getBoundingBox().inflate(12.0));
        for (BronzeEntity bronze : allies) {
            if (bronze != entity && bronze.isAlive()) {
                bronze.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));
            }
        }
    }
}
