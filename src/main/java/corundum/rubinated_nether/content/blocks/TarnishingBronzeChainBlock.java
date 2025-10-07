package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class TarnishingBronzeChainBlock extends ChainBlock implements TarnishingBronze {
    public static final MapCodec<TarnishingBronzeChainBlock> CODEC = RecordCodecBuilder.mapCodec(
            blockInstance -> blockInstance.group(
                            TarnishingBronze.TarnishState.CODEC
                                    .fieldOf("tarnishing_state")
                                    .forGetter(ChangeOverTimeBlock::getAge),
                            propertiesCodec()
                    )
                    .apply(blockInstance, TarnishingBronzeChainBlock::new)
    );
    private final TarnishingBronze.TarnishState tarnishState;

    // Remove the codec() override method entirely

    public TarnishingBronzeChainBlock(TarnishingBronze.TarnishState tarnishState, BlockBehaviour.Properties properties) {
        super(properties);
        this.tarnishState = tarnishState;
        this.registerDefaultState(defaultBlockState()
                .setValue(WAXED, false)
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WAXED, AXIS, WATERLOGGED);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(WAXED))
            return;

        boolean hasCatalystNearby = BlockPos.betweenClosedStream(
                        pos.offset(-1, -1, -1), pos.offset(1, 1, 1)
                )
                .anyMatch(neighborPos -> level.getBlockState(neighborPos).is(RNTags.Blocks.CRYSTALLIZATION_CATALYST));

        if (hasCatalystNearby)
            this.getCrystallized(state).ifPresent(blockState -> level.setBlockAndUpdate(pos, blockState));
        else
            this.changeOverTime(state, level, pos, random);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        return waxing(
                stack,
                state,
                level,
                pos,
                player,
                hand,
                hitResult
        )
                ? ItemInteractionResult.SUCCESS
                : super.useItemOn(
                stack,
                state,
                level,
                pos,
                player,
                hand,
                hitResult
        );
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return TarnishingBronze.canCrystallize(state.getBlock());
    }

    public TarnishingBronze.TarnishState getAge() {
        return this.tarnishState;
    }

    @Override
    public ItemStack getCloneItemStack(
            BlockState state,
            HitResult target,
            LevelReader level,
            BlockPos pos,
            Player player
    ) {
        return new ItemStack(
                state.getValue(WAXED)
                        ? BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(this)))
                        : this
        );
    }
}