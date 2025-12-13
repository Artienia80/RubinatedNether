package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.entity.goals.CorrodedHideAndAmbushGoal;
import corundum.rubinated_nether.content.entity.goals.CrystallizeNearbyBronzeGoal;
import corundum.rubinated_nether.content.entity.goals.CrystallizedOrbitGoal;
import corundum.rubinated_nether.content.entity.goals.DiscoloredRamGoal;
import corundum.rubinated_nether.content.entity.goals.TarnishedShockwaveGoal;
import corundum.rubinated_nether.content.entity.goals.UnaffectedAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BronzeEntity extends TarnishingEntity {
    public int idleAnimationTimeout = 0;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState unaffectedAttackAnimationState = new AnimationState();
    public final AnimationState defendAnimationState = new AnimationState();
    public final AnimationState shockwaveAnimationState = new AnimationState();
    public final AnimationState stunAnimationState = new AnimationState();
    public final AnimationState drillAnimationState = new AnimationState();
    public final AnimationState undergroundWalkAnimationState = new AnimationState();
    public final AnimationState ambushAnimationState = new AnimationState();
    public final AnimationState ramAnimationState = new AnimationState();

    private final BronzePart[] subEntities;
    private final BronzePart bodyPart;
    private final BronzePart keyPart;

    private int lastTarnishLevel = -1;
    private TarnishedShockwaveGoal shockwaveGoal;
    private DiscoloredRamGoal dashGoal;
    private int shockwaveCooldownTicks = 0;
    private int ramCooldownTicks = 0;
    private int ambushCooldownTicks = 0;

    private static final EntityDataAccessor<Boolean> IS_BURROWED =
            SynchedEntityData.defineId(BronzeEntity.class, EntityDataSerializers.BOOLEAN);


    public BronzeEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);

        this.bodyPart = new BronzePart(this, "body", 0.7F, 1.4F);
        this.keyPart = new BronzePart(this, "key", 0.25F, 0.375F);

        this.subEntities = new BronzePart[]{this.bodyPart, this.keyPart};
        this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1);
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.subEntities.length; i++) {
            this.subEntities[i].setId(id + i + 1);
        }
    }

    private void tickPart(BronzePart part, double offsetX, double offsetY, double offsetZ, double burrowedOffset) {
        if(this.isBurrowed()){
            part.setPos(this.getX() + offsetX, this.getY() + offsetY - burrowedOffset, this.getZ() + offsetZ);
        } else {
            part.setPos(this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ);
        }
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
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    private void registerUnaffectedGoals() {
        this.targetSelector.addGoal(1, new UnaffectedAttackGoal<>(this, Player.class));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2, false) {
            public boolean canUse() {
                return (BronzeEntity.this.getTarnishLevel() == 0) && super.canUse();
            }
        });
    }

    private void registerDiscoloredGoals() {
        this.dashGoal = new DiscoloredRamGoal(this);
        this.goalSelector.addGoal(2, this.dashGoal);
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 10.0F, 1.2, 1.2) {
            public boolean canUse() {
                return BronzeEntity.this.getTarnishLevel() == 1 && super.canUse();
            }
        });
    }

    private void registerCorrodedGoals() {
        this.targetSelector.addGoal(1, new CorrodedHideAndAmbushGoal(this));
    }

    private void registerTarnishedGoals() {
        this.shockwaveGoal = new TarnishedShockwaveGoal(this);
        this.targetSelector.addGoal(1, shockwaveGoal);
    }

    private void registerCrystallizedGoals() {
        this.goalSelector.addGoal(4, new CrystallizedOrbitGoal(this));
        this.goalSelector.addGoal(1, new CrystallizeNearbyBronzeGoal(this));
    }

    private void removeUnaffectedGoals() {
        this.targetSelector.removeGoal(new UnaffectedAttackGoal<>(this, Player.class));
        this.goalSelector.removeGoal(new MeleeAttackGoal(this, 1.2, false) {
            public boolean canUse() {
                return (BronzeEntity.this.getTarnishLevel() == 0) && super.canUse();
            }
        });
    }

    private void removeDiscoloredGoals() {
        this.dashGoal = new DiscoloredRamGoal(this);
        this.goalSelector.removeGoal(this.dashGoal);
        this.goalSelector.removeGoal(new AvoidEntityGoal<Player>(this, Player.class, 10.0F, 1.2, 1.2){
            public boolean canUse() { return BronzeEntity.this.getTarnishLevel() == 1 && super.canUse(); }
        });
    }

    private void removeCorrodedGoals() {
        this.targetSelector.removeGoal(new CorrodedHideAndAmbushGoal(this));
    }

    private void removeTarnishedGoals() {
        this.shockwaveGoal = new TarnishedShockwaveGoal(this);
        this.targetSelector.removeGoal(shockwaveGoal);
    }

    private void removeCrystallizedGoals() {
        this.goalSelector.removeGoal(new AvoidEntityGoal<Player>(this, Player.class, 15.0F, 2.2, 2.2){
            public boolean canUse() { return BronzeEntity.this.getTarnishLevel() == 4 && super.canUse(); }
        });
        this.goalSelector.removeGoal(new CrystallizeNearbyBronzeGoal(this));
    }

    private void removeAllGoals() {
        removeUnaffectedGoals();
        removeDiscoloredGoals();
        removeCorrodedGoals();
        removeTarnishedGoals();
        removeCrystallizedGoals();
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }
    }

    public int getShockwaveCooldown() {
        return this.shockwaveCooldownTicks;
    }

    public void setShockwaveCooldown(int ticks) {
        this.shockwaveCooldownTicks = ticks;
    }

    public boolean isCrystallized(){
        return this.getTarnishLevel() == 4;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (shockwaveGoal != null && shockwaveGoal.isStunned) {
            setDeltaMovement(Vec3.ZERO);
        }
    }


    @Override
    public void tick() {
        super.tick();
        setupAnimationStates();

        if (this.subEntities != null) {
            Vec3[] partPositions = new Vec3[this.subEntities.length];


            for (int i = 0; i < this.subEntities.length; i++) {
                partPositions[i] = new Vec3(this.subEntities[i].getX(), this.subEntities[i].getY(), this.subEntities[i].getZ());
            }

            // Body part at entity position
            this.tickPart(this.bodyPart, 0.0, 0.0, 0.0, 500);

            // Key part positioned above body
            this.tickPart(this.keyPart, 0.0, 1.4, 0.0, 1.4);

            for (int j = 0; j < this.subEntities.length; j++) {
                this.subEntities[j].xo = partPositions[j].x;
                this.subEntities[j].yo = partPositions[j].y;
                this.subEntities[j].zo = partPositions[j].z;
                this.subEntities[j].xOld = partPositions[j].x;
                this.subEntities[j].yOld = partPositions[j].y;
                this.subEntities[j].zOld = partPositions[j].z;
            }

            // Sync rotation and ensure parts are in world
            for (BronzePart part : this.subEntities) {
                part.setYRot(this.getYRot());
                part.setXRot(this.getXRot());
                if (!this.level().isClientSide() && part.isRemoved()) {
                    this.level().addFreshEntity(part);
                }
            }
        }

        if (!level().isClientSide()) {

            handleMovingAnimationStates();

            int currentLevel = this.getTarnishLevel();
            if (currentLevel != lastTarnishLevel) {
                lastTarnishLevel = currentLevel;
                updateAttributesForTarnish(currentLevel);
                changeGoalsOnLevelChange(currentLevel);
            }
        }

        decreaseCooldowns();


        if (!this.level().isClientSide()) {
            List<BronzeEntity> bronzes = this.level().getEntitiesOfClass(BronzeEntity.class, this.getBoundingBox().inflate(25.0D));

            long crystallizedBronze = bronzes.stream().filter(BronzeEntity::isCrystallized).count();
            int cozinessLevel = (int) Math.min(crystallizedBronze - 1, 4);

            List<Player> nearbyPlayers = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(25.0D));

            for (Player player : nearbyPlayers) {
                if (crystallizedBronze > 0) {
                    int desiredAmplifier = cozinessLevel;
                    MobEffectInstance current = player.getEffect(RNEffects.BRONZE_DISEASED);

                    if (current == null || current.getAmplifier() < desiredAmplifier || current.getDuration() < 100) {
                        player.addEffect(new MobEffectInstance(RNEffects.BRONZE_DISEASED, 210, desiredAmplifier, false, true));
                    }
                } else {
                    if (player.hasEffect(RNEffects.BRONZE_DISEASED)) {
                        MobEffectInstance current = player.getEffect(RNEffects.BRONZE_DISEASED);
                        int currentAmplifier = current.getAmplifier();
                        if (currentAmplifier > 0) {
                            player.addEffect(new MobEffectInstance(RNEffects.BRONZE_DISEASED, 210, currentAmplifier - 1, false, true));
                        } else {
                            player.removeEffect(RNEffects.BRONZE_DISEASED);
                        }
                    }
                }
            }
        }
    }


    private void decreaseCooldowns() {
        if (shockwaveCooldownTicks > 0) {
            shockwaveCooldownTicks--;
        }
        if (ramCooldownTicks > 0) {
            ramCooldownTicks--;
        }
        if (ambushCooldownTicks > 0) {
            ambushCooldownTicks--;
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
        }
    }

    private void handleMovingAnimationStates() {
        if(this.isBurrowed()){
            walkAnimationState.stop();
            idleAnimationState.stop();
            return;
        }

        if (this.isMoving()) {
            walkAnimationState.startIfStopped(tickCount);
            idleAnimationState.stop();
        } else {
            idleAnimationState.startIfStopped(tickCount);
            walkAnimationState.stop();
        }
    }

    public int getRamCooldown() {
        return ramCooldownTicks;
    }

    public void setRamCooldown(int ticks) {
        this.ramCooldownTicks = ticks;
    }

    public int getAmbushCooldown() {
        return ambushCooldownTicks;
    }

    public void setAmbushCooldown(int ticks) {
        this.ambushCooldownTicks = ticks;
    }

    public boolean isBurrowed() {
        return this.entityData.get(IS_BURROWED);
    }

    public void setBurrowed(boolean burrowed) {
        this.entityData.set(IS_BURROWED, burrowed);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    private void updateAttributesForTarnish(int level) {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance defense = this.getAttribute(Attributes.ARMOR);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);

        if (speed != null && health != null && defense != null && attack != null) {
            float oldMaxHealth = (float) health.getBaseValue();

            switch (level) {
                case 0 -> {
                    speed.setBaseValue(0.25D);
                    health.setBaseValue(20F);
                    attack.setBaseValue(5F);
                }
                case 1 -> {
                    speed.setBaseValue(0.2D);
                    health.setBaseValue(20F);
                    attack.setBaseValue(10F);
                }
                case 2 -> {
                    speed.setBaseValue(0.15D);
                    health.setBaseValue(20F);
                    attack.setBaseValue(15F);
                }
                case 3 -> {
                    speed.setBaseValue(0.1D);
                    health.setBaseValue(20F);
                    attack.setBaseValue(10F);
                }
                case 4 -> {
                    speed.setBaseValue(0.32D);
                    health.setBaseValue(8F);
                    attack.setBaseValue(4F);
                }
            }

            float oldDefense = (float) defense.getBaseValue();
            double newDefense = switch (level) {
                case 0 -> 0F;
                case 1 -> 8F;
                case 2 -> 16F;
                case 3 -> 20F;
                case 4 -> 0F;
                default -> defense.getBaseValue();
            };

            if (defense.getBaseValue() == oldDefense && newDefense > oldDefense) {
                defense.setBaseValue(newDefense);
            }
            else if (defense.getBaseValue() != newDefense) {
                defense.setBaseValue(newDefense);
            }

        }
    }

    private void changeGoalsOnLevelChange(int currentLevel) {
        removeAllGoals();
        switch (currentLevel) {
            case 1:
                registerDiscoloredGoals();
                break;
            case 2:
                registerCorrodedGoals();
                break;
            case 3:
                registerTarnishedGoals();
                break;
            case 4:
                registerCrystallizedGoals();
                break;
            default:
                registerUnaffectedGoals();
        };
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
        if (state == 68){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.defendAnimationState.startIfStopped(200);
        }
        if (state == 69){
            this.defendAnimationState.stop();
        }
        if (state == 89){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.defendAnimationState.stop();
            this.shockwaveAnimationState.startIfStopped(10);
        }
        if (state == 92){
            this.shockwaveAnimationState.stop();
        }
        if (state == 71){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.defendAnimationState.stop();
            this.shockwaveAnimationState.stop();
            this.stunAnimationState.startIfStopped(this.tickCount);
        }
        if (state == 73){
            this.stunAnimationState.stop();
        }
        if (state == 76){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.drillAnimationState.startIfStopped(this.tickCount);
        }
        if (state == 79){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.drillAnimationState.stop();
            this.undergroundWalkAnimationState.startIfStopped(this.tickCount);
        }
        if (state == 81){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.drillAnimationState.stop();
            this.undergroundWalkAnimationState.stop();
            this.ambushAnimationState.startIfStopped(this.tickCount);
        }
        if (state == 87){
            this.ambushAnimationState.stop();
            this.undergroundWalkAnimationState.stop();
            this.drillAnimationState.stop();
        }
        if (state == 97){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.ramAnimationState.startIfStopped(this.tickCount);
        }
        if (state == 93){
            this.ramAnimationState.stop();
        }
        else super.handleEntityEvent(state);
    }


    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        BronzePart[] parts = this.subEntities;
        for (int i = 0; i < parts.length; i++) {
            parts[i].setId(i + packet.getId());
        }
    }


    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return !canBeCollidedWith() && super.isPushable();
    }

    @Override
    public boolean canBeCollidedWith() {
        return super.canBeCollidedWith();
    }

    public boolean hurtFromPart(BronzePart part, DamageSource source, float amount) {
        // Apply damage multiplier based on which part was hit
        if (part == this.keyPart) {
            amount *= 2.0F;
        }

        // Tarnished defense mechanics
        if (this.getTarnishLevel() == 3 && shockwaveGoal != null) {
            if (shockwaveGoal.isDefending()) {
                if (!this.level().isClientSide()) {
                    ((ServerLevel) this.level()).sendParticles(
                            ParticleTypes.CRIT,
                            this.getX(), this.getY(0.5), this.getZ(),
                            10, 0.2, 0.4, 0.2, 0.1
                    );
                }
                shockwaveGoal.onHitWhileDefending();
                return false;
            }

            if (shockwaveGoal.isStunned) {
                amount *= 1.5f;
            }
        }

        if (!level().isClientSide() && source.getEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            int level = getTarnishLevel();

            if (!isWaxed() && weapon.is(ItemTags.AXES)) {
                if (level > 0 && level < CRYSTALLIZED) {
                    if (random.nextFloat() < 0.05f) {
                        decreaseTarnishLevel();
                        level().playSound(null, blockPosition(), SoundEvents.AXE_SCRAPE, SoundSource.PLAYERS, 1.0F, 1.0F);

                        if (level().random.nextFloat() < 0.5f) {
                            ItemEntity powder = new ItemEntity(level(), getX(), getY() + 1, getZ(),
                                    new ItemStack(RNItems.BRONZE_POWDER.get()));
                            level().addFreshEntity(powder);
                        }
                    }
                } else if (level == CRYSTALLIZED) {
                    if (random.nextFloat() < 0.05f) {
                        setTarnishLevel(0);
                        level().playSound(null, blockPosition(), SoundEvents.AXE_SCRAPE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (this.subEntities != null) {
            for (BronzePart part : this.subEntities) {
                part.remove(reason);
            }
        }
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (this.getTarnishLevel() == 3 && shockwaveGoal != null && shockwaveGoal.isDefending()) {
            return;
        }
        super.knockback(strength, x, z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BURROWED, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);
        this.refreshDimensions();
    }
}