package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.GearboxBlockEntity;
import corundum.rubinated_nether.utils.BEBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public GearboxBlock(TarnishStage stage, Properties properties) {
        super(stage, properties);
        this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
                .setValue(POWERED, false);
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
        builder.add(FACING, LIT, POWERED);
        super.createBlockStateDefinition(builder);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(LIT, context.getLevel().hasNeighborSignal(context.getClickedPos()))
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level instanceof ServerLevel serverlevel) {
            this.checkAndFlip(state, serverlevel, pos);
        }

    }

    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
        boolean flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(POWERED)) {
            BlockState blockstate = state;
            if (!(Boolean)state.getValue(POWERED)) {
                blockstate = state.cycle(LIT);
                //TODO: Same sound, but different registration to be made
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS);
            }
            level.setBlock(pos, blockstate.setValue(POWERED, flag), 3);
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

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if(state.getValue(LIT)) {
            switch(state.getValue(FACING)) {
                case NORTH,
                     SOUTH -> {
                    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() - 0.2,
                            0.0, 0.02D, -0.01);

                    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 1.2,
                            0.0, 0.02D, 0.01);
                }
                case WEST,
                     EAST -> {
                    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            pos.getX() - 0.2, pos.getY() + 0.5, pos.getZ() + 0.5,
                            -0.01, 0.02D, 0.0);

                    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            pos.getX() + 1.2, pos.getY() + 0.5, pos.getZ() + 0.5,
                            0.01, 0.02D, 0.0);
                }
            }
        }
        super.animateTick(state, level, pos, random);
    }

    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, components, tooltipFlag);
        Spawner.appendHoverText(itemStack, components, "spawn_data");
    }

    public static boolean noViewBlocking(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return false;
    }
}
