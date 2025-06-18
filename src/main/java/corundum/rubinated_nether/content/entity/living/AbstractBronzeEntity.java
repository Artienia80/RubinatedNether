package corundum.rubinated_nether.content.entity.living;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.BronzeTarnishingStep;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
    protected boolean isWaxed;

    protected AbstractBronzeEntity(EntityType<? extends AbstractBronzeEntity> entityType, Level level) {
        super(entityType, level);
        this.isWaxed = false;
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

        } else if (heldItem.getItem() == RNItems.BRONZE_POWDER.get() && this.getNextTarnishingLevel() != null) {
            this.changeOverTime();
            heldItem.consume(1, player);
            return InteractionResult.SUCCESS;

        } else if (heldItem.getItem() instanceof AxeItem && this.getPreviousTarnishingLevel() != null) {
            var bronzeEntity = switch(this.getPreviousTarnishingLevel()) {
                case BRONZE -> new BronzeEntity(this.level());
                case DISCOLORED -> new DiscoloredEntity(this.level());
                case CORRODED -> new CorrodedEntity(this.level());
                case null, default -> this;
            };
            replaceWith(bronzeEntity);
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
            this.getCrystallized();
        else
            this.changeOverTime();
    }

    private void changeOverTime() {
        var bronzeEntity = switch(this.getNextTarnishingLevel()) {
            case DISCOLORED -> new DiscoloredEntity(this.level());
            case CORRODED -> new CorrodedEntity(this.level());
            case TARNISHED -> new TarnishedEntity(this.level());
            case null, default -> this;
        };
        replaceWith(bronzeEntity);
    }

    private void getCrystallized() {
        if(this.getTarnishingLevel() == BronzeTarnishingStep.CRYSTALLIZED) return;
        CrystallizedEntity crystallized = new CrystallizedEntity(this.level());
        replaceWith(crystallized);
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

    private AbstractBronzeEntity copyStateTo(AbstractBronzeEntity entity1, AbstractBronzeEntity entity2) {
        //entity2.setUUID(entity1.getUUID());
        entity2.setHealth(entity1.getHealth());
        for(var effect : entity1.getActiveEffects()) entity2.addEffect(effect);
        entity2.setPos(entity1.getX(), entity1.getY(), entity1.getZ());
        entity2.setLeashData(entity1.getLeashData());
        entity2.setAggressive(entity1.isAggressive());
        entity2.setAirSupply(entity1.getAirSupply());
        entity2.setRot(entity1.yRotO, entity1.xRotO);
        entity2.setYBodyRot(entity1.yBodyRot);
        entity2.setYHeadRot(entity1.yHeadRot);
        entity2.setCustomName(entity1.getCustomName());
        entity2.setCustomNameVisible(entity1.isCustomNameVisible());
        entity2.setDeltaMovement(entity1.getDeltaMovement());
        entity2.setNoAi(entity1.isNoAi());
        entity2.setTarget(entity1.getTarget());
        AbstractBronzeEntity.copyPersistentData(entity1, entity2);
        return entity2;
    }

    private void replaceWith(AbstractBronzeEntity bronzeEntity) {
        // If they're the same, it means there's no changes to be applied
        if (this.getTarnishingLevel() == bronzeEntity.getTarnishingLevel()) return;

        RubinatedNether.LOGGER.debug("Transforming {} into {}", this.getTarnishingLevel(), bronzeEntity.getTarnishingLevel());

        try {
            this.copyStateTo(this, bronzeEntity);
            this.level().addFreshEntity(bronzeEntity);
            this.discard();
        } catch (Exception e) {
            RubinatedNether.LOGGER.error("Something went wrong during Entity Tarnishing.");
        }
    }

    private static void copyPersistentData(AbstractBronzeEntity entity1, AbstractBronzeEntity entity2){
        CompoundTag fromTag = entity1.getPersistentData();
        CompoundTag toTag = entity2.getPersistentData();

        for (String key : fromTag.getAllKeys()) {
            toTag.put(key, fromTag.get(key).copy());
        }
    }

    //TODO: Analyze and elaborate more specific behaviours and methods

    public abstract BronzeTarnishingStep getTarnishingLevel();
    public abstract BronzeTarnishingStep getNextTarnishingLevel();
    public abstract BronzeTarnishingStep getPreviousTarnishingLevel();
}
