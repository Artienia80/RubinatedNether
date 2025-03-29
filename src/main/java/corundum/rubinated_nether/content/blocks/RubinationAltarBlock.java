package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.content.blocks.entities.RubinationAltarBlockEntity;
import corundum.rubinated_nether.content.menu.RubinationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RubinationAltarBlock extends BaseEntityBlock {

    private final MapCodec<? extends BaseEntityBlock> codec = simpleCodec(RubinationAltarBlock::new);

    protected static final VoxelShape SHAPE_BOTTOM = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
    protected static final VoxelShape SHAPE_TOP = Block.box(0.0, 4.0, 0.0, 16.0, 14.0, 16.0);
    protected static final VoxelShape SHAPE = Shapes.or(SHAPE_BOTTOM, SHAPE_TOP);

    public static final List<BlockPos> RUNESTONE_OFFSETS = BlockPos.betweenClosedStream(-3, 0, -3, 3, 1, 3)
            .filter((blockPos) -> Math.abs(blockPos.getX()) > 1 || Math.abs(blockPos.getZ()) > 1).map(BlockPos::immutable).toList();

    public RubinationAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return codec;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.rubinated_nether.wip.tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        for (BlockPos blockPos : RUNESTONE_OFFSETS) {
            if (random.nextInt(1) == 0 && isValidCatalyst(level, pos, blockPos))
                level.addParticle(RNParticleTypes.RUBINATE.get(),
                        (double) pos.getX() + 0.5, (double) pos.getY() + 2.0, (double) pos.getZ() + 0.5,
                        (double) ((float) blockPos.getX() + random.nextFloat()) - 0.5,
                        (float) blockPos.getY() - random.nextFloat() - 0.5F,
                        (double) ((float) blockPos.getZ() + random.nextFloat()) - 0.5);
        }
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RubinationAltarBlockEntity(pos, state);
    }

    //TODO: Create custom Menu
    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTicker(level, blockEntityType, RNBlockEntities.RUBINATION_ALTAR.get());
    }

    private static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> serverType, BlockEntityType<? extends RubinationAltarBlockEntity> clientType) {
        return level.isClientSide ? createTickerHelper(serverType, clientType, RubinationAltarBlockEntity::tick) : null;
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return use(state, level, pos, player);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return switch(use(state, level, pos, player)) {
            case InteractionResult.SUCCESS -> ItemInteractionResult.SUCCESS;
            case InteractionResult.CONSUME -> ItemInteractionResult.CONSUME;
            default -> ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        };
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(!(blockEntity instanceof RubinationAltarBlockEntity altarBlockEntity)) return null;

        Component component = altarBlockEntity.getDisplayName();
        return new SimpleMenuProvider(
                (i, inventory, player) -> new RubinationMenu(i, inventory, ContainerLevelAccess.create(level, pos)),
                component
        );
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    public static boolean isValidCatalyst(Level level, BlockPos tablePos, BlockPos offsetPos) {
        return level.getBlockState(tablePos.offset(offsetPos)).is(RNBlocks.RUNESTONE.get()) && level.getBlockState(tablePos.offset(offsetPos.getX() / 2, offsetPos.getY(), offsetPos.getZ() / 2)).is(BlockTags.ENCHANTMENT_POWER_TRANSMITTER);
    }
}
