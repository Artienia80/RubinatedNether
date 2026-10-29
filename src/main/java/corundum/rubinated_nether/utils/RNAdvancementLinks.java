package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Vanilla advancements only have one parent, so the advancement screen can only draw one connecting line per advancement.
 * {@link #extraParents} lists additional "visual parents": advancements that should also get a connecting line drawn into the
 * child (and the child is centered between all of its parents). It is only used for drawing (see AdvancementWidgetMixin);
 * it does not change unlock requirements.
 * <p>
 * Vanilla also keeps an advancement's children in an unordered set, so the order of siblings in the tree is not guaranteed.
 * {@link #CHILD_ORDER} fixes that for our own advancements (see AdvancementNodeMixin).
 */
public final class RNAdvancementLinks {
	public static final ResourceLocation DOWSING = RubinatedNether.id("placeholder/dowsing");
	public static final ResourceLocation SHRINESTONE = RubinatedNether.id("placeholder/shrinestone");
	public static final ResourceLocation FILL_BRAZIER = RubinatedNether.id("placeholder/fill_brazier");
	public static final ResourceLocation RUBINATED_RUNE = RubinatedNether.id("placeholder/rubinated_rune");
	public static final ResourceLocation INSERT_RUNE = RubinatedNether.id("placeholder/insert_rune");
	public static final ResourceLocation RUBINATE_ITEM = RubinatedNether.id("placeholder/rubinate_item");
	public static final ResourceLocation BRONZE_ROD_OFFERING = RubinatedNether.id("placeholder/bronze_rod_offering");
	public static final ResourceLocation GIVE_OFFERING = RubinatedNether.id("placeholder/give_offering");
	public static final ResourceLocation POCKETWATCH_SAVE = RubinatedNether.id("placeholder/pocketwatch_save");
	public static final ResourceLocation KILL_BRONZE = RubinatedNether.id("placeholder/kill_bronze");
	public static final ResourceLocation SUMMON_WAVE = RubinatedNether.id("placeholder/summon_wave");
	public static final ResourceLocation CREATE_POCKETWATCH = RubinatedNether.id("placeholder/create_pocketwatch");
	public static final ResourceLocation ACCUMULATE_BLESSING = RubinatedNether.id("placeholder/accumulate_blessing");
	public static final ResourceLocation MECHANICAL_TOOLS = RubinatedNether.id("placeholder/mechanical_tools");
	public static final ResourceLocation SMITH_MODULE = RubinatedNether.id("placeholder/smith_module");
	public static final ResourceLocation KILL_VARIANTS = RubinatedNether.id("placeholder/kill_variants");
	public static final ResourceLocation BLEEDING_OBSIDIAN = RubinatedNether.id("placeholder/bleeding_obsidian");
	public static final ResourceLocation DRAWING_BLOOD = RubinatedNether.id("placeholder/drawing_blood");

	/**
	 * Desired sibling order, top to bottom in the advancement screen. Only the relative order of advancements that share
	 * a parent matters, so this is simply the tree written out depth-first, in the same left-to-right order as the design diagram.
	 */
	public static final List<ResourceLocation> CHILD_ORDER = List.of(
			DOWSING,
			SHRINESTONE,
			FILL_BRAZIER,
			RUBINATED_RUNE,
			INSERT_RUNE,
			RUBINATE_ITEM,
			BRONZE_ROD_OFFERING,
			GIVE_OFFERING,
			POCKETWATCH_SAVE,
			KILL_BRONZE,
			SUMMON_WAVE,
			CREATE_POCKETWATCH,
			ACCUMULATE_BLESSING,
			MECHANICAL_TOOLS,
			SMITH_MODULE,
			KILL_VARIANTS,
			BLEEDING_OBSIDIAN,
			DRAWING_BLOOD
	);

	private static final Map<ResourceLocation, Integer> ORDER_INDEX = new HashMap<>();

	static {
		for (int i = 0; i < CHILD_ORDER.size(); i++) {
			ORDER_INDEX.put(CHILD_ORDER.get(i), i);
		}
	}

	/** child -> additional parents (the real parent is still set in the advancement JSON). */
	private static final Map<ResourceLocation, List<ResourceLocation>> EXTRA_PARENTS = Map.of(
			RUBINATE_ITEM, List.of(GIVE_OFFERING),
			POCKETWATCH_SAVE, List.of(ACCUMULATE_BLESSING)
	);

	private RNAdvancementLinks() {
	}

	public static List<ResourceLocation> extraParents(ResourceLocation child) {
		return EXTRA_PARENTS.getOrDefault(child, List.of());
	}

	public static boolean hasOrder(ResourceLocation id) {
		return ORDER_INDEX.containsKey(id);
	}

	/** Position in {@link #CHILD_ORDER}; advancements that are not listed sort last. */
	public static int order(ResourceLocation id) {
		return ORDER_INDEX.getOrDefault(id, Integer.MAX_VALUE);
	}
}
