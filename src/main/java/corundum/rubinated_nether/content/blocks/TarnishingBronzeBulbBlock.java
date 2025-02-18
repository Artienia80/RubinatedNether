package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class TarnishingBronzeBulbBlock extends BronzeBulbBlock implements TarnishingBronze {
	public static final MapCodec<TarnishingBronzeBulbBlock> CODEC = RecordCodecBuilder.mapCodec(
			blockInstance -> blockInstance.group(
							TarnishingBronze.TarnishState.CODEC
									.fieldOf("tarnishing_state")
									.forGetter(ChangeOverTimeBlock::getAge),
							propertiesCodec()
					)
					.apply(blockInstance, TarnishingBronzeBulbBlock::new)
	);
	private final TarnishingBronze.TarnishState tarnishState;

	@Override
	protected MapCodec<TarnishingBronzeBulbBlock> codec() {
		return CODEC;
	}

	public TarnishingBronzeBulbBlock(TarnishingBronze.TarnishState tarnishState, BlockBehaviour.Properties properties) {
		super(properties);
		this.tarnishState = tarnishState;
		this.registerDefaultState(defaultBlockState().setValue(WAXED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WAXED);
		super.createBlockStateDefinition(builder);
	}

	/**
	 * Performs a random tick on a block.
	 */
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
	protected boolean isRandomlyTicking(BlockState state) {
		return TarnishingBronze.getNext(state.getBlock()).isPresent();
	}

	public TarnishingBronze.TarnishState getAge() {
		return this.tarnishState;
	}
}
