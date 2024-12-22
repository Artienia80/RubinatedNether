package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

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
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
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
