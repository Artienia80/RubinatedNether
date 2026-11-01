package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.BronzeStrongboxBlockEntity;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BronzeStrongboxBlock extends ChestBlock implements TarnishingBronze {
    public static final MapCodec<BronzeStrongboxBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(BronzeStrongboxBlock::getAge),
                    propertiesCodec()
            ).apply(instance, BronzeStrongboxBlock::new)
    );

    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
    public static final IntegerProperty KEY = IntegerProperty.create("key", 0, 8);

    /** Key states 1..KEY_POSITIONS are the key turned in steps of 1/8 of a circle; 0 means no key. */
    public static final int KEY_POSITIONS = 8;

    private static final int MIN_NATURAL_CRANKS = 2;
    private static final int MAX_NATURAL_CRANKS = 6;

    private final TarnishStage tarnishStage;

    public BronzeStrongboxBlock(TarnishStage stage, Properties properties) {
        super(properties, RNBlockEntities.BRONZE_STRONGBOX::get);
        this.tarnishStage = stage;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(NATURAL, true)
                .setValue(WAXED, false)
                .setValue(KEY, 0));
    }

    @Override
    public MapCodec<? extends ChestBlock> codec() {
        return CODEC;
    }

    @Override
    public TarnishStage getAge() {
        return this.tarnishStage;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NATURAL, WAXED, KEY);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BronzeStrongboxBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        state = state.setValue(NATURAL, false);
        if (state.getValue(TYPE) != ChestType.SINGLE) {
            BlockState other = context.getLevel().getBlockState(context.getClickedPos().relative(getConnectedDirection(state)));
            if (other.is(this) && other.getValue(NATURAL)) {
                state = state.setValue(TYPE, ChestType.SINGLE);
            }
        }
        return state;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (waxing(stack, state, level, pos, player, hand, hitResult)) {
            return ItemInteractionResult.SUCCESS;
        }
        if (stack.canPerformAction(ItemAbilities.SHEARS_HARVEST) && state.getValue(KEY) != 0) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof BronzeStrongboxBlockEntity strongbox) {
                this.removeKey(state, level, pos, player, strongbox);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.is(RNItems.WINDING_KEY.get()) && state.getValue(KEY) == 0) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof BronzeStrongboxBlockEntity strongbox) {
                if (!strongbox.acceptsKey(stack)) {
                    level.playSound(null, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
                    player.displayClientMessage(Component.translatable("container.bronze_strongbox_wrong_key"), true);
                    return ItemInteractionResult.sidedSuccess(false);
                }
                if (state.getValue(NATURAL) && strongbox.getCranksRemaining() <= 0) {
                    // Pulling a key out and putting it back must not re-roll the cranks that are left.
                    strongbox.setCranksRemaining(MIN_NATURAL_CRANKS + level.random.nextInt(MAX_NATURAL_CRANKS - MIN_NATURAL_CRANKS + 1));
                }
                strongbox.insertKey(stack);
                level.setBlock(pos, state.setValue(KEY, 1), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /** Shears pull the key back out. In a vault (generated strongbox) the key breaks half of the time. */
    private void removeKey(BlockState state, Level level, BlockPos pos, Player player, BronzeStrongboxBlockEntity strongbox) {
        ItemStack key = strongbox.takeKey();
        level.setBlock(pos, state.setValue(KEY, 0), Block.UPDATE_ALL);
        if (state.getValue(NATURAL) && level.random.nextBoolean()) {
            level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            return;
        }
        level.playSound(null, pos, SoundEvents.CHAIN_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        player.getInventory().placeItemBackInInventory(key);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(KEY) == 0) {
            if (!level.isClientSide) {
                level.playSound(null, pos, SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
                player.displayClientMessage(Component.translatable("container.bronze_strongbox_locked"), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof BronzeStrongboxBlockEntity strongbox)) {
            return InteractionResult.PASS;
        }

        if (state.getValue(NATURAL)) {
            strongbox.dispense((ServerLevel) level, pos, player);
            this.crank(state, (ServerLevel) level, pos, strongbox);
            return InteractionResult.CONSUME;
        }

        // A placed strongbox just opens; the key stays in until it is removed with shears.
        if (this.getMenuProvider(state, level, pos) == null) {
            return InteractionResult.CONSUME;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    private void crank(BlockState state, ServerLevel level, BlockPos pos, BronzeStrongboxBlockEntity strongbox) {
        if (strongbox.consumeCrank() <= 0) {
            strongbox.forgetKey();
            level.setBlock(pos, state.setValue(KEY, 0), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
            return;
        }
        int next = state.getValue(KEY) % KEY_POSITIONS + 1;
        level.setBlock(pos, state.setValue(KEY, next), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.5F, 0.5F + next / 16.0F);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (newState.getBlock() instanceof BronzeStrongboxBlock) {
            // Tarnishing only swaps the block: keep the block entity so the contents, key and password survive.
            return;
        }
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof BronzeStrongboxBlockEntity strongbox) {
            strongbox.dropKey(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(WAXED)) {
            return;
        }

        boolean hasCatalystNearby = BlockPos.betweenClosedStream(
                pos.offset(-1, -1, -1), pos.offset(1, 1, 1)
        ).anyMatch(neighbor -> level.getBlockState(neighbor).is(RNTags.Blocks.CRYSTALLIZATION_CATALYST));

        if (hasCatalystNearby) {
            this.getCrystallized(state).ifPresent(next -> level.setBlockAndUpdate(pos, next));
        } else {
            this.changeOverTime(state, level, pos, random);
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return TarnishingBronze.canCrystallize(state.getBlock());
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return new ItemStack(
                state.getValue(WAXED)
                        ? BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(this)))
                        : this
        );
    }
}
