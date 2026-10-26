package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.blocks.entities.CofferBlockEntity;
import corundum.rubinated_nether.content.blocks.entities.RubinatedCofferBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class RubinatedCofferBlock extends CofferBlock {
    public static final MapCodec<RubinatedCofferBlock> CODEC = simpleCodec(properties -> new RubinatedCofferBlock(properties, RNBlockEntities.RUBINATED_COFFER::get));

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public RubinatedCofferBlock(Properties properties, Supplier<BlockEntityType<? extends CofferBlockEntity>> blockEntityType) {
        super(properties, blockEntityType);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RubinatedCofferBlockEntity(pos, state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide && !oldState.is(state.getBlock())) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide) {
            this.updatePowered(state, level, pos);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        this.updatePowered(state, level, pos);
    }

    private void updatePowered(BlockState state, Level level, BlockPos pos) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered == state.getValue(POWERED)) {
            return;
        }
        level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
        if (powered && level.getBlockEntity(pos) instanceof RubinatedCofferBlockEntity coffer) {
            coffer.clearCache();
        }
    }

    @Override
    public MapCodec<RubinatedCofferBlock> codec() {
        return CODEC;
    }
}
