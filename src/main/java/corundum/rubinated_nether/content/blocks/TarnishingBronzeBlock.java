package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;

public class TarnishingBronzeBlock extends Block implements TarnishingBronze {
	public static final MapCodec<TarnishingBronzeBlock> CODEC = RecordCodecBuilder.mapCodec(
		blockInstance -> blockInstance.group(
			TarnishingBronze.TarnishState.CODEC
				.fieldOf("tarnishing_state")
				.forGetter(ChangeOverTimeBlock::getAge),

			propertiesCodec()
		)
		.apply(blockInstance, TarnishingBronzeBlock::new)
	);
	private final TarnishingBronze.TarnishState tarnishState;

	@Override
	public MapCodec<TarnishingBronzeBlock> codec() {
		return CODEC;
	}

	public TarnishingBronzeBlock(TarnishingBronze.TarnishState tarnishState, BlockBehaviour.Properties properties) {
		super(properties);
		this.tarnishState = tarnishState;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		// Check if there is a diamond block in the vicinity
		boolean hasDiamondNearby = BlockPos.betweenClosedStream(
				pos.offset(-1, -1, -1), pos.offset(1, 1, 1)
		).anyMatch(neighborPos -> level.getBlockState(neighborPos).is(Blocks.DIAMOND_BLOCK));

		if (hasDiamondNearby) {
			// If a diamond block is nearby, crystallize the block (force it to crystallize)
			Optional<Block> crystallizedBlockOptional = Optional.ofNullable(TarnishingBronze.getCrystallized(state.getBlock()));
			if (crystallizedBlockOptional.isPresent()) {
				Block crystallizedBlock = crystallizedBlockOptional.get();
				BlockState crystallizedState = crystallizedBlock.defaultBlockState();

				// Set the block to crystallized state
				level.setBlock(pos, crystallizedState, Block.UPDATE_ALL_IMMEDIATE);
			}
		} else {
			// If no diamond block nearby, proceed with the regular tarnishing process
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
		return TarnishingBronze.getNext(state.getBlock()).isPresent();
	}

	public TarnishingBronze.TarnishState getAge() {
		return this.tarnishState;
	}
}
