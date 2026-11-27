package corundum.rubinated_nether.utils;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RNAdvancementLinks {
	public static final List<ResourceLocation> CHILD_ORDER = List.of();

	private static final Map<ResourceLocation, Integer> ORDER_INDEX = new HashMap<>();

	static {
		for (int i = 0; i < CHILD_ORDER.size(); i++) {
			ORDER_INDEX.put(CHILD_ORDER.get(i), i);
		}
	}

	private static final Map<ResourceLocation, List<ResourceLocation>> EXTRA_PARENTS = Map.of();

	private RNAdvancementLinks() {
	}

	public static List<ResourceLocation> extraParents(ResourceLocation child) {
		return EXTRA_PARENTS.getOrDefault(child, List.of());
	}

	public static boolean hasOrder(ResourceLocation id) {
		return ORDER_INDEX.containsKey(id);
	}

	public static int order(ResourceLocation id) {
		return ORDER_INDEX.getOrDefault(id, Integer.MAX_VALUE);
	}
}
