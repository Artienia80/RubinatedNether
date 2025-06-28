package corundum.rubinated_nether.content.blocks;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataMaps;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public interface TarnishingBronze extends ChangeOverTimeBlock<TarnishingBronze.TarnishState> {
	public static final BooleanProperty WAXED = BooleanProperty.create("waxed");

	Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(
			() -> ImmutableBiMap.<Block, Block>builder()
					.put(RNBlocks.BRONZE_BLOCK.get(), RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
					.put(RNBlocks.DISCOLORED_BRONZE_BLOCK.get(), RNBlocks.CORRODED_BRONZE_BLOCK.get())
					.put(RNBlocks.CORRODED_BRONZE_BLOCK.get(), RNBlocks.TARNISHED_BRONZE_BLOCK.get())

					.put(RNBlocks.CHISELED_BRONZE.get(), RNBlocks.DISCOLORED_CHISELED_BRONZE.get())
					.put(RNBlocks.DISCOLORED_CHISELED_BRONZE.get(), RNBlocks.CORRODED_CHISELED_BRONZE.get())
					.put(RNBlocks.CORRODED_CHISELED_BRONZE.get(), RNBlocks.TARNISHED_CHISELED_BRONZE.get())

					.put(RNBlocks.CUT_BRONZE_PILLAR.get(), RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR.get())
					.put(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR.get(), RNBlocks.CORRODED_CUT_BRONZE_PILLAR.get())
					.put(RNBlocks.CORRODED_CUT_BRONZE_PILLAR.get(), RNBlocks.TARNISHED_CUT_BRONZE_PILLAR.get())

					.put(RNBlocks.CUT_BRONZE_BRICKS.get(), RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS.get())
					.put(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS.get(), RNBlocks.CORRODED_CUT_BRONZE_BRICKS.get())
					.put(RNBlocks.CORRODED_CUT_BRONZE_BRICKS.get(), RNBlocks.TARNISHED_CUT_BRONZE_BRICKS.get())

					.put(RNBlocks.CUT_BRONZE_BRICKS_STAIRS.get(), RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS.get())
					.put(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS.get(), RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS.get())
					.put(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS.get(), RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS.get())

					.put(RNBlocks.CUT_BRONZE_BRICKS_SLAB.get(), RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB.get())
					.put(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB.get(), RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB.get())
					.put(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB.get(), RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB.get())

					.put(RNBlocks.BRONZE_BULB.get(), RNBlocks.DISCOLORED_BRONZE_BULB.get())
					.put(RNBlocks.DISCOLORED_BRONZE_BULB.get(), RNBlocks.CORRODED_BRONZE_BULB.get())
					.put(RNBlocks.CORRODED_BRONZE_BULB.get(), RNBlocks.TARNISHED_BRONZE_BULB.get())

					.put(RNBlocks.BRONZE_GRATE.get(), RNBlocks.DISCOLORED_BRONZE_GRATE.get())
					.put(RNBlocks.DISCOLORED_BRONZE_GRATE.get(), RNBlocks.CORRODED_BRONZE_GRATE.get())
					.put(RNBlocks.CORRODED_BRONZE_GRATE.get(), RNBlocks.TARNISHED_BRONZE_GRATE.get())

					.build()
	);

	// Map that directly turns any block into its crystallized version
	Supplier<Map<Block, Block>> CRYSTALLIZED_BY_BLOCK = Suppliers.memoize(
			() -> {
				Map<Block, Block> map = new HashMap<>();

				map.put(RNBlocks.BRONZE_BLOCK.get(), RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get());
				map.put(RNBlocks.DISCOLORED_BRONZE_BLOCK.get(), RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get());
				map.put(RNBlocks.CORRODED_BRONZE_BLOCK.get(), RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get());
				map.put(RNBlocks.TARNISHED_BRONZE_BLOCK.get(), RNBlocks.CRYSTALLIZED_BRONZE_BLOCK.get());

				map.put(RNBlocks.CHISELED_BRONZE.get(), RNBlocks.CRYSTALLIZED_CHISELED_BRONZE.get());
				map.put(RNBlocks.DISCOLORED_CHISELED_BRONZE.get(), RNBlocks.CRYSTALLIZED_CHISELED_BRONZE.get());
				map.put(RNBlocks.CORRODED_CHISELED_BRONZE.get(), RNBlocks.CRYSTALLIZED_CHISELED_BRONZE.get());
				map.put(RNBlocks.TARNISHED_CHISELED_BRONZE.get(), RNBlocks.CRYSTALLIZED_CHISELED_BRONZE.get());

				map.put(RNBlocks.CUT_BRONZE_PILLAR.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR.get());
				map.put(RNBlocks.DISCOLORED_CUT_BRONZE_PILLAR.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR.get());
				map.put(RNBlocks.CORRODED_CUT_BRONZE_PILLAR.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR.get());
				map.put(RNBlocks.TARNISHED_CUT_BRONZE_PILLAR.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_PILLAR.get());

				map.put(RNBlocks.CUT_BRONZE_BRICKS.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS.get());
				map.put(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS.get());
				map.put(RNBlocks.CORRODED_CUT_BRONZE_BRICKS.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS.get());
				map.put(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS.get());

				map.put(RNBlocks.CUT_BRONZE_BRICKS_STAIRS.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get());
				map.put(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_STAIRS.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get());
				map.put(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_STAIRS.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get());
				map.put(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_STAIRS.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_STAIRS.get());

				map.put(RNBlocks.CUT_BRONZE_BRICKS_SLAB.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB.get());
				map.put(RNBlocks.DISCOLORED_CUT_BRONZE_BRICKS_SLAB.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB.get());
				map.put(RNBlocks.CORRODED_CUT_BRONZE_BRICKS_SLAB.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB.get());
				map.put(RNBlocks.TARNISHED_CUT_BRONZE_BRICKS_SLAB.get(), RNBlocks.CRYSTALLIZED_CUT_BRONZE_BRICKS_SLAB.get());

				map.put(RNBlocks.BRONZE_BULB.get(), RNBlocks.CRYSTALLIZED_BRONZE_BULB.get());
				map.put(RNBlocks.DISCOLORED_BRONZE_BULB.get(), RNBlocks.CRYSTALLIZED_BRONZE_BULB.get());
				map.put(RNBlocks.CORRODED_BRONZE_BULB.get(), RNBlocks.CRYSTALLIZED_BRONZE_BULB.get());
				map.put(RNBlocks.TARNISHED_BRONZE_BULB.get(), RNBlocks.CRYSTALLIZED_BRONZE_BULB.get());

				map.put(RNBlocks.BRONZE_GRATE.get(), RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get());
				map.put(RNBlocks.DISCOLORED_BRONZE_GRATE.get(), RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get());
				map.put(RNBlocks.CORRODED_BRONZE_GRATE.get(), RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get());
				map.put(RNBlocks.TARNISHED_BRONZE_GRATE.get(), RNBlocks.CRYSTALLIZED_BRONZE_GRATE.get());

				return Collections.unmodifiableMap(map);
			}
	);


	Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> NEXT_BY_BLOCK.get().inverse());

	Map<Block, Block> INVERSE_TARNISHABLES_DATAMAP_INTERNAL = new HashMap<>();

	Map<Block, Block> INVERSE_TARNISHABLES_DATAMAP = Collections.unmodifiableMap(INVERSE_TARNISHABLES_DATAMAP_INTERNAL);

	static Block getPreviousTarnishStage(Block block) {
		return INVERSE_TARNISHABLES_DATAMAP.containsKey(block) ? INVERSE_TARNISHABLES_DATAMAP.get(block) : TarnishingBronze.PREVIOUS_BY_BLOCK.get().get(block);
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
		return CRYSTALLIZED_BY_BLOCK.get().getOrDefault(p_block, p_block);
	}

	static Optional<Block> getCrystallized(Block block) {
		return Optional.ofNullable(getCrystallizedStage(block));
	}

	default Optional<BlockState> getCrystallized(BlockState state) {
		return getCrystallized(state.getBlock()).map(block -> block.withPropertiesOf(state));
	}

	static boolean canCrystallize(Block p_block) {
		return CRYSTALLIZED_BY_BLOCK.get().get(p_block) != null;
	}

	static Optional<BlockState> getPrevious(BlockState state) {
		return getPrevious(state.getBlock()).map(block -> block.withPropertiesOf(state));
	}

	@SuppressWarnings("deprecation") // IDK if theres a non-deprecated method
	static Block getNextTarnishStage(Block block) {
		var tarnishable = block.builtInRegistryHolder().getData(RNDataMaps.TARNISHABLES);

		return tarnishable != null
				? tarnishable.nextTarnishmentStage()
				: TarnishingBronze.NEXT_BY_BLOCK.get().get(block);
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
		return this.getAge() == TarnishingBronze.TarnishState.UNAFFECTED ? 0.75F : 1.0F;
	}

	enum TarnishState implements StringRepresentable {
		UNAFFECTED("unaffected"),
		DISCOLORED("discolored"),
		CORRODED("corroded"),
		TARNISHED("tarnished"),
		CRYSTALLIZED("crystallized");

		public static final Codec<TarnishingBronze.TarnishState> CODEC = StringRepresentable.fromEnum(TarnishingBronze.TarnishState::values);
		private final String name;

		TarnishState(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
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
				// Remove wax - no bronze powder drop
				level.setBlock(pos, state.setValue(WAXED, false), 2);
				level.levelEvent(player, 3004, pos, 0);
			} else {
				// Scrape to previous tarnish state - drop bronze powder
				level.setBlock(pos, getPrevious(state).get(), 2);
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
}