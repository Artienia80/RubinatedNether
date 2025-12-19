package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
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
		super(properties, null, null); // We'll handle transitions through TarnishingBronze interface
		this.tarnishStage = tarnishStage;
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
		// Use the TarnishingBronze waxing logic with bulb-specific state preservation
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
		var waxed = state.getValue(WAXED);

		if (stack.is(ItemTags.AXES)) {
			if (!waxed && TarnishingBronze.getPrevious(state).isEmpty())
				return false;

			stack.hurtAndBreak(1, player, null);
			level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1F, 1F);

			if (waxed) {
				// Remove wax - no bronze powder drop
				level.setBlock(pos, state.setValue(WAXED, false), 2);
				level.levelEvent(player, 3004, pos, 0);
			} else {
				// Scrape to previous tarnish state - drop bronze powder
				TarnishingBronze.getPrevious(state).ifPresent(newState -> {
					BlockState finalState = newState;
					// Preserve LIT and POWERED states
					try {
						if (newState.hasProperty(LIT)) {
							finalState = finalState.setValue(LIT, state.getValue(LIT));
						}
					} catch (IllegalArgumentException ignored) {}

					try {
						if (newState.hasProperty(POWERED)) {
							finalState = finalState.setValue(POWERED, state.getValue(POWERED));
						}
					} catch (IllegalArgumentException ignored) {}

					level.setBlock(pos, finalState, 2);
				});
				level.levelEvent(player, 3005, pos, 0);

				// Drop bronze powder only when scraping (not when removing wax)
				if (!level.isClientSide() && level.random.nextFloat() < 0.50f) {
					ItemEntity bronzeDrop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
							new ItemStack(RNItems.BRONZE_POWDER.get()));
					bronzeDrop.setDefaultPickUpDelay();
					level.addFreshEntity(bronzeDrop);
				}
			}

			return true;
		}

		// Bronze powder advances tarnish state
		// Bronze powder advances tarnish state
		if (stack.is(RNItems.BRONZE_POWDER.get())) {
			var nextState = this.getNext(state);  // Changed from TarnishingBronze.getNext(state)
			if (nextState.isPresent()) {
				BlockState newState = nextState.get();

				// Preserve LIT, POWERED, and WAXED states
				try {
					if (newState.hasProperty(LIT)) {
						newState = newState.setValue(LIT, state.getValue(LIT));
					}
				} catch (IllegalArgumentException ignored) {}

				try {
					if (newState.hasProperty(POWERED)) {
						newState = newState.setValue(POWERED, state.getValue(POWERED));
					}
				} catch (IllegalArgumentException ignored) {}

				try {
					if (newState.hasProperty(WAXED)) {
						newState = newState.setValue(WAXED, waxed);
					}
				} catch (IllegalArgumentException ignored) {}

				level.setBlock(pos, newState, 2);

				if (!player.isCreative()) {
					stack.shrink(1);
				}

				level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1F, 0.8F);
				level.levelEvent(player, 3005, pos, 0);

				return true;
			}
			return false; // Already at max tarnish state
		}

		if (stack.is(Items.HONEYCOMB) && !waxed) {
			level.setBlock(pos, state.setValue(WAXED, true), 2);

			if (!player.isCreative())
				stack.shrink(1);

			level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1F, 1F);
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

	public TarnishStage getAge() {
		return this.tarnishStage;
	}
}