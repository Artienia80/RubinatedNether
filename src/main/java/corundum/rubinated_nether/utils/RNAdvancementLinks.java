package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RNAdvancementLinks {
	public static final List<ResourceLocation> CHILD_ORDER = List.of(
			RubinatedNether.id("runestone"),
			RubinatedNether.id("blank_slate"),
			RubinatedNether.id("witness"),
			RubinatedNether.id("bronze_rod"),
			RubinatedNether.id("winding_key")
	);

	private static final Map<ResourceLocation, Integer> ORDER_INDEX = new HashMap<>();

	static {
		for (int i = 0; i < CHILD_ORDER.size(); i++) {
			ORDER_INDEX.put(CHILD_ORDER.get(i), i);
		}
	}

	private static final Map<ResourceLocation, List<ResourceLocation>> EXTRA_PARENTS = Map.of(
			RubinatedNether.id("offer_ritual_offering"), List.of(RubinatedNether.id("insert_rune"), RubinatedNether.id("baptise_rod"))
	);

	private static final Map<ResourceLocation, ResourceLocation> ALIGNED_CHILD = Map.of(
			RubinatedNether.id("witness"), RubinatedNether.id("bronze_rod")
	);

	private RNAdvancementLinks() {
	}

	public static List<ResourceLocation> extraParents(ResourceLocation child) {
		return EXTRA_PARENTS.getOrDefault(child, List.of());
	}

	public static ResourceLocation alignedChild(ResourceLocation id) {
		return ALIGNED_CHILD.get(id);
	}

	public static boolean hasOrder(ResourceLocation id) {
		return ORDER_INDEX.containsKey(id);
	}

	public static int order(ResourceLocation id) {
		return ORDER_INDEX.getOrDefault(id, Integer.MAX_VALUE);
	}
}
