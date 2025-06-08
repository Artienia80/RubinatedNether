package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
		super(properties, null, null); // We'll handle transitions through TarnishingBronze interface
		this.tarnishState = tarnishState;
		this.registerDefaultState(defaultBlockState().setValue(WAXED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WAXED, LIT, POWERED);
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
		// First try the TarnishingBronze waxing/scraping logic
		if (waxingBulb(stack, state, level, pos, player, hand, hitResult)) {
			return ItemInteractionResult.SUCCESS;
		}

		// Don't call super.useItemOn as it has conflicting waxing logic
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	// Custom waxing method for bulbs that preserves LIT and POWERED states
	private boolean waxingBulb(
			ItemStack stack,
			BlockState state,
			Level level,
			BlockPos pos,
			Player player,
			InteractionHand hand,
			BlockHitResult hitResult
	) {
		var bool = state.getValue(WAXED);

		if (stack.is(net.minecraft.tags.ItemTags.AXES)) {
			if (!bool && TarnishingBronze.getPrevious(state).isEmpty())
				return false;

			stack.hurtAndBreak(1, player, null);
			level.playSound(player, pos, net.minecraft.sounds.SoundEvents.AXE_WAX_OFF, net.minecraft.sounds.SoundSource.BLOCKS, 1F, 1F);

			if (bool) {
				level.setBlock(pos, state.setValue(WAXED, false), 2);
				level.levelEvent(player, 3004, pos, 0);
			} else {
				// When scraping, preserve LIT and POWERED states
				TarnishingBronze.getPrevious(state).ifPresent(newState -> {
					BlockState finalState = newState;
					// Check if the new state has LIT and POWERED properties and preserve them
					try {
						if (newState.getProperties().contains(LIT)) {
							finalState = finalState.setValue(LIT, state.getValue(LIT));
						}
					} catch (IllegalArgumentException ignored) {}

					try {
						if (newState.getProperties().contains(POWERED)) {
							finalState = finalState.setValue(POWERED, state.getValue(POWERED));
						}
					} catch (IllegalArgumentException ignored) {}

					level.setBlock(pos, finalState, 2);
				});
				level.levelEvent(player, 3005, pos, 0);
			}

			if (!level.isClientSide() && level.random.nextFloat() < 0.50f) {
				net.minecraft.world.entity.item.ItemEntity bronzeDrop = new net.minecraft.world.entity.item.ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
						new ItemStack(corundum.rubinated_nether.content.RNItems.BRONZE_POWDER.get()));
				bronzeDrop.setDefaultPickUpDelay();
				level.addFreshEntity(bronzeDrop);
			}

			return true;
		}

		if (stack.is(net.minecraft.world.item.Items.HONEYCOMB) && !bool) {
			level.setBlock(pos, state.setValue(WAXED, true), 2);

			if (!player.isCreative())
				stack.shrink(1);

			level.playSound(player, pos, net.minecraft.sounds.SoundEvents.HONEYCOMB_WAX_ON, net.minecraft.sounds.SoundSource.BLOCKS, 1F, 1F);
			level.levelEvent(player, 3003, pos, 0);

			return true;
		}

		return false;
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