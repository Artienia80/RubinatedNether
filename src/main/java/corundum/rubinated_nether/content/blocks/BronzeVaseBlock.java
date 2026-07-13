package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.content.blocks.entities.VaseBlockEntity;
import corundum.rubinated_nether.content.trim.VaseEngraving;
import corundum.rubinated_nether.content.trim.VaseEngravingTooltip;
import corundum.rubinated_nether.utils.BEBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
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

    // Lower half: base cube, matches bronze_vase_base.json element [0,0,0] -> [16,16,16]
    protected static final VoxelShape SHAPE_BOTTOM = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    // Upper half: neck ring + waist + rim disc, matches bronze_vase_lid.json elements
    protected static final VoxelShape SHAPE_TOP = Shapes.or(
            Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            Block.box(2.0, 4.0, 2.0, 14.0, 6.0, 14.0),
            Block.box(1.0, 6.0, 1.0, 15.0, 8.0, 15.0)
    );

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
                if(enchantments.getLevel(holder) <= 0 && !player.isCreative()) {
                    preventDrops(level, pos, player);
                    for(int i=0; i < 8; i++){
                        ItemStack itemstack = new ItemStack(RNItems.BRONZE_SCRAP.asItem());
                        ItemEntity itementity = new ItemEntity(level, (double) pos.getX() + (double) 0.5F, (double) pos.getY() + (double) 0.5F, (double) pos.getZ() + (double) 0.5F, itemstack);
                        itementity.setDefaultPickUpDelay();
                        level.addFreshEntity(itementity);
                    }
                } else {
                    if (!vase.isEmpty()) {
                        ItemStack itemstack = new ItemStack(RNBlocks.BRONZE_VASE.asItem());
                        itemstack.applyComponents(blockentity.collectComponents());
                        ItemEntity itementity = new ItemEntity(level, (double) pos.getX() + (double) 0.5F, (double) pos.getY() + (double) 0.5F, (double) pos.getZ() + (double) 0.5F, itemstack);
                        itementity.setDefaultPickUpDelay();
                        level.addFreshEntity(itementity);
                    }
                    preventDrops(level, pos, player);
                }
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    protected static void preventDrops(Level level, BlockPos pos, Player player) {
        BlockState blockstate = level.getBlockState(pos);
        BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        level.setBlock(pos, blockstate1, 35);
        level.levelEvent(player, 2001, pos, Block.getId(blockstate));
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

        VaseEngraving engraving = stack.get(RNDataComponents.VASE_ENGRAVING.get());
        VaseEngravingTooltip.appendHoverText(engraving, context.registries(), tooltipComponents);

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

    // --- Tarnishing ---
    // The vase is a two-tall (HALF) block, unlike the rest of TarnishingBronzeBlock's
    // single-position blocks. All ticking and change-over-time logic is gated on the
    // LOWER half and moves both halves together, so the two halves never end up on
    // mismatched tarnish stages (canSurvive requires blockstate.is(this) for the UPPER
    // half to stay placed). Container contents are explicitly saved and restored around
    // the swap since the block instance changes at both positions.

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER
                && !state.getValue(WAXED)
                && TarnishingBronze.canCrystallize(this);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(WAXED) || state.getValue(HALF) != DoubleBlockHalf.LOWER) return;

        boolean hasCatalystNearby = BlockPos.betweenClosedStream(
                pos.offset(-1, -1, -1), pos.offset(1, 1, 1)
        ).anyMatch(neighborPos -> level.getBlockState(neighborPos).is(RNTags.Blocks.CRYSTALLIZATION_CATALYST));

        if (hasCatalystNearby) {
            TarnishingBronze.getCrystallized(this).ifPresent(nextBlock -> setBothHalves(level, pos, nextBlock));
        } else {
            this.changeOverTime(state, level, pos, random);
        }
    }

    @Override
    public void changeOverTime(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() >= this.getChanceModifier() / 4.0F) return;

        TarnishingBronze.getNext(this).ifPresent(nextBlock -> setBothHalves(level, pos, nextBlock));
    }

    private void setBothHalves(ServerLevel level, BlockPos lowerPos, Block nextBlock) {
        if (!(nextBlock instanceof BronzeVaseBlock)) return;

        BlockPos upperPos = lowerPos.above();
        if (!level.getBlockState(upperPos).is(this)) return;

        BlockEntity oldBlockEntity = level.getBlockEntity(lowerPos);
        NonNullList<ItemStack> savedItems = null;
        if (oldBlockEntity instanceof Container container) {
            savedItems = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
            for (int i = 0; i < container.getContainerSize(); i++) {
                savedItems.set(i, container.getItem(i).copy());
            }
        }

        level.setBlock(upperPos, nextBlock.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(WAXED, false), Block.UPDATE_CLIENTS);
        level.setBlock(lowerPos, nextBlock.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WAXED, false), Block.UPDATE_CLIENTS);

        if (savedItems != null) {
            BlockEntity newBlockEntity = level.getBlockEntity(lowerPos);
            if (newBlockEntity instanceof Container newContainer) {
                for (int i = 0; i < Math.min(savedItems.size(), newContainer.getContainerSize()); i++) {
                    newContainer.setItem(i, savedItems.get(i));
                }
                newBlockEntity.setChanged();
            }
        }
    }
}