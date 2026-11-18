package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public interface TarnishingBronze extends ChangeOverTimeBlock<TarnishStage> {
	public static final BooleanProperty WAXED = BooleanProperty.create("waxed");

	static Block getPreviousTarnishStage(Block block) {
		return TarnishData.previous(block);
	}

	static Optional<Block> getPrevious(Block block) {
		return Optional.ofNullable(getPreviousTarnishStage(block));
	}

	static Block getFirst(Block p_block) {
		Block block = p_block;

		for (Block block1 = getPreviousTarnishStage(p_block); block1 != null; block1 = getPreviousTarnishStage(block1)) {
			block = block1;
		}

		return block;
	}

	static Block getCrystallizedStage(Block p_block) {
		Block crystallized = TarnishData.crystallized(p_block);
		return crystallized != null ? crystallized : p_block;
	}

	static Optional<Block> getCrystallized(Block block) {
		return Optional.ofNullable(getCrystallizedStage(block));
	}

	default Optional<BlockState> getCrystallized(BlockState state) {
		return getCrystallized(state.getBlock()).map(block -> block.withPropertiesOf(state));
	}

	static boolean canCrystallize(Block p_block) {
		return TarnishData.crystallized(p_block) != null;
	}

	static Optional<BlockState> getPrevious(BlockState state) {
		return getPrevious(state.getBlock()).map(block -> block.withPropertiesOf(state));
	}

	static Block getNextTarnishStage(Block block) {
		return TarnishData.next(block);
	}

	static Optional<Block> getNext(Block block) {
		return Optional.ofNullable(getNextTarnishStage(block));
	}


	static BlockState getFirst(BlockState state) {
		return getFirst(state.getBlock()).withPropertiesOf(state);
	}

	@Override
	default Optional<BlockState> getNext(BlockState state) {
		return getNext(state.getBlock()).map(block -> block.withPropertiesOf(state));
	}

	@Override
	default float getChanceModifier() {
		return this.getAge() == TarnishStage.UNAFFECTED ? 0.75F : 1.0F;
	}

	default boolean waxing(
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
			if (!waxed && getPrevious(state).isEmpty())
				return false;

			stack.hurtAndBreak(1, player, null);
			level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1F, 1F);

			if (waxed) {
				level.setBlock(pos, state.setValue(WAXED, false), 2);
				level.levelEvent(player, 3004, pos, 0);
			} else {
				level.setBlock(pos, getPrevious(state).get(), 2);
				level.levelEvent(player, 3005, pos, 0);

				if (!level.isClientSide() && level.random.nextFloat() < 0.50f) {
					ItemEntity bronzeDrop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
							new ItemStack(RNItems.BRONZE_POWDER.get()));
					bronzeDrop.setDefaultPickUpDelay();
					level.addFreshEntity(bronzeDrop);
				}
			}

			return true;
		}

		if (stack.is(RNItems.BRONZE_POWDER.get())) {
			var nextState = getNext(state);
			if (nextState.isPresent()) {
				level.setBlock(pos, nextState.get(), 2);

				if (!player.isCreative()) {
					stack.shrink(1);
				}

				level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1F, 0.8F);
				level.levelEvent(player, 3005, pos, 0);


				return true;
			}
			return false;
		}

		if (stack.is(RNTags.Items.WAX) && !waxed) {
			level.setBlock(pos, state.setValue(WAXED, true), 2);

			if (!player.isCreative())
				stack.shrink(1);

			level.playSound(player, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1F, 1F);
			level.levelEvent(player, 3003, pos, 0);

			return true;
		}

		return false;
	}
}