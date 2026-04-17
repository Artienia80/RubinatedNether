package corundum.rubinated_nether.content.blocks.bases;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataMaps;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public interface TarnishingBronze extends ChangeOverTimeBlock<TarnishStage> {
    //TODO: This ABSOLUTELY needs to be changed. Tarnish States should be tied to blocks themselves.

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

					.put(RNBlocks.BRONZE_CHAIN.get(), RNBlocks.DISCOLORED_BRONZE_CHAIN.get())
					.put(RNBlocks.DISCOLORED_BRONZE_CHAIN.get(), RNBlocks.CORRODED_BRONZE_CHAIN.get())
					.put(RNBlocks.CORRODED_BRONZE_CHAIN.get(), RNBlocks.TARNISHED_BRONZE_CHAIN.get())

					.put(RNBlocks.BRONZE_LANTERN.get(), RNBlocks.DISCOLORED_BRONZE_LANTERN.get())
					.put(RNBlocks.DISCOLORED_BRONZE_LANTERN.get(), RNBlocks.CORRODED_BRONZE_LANTERN.get())
					.put(RNBlocks.CORRODED_BRONZE_LANTERN.get(), RNBlocks.TARNISHED_BRONZE_LANTERN.get())

					.put(RNBlocks.BRONZE_CHANDELIER.get(), RNBlocks.DISCOLORED_BRONZE_CHANDELIER.get())
					.put(RNBlocks.DISCOLORED_BRONZE_CHANDELIER.get(), RNBlocks.CORRODED_BRONZE_CHANDELIER.get())
					.put(RNBlocks.CORRODED_BRONZE_CHANDELIER.get(), RNBlocks.TARNISHED_BRONZE_CHANDELIER.get())

					.put(RNBlocks.BRONZE_LAMP.get(), RNBlocks.DISCOLORED_BRONZE_LAMP.get())
					.put(RNBlocks.DISCOLORED_BRONZE_LAMP.get(), RNBlocks.CORRODED_BRONZE_LAMP.get())
					.put(RNBlocks.CORRODED_BRONZE_LAMP.get(), RNBlocks.TARNISHED_BRONZE_LAMP.get())

					.put(RNBlocks.BRONZE_LASER.get(), RNBlocks.DISCOLORED_BRONZE_LASER.get())
					.put(RNBlocks.DISCOLORED_BRONZE_LASER.get(), RNBlocks.CORRODED_BRONZE_LASER.get())
					.put(RNBlocks.CORRODED_BRONZE_LASER.get(), RNBlocks.TARNISHED_BRONZE_LASER.get())

					.put(RNBlocks.BRONZE_SPRING.get(), RNBlocks.DISCOLORED_BRONZE_SPRING.get())
					.put(RNBlocks.DISCOLORED_BRONZE_SPRING.get(), RNBlocks.CORRODED_BRONZE_SPRING.get())
					.put(RNBlocks.CORRODED_BRONZE_SPRING.get(), RNBlocks.TARNISHED_BRONZE_SPRING.get())

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

				map.put(RNBlocks.BRONZE_CHAIN.get(), RNBlocks.CRYSTALLIZED_BRONZE_CHAIN.get());
				map.put(RNBlocks.DISCOLORED_BRONZE_CHAIN.get(), RNBlocks.CRYSTALLIZED_BRONZE_CHAIN.get());
				map.put(RNBlocks.CORRODED_BRONZE_CHAIN.get(), RNBlocks.CRYSTALLIZED_BRONZE_CHAIN.get());
				map.put(RNBlocks.TARNISHED_BRONZE_CHAIN.get(), RNBlocks.CRYSTALLIZED_BRONZE_CHAIN.get());

				map.put(RNBlocks.BRONZE_LANTERN.get(), RNBlocks.CRYSTALLIZED_BRONZE_LANTERN.get());
				map.put(RNBlocks.DISCOLORED_BRONZE_LANTERN.get(), RNBlocks.CRYSTALLIZED_BRONZE_LANTERN.get());
				map.put(RNBlocks.CORRODED_BRONZE_LANTERN.get(), RNBlocks.CRYSTALLIZED_BRONZE_LANTERN.get());
				map.put(RNBlocks.TARNISHED_BRONZE_LANTERN.get(), RNBlocks.CRYSTALLIZED_BRONZE_LANTERN.get());

				map.put(RNBlocks.BRONZE_CHANDELIER.get(), RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get());
				map.put(RNBlocks.DISCOLORED_BRONZE_CHANDELIER.get(), RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get());
				map.put(RNBlocks.CORRODED_BRONZE_CHANDELIER.get(), RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get());
				map.put(RNBlocks.TARNISHED_BRONZE_CHANDELIER.get(), RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER.get());

				map.put(RNBlocks.BRONZE_LAMP.get(), RNBlocks.CRYSTALLIZED_BRONZE_LAMP.get());
				map.put(RNBlocks.DISCOLORED_BRONZE_LAMP.get(), RNBlocks.CRYSTALLIZED_BRONZE_LAMP.get());
				map.put(RNBlocks.CORRODED_BRONZE_LAMP.get(), RNBlocks.CRYSTALLIZED_BRONZE_LAMP.get());
				map.put(RNBlocks.TARNISHED_BRONZE_LAMP.get(), RNBlocks.CRYSTALLIZED_BRONZE_LAMP.get());

				map.put(RNBlocks.BRONZE_LASER.get(), RNBlocks.CRYSTALLIZED_BRONZE_LASER.get());
				map.put(RNBlocks.DISCOLORED_BRONZE_LASER.get(), RNBlocks.CRYSTALLIZED_BRONZE_LASER.get());
				map.put(RNBlocks.CORRODED_BRONZE_LASER.get(), RNBlocks.CRYSTALLIZED_BRONZE_LASER.get());
				map.put(RNBlocks.TARNISHED_BRONZE_LASER.get(), RNBlocks.CRYSTALLIZED_BRONZE_LASER.get());

				map.put(RNBlocks.BRONZE_SPRING.get(), RNBlocks.CRYSTALLIZED_BRONZE_SPRING.get());
				map.put(RNBlocks.DISCOLORED_BRONZE_SPRING.get(), RNBlocks.CRYSTALLIZED_BRONZE_SPRING.get());
				map.put(RNBlocks.CORRODED_BRONZE_SPRING.get(), RNBlocks.CRYSTALLIZED_BRONZE_SPRING.get());
				map.put(RNBlocks.TARNISHED_BRONZE_SPRING.get(), RNBlocks.CRYSTALLIZED_BRONZE_SPRING.get());

				return Collections.unmodifiableMap(map);
			}
	);


	Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> NEXT_BY_BLOCK.get().inverse());

	Map<Block, Block> INVERSE_TARNISHABLES_DATAMAP_INTERNAL = new HashMap<>();

	Map<Block, Block> INVERSE_TARNISHABLES_DATAMAP = Collections.unmodifiableMap(INVERSE_TARNISHABLES_DATAMAP_INTERNAL);

    default void onTarnishTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean hasCatalystNearby = BlockPos.betweenClosedStream(
                        pos.offset(-1, -1, -1), pos.offset(1, 1, 1)
                )
                .anyMatch(neighborPos -> level.getBlockState(neighborPos).is(RNTags.Blocks.CRYSTALLIZATION_CATALYST));

        if (hasCatalystNearby)
            this.getCrystallized(state).ifPresent(blockState -> level.setBlockAndUpdate(pos, blockState));
        else
            this.changeOverTime(state, level, pos, random);
    }

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
		return this.getAge() == TarnishStage.UNAFFECTED ? 0.75F : 1.0F;
	}
}