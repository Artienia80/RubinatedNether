package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNDataMaps;
import corundum.rubinated_nether.content.datamaps.Tarnishable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public final class TarnishData {
	private TarnishData() {
	}

	private record PreviousCache(Map<ResourceKey<Block>, Tarnishable> source, Map<Block, Block> previous) {
	}

	private static volatile PreviousCache cache;

	@SuppressWarnings("deprecation")
	public static Tarnishable get(Block block) {
		return block.builtInRegistryHolder().getData(RNDataMaps.TARNISHABLES);
	}

	public static Block next(Block block) {
		Tarnishable data = get(block);
		return data == null ? null : data.nextTarnishmentStage().orElse(null);
	}

	public static Block crystallized(Block block) {
		Tarnishable data = get(block);
		return data == null ? null : data.crystallized().orElse(null);
	}

	public static Block previous(Block block) {
		Map<ResourceKey<Block>, Tarnishable> source = BuiltInRegistries.BLOCK.getDataMap(RNDataMaps.TARNISHABLES);
		PreviousCache current = cache;
		if (current == null || current.source() != source) {
			Map<Block, Block> previous = new HashMap<>();
			for (Map.Entry<ResourceKey<Block>, Tarnishable> entry : source.entrySet()) {
				Block from = BuiltInRegistries.BLOCK.get(entry.getKey());
				entry.getValue().nextTarnishmentStage().ifPresent(next -> previous.put(next, from));
			}
			current = new PreviousCache(source, previous);
			cache = current;
		}
		return current.previous().get(block);
	}
}
