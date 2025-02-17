package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TarnishingBronzeBulbBlock extends BronzeBulbBlock implements TarnishingBronze {
	public static final MapCodec<TarnishingBronzeBulbBlock> CODEC = RecordCodecBuilder.mapCodec(
			p_309135_ -> p_309135_.group(
							TarnishingBronze.TarnishState.CODEC.fieldOf("weathering_state").forGetter(TarnishingBronzeBulbBlock::getAge), propertiesCodec()
					)
					.apply(p_309135_, TarnishingBronzeBulbBlock::new)
	);
	private final TarnishingBronze.TarnishState tarnishState;

	@Override
	protected MapCodec<TarnishingBronzeBulbBlock> codec() {
		return CODEC;
	}

	public TarnishingBronzeBulbBlock(TarnishingBronze.TarnishState tarnishState, BlockBehaviour.Properties properties) {
		super(properties);
		this.tarnishState = tarnishState;
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
