package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class TarnishingBronzeStairBlock extends StairBlock implements TarnishingBronze {
	public static final MapCodec<TarnishingBronzeStairBlock> CODEC = RecordCodecBuilder.mapCodec(
		blockInstance -> blockInstance.group(
			TarnishState.CODEC
				.fieldOf("tarnishing_state")
				.forGetter(ChangeOverTimeBlock::getAge),
				BlockState.CODEC.fieldOf("base_state").forGetter(stairBlock -> stairBlock.baseState),
			propertiesCodec()
		)
		.apply(blockInstance, TarnishingBronzeStairBlock::new)
	);
	private final TarnishState tarnishState;

	@Override
	public MapCodec<TarnishingBronzeStairBlock> codec() {
		return CODEC;
	}

	public TarnishingBronzeStairBlock(TarnishState tarnishState, BlockState state, Properties properties) {
		super(state, properties);
		this.tarnishState = tarnishState;
	}


	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
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
		return TarnishingBronze.canCrystallize(state.getBlock());
	}

	public TarnishState getAge() {
		return this.tarnishState;
	}
}
