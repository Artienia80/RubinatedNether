package corundum.rubinated_nether.content.entity.living;

import corundum.rubinated_nether.content.TarnishingBronzeStep;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;

public class BronzeTypeEntity extends Monster {

    //TODO: Overall cleanup and well-defined base structure

    private int idleAnimationTimeout = 0;
    private final AnimationState idleAnimationState = new AnimationState();
    private final AnimationState walkAnimationState = new AnimationState();
    public boolean isWaxed;
    private static final EntityDataAccessor<Integer> DATA_ID_TARN_STEP = SynchedEntityData.defineId(BronzeTypeEntity.class, EntityDataSerializers.INT);

    public BronzeTypeEntity(EntityType<? extends BronzeTypeEntity> entityType, Level level) {
        super(entityType, level);
        this.isWaxed = false;
    }

    public static void init() {
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0, 0.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_TARN_STEP, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("bronzeStep", getStepIndex());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.entityData.set(DATA_ID_TARN_STEP, compound.getInt("bronzeStep"));
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

        this.randomTick((ServerLevel) this.level(), this.getOnPos(), random);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);

        if(heldItem.is(Items.HONEYCOMB) && !this.isWaxed) {
            this.setWaxed(true);
            heldItem.consume(1, player);
            return InteractionResult.SUCCESS;

        } else if (heldItem.getItem() == RNItems.BRONZE_POWDER.get() && this.getNextTarnishingLevel() != this.getTarnishingLevel()) {
            this.changeOverTime();
            heldItem.consume(1, player);
            return InteractionResult.SUCCESS;

        } else if (heldItem.getItem() instanceof AxeItem && this.getPreviousTarnishingLevel() != this.getTarnishingLevel()) {
            updateTarnishingLevel(getPreviousTarnishingLevel().ordinal());
            heldItem.setDamageValue(heldItem.getDamageValue() - 1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void randomTick(ServerLevel level, BlockPos pos, RandomSource random) {
        //TODO: Remember to check attack animations to cancel tarnishing
        if(random.nextInt(350) != 5 || this.isWaxed()) return;
        boolean hasCatalystNearby = BlockPos.betweenClosedStream(
                        pos.offset(-5, -5, -5), pos.offset(5, 5, 5)
                )
                .anyMatch(neighborPos -> level.getBlockState(neighborPos).is(RNTags.Blocks.CRYSTALLIZATION_CATALYST));

        if(hasCatalystNearby)
            this.crystallize();
        else
            this.changeOverTime();
    }

    private void changeOverTime() {
        updateTarnishingLevel(this.getNextTarnishingLevel().ordinal());
    }

    private void crystallize() {
        if(this.getTarnishingLevel() == TarnishingBronzeStep.CRYSTALLIZED) return;
        updateTarnishingLevel(4);
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

    public boolean isWaxed(){
        return isWaxed;
    }

    public void setWaxed(boolean waxed){
        this.isWaxed = waxed;
    }

    public TarnishingBronzeStep getTarnishingLevel() {
        return TarnishingBronzeStep.byIndex(getStepIndex());
    }

    private int getStepIndex() {
        return this.entityData.get(DATA_ID_TARN_STEP);
    }

    public void updateTarnishingLevel(int step) {
        this.entityData.set(DATA_ID_TARN_STEP, step);
    }

    public TarnishingBronzeStep getNextTarnishingLevel() {
        int step = this.getStepIndex();
        return TarnishingBronzeStep.byIndex(step < 3 ? step + 1 : step);
    }

    public TarnishingBronzeStep getPreviousTarnishingLevel() {
        int step = this.getStepIndex();
        return TarnishingBronzeStep.byIndex(step > 0 ? step - 1 : step);
    }

    //TODO: Analyze and elaborate more specific behaviours and methods
}
