package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.VaseBlockEntity;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.utils.BEBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class BronzeVaseBlock extends TarnishingBronzeBlock implements BEBlock<VaseBlockEntity> {

    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    protected static final VoxelShape SHAPE_TOP = Shapes.or(Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0));
    protected static final VoxelShape SHAPE_BOTTOM = Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.box(1.0, 10.0, 1.0, 15.0, 16.0, 15.0));

    public static final ResourceLocation CONTENTS = RubinatedNether.id("contents");
    private static final Component UNKNOWN_CONTENTS = Component.translatable("container.bronzeVase.unknownContents");

    public BronzeVaseBlock(TarnishStage tarnishStage, BlockBehaviour.Properties properties) {
        super(tarnishStage, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WAXED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF);
        super.createBlockStateDefinition(builder);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? SHAPE_BOTTOM : SHAPE_TOP;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (facing.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (facing == Direction.UP))
            return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        else
            return facingState.getBlock() instanceof BronzeVaseBlock && facingState.getValue(HALF) != doubleblockhalf
                    ? facingState.setValue(HALF, doubleblockhalf)
                    : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            pos = getCorrectBlockPos(pos, state);
            BlockEntity blockentity = level.getBlockEntity(pos);
            var stack = player.getMainHandItem();
            ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            Registry<Enchantment> registry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            Holder<Enchantment> holder = registry.getHolderOrThrow(Enchantments.SILK_TOUCH);

            if (blockentity instanceof VaseBlockEntity vase) {
                if ((player.isCreative() && !vase.isEmpty()) || enchantments.getLevel(holder) > 0) {
                    //preventDropFromBottomPart(level, pos, state, player);
                    ItemStack itemstack = new ItemStack(RNBlocks.BRONZE_VASE.asItem());
                    itemstack.applyComponents(blockentity.collectComponents());
                    ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + (double)0.5F, (double)pos.getY() + (double)0.5F, (double)pos.getZ() + (double)0.5F, itemstack);
                    itementity.setDefaultPickUpDelay();
                    level.addFreshEntity(itementity);
                } /*else {
                    vase.unpackLootTable(player);
                }*/
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    protected static void preventDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
        if (doubleblockhalf == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(blockpos, blockstate1, 35);
                level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
        }

    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var blockPos = context.getClickedPos();
        var level = context.getLevel();
        var abovePos = blockPos.above();
        var aboveState = level.getBlockState(abovePos);

        if (blockPos.getY() < level.getMaxBuildHeight() - 1 && aboveState.canBeReplaced(context))
            return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WAXED, false);
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var blockpos = pos.below();
        var blockstate = level.getBlockState(blockpos);

        return state.getValue(HALF) == DoubleBlockHalf.LOWER
                ? blockstate.isFaceSturdy(level, blockpos, Direction.UP)
                : blockstate.is(this);
    }

    private BlockPos getCorrectBlockPos(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    }

    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockentity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof VaseBlockEntity blockEntity) {
            params = params.withDynamicDrop(CONTENTS, (consumer) -> {
                for(int i = 0; i < blockEntity.getContainerSize(); ++i) {
                    consumer.accept(blockEntity.getItem(i));
                }

            });
        }

        return super.getDrops(state, params);
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof VaseBlockEntity) {
            Containers.dropContentsOnDestroy(state, newState, level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (player.isSpectator()) {
            return InteractionResult.CONSUME;
        } else {
            pos = getCorrectBlockPos(pos, state);
            if (level.getBlockEntity(pos) instanceof VaseBlockEntity vase) {
                player.openMenu(vase);
                //player.awardStat(Stats.OPEN_SHULKER_BOX);
                PiglinAi.angerNearbyPiglins(player, true);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (stack.has(DataComponents.CONTAINER_LOOT)) {
            tooltipComponents.add(UNKNOWN_CONTENTS);
        }

        int i = 0;
        int j = 0;

        for(ItemStack itemstack : stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems()) {
            ++j;
            if (i <= 4) {
                ++i;
                tooltipComponents.add(Component.translatable("container.bronzeVase.itemCount", itemstack.getHoverName(), itemstack.getCount()));
            }
        }

        if (j - i > 0) {
            tooltipComponents.add(Component.translatable("container.bronzeVase.more", j - i).withStyle(ChatFormatting.ITALIC));
        }

    }

    @Override
    public BlockEntityType<? extends VaseBlockEntity> getBlockEntityType() {
        return RNBlockEntities.BRONZE_VASE.get();
    }

    @Override
    public Class<? extends VaseBlockEntity> getBlockEntityClass() {
        return VaseBlockEntity.class;
    }
}
