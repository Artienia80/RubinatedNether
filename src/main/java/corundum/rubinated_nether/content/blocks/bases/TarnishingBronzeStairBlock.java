package corundum.rubinated_nether.content.blocks.bases;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class TarnishingBronzeStairBlock extends StairBlock implements TarnishingBronze {
	public static final MapCodec<TarnishingBronzeStairBlock> CODEC = RecordCodecBuilder.mapCodec(
		blockInstance -> blockInstance.group(
			TarnishStage.CODEC
				.fieldOf("tarnishing_state")
				.forGetter(ChangeOverTimeBlock::getAge),
				BlockState.CODEC.fieldOf("base_state").forGetter(stairBlock -> stairBlock.baseState),
			propertiesCodec()
		)
		.apply(blockInstance, TarnishingBronzeStairBlock::new)
	);
	private final TarnishStage tarnishStage;

	@Override
	public MapCodec<TarnishingBronzeStairBlock> codec() {
		return CODEC;
	}

	public TarnishingBronzeStairBlock(TarnishStage tarnishStage, BlockState state, Properties properties) {
		super(state, properties);
		this.tarnishStage = tarnishStage;
	}


	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		this.onTarnishTick(state, level, pos, random);
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return TarnishingBronze.canCrystallize(state.getBlock());
	}

	public TarnishStage getAge() {
		return this.tarnishStage;
	}
}
