package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.blocks.TarnishingBronze;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BronzeEntity extends TarnishingEntity {
    public int idleAnimationTimeout = 0;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();

    public final AnimationState unaffectedAttackAnimationState = new AnimationState();

    private int lastTarnishLevel = -1;


    public BronzeEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SILVERFISH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SILVERFISH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH;
    }


    @Nullable
    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistance() > 0.01F;
    }

    protected boolean isSunSensitive() {
        return false;
    }
    @Override
    protected void registerGoals() {
        // base
        this.goalSelector.addGoal(0, new FloatGoal(this){
            public boolean canUse() { return (BronzeEntity.this.getTarnishLevel() == 0 || BronzeEntity.this.getTarnishLevel() == 1 || BronzeEntity.this.getTarnishLevel() == 4) && super.canUse(); }
        });
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false));

        //unaffected
        this.targetSelector.addGoal(2, new UnaffectedAttackGoal<>(this, Player.class));

        //discolored

        //corroded

        //tarnished

        //crystallized
        this.goalSelector.addGoal(4, new AvoidEntityGoal(this, Player.class, 15.0F, 2.2, 2.2){
            public boolean canUse() { return BronzeEntity.this.getTarnishLevel() == 4 && super.canUse(); }
        });
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }
    
    public boolean isCrystallized(){
        return this.getTarnishLevel() == 4;
    }
    
    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();

        if (!level().isClientSide()) {
            if (isMoving()) {
                walkAnimationState.startIfStopped(tickCount);
                idleAnimationState.stop();
            } else {
                idleAnimationState.startIfStopped(tickCount);
                walkAnimationState.stop();
            }

            int currentLevel = getTarnishLevel();
            if (currentLevel != lastTarnishLevel) {
                lastTarnishLevel = currentLevel;
                updateAttributesForTarnish(currentLevel);
            }
        }
        if (!this.level().isClientSide()) {
            List<BronzeEntity> bronzes = this.level().getEntitiesOfClass(BronzeEntity.class, this.getBoundingBox().inflate(10.0D));

            long crystallizedBronze = bronzes.stream().filter(BronzeEntity::isCrystallized).count();
            int cozinessLevel = (int) Math.min(crystallizedBronze - 1, 4);

            List<Player> nearbyPlayers = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10.0D));

            for (Player player : nearbyPlayers) {
                if (crystallizedBronze > 0) {
                    int currentLevel = player.hasEffect(RNEffects.BRONZE_DISEASED)
                            ? player.getEffect(RNEffects.BRONZE_DISEASED).getAmplifier()
                            : -1;

                    if (currentLevel < cozinessLevel) {
                        player.addEffect(new MobEffectInstance(RNEffects.BRONZE_DISEASED, 200, cozinessLevel, false, true));
                    }
                } else {
                    if (player.hasEffect(RNEffects.BRONZE_DISEASED)) {
                        MobEffectInstance effect = player.getEffect(RNEffects.BRONZE_DISEASED);
                        int currentLevel = effect.getAmplifier();
                        if (currentLevel > 0) {
                            player.addEffect(new MobEffectInstance(RNEffects.BRONZE_DISEASED, 200, currentLevel - 1, false, true));
                        } else {
                            player.removeEffect(RNEffects.BRONZE_DISEASED);
                        }
                    }
                }
            }
        }
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    private void updateAttributesForTarnish(int level) {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);

        if (speed != null && health != null && attack != null) {
            float oldMaxHealth = (float) health.getBaseValue();

            switch (level) {
                case 0 -> {
                    speed.setBaseValue(0.25D);
                    health.setBaseValue(20F);
                    attack.setBaseValue(5F);
                }
                case 1 -> {
                    speed.setBaseValue(0.2D);
                    health.setBaseValue(30F);
                    attack.setBaseValue(10F);
                }
                case 2 -> {
                    speed.setBaseValue(0.15D);
                    health.setBaseValue(40F);
                    attack.setBaseValue(15F);
                }
                case 3 -> {
                    speed.setBaseValue(0.1D);
                    health.setBaseValue(60F);
                    attack.setBaseValue(10F);
                }
                case 4 -> {
                    speed.setBaseValue(0.32D);
                    health.setBaseValue(8F);
                    attack.setBaseValue(4F);
                }
            }

            float newMaxHealth = (float) health.getBaseValue();
            if (this.getHealth() == oldMaxHealth) {
                this.setHealth(newMaxHealth);
            }
        }
    }


    public class UnaffectedAttackGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final TarnishingEntity entity;

        public UnaffectedAttackGoal(TarnishingEntity entity, Class<T> targetType) {
            super(entity, targetType, true);
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return entity.getTarnishLevel() == 0 && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return entity.getTarnishLevel() == 0 && super.canContinueToUse();
        }

        @Override
        public void start() {
            super.start();
            if(!level().isClientSide){
                entity.level().broadcastEntityEvent(entity, (byte) 61);
            }
        }

        @Override
        public void tick(){
            if(!level().isClientSide){
                entity.level().broadcastEntityEvent(entity, (byte) 61);
            }
        }
        @Override
        public void stop() {
            super.stop();
            if(!level().isClientSide){
                entity.level().broadcastEntityEvent(entity, (byte) 64);
            }
        }
    }


    @Override
    public void handleEntityEvent(byte state) {
        if (state == 61){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.unaffectedAttackAnimationState.startIfStopped(this.tickCount);
        }
        if (state == 64){
            this.unaffectedAttackAnimationState.stop();
        }
        else super.handleEntityEvent(state);
    }


    public class DiscoloredAttackGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
        private final TarnishingEntity entity;

        public DiscoloredAttackGoal(TarnishingEntity entity, Class<T> targetType) {
            super(entity, targetType, true);
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return entity.getTarnishLevel() == 1 && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return entity.getTarnishLevel() == 1 && super.canContinueToUse();
        }

        @Override
        public void start() {
            super.start();
        }

        @Override
        public void tick(){
        }
        @Override
        public void stop() {
            super.stop();
        }
    }

}
