package corundum.rubinated_nether.content.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.TarnishingBronze;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class BronzeEntity extends TarnishingEntity {
    public int idleAnimationTimeout = 0;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState unaffectedAttackAnimationState = new AnimationState();
    public final AnimationState defendAnimationState = new AnimationState();
    public final AnimationState stunAnimationState = new AnimationState();
    public final AnimationState drillAnimationState = new AnimationState();
    public final AnimationState undergroundWalkAnimationState = new AnimationState();
    public final AnimationState ambushAnimationState = new AnimationState();

    private int lastTarnishLevel = -1;
    private TarnishedShockwaveGoal shockwaveGoal;
    private DiscoloredRamGoal dashGoal;
    private int shockwaveCooldownTicks = 0;
    private int ramCooldownTicks = 0;
    private int ambushCooldownTicks = 0;
    private boolean isBurrowed = false;
    private boolean noCollision = false;


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
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));

        this.registerUnaffectedGoals();
        this.registerDiscoloredGoals();
        this.registerCorrodedGoals();
        this.registerTarnishedGoals();
        this.registerCrystallizedGoals();
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
        this.goalSelector.addGoal(3, new AvoidEntityGoal<Player>(this, Player.class, 10.0F, 1.2, 1.2){
            public boolean canUse() { return BronzeEntity.this.getTarnishLevel() == 1 && super.canUse(); }
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
        this.goalSelector.addGoal(4, new AvoidEntityGoal<Player>(this, Player.class, 15.0F, 2.2, 2.2){
            public boolean canUse() { return BronzeEntity.this.getTarnishLevel() == 4 && super.canUse(); }
        });
        this.goalSelector.addGoal(1, new CrystallizeNearbyBronzeGoal(this));
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

    public void setNoCollision(boolean noCollision) {
        this.noCollision = noCollision;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (shockwaveGoal != null && shockwaveGoal.isStunned) {
            setDeltaMovement(Vec3.ZERO);
        }
    }

    public boolean isDashing() {
        return this.dashGoal != null && this.dashGoal.isDashing();
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
        return isBurrowed;
    }

    public void setBurrowed(boolean burrowed) {
        this.isBurrowed = burrowed;
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
        if (state == 68){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.defendAnimationState.startIfStopped(200);
        }
        if (state == 69){
            this.defendAnimationState.stop();
        }
        if (state == 71){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.defendAnimationState.stop();
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
        else super.handleEntityEvent(state);
    }

    public class CrystallizeNearbyBronzeGoal extends Goal {
        private final BronzeEntity entity;
        private int cooldown;

        public CrystallizeNearbyBronzeGoal(BronzeEntity entity) {
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return entity.getTarnishLevel() == 4;
        }

        @Override
        public void tick() {
            if (--cooldown > 0) return;
            cooldown = 20 + entity.getRandom().nextInt(200);

            BlockPos origin = entity.blockPosition();
            Level level = entity.level();

            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-3, -3, -3), origin.offset(3, 3, 3))) {
                BlockState state = level.getBlockState(pos);
                Block block = state.getBlock();

                if (block instanceof TarnishingBronze tarnishing) {
                    if (state.hasProperty(TarnishingBronze.WAXED) && state.getValue(TarnishingBronze.WAXED)) {
                        BlockState unwaxed = state.setValue(TarnishingBronze.WAXED, false);
                        level.setBlock(pos, unwaxed, 3);
                        return;
                    }

                    if (!state.getValue(TarnishingBronze.WAXED) && TarnishingBronze.canCrystallize(block)) {
                        tarnishing.getCrystallized(state).ifPresent(newState -> level.setBlockAndUpdate(pos, newState));
                        return;
                    }
                }
            }
        }
    }

    public class TarnishedShockwaveGoal extends Goal {
        private final BronzeEntity entity;
        private int defenseTicks = 0;
        private int hitCount = 0;
        public boolean isDefending = false;

        private boolean isStunned = false;
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
            if (isDefending) {
                if (!level().isClientSide) {
                    entity.level().broadcastEntityEvent(entity, (byte) 68);
                }
                defenseTicks++;

                if (defenseTicks >= MAX_DEFENSE_TICKS) {
                    isDefending = false;
                    if (!level().isClientSide) {
                        entity.level().broadcastEntityEvent(entity, (byte) 69);
                    }

                    if (hitCount < MAX_HITS_ALLOWED) {
                        spawnShockwaveParticles();
                        buffNearbyBronzes();
                        entity.setShockwaveCooldown(COOLDOWN_DURATION);
                        this.stop();
                    } else {
                        isStunned = true;
                        stunTicks = STUN_DURATION;

                        if (!level().isClientSide) {
                            entity.level().broadcastEntityEvent(entity, (byte) 71);
                        }
                    }
                }

                if (hitCount >= MAX_HITS_ALLOWED) {
                    isDefending = false;
                    isStunned = true;
                    stunTicks = STUN_DURATION;

                    if (!level().isClientSide) {
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

            if (!level().isClientSide) {
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


    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(this.isBurrowed()){
            return false;
        }
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

        return super.hurt(source, amount);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (this.getTarnishLevel() == 3 && shockwaveGoal != null && shockwaveGoal.isDefending()) {
            return;
        }
        super.knockback(strength, x, z);
    }


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
        private static final int STUN_DURATION = 60;


        public DiscoloredRamGoal(BronzeEntity entity) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (entity.getTarnishLevel() != 1) return false;
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
            return (phase > 0 || isStunned) && target != null && target.isAlive() && entity.getTarnishLevel() == 1;
        }

        @Override
        public void tick() {
            if (isStunned) {
                stunTicks--;
                entity.setDeltaMovement(0, entity.getDeltaMovement().y, 0);
                if (stunTicks <= 0) {
                    isStunned = false;
                    entity.setRamCooldown(COOLDOWN);
                    if (!level().isClientSide) {
                        entity.level().broadcastEntityEvent(entity, (byte) 73);
                    }
                    stop();
                }
                return;
            }

            if (target == null) return;

            switch (phase) {
                case 1:
                    entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
                    phaseTicks++;
                    if (phaseTicks >= CHARGE_TIME) {
                        dashDirection = target.position().subtract(entity.position()).normalize();
                        phase = 2;
                        phaseTicks = 0;
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
            entity.level().broadcastEntityEvent(entity, (byte) 71);
            phase = 0;
        }

        @Override
        public void stop() {
            if (!isStunned) {
                entity.setRamCooldown(COOLDOWN);
            }
            entity.setDeltaMovement(Vec3.ZERO);
            phase = 0;
            target = null;
        }
    }


    public class CorrodedHideAndAmbushGoal extends Goal {
        private final BronzeEntity entity;
        private Player target;

        private int state = 0;
        private int stateTicks = 0;
        private Vec3 ambushTargetPos;

        private static final double MOVE_SPEED = 0.3;
        private static final float ATTACK_RANGE = 1.5f;
        private boolean hasAttacked = false;

        public CorrodedHideAndAmbushGoal(BronzeEntity entity) {
            this.entity = entity;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (entity.getAmbushCooldown() > 0) return false;
            if (entity.getTarget() instanceof Player player && entity.getTarnishLevel() == 2) {
                if (entity.distanceTo(player) < 10 && entity.hasLineOfSight(player)) {
                    this.target = player;
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return state != 0 && entity.getTarnishLevel() == 2 && target != null && !entity.isInWater() && target.isAlive();
        }

        @Override
        public void start() {
            state = 1;
            stateTicks = 0;
            entity.setBurrowed(true);
            entity.getNavigation().stop();
            entity.setNoCollision(true);

            if (!level().isClientSide) {
                entity.level().broadcastEntityEvent(entity, (byte) 76);
            }
        }

        @Override
        public void tick() {
            ambushTargetPos = target.position();
            switch (state) {
                case 1 -> {
                    stateTicks++;
                    if (stateTicks >= 20) {
                        state = 2;
                        stateTicks = 0;

                        if (entity.level() instanceof ServerLevel server) {
                            BlockPos below = entity.blockPosition().below();
                            BlockState blockstate = entity.level().getBlockState(below);
                            ParticleOptions dust = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);

                            server.sendParticles(dust, entity.getX(), entity.getY() + 0.1, entity.getZ(), 4, 0.2, 0.05, 0.2, 0.02);
                        }

                        if (!level().isClientSide) {
                            entity.level().broadcastEntityEvent(entity, (byte) 79);
                        }
                    }
                }

                case 2 -> {
                    Vec3 direction = ambushTargetPos.subtract(entity.position()).normalize();
                    entity.setDeltaMovement(direction.scale(MOVE_SPEED));
                    if (entity.level() instanceof ServerLevel server) {
                        BlockPos below = entity.blockPosition().below();
                        BlockState blockstate = entity.level().getBlockState(below);
                        ParticleOptions dust = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);

                        server.sendParticles(dust, entity.getX(), entity.getY() + 0.1, entity.getZ(), 4, 0.2, 0.05, 0.2, 0.02);
                    }

                    if (entity.distanceToSqr(ambushTargetPos) < 1.1) {
                        entity.setDeltaMovement(Vec3.ZERO);
                        state = 3;
                        stateTicks = 0;

                        if (!level().isClientSide) {
                            entity.level().broadcastEntityEvent(entity, (byte) 81);
                        }
                        if (entity.level() instanceof ServerLevel server) {
                            BlockPos below = entity.blockPosition().below();
                            BlockState blockstate = entity.level().getBlockState(below);
                            ParticleOptions dust = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);

                            server.sendParticles(dust, entity.getX(), entity.getY() + 0.1, entity.getZ(), 4, 0.2, 0.05, 0.2, 0.02);
                        }
                    }
                }

                case 3 -> {
                    stateTicks++;
                    entity.setDeltaMovement(Vec3.ZERO);
                    if (!level().isClientSide) {
                        entity.level().broadcastEntityEvent(entity, (byte) 81);
                    }
                    if (stateTicks == 15 && !hasAttacked) {
                        hasAttacked = true;

                        boolean shouldDamage = entity.distanceToSqr(target) < ATTACK_RANGE;
                        if (shouldDamage) {
                            if (entity.level() instanceof ServerLevel server) {
                                server.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1, target.getZ(), 10, 0.3, 0.2, 0.3, 0.1);
                            }
                            target.hurt(entity.damageSources().mobAttack(entity), 6.0F);
                        }
                    }

                    if (stateTicks >= 25) {
                        entity.setBurrowed(false);
                        stop();
                    }
                }


                case 4 -> {

                }
            }
        }

        @Override
        public void stop() {
            state = 0;
            stateTicks = 0;
            target = null;
            entity.setBurrowed(false);
            entity.setAmbushCooldown(30);
            hasAttacked = false;
            entity.setNoCollision(false);

            if (!level().isClientSide) {
                entity.level().broadcastEntityEvent(entity, (byte) 87);
            }
        }
    }

    @Override
    public boolean isPushable() {
        return !noCollision && super.isPushable();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !noCollision && super.canBeCollidedWith();
    }

}
