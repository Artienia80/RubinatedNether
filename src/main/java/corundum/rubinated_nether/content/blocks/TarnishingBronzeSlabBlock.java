package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;

public class TarnishingBronzeSlabBlock extends SlabBlock implements TarnishingBronze {
	public static final MapCodec<TarnishingBronzeSlabBlock> CODEC = RecordCodecBuilder.mapCodec(
		blockInstance -> blockInstance.group(
			TarnishState.CODEC
				.fieldOf("tarnishing_state")
				.forGetter(ChangeOverTimeBlock::getAge),

			propertiesCodec()
		)
		.apply(blockInstance, TarnishingBronzeSlabBlock::new)
	);
	private final TarnishState tarnishState;

	@Override
	public MapCodec<TarnishingBronzeSlabBlock> codec() {
		return CODEC;
	}

	public TarnishingBronzeSlabBlock(TarnishState tarnishState, Properties properties) {
		super(properties);
		this.tarnishState = tarnishState;
	}


	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		boolean hasCatalystNearby = BlockPos.betweenClosedStream(
				pos.offset(-1, -1, -1), pos.offset(1, 1, 1)
		).anyMatch(neighborPos -> level.getBlockState(neighborPos).is(RNTags.Blocks.CRYSTALLIZATION_CATALYST));

		if (hasCatalystNearby) {
			this.getCrystallized(state).ifPresent(blockState -> level.setBlockAndUpdate(pos, blockState));
		} else {
			this.changeOverTime(state, level, pos, random);
		}
	}

	/**
	 * Helper method to safely set a property on a block state.
	 */
	private <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, Comparable<?> value) {
		return state.setValue(property, property.getValueClass().cast(value));
	}




	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return TarnishingBronze.canCrystallize(state.getBlock());
	}

	public TarnishState getAge() {
		return this.tarnishState;
	}
}
