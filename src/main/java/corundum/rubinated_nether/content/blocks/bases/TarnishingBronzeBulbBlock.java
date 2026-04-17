package corundum.rubinated_nether.content.blocks.bases;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.BronzeBulbBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;

public class TarnishingBronzeBulbBlock extends BronzeBulbBlock implements TarnishingBronze {
	public static final MapCodec<TarnishingBronzeBulbBlock> CODEC = RecordCodecBuilder.mapCodec(
			blockInstance -> blockInstance.group(
							TarnishStage.CODEC
									.fieldOf("tarnishing_state")
									.forGetter(ChangeOverTimeBlock::getAge),
							propertiesCodec()
					)
					.apply(blockInstance, TarnishingBronzeBulbBlock::new)
	);

	private final TarnishStage tarnishStage;

	@Override
	protected MapCodec<TarnishingBronzeBulbBlock> codec() {
		return CODEC;
	}

	public TarnishingBronzeBulbBlock(TarnishStage tarnishStage, BlockBehaviour.Properties properties) {
		super(properties, null, null);
		this.tarnishStage = tarnishStage;
	}

	/**
	 * Performs a random tick on a block.
	 */
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		this.onTarnishTick(state, level, pos, random);
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return TarnishingBronze.getNext(state.getBlock()).isPresent();
	}

	public TarnishStage getAge() {
		return this.tarnishStage;
	}
}