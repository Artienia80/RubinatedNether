package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.GearboxBlockEntity;
import corundum.rubinated_nether.utils.BEBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GearboxBlock extends TarnishingBronzeBlock implements BEBlock<GearboxBlockEntity> {
    public static final MapCodec<GearboxBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(GearboxBlock::getAge),
                    propertiesCodec()
            ).apply(instance, GearboxBlock::new)
    );

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public GearboxBlock(TarnishStage stage, Properties properties) {
        super(stage, properties);
        this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false);
    }

    public MapCodec<GearboxBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntityType<? extends GearboxBlockEntity> getBlockEntityType() {
        return RNBlockEntities.GEARBOX.get();
    }

    @Override
    public Class<? extends GearboxBlockEntity> getBlockEntityClass() {
        return GearboxBlockEntity.class;
    }

    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(LIT, context.getLevel().hasNeighborSignal(context.getClickedPos()))
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            boolean flag = state.getValue(LIT);
            if (flag != level.hasNeighborSignal(pos)) {
                if (flag) {
                    level.scheduleTick(pos, this, 4);
                } else {
                    level.setBlock(pos, state.cycle(LIT), 2);
                }
            }
        }

    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        BlockEntityTicker<T> ticker;
        if (level instanceof ServerLevel serverlevel) {
            ticker = createTickerHelper(entityType, BlockEntityType.TRIAL_SPAWNER,
                    (sLevel, sBlockPos, sBlockState, sEntity) ->
                            sEntity.getTrialSpawner().tickServer(
                                    serverlevel, sBlockPos,
                                    sBlockState.getOptionalValue(BlockStateProperties.OMINOUS).orElse(false)
                            )
            );
        } else {
            ticker = createTickerHelper(entityType, BlockEntityType.TRIAL_SPAWNER,
                    (cLevel, cBlockPos, cBlockState, cEntity) ->
                            cEntity.getTrialSpawner().tickClient(
                                    cLevel, cBlockPos,
                                    cBlockState.getOptionalValue(BlockStateProperties.OMINOUS).orElse(false)
                            )
            );
        }
        return ticker;
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker) {
        return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
    }

    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, components, tooltipFlag);
        Spawner.appendHoverText(itemStack, components, "spawn_data");
    }
}
