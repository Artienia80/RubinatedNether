package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.content.entity.goals.CorrodedHideAndAmbushGoal;
import corundum.rubinated_nether.content.blocks.CrystalConversion;
import corundum.rubinated_nether.content.entity.goals.CrystallizedRodVolleyGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import corundum.rubinated_nether.content.entity.goals.CrystallizedOrbitGoal;
import corundum.rubinated_nether.content.entity.goals.DiscoloredRamGoal;
import corundum.rubinated_nether.content.entity.goals.TarnishedPounceGoal;
import corundum.rubinated_nether.content.entity.goals.TarnishedStalkGoal;
import corundum.rubinated_nether.content.entity.goals.UnaffectedAttackGoal;
import corundum.rubinated_nether.content.items.MusicBoxItem;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
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
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.entity.PartEntity;

import java.util.ArrayList;
import java.util.List;

public class BronzeEntity extends TarnishingEntity {

    private TarnishStage lastTarnishLevel = null;

    private final BronzePart[] subEntities;
    private final BronzePart bodyPart;
    private final BronzePart keyPart;

    public static final float ROD_SPIN_SPEED = 0.2F;
    private static final int MAX_RODS = 6;
    private static final double HEAD_CENTER_HEIGHT = 1.0625;
    private static final double ROD_SPAWN_DISTANCE = 1.0;
    private static final float ROD_SHOT_SPEED = 1.6F;
    private static final double ROD_ALIGN_DEGREES = 8.0;
    private static final double ROD_LEAD = 0.85;
    private static final double ROD_LEAD_VERTICAL = 0.5;
    private static final int ROD_LEAD_ITERATIONS = 3;
    private static final int ROD_TRANSITION_TICKS = 10;
    private static final int ROD_AIM_BLEND_TICKS = 10;
    private static final float ROD_AIM_CHASE = 0.3F;
    private static final int PLUMMET_MIN_TICKS = 3;
    private static final int PLUMMET_MAX_TICKS = 100;
    private static final double DETONATION_DAMAGE_RADIUS = 5.0;
    private static final float DETONATION_MAX_DAMAGE = 16.0F;
    private static final float DETONATION_MIN_DAMAGE = 6.0F;
    private static final EntityDataAccessor<Integer> ROD_COUNT =
            SynchedEntityData.defineId(BronzeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ROD_AIMING =
            SynchedEntityData.defineId(BronzeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FIRED_ROD_SLOT =
            SynchedEntityData.defineId(BronzeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ROD_SPIN =
            SynchedEntityData.defineId(BronzeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> TELEGRAPHING =
            SynchedEntityData.defineId(BronzeEntity.class, EntityDataSerializers.BOOLEAN);

    private final PathNavigation groundNavigation;
    private final MoveControl groundMoveControl;
    private final PathNavigation flyingNavigation;
    private final MoveControl flyingMoveControl;
    private CrystallizedRodVolleyGoal volleyGoal;
    private boolean plummeting;
    private float crystalSpeedScale = 1.0F;
    private int plummetTicks;
    private int trackedRodCount = MAX_RODS;
    private int rodTransitionFrom = MAX_RODS;
    private int rodTransition;
    private int rodTransitionFired = -1;
    private float rodSpin;
    private float rodSpinO;
    private float rodSpinSpeed = ROD_SPIN_SPEED;
    private int rodAimTicks;

    // Goals
    private TarnishedStalkGoal stalkGoal;
    private TarnishedPounceGoal pounceGoal;
    private DiscoloredRamGoal dashGoal;
    private int pounceCooldownTicks = 0;
    private int ramCooldownTicks = 0;
    private int ambushCooldownTicks = 0;

    // Synchronized
    private static final EntityDataAccessor<Boolean> IS_BURROWED =
            SynchedEntityData.defineId(BronzeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_DEFENDING =
            SynchedEntityData.defineId(BronzeEntity.class, EntityDataSerializers.BOOLEAN);

    // Animation
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
    public final AnimationState crouchAnimationState = new AnimationState();
    public final AnimationState leapAnimationState = new AnimationState();
    public final AnimationState slamAnimationState = new AnimationState();
    public final AnimationState diveAnimationState = new AnimationState();

    public static final byte NORMAL_ATTACK_START = 61;
    public static final byte NORMAL_ATTACK_STOP = 64;
    public static final byte DEFENCE_START = 68;
    public static final byte DEFENCE_STOP = 69;
    public static final byte SHOCKWAVE_START = 89;
    public static final byte SHOCKWAVE_STOP = 92;
    public static final byte STUN_START = 71;
    public static final byte STUN_STOP = 73;
    public static final byte DRILL_START = 76;
    public static final byte UNDERGROUND_START = 79;
    public static final byte AMBUSH_START = 81;
    public static final byte AMBUSH_STOP = 87;
    public static final byte RAM_STOP = 93;
    public static final byte RAM_START = 97;
    public static final byte CROUCH_START = 110;
    public static final byte CROUCH_STOP = 111;
    public static final byte LEAP_START = 112;
    public static final byte LEAP_STOP = 113;
    public static final byte SLAM_START = 114;
    public static final byte SLAM_STOP = 115;
    public static final byte DIVE_START = 116;


    public BronzeEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);

        this.bodyPart = new BronzePart(this, "body", 0.7F, 1.4F);
        this.keyPart = new BronzePart(this, "key", 0.35F, 0.375F);

        this.subEntities = new BronzePart[]{this.bodyPart, this.keyPart};
        this.setId(ENTITY_COUNTER.getAndAdd(this.subEntities.length + 1) + 1);

        // Lava / fire: don't avoid them in pathfinding (default malus for lava is -1 = never path)
        this.setPathfindingMalus(PathType.LAVA, 0.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0F);

        this.groundNavigation = this.navigation;
        this.groundMoveControl = this.moveControl;
        this.flyingNavigation = new FlyingPathNavigation(this, level);
        this.flyingMoveControl = new FlyingMoveControl(this, 20, true);

        this.setTarnishLevel(TarnishStage.UNAFFECTED);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FLYING_SPEED, 0.4) // required by FlyingMoveControl (crystallized stage)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    // ------------------------------------------------------------------
    // Lava walking
    // ------------------------------------------------------------------

    @Override
    public boolean canStandOnFluid(FluidState fluidState) {
        return fluidState.is(FluidTags.LAVA);
    }

    @Override
    public boolean fireImmune() {
        return true; // otherwise they take lava/fire damage while standing on it
    }

    private void floatOnLava() {
        if (this.isInLava()) {
            CollisionContext ctx = CollisionContext.of(this);
            if (ctx.isAbove(LiquidBlock.STABLE_SHAPE, this.blockPosition(), true)
                    && !this.level().getFluidState(this.blockPosition().above()).is(FluidTags.LAVA)) {
                this.setOnGround(true);
            } else {
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5).add(0.0, 0.05, 0.0));
            }
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new BronzePathNavigation(this, level);
    }

    static class BronzePathNavigation extends GroundPathNavigation {
        BronzePathNavigation(Mob mob, Level level) {
            super(mob, level);
        }

        @Override
        protected PathFinder createPathFinder(int maxVisitedNodes) {
            this.nodeEvaluator = new WalkNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
        }

        @Override
        protected boolean hasValidPathType(PathType type) {
            return type == PathType.LAVA
                    || type == PathType.DAMAGE_FIRE
                    || type == PathType.DANGER_FIRE
                    || super.hasValidPathType(type);
        }

        @Override
        public boolean isStableDestination(BlockPos pos) {
            return this.level.getBlockState(pos).is(Blocks.LAVA) || super.isStableDestination(pos);
        }
    }

    // ------------------------------------------------------------------
    // Goals
    // ------------------------------------------------------------------

    private static final int SINNER_NOTE_INTERVAL = 8;
    private static final double SINNER_NOTE_RANGE = 32.0D;
    private static final int[] SINNER_NOTE_SEQUENCE = {0, 4, 7, 12, 7, 4, 9, 14, 9, 5, 12, 16, 12, 7, 4, 0};

    @Override
    protected void registerGoals() {
        // base
        this.goalSelector.addGoal(0, new FloatGoal(this){
            public boolean canUse() {
                return !BronzeEntity.this.isInLava() &&
                        (BronzeEntity.this.getTarnishLevel().equals(TarnishStage.UNAFFECTED) ||
                                BronzeEntity.this.getTarnishLevel().equals(TarnishStage.DISCOLORED) ||
                                BronzeEntity.this.getTarnishLevel().equals(TarnishStage.CRYSTALLIZED)) &&
                        super.canUse();
            }
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
                return (BronzeEntity.this.getTarnishLevel().equals(TarnishStage.UNAFFECTED)) && super.canUse();
            }
        });
    }

    private void registerDiscoloredGoals() {
        this.dashGoal = new DiscoloredRamGoal(this);
        this.goalSelector.addGoal(2, this.dashGoal);
        this.goalSelector.addGoal(3, new AvoidEntityGoal<Player>(this, Player.class, e -> !isSoothed(e), 10.0F, 1.2, 1.2, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test) {
            public boolean canUse() {
                return BronzeEntity.this.getTarnishLevel().equals(TarnishStage.DISCOLORED) && super.canUse();
            }
        });
    }

    private void registerCorrodedGoals() {
        this.targetSelector.addGoal(1, new CorrodedHideAndAmbushGoal(this));
    }

    private void registerTarnishedGoals() {
        removeTarnishedGoals();
        this.pounceGoal = new TarnishedPounceGoal(this);
        this.stalkGoal = new TarnishedStalkGoal(this);
        this.goalSelector.addGoal(2, this.pounceGoal);
        this.goalSelector.addGoal(3, this.stalkGoal);
    }

    private void registerCrystallizedGoals() {
        this.goalSelector.addGoal(4, new CrystallizedOrbitGoal(this));
        if (this.volleyGoal != null) {
            this.goalSelector.removeGoal(this.volleyGoal);
        }
        this.volleyGoal = new CrystallizedRodVolleyGoal(this);
        this.goalSelector.addGoal(2, this.volleyGoal);
    }

    private void removeUnaffectedGoals() {
        this.targetSelector.removeGoal(new UnaffectedAttackGoal<>(this, Player.class));
        this.goalSelector.removeGoal(new MeleeAttackGoal(this, 1.2, false) {
            public boolean canUse() {
                return (BronzeEntity.this.getTarnishLevel().equals(TarnishStage.UNAFFECTED)) && super.canUse();
            }
        });
    }

    private void removeDiscoloredGoals() {
        this.dashGoal = new DiscoloredRamGoal(this);
        this.goalSelector.removeGoal(this.dashGoal);
        this.goalSelector.removeGoal(new AvoidEntityGoal<>(this, Player.class, 10.0F, 1.2, 1.2) {
            public boolean canUse() {
                return BronzeEntity.this.getTarnishLevel().equals(TarnishStage.DISCOLORED) && super.canUse();
            }
        });
    }

    private void removeCorrodedGoals() {
        this.targetSelector.removeGoal(new CorrodedHideAndAmbushGoal(this));
    }

    private void removeTarnishedGoals() {
        if (this.pounceGoal != null) {
            this.goalSelector.removeGoal(this.pounceGoal);
            this.pounceGoal = null;
        }
        if (this.stalkGoal != null) {
            this.goalSelector.removeGoal(this.stalkGoal);
            this.stalkGoal = null;
        }
    }

    private void removeCrystallizedGoals() {
        this.goalSelector.removeGoal(new AvoidEntityGoal<>(this, Player.class, 15.0F, 2.2, 2.2) {
            public boolean canUse() {
                return BronzeEntity.this.getTarnishLevel().equals(TarnishStage.CRYSTALLIZED) && super.canUse();
            }
        });
        if (this.volleyGoal != null) {
            this.goalSelector.removeGoal(this.volleyGoal);
            this.volleyGoal = null;
        }
    }

    private void removeAllGoals() {
        removeUnaffectedGoals();
        removeDiscoloredGoals();
        removeCorrodedGoals();
        removeTarnishedGoals();
        removeCrystallizedGoals();
    }

    @Override
    public void handleEntityEvent(byte state) {
        if (state == NORMAL_ATTACK_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.unaffectedAttackAnimationState.startIfStopped(this.tickCount);
        }
        if (state == NORMAL_ATTACK_STOP){
            this.unaffectedAttackAnimationState.stop();
        }
        if (state == DEFENCE_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.defendAnimationState.startIfStopped(200);
        }
        if (state == DEFENCE_STOP){
            this.defendAnimationState.stop();
        }
        if (state == SHOCKWAVE_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.defendAnimationState.stop();
            this.shockwaveAnimationState.startIfStopped(10);
        }
        if (state == SHOCKWAVE_STOP){
            this.shockwaveAnimationState.stop();
        }
        if (state == STUN_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.defendAnimationState.stop();
            this.shockwaveAnimationState.stop();
            this.crouchAnimationState.stop();
            this.leapAnimationState.stop();
            this.slamAnimationState.stop();
            this.diveAnimationState.stop();
            this.stunAnimationState.startIfStopped(this.tickCount);
        }
        if (state == STUN_STOP){
            this.stunAnimationState.stop();
        }
        if (state == DRILL_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.drillAnimationState.startIfStopped(this.tickCount);
        }
        if (state == UNDERGROUND_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.drillAnimationState.stop();
            this.undergroundWalkAnimationState.startIfStopped(this.tickCount);
        }
        if (state == AMBUSH_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.drillAnimationState.stop();
            this.undergroundWalkAnimationState.stop();
            this.ambushAnimationState.startIfStopped(this.tickCount);
        }
        if (state == AMBUSH_STOP){
            this.ambushAnimationState.stop();
            this.undergroundWalkAnimationState.stop();
            this.drillAnimationState.stop();
        }
        if (state == CROUCH_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.crouchAnimationState.startIfStopped(this.tickCount);
        }
        if (state == CROUCH_STOP){
            this.crouchAnimationState.stop();
        }
        if (state == LEAP_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.crouchAnimationState.stop();
            this.leapAnimationState.startIfStopped(this.tickCount);
        }
        if (state == DIVE_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.leapAnimationState.stop();
            this.diveAnimationState.startIfStopped(this.tickCount);
        }
        if (state == LEAP_STOP){
            this.leapAnimationState.stop();
            this.diveAnimationState.stop();
        }
        if (state == SLAM_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.leapAnimationState.stop();
            this.diveAnimationState.stop();
            this.slamAnimationState.startIfStopped(this.tickCount);
        }
        if (state == SLAM_STOP){
            this.slamAnimationState.stop();
        }
        if (state == RAM_START){
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            this.ramAnimationState.startIfStopped(this.tickCount);
        }
        if (state == RAM_STOP){
            this.ramAnimationState.stop();
        }
        else super.handleEntityEvent(state);
    }

    private void setupAnimationStates() {
        if (this.isCrystallized()) {
            this.idleAnimationState.stop();
            this.walkAnimationState.stop();
            return;
        }

        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 80;
            if (!this.ramAnimationState.isStarted() && !this.crouchAnimationState.isStarted()
                    && !this.leapAnimationState.isStarted() && !this.diveAnimationState.isStarted()
                    && !this.slamAnimationState.isStarted()) {
                this.idleAnimationState.start(this.tickCount);
            }
        } else {
            --this.idleAnimationTimeout;
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

    /**
     * The server never syncs yBodyRot to clients; the client derives it itself, and vanilla's body control
     * only turns the body once the head is ~75 degrees off. The rods are attached to the body (not the head),
     * so during a ram the body (and rods) would barely follow the player. While the ram animation is running,
     * lock the client's body rotation to the head rotation so the rods always point where it is facing.
     */
    @Override
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this) {
            @Override
            public void clientTick() {
                if (BronzeEntity.this.ramAnimationState.isStarted() || BronzeEntity.this.isTelegraphing()) {
                    BronzeEntity.this.yBodyRot = BronzeEntity.this.yHeadRot;
                    return;
                }
                super.clientTick();
            }
        };
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (pounceGoal != null && pounceGoal.isStunned) {
            setDeltaMovement(Vec3.ZERO);
        }
    }


    public static boolean hurtWithSinnerDrop(LivingEntity victim, DamageSource source, float amount) {
        boolean wasAlive = victim.isAlive();
        boolean hurt = victim.hurt(source, amount);
        if (wasAlive && victim instanceof BronzeEntity && !victim.isAlive() && !victim.level().isClientSide()) {
            victim.level().addFreshEntity(new ItemEntity(
                    victim.level(),
                    victim.getX(), victim.getY(), victim.getZ(),
                    new ItemStack(RNItems.MUSIC_DISC_SINNER.get())
            ));
        }
        return hurt;
    }

    public static boolean isSoothed(@Nullable Entity entity) {
        return entity instanceof Player player && MusicBoxItem.isPlayingSinner(player);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !isSoothed(target) && super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(isSoothed(target) ? null : target);
    }

    private void spawnSinnerNotes(ServerLevel serverLevel) {
        Player listener = serverLevel.getNearestPlayer(this.getX(), this.getY(), this.getZ(), SINNER_NOTE_RANGE, BronzeEntity::isSoothed);
        if (listener == null) {
            return;
        }
        int note = SINNER_NOTE_SEQUENCE[(this.tickCount / SINNER_NOTE_INTERVAL) % SINNER_NOTE_SEQUENCE.length];
        serverLevel.sendParticles(ParticleTypes.NOTE, this.getX(), this.getY() + this.getBbHeight() + 0.6D, this.getZ(), 0, note / 24.0D, 0.0D, 0.0D, 1.0D);
    }

    @Override
    public void tick() {
        super.tick();
        floatOnLava();
        setupAnimationStates();

        this.tickRodTransition();

        if (this.isCrystallized() && this.level() instanceof ServerLevel crystalLevel) {
            if (this.plummeting) {
                this.plummetTicks++;
                if ((this.plummetTicks > PLUMMET_MIN_TICKS && this.onGround()) || this.plummetTicks > PLUMMET_MAX_TICKS) {
                    this.crystalDetonation(crystalLevel);
                }
            } else if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.08, 0.0));
            }
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            LivingEntity currentTarget = this.getTarget();
            if (currentTarget != null && isSoothed(currentTarget)) {
                this.setTarget(null);
                this.getNavigation().stop();
            }
            if (this.tickCount % SINNER_NOTE_INTERVAL == 0) {
                spawnSinnerNotes(serverLevel);
            }
        }

        // Spawn particles when stunned
        if ((pounceGoal != null && pounceGoal.isStunned) || (dashGoal != null && dashGoal.isStunned())) {
            spawnStunParticles();
        }

        if (this.subEntities != null) {
            Vec3[] partPositions = new Vec3[this.subEntities.length];

            for (int i = 0; i < this.subEntities.length; i++) {
                partPositions[i] = new Vec3(this.subEntities[i].getX(), this.subEntities[i].getY(), this.subEntities[i].getZ());
            }

            // Body part at entity position
            this.tickPart(this.bodyPart, 0.0, 0.0, 0.0, 500, 0.25);

            // Key part positioned above body
            this.tickPart(this.keyPart, 0.0, 1.4, 0.0, 1.4, 0.25);

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
        }

        decreaseCooldowns();
    }

    @Override
    protected AABB makeBoundingBox() {
        EntityDimensions dimensions = this.getDimensions(this.getPose());

        if (this.isDefending()) {
            // Shrink height by 0.25 blocks
            float width = dimensions.width();
            float height = dimensions.height() - 0.25F;
            float f = width / 2.0F;

            Vec3 pos = this.position();
            return new AABB(
                    pos.x - f, pos.y, pos.z - f,
                    pos.x + f, pos.y + height, pos.z + f
            );
        }

        return super.makeBoundingBox();
    }

    private void spawnStunParticles() {
        // Spawn on BOTH client and server
        // Smoke particles emanating outward erratically
        for (int i = 0; i < 3; i++) {
            double offsetX = (this.random.nextDouble() - 0.5) * 0.6;
            double offsetY = this.random.nextDouble() * 1.2;
            double offsetZ = (this.random.nextDouble() - 0.5) * 0.6;

            double velocityX = (this.random.nextDouble() - 0.5) * 0.15;
            double velocityY = this.random.nextDouble() * 0.05;
            double velocityZ = (this.random.nextDouble() - 0.5) * 0.15;

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        1, velocityX, velocityY, velocityZ, 0.0);
            }
        }

        // Electric spark particles shooting out in random directions
        if (this.random.nextFloat() < 0.6f) {
            double offsetX = (this.random.nextDouble() - 0.5) * 0.4;
            double offsetZ = (this.random.nextDouble() - 0.5) * 0.4;

            double velocityX = (this.random.nextDouble() - 0.5) * 0.5;
            double velocityY = (this.random.nextDouble() - 0.5) * 0.5;
            double velocityZ = (this.random.nextDouble() - 0.5) * 0.5;

            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                        this.getX() + offsetX,
                        this.getY() + 1.0,
                        this.getZ() + offsetZ,
                        2, velocityX, velocityY, velocityZ, 1.0);
            }
        }
    }

    private void updateAttributesForTarnish(TarnishStage level) {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance defense = this.getAttribute(Attributes.ARMOR);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);

        if (speed == null || health == null || defense == null || attack == null) return;

        speed.setBaseValue(level.getSpeed());
        health.setBaseValue(level.getHealth());
        attack.setBaseValue(level.getAttack());

        AttributeInstance knockbackResistance = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockbackResistance != null) {
            knockbackResistance.setBaseValue(level == TarnishStage.TARNISHED ? 1.0 : 0.0);
        }

        float oldDefense = (float) defense.getBaseValue();
        double newDefense = level.getArmor();

        if (defense.getBaseValue() == oldDefense && newDefense > oldDefense) {
            defense.setBaseValue(newDefense);
        }
        else if (defense.getBaseValue() != newDefense) {
            defense.setBaseValue(newDefense);
        }
    }

    private void changeGoalsOnLevelChange(TarnishStage currentLevel) {
        removeAllGoals();
        switch (currentLevel) {
            case DISCOLORED:
                registerDiscoloredGoals();
                break;
            case CORRODED:
                registerCorrodedGoals();
                break;
            case TARNISHED:
                registerTarnishedGoals();
                break;
            case CRYSTALLIZED:
                registerCrystallizedGoals();
                break;
            default:
                registerUnaffectedGoals();
        };
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        if (this.getTarnishLevel().equals(TarnishStage.TARNISHED) || this.isCrystallized()) {
            return false;
        }
        return super.causeFallDamage(fallDistance, multiplier, source);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if(this.isBurrowed() || this.isDefending()) return;

        super.knockback(strength, x, z);
    }

    private void tickPart(BronzePart part, double offsetX, double offsetY, double offsetZ, double burrowedOffset, double defendingOffset) {
        if(this.isBurrowed()){
            part.setPos(this.getX() + offsetX, this.getY() + offsetY - burrowedOffset, this.getZ() + offsetZ);
        } else if(this.isDefending()) {
            part.setPos(this.getX() + offsetX, this.getY() + offsetY - defendingOffset, this.getZ() + offsetZ);
        } else {
            part.setPos(this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ);
        }
    }
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.getTarnishLevel().equals(TarnishStage.CRYSTALLIZED) &&
                source.getDirectEntity() instanceof BronzeShotProjectileEntity) {

            if (!this.level().isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                BlockPos centerPos = this.blockPosition();

                spawnCrystalExplosionParticles(serverLevel, centerPos);

                serverLevel.playSound(
                        null,
                        centerPos,
                        SoundEvents.AMETHYST_BLOCK_BREAK,
                        SoundSource.HOSTILE,
                        2.0F,
                        0.8F + this.random.nextFloat() * 0.4F
                );

                spreadCrystalizedBlocks(serverLevel, centerPos);
            }

            return super.hurt(source, 99.0F);
        }

        return super.hurt(source, amount);
    }

    public boolean hurtFromPart(BronzePart part, DamageSource source, float amount) {
        boolean wasDefending = this.isDefending();
        boolean wasBurrowed = this.isBurrowed();

        boolean tarnished = this.getTarnishLevel().equals(TarnishStage.TARNISHED) && pounceGoal != null;

        if (part == this.keyPart) {
            amount *= 2.0F;

            if (tarnished && pounceGoal.isCrouching() && source.getDirectEntity() instanceof BronzeShotProjectileEntity) {
                pounceGoal.triggerStunFromKeyHit();
                return false;
            }
        }

        if (tarnished && pounceGoal.isStunned) {
            amount *= 1.5f;
        }

        boolean result = super.hurt(source, amount);

        if (wasDefending && this.isDefending() || wasBurrowed && this.isBurrowed()) {
            updatePartPositions();
        }

        return result;
    }

    private void spawnCrystalExplosionParticles(ServerLevel level, BlockPos centerPos) {
        for (int i = 0; i < 50 + random.nextInt(30); i++) {
            double offsetX = (random.nextDouble() - 0.5) * 2.0;
            double offsetY = random.nextDouble() * 2.0;
            double offsetZ = (random.nextDouble() - 0.5) * 2.0;

            double velocityX = (random.nextDouble() - 0.5) * 0.8;
            double velocityY = random.nextDouble() * 0.6 + 0.2;
            double velocityZ = (random.nextDouble() - 0.5) * 0.8;

            level.sendParticles(
                    ParticleTypes.END_ROD,
                    centerPos.getX() + 0.5 + offsetX,
                    centerPos.getY() + 1.0 + offsetY,
                    centerPos.getZ() + 0.5 + offsetZ,
                    2,
                    velocityX, velocityY, velocityZ,
                    0.2
            );
        }

        for (int i = 0; i < 30; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 1.5;
            double offsetY = random.nextDouble() * 1.5;
            double offsetZ = (random.nextDouble() - 0.5) * 1.5;

            level.sendParticles(
                    ParticleTypes.GLOW,
                    centerPos.getX() + 0.5 + offsetX,
                    centerPos.getY() + 1.0 + offsetY,
                    centerPos.getZ() + 0.5 + offsetZ,
                    1,
                    0, 0, 0,
                    0.1
            );
        }
    }

    private void spreadCrystalizedBlocks(ServerLevel level, BlockPos centerPos) {
        int successfulConversions = 0;
        final int MAX_ATTEMPTS = 150;
        final double RADIUS = 8.0;

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            BlockPos targetPos = generateRandomPositionInSphere(centerPos, RADIUS);

            BlockState currentState = level.getBlockState(targetPos);

            if (!currentState.isAir()) {
                continue;
            }

            Direction attachmentDirection = findAttachmentDirection(level, targetPos);
            if (attachmentDirection == null) {
                continue;
            }

            double distance = centerPos.distSqr(targetPos);
            double maxDistanceSquared = RADIUS * RADIUS;
            double normalizedDistance = Math.sqrt(distance) / RADIUS;
            double replacementChance = 1.0 - normalizedDistance;

            if (random.nextDouble() < replacementChance) {
                BlockState crystalState = RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL.get().defaultBlockState();

                if (crystalState.hasProperty(BlockStateProperties.FACING)) {
                    crystalState = crystalState.setValue(BlockStateProperties.FACING, attachmentDirection.getOpposite());
                }

                level.setBlockAndUpdate(targetPos, crystalState);

                spawnCrystalPlacementParticles(level, targetPos);

                successfulConversions++;
            }
        }
    }

    private Direction findAttachmentDirection(Level level, BlockPos airPos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = airPos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.isSolidRender(level, neighborPos) && !neighborState.isAir()) {
                return direction;
            }
        }
        return null;
    }

    private BlockPos generateRandomPositionInSphere(BlockPos center, double radius) {
        double x, y, z;
        do {
            x = (random.nextDouble() * 2.0 - 1.0) * radius;
            y = (random.nextDouble() * 2.0 - 1.0) * radius;
            z = (random.nextDouble() * 2.0 - 1.0) * radius;
        } while (x * x + y * y + z * z > radius * radius);

        return center.offset((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }

    private void spawnCrystalPlacementParticles(ServerLevel level, BlockPos pos) {
        for (int i = 0; i < 5; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 0.6;
            double offsetY = (random.nextDouble() - 0.5) * 0.6;
            double offsetZ = (random.nextDouble() - 0.5) * 0.6;

            level.sendParticles(
                    ParticleTypes.GLOW,
                    pos.getX() + 0.5 + offsetX,
                    pos.getY() + 0.5 + offsetY,
                    pos.getZ() + 0.5 + offsetZ,
                    1,
                    0, 0, 0,
                    0.05
            );
        }
    }


    private void decreaseCooldowns() {
        if (pounceCooldownTicks > 0) {
            pounceCooldownTicks--;
        }
        if (ramCooldownTicks > 0) {
            ramCooldownTicks--;
        }
        if (ambushCooldownTicks > 0) {
            ambushCooldownTicks--;
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
        }
    }

    @Override
    public void setId(int id) {
        super.setId(id);
        for (int i = 0; i < this.subEntities.length; i++) {
            this.subEntities[i].setId(id + i + 1);
        }
    }

    public int getPounceCooldown() {
        return this.pounceCooldownTicks;
    }

    public void setPounceCooldown(int ticks) {
        this.pounceCooldownTicks = ticks;
    }

    public boolean isCrystallized() {
        return this.getTarnishLevel().equals(TarnishStage.CRYSTALLIZED);
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

    public boolean isDefending() {
        return this.entityData.get(IS_DEFENDING);
    }


    public void setDefending(boolean defending) {
        this.entityData.set(IS_DEFENDING, defending);
        updatePartPositions();
    }

    public void setBurrowed(boolean burrowed) {
        this.entityData.set(IS_BURROWED, burrowed);
        updatePartPositions();
    }

    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistance() > 0.01F;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return (level.getBlockState(pos).isAir() || level.getFluidState(pos).is(FluidTags.LAVA)) ? 10.0F : 0.0F;
    }


    @Override
    public boolean isPickable() {
        return true;
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
    public void setTarnishLevel(TarnishStage stage) {
        boolean wasCrystallized = this.isCrystallized();
        super.setTarnishLevel(stage);
        this.updateAttributesForTarnish(stage);
        this.changeGoalsOnLevelChange(stage);
        if (stage == TarnishStage.CRYSTALLIZED && !wasCrystallized) {
            this.plummeting = false;
            this.plummetTicks = 0;
            this.crystalSpeedScale = 1.0F;
            this.entityData.set(FIRED_ROD_SLOT, -1);
            this.entityData.set(ROD_AIMING, false);
            this.setRodCount(MAX_RODS);
        }
        this.updateLocomotion(stage);
    }

    private void updateLocomotion(TarnishStage stage) {
        boolean flying = stage == TarnishStage.CRYSTALLIZED && !this.plummeting;
        this.navigation.stop();
        this.navigation = flying ? this.flyingNavigation : this.groundNavigation;
        this.moveControl = flying ? this.flyingMoveControl : this.groundMoveControl;
        this.setNoGravity(flying);
    }

    private void tickRodTransition() {
        int current = this.getRodCount();
        if (current != this.trackedRodCount) {
            this.rodTransitionFrom = this.trackedRodCount;
            this.rodTransitionFired = this.entityData.get(FIRED_ROD_SLOT);
            this.trackedRodCount = current;
            this.rodTransition = ROD_TRANSITION_TICKS;
        } else if (this.rodTransition > 0) {
            this.rodTransition--;
        }

        this.rodSpinO = this.rodSpin;
        if (this.isRodAiming()) {
            if (this.rodAimTicks < ROD_AIM_BLEND_TICKS) {
                this.rodAimTicks++;
            }
        } else {
            this.rodAimTicks = 0;
        }

        if (this.level().isClientSide()) {
            // The server owns the ring so every client sees the same pose.
            this.rodSpin = this.entityData.get(ROD_SPIN);
            if (Math.abs(this.rodSpin - this.rodSpinO) > 100.0F) {
                this.rodSpinO = this.rodSpin;
            }
        } else if (this.isCrystallized()) {
            this.tickRodSpinServer();
            this.entityData.set(ROD_SPIN, this.rodSpin);
        }
    }

    /**
     * The ring never stops dead: while aiming it brakes over ROD_AIM_BLEND_TICKS and is turned (as a whole, so the
     * spacing stays even) until the aimed rod points where the head looks. After a shot it accelerates back up.
     */
    private void tickRodSpinServer() {
        int count = this.getRodCount();
        boolean aiming = this.isRodAiming() && count > 1;

        float step = ROD_SPIN_SPEED / ROD_AIM_BLEND_TICKS;
        float targetSpeed = aiming ? 0.0F : ROD_SPIN_SPEED;
        this.rodSpinSpeed += Mth.clamp(targetSpeed - this.rodSpinSpeed, -step, step);
        this.rodSpin += this.rodSpinSpeed;

        if (aiming) {
            int slot = Mth.clamp(this.getRodAimSlot(), 0, count - 1);
            double front = this.getYHeadRot() * Mth.DEG_TO_RAD + Mth.HALF_PI;
            double aimedRod = this.rodSpin + Mth.TWO_PI * this.getRodSlot(slot, count, 0.0F);
            double error = Mth.wrapDegrees((front - aimedRod) * Mth.RAD_TO_DEG) * Mth.DEG_TO_RAD;
            float ramp = Mth.clamp((float) this.rodAimTicks / ROD_AIM_BLEND_TICKS, 0.0F, 1.0F);
            float ease = ramp * ramp * (3.0F - 2.0F * ramp);
            this.rodSpin += (float) (error * ROD_AIM_CHASE * ease);
        }

        if (this.rodSpin > 3000.0F) {
            this.rodSpin -= Mth.TWO_PI * 400.0F;
            this.rodSpinO = this.rodSpin;
        }
    }

    public boolean isRodAiming() {
        return this.entityData.get(ROD_AIMING);
    }

    public int getRodAimSlot() {
        return this.entityData.get(FIRED_ROD_SLOT);
    }

    public float getRodSpin(float partial) {
        return Mth.lerp(partial, this.rodSpinO, this.rodSpin);
    }

    public float getRodAimProgress(float partial) {
        return Mth.clamp((this.rodAimTicks + partial) / ROD_AIM_BLEND_TICKS, 0.0F, 1.0F);
    }

    public boolean beginRodAim(LivingEntity target) {
        int count = this.getRodCount();
        if (this.level().isClientSide() || !this.isCrystallized() || this.plummeting || count < 2 || this.isRodAiming()) {
            return false;
        }

        Vec3 head = this.position().add(0.0, HEAD_CENTER_HEIGHT, 0.0);
        Vec3 aim = target.getBoundingBox().getCenter().subtract(head);
        double targetAzimuth = Mth.atan2(aim.z, aim.x);
        double best = Double.MAX_VALUE;
        int chosen = 0;
        for (int slot = 0; slot < count; slot++) {
            double slotAzimuth = this.rodSpin + Mth.TWO_PI * slot / count;
            double diff = Math.abs(Mth.wrapDegrees((slotAzimuth - targetAzimuth) * Mth.RAD_TO_DEG));
            if (diff < best) {
                best = diff;
                chosen = slot;
            }
        }

        this.entityData.set(FIRED_ROD_SLOT, chosen);
        this.entityData.set(ROD_AIMING, true);
        return true;
    }

    public void cancelRodAim() {
        if (!this.level().isClientSide()) {
            this.entityData.set(ROD_AIMING, false);
        }
    }

    public float getRodSlot(int index, int count, float partial) {
        float target = (float) index / count;
        if (this.rodTransition <= 0 || this.rodTransitionFrom <= 1 || this.rodTransitionFrom == count || index >= count) {
            return target;
        }
        float start;
        if (this.rodTransitionFired >= 0 && this.rodTransitionFrom == count + 1) {
            start = (float) (index < this.rodTransitionFired ? index : index + 1) / this.rodTransitionFrom;
        } else if (index < this.rodTransitionFrom) {
            start = (float) index / this.rodTransitionFrom;
        } else {
            return target;
        }
        float blend = Mth.clamp((this.rodTransition - partial) / ROD_TRANSITION_TICKS, 0.0F, 1.0F);
        return Mth.lerp(blend, target, start);
    }

    public int getRodCount() {
        return this.entityData.get(ROD_COUNT);
    }

    public void setRodCount(int count) {
        this.entityData.set(ROD_COUNT, Mth.clamp(count, 0, MAX_RODS));
    }

    public boolean isPlummeting() {
        return this.plummeting;
    }

    public boolean isTelegraphing() {
        return this.entityData.get(TELEGRAPHING);
    }

    public void setTelegraphing(boolean telegraphing) {
        if (!this.level().isClientSide()) {
            this.entityData.set(TELEGRAPHING, telegraphing);
        }
    }

    public Vec3 predictAim(LivingEntity target, Vec3 targetVelocity) {
        Vec3 head = this.position().add(0.0, HEAD_CENTER_HEIGHT, 0.0);
        Vec3 center = target.getBoundingBox().getCenter();
        Vec3 lead = new Vec3(targetVelocity.x, targetVelocity.y * ROD_LEAD_VERTICAL, targetVelocity.z).scale(ROD_LEAD);
        Vec3 aim = center;
        for (int i = 0; i < ROD_LEAD_ITERATIONS; i++) {
            double flightTicks = aim.subtract(head).length() / ROD_SHOT_SPEED;
            aim = center.add(lead.scale(flightTicks));
        }
        return aim;
    }

    public float getCrystalSpeedScale() {
        return this.crystalSpeedScale;
    }

    public void setCrystalSpeedScale(float scale) {
        this.crystalSpeedScale = Mth.clamp(scale, 0.0F, 1.0F);
    }

    public boolean fireRodAt(LivingEntity target, Vec3 targetVelocity) {
        int count = this.getRodCount();
        if (this.level().isClientSide() || !this.isCrystallized() || this.plummeting || count <= 0) {
            return false;
        }
        if (count > 1 && !this.isRodAiming()) {
            return false;
        }

        Vec3 head = this.position().add(0.0, HEAD_CENTER_HEIGHT, 0.0);
        Vec3 aimPoint = this.predictAim(target, targetVelocity);
        Vec3 aim = aimPoint.subtract(head);
        if (aim.lengthSqr() < 1.0E-4) {
            return false;
        }

        double azimuth = this.getYHeadRot() * Mth.DEG_TO_RAD + Mth.HALF_PI;
        double targetAzimuth = Mth.atan2(aim.z, aim.x);
        if (Math.abs(Mth.wrapDegrees((azimuth - targetAzimuth) * Mth.RAD_TO_DEG)) > ROD_ALIGN_DEGREES) {
            return false;
        }

        Vec3 spawn = head.add(Math.cos(azimuth) * ROD_SPAWN_DISTANCE, 0.0, Math.sin(azimuth) * ROD_SPAWN_DISTANCE);
        Vec3 direction = aimPoint.subtract(spawn);
        if (direction.lengthSqr() < 1.0E-4) {
            return false;
        }

        CrystallizedBronzeRodProjectileEntity rod = new CrystallizedBronzeRodProjectileEntity(this.level(), spawn.x, spawn.y, spawn.z);
        rod.setOwner(this);
        rod.shoot(direction.x, direction.y, direction.z, ROD_SHOT_SPEED, 0.0F);
        this.level().addFreshEntity(rod);
        this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 1.5F, 0.7F + this.random.nextFloat() * 0.3F);

        int firedSlot = count == 1 ? 0 : Mth.clamp(this.getRodAimSlot(), 0, count - 1);
        int remaining = count - 1;
        this.entityData.set(ROD_AIMING, false);
        this.entityData.set(FIRED_ROD_SLOT, firedSlot);
        this.setRodCount(remaining);
        if (remaining <= 0) {
            this.startPlummet();
        }
        return true;
    }

    private void startPlummet() {
        this.plummeting = true;
        this.plummetTicks = 0;
        this.updateLocomotion(this.getTarnishLevel());
    }

    private void damageNearbyBronzes(ServerLevel level) {
        List<BronzeEntity> victims = level.getEntitiesOfClass(
                BronzeEntity.class,
                this.getBoundingBox().inflate(DETONATION_DAMAGE_RADIUS),
                other -> other != this && other.isAlive() && !other.isCrystallized()
        );
        for (BronzeEntity victim : victims) {
            double distance = this.distanceTo(victim);
            if (distance > DETONATION_DAMAGE_RADIUS) {
                continue;
            }
            float damage = (float) Mth.lerp(distance / DETONATION_DAMAGE_RADIUS, DETONATION_MAX_DAMAGE, DETONATION_MIN_DAMAGE);
            hurtWithSinnerDrop(victim, this.damageSources().explosion(this, this), damage);
        }
    }

    private void crystalDetonation(ServerLevel level) {
        BlockPos center = this.blockPosition();
        this.plummeting = false;
        spawnCrystalExplosionParticles(level, center);
        CrystalConversion.detonate(level, center, true);
        spreadCrystalizedBlocks(level, center);
        damageNearbyBronzes(level);
        this.hurt(this.damageSources().genericKill(), Float.MAX_VALUE);
        if (this.isAlive()) {
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("RodCount", this.getRodCount());
        tag.putBoolean("Plummeting", this.plummeting);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("RodCount")) {
            this.setRodCount(tag.getInt("RodCount"));
        }
        this.plummeting = tag.getBoolean("Plummeting");
        this.updateLocomotion(this.getTarnishLevel());
    }

    @Override
    public Component getName() {
        TarnishStage stage = getTarnishLevel();
        String stageName = stage.name().toLowerCase();
        return Component.translatable("entity.rubinated_nether.bronze." + stageName);
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

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_BURROWED, false);
        builder.define(IS_DEFENDING, false);
        builder.define(ROD_COUNT, MAX_RODS);
        builder.define(FIRED_ROD_SLOT, -1);
        builder.define(ROD_AIMING, false);
        builder.define(ROD_SPIN, 0.0F);
        builder.define(TELEGRAPHING, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        super.onSyncedDataUpdated(pKey);

        // Update part positions immediately when defending state changes
        if (pKey.equals(IS_DEFENDING) || pKey.equals(IS_BURROWED)) {
            updatePartPositions();
        }

        this.refreshDimensions();
    }

    private void updatePartPositions() {
        if (this.subEntities != null) {
            // Body part at entity position
            this.tickPart(this.bodyPart, 0.0, 0.0, 0.0, 500, 0.25);
            // Key part positioned above body
            this.tickPart(this.keyPart, 0.0, 1.4, 0.0, 1.4, 0.25);
        }
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
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (this.subEntities != null) {
            for (BronzePart part : this.subEntities) {
                part.remove(reason);
            }
        }
    }
}