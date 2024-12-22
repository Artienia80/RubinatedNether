package corundum.rubinated_nether.content.blocks;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataMaps;
import corundum.rubinated_nether.content.datamaps.Tarnishable;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface TarnishingBronze extends ChangeOverTimeBlock<TarnishingBronze.TarnishState> {

	Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(
			() -> ImmutableBiMap.<Block, Block>builder()
				.put(RNBlocks.BRONZE_BLOCK.get(), RNBlocks.DISCOLORED_BRONZE_BLOCK.get())
				.put(RNBlocks.DISCOLORED_BRONZE_BLOCK.get(), RNBlocks.CORRODED_BRONZE_BLOCK.get())
				.put(RNBlocks.CORRODED_BRONZE_BLOCK.get(), RNBlocks.TARNISHED_BRONZE_BLOCK.get())
				.build()
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

	static Optional<BlockState> getPrevious(BlockState state) {
		return getPrevious(state.getBlock().defaultBlockState()).map(p_154903_ -> p_154903_.getBlock().withPropertiesOf(state));
	}

	public static Block getNextTarnishStage(Block block) {
		@SuppressWarnings("deprecation") // IDK if theres a non-deprecated method 
		Tarnishable tarnishable = block.builtInRegistryHolder().getData(RNDataMaps.TARNISHABLES);

		return tarnishable != null ? tarnishable.nextTarnishmentStage() : TarnishingBronze.NEXT_BY_BLOCK.get().get(block);
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
}
