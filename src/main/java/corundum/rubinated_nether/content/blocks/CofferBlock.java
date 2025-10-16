package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.blocks.entities.CofferBlockEntity;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerSynchronizer;
import fuzs.limitlesscontainers.api.limitlesscontainers.v1.LimitlessContainerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CofferBlock extends AbstractChestBlock<CofferBlockEntity> implements SimpleWaterloggedBlock {
    public static final MapCodec<CofferBlock> CODEC = simpleCodec(cofferBlock -> new CofferBlock(cofferBlock, RNBlockEntities.COFFER::get));

    protected static final VoxelShape SHAPE = Stream.of(
            box(0, 0, 0, 16, 14, 16)
    ).reduce(Shapes::or).get();

    public CofferBlock(BlockBehaviour.Properties properties, Supplier<BlockEntityType<? extends CofferBlockEntity>> blockEntityType) {
        super(properties, blockEntityType);
    }

    // This is mandatory to render a custom modeled block. It tells the game that it
    // has to use the custom shape.
    // REMEMBER: DO NOT USE DATA GENERATION FOR BLOCKS WITH CUSTOM MODELS
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        return new CofferBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof CofferBlockEntity blockEntity) {
                LimitlessContainerUtils.dropContents(level, pos, blockEntity.getContainer());
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);

        MenuProvider menuProvider = this.getMenuProvider(state, level, pos);
        if (menuProvider != null) {
            player.openMenu(menuProvider)
                    .ifPresent(containerId ->
                            LimitlessContainerSynchronizer.setSynchronizerFor((ServerPlayer) player, containerId)
                    );
            PiglinAi.angerNearbyPiglins(player, true);
        }
        return InteractionResult.sidedSuccess(false);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? createTickerHelper(blockEntityType, this.blockEntityType.get(), CofferBlockEntity::lidAnimateTick) : null;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (!(blockentity instanceof CofferBlockEntity cofferBlockEntity)) return;

        cofferBlockEntity.recheckOpen();
    }

    @Override
    public MapCodec<? extends CofferBlock> codec() {
        return CODEC;
    }

    @Override
    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState blockState, Level level, BlockPos blockPos, boolean b) {
        return DoubleBlockCombiner.Combiner::acceptNone;
    }
}
