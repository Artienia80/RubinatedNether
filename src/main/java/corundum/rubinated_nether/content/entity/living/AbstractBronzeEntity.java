package corundum.rubinated_nether.content.entity.living;

import corundum.rubinated_nether.content.BronzeTarnishingStep;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

/**
 * Abstract class that represents all the Bronze-type entities.
 * <br/>
 * For information, this can't be instantiated and is just used as
 * a replacement for the concrete implementations.
 * <br/>
 * Permitted classes are: <br/>
 *  - {@code BronzeEntity} <br/>
 *  - {@code DiscoloredEntity} <br/>
 *  - {@code CorrodedEntity} <br/>
 *  - {@code TarnishedEntity} <br/>
 *  - {@code CrystallizedEntity} <br/>
 * <br/>
 * Why sealed? Because I fucking wanted to.
 */
public sealed abstract class AbstractBronzeEntity extends Monster permits BronzeEntity, DiscoloredEntity, CorrodedEntity, TarnishedEntity, CrystallizedEntity {

    //TODO: Overall cleanup and well-defined base structure

    protected int idleAnimationTimeout = 0;
    protected final AnimationState idleAnimationState = new AnimationState();
    protected final AnimationState walkAnimationState = new AnimationState();

    protected AbstractBronzeEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    /**
     * Needs to be overridden to be able to set up SpawnPlacements in an
     * organized way.
     */
    public static void init() {
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
    }

    protected void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 120;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    @Override
    public void tick() {
        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
        super.tick();

        if (this.level().isClientSide()) return;

        if (this.isMoving()) {
            walkAnimationState.startIfStopped(tickCount);
            idleAnimationState.stop();
        } else {
            idleAnimationState.startIfStopped(tickCount);
            walkAnimationState.stop();
        }
    }

    //TODO: Implement Bronze Sounds

    @Override
    public Fallsounds getFallSounds() {
        return super.getFallSounds();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return super.getHurtSound(damageSource);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return super.getAmbientSound();
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    public boolean isMoving() {
        return this.getDeltaMovement().horizontalDistance() > 0.01F;
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    //TODO: Analyze and elaborate more specific behaviours and methods

    public abstract BronzeTarnishingStep getTarnishingLevel();
}
