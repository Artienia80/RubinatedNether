package corundum.rubinated_nether.content.trim;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.neoforged.neoforge.registries.DeferredItem;

public class RNTrimPatterns {

    public static final ResourceKey<TrimPattern> GREED_RUNE = create("greed_rune");
    public static final ResourceKey<TrimPattern> GLUTTONY_RUNE = create("gluttony_rune");
    public static final ResourceKey<TrimPattern> SLOTH_RUNE = create("sloth_rune");

    public static final ResourceKey<TrimPattern> WRATH_RUNE = create("wrath_rune");
    public static final ResourceKey<TrimPattern> ENVY_RUNE = create("envy_rune");
    public static final ResourceKey<TrimPattern> VAINGLORY_RUNE = create("vainglory_rune");

    public static final ResourceKey<TrimPattern> PRIDE_RUNE = create("pride_rune");
    public static final ResourceKey<TrimPattern> ACEDIA_RUNE = create("acedia_rune");
    public static final ResourceKey<TrimPattern> LUXURIA_RUNE = create("luxuria_rune");

    public static final ResourceKey<TrimPattern> INSIDIAE_RUNE = create("insidiae_rune");
    public static final ResourceKey<TrimPattern> SUPERBIA_RUNE = create("superbia_rune");
    public static final ResourceKey<TrimPattern> TRISTIA_RUNE = create("tristia_rune");

    public static final ResourceKey<TrimPattern> STUDIOSE_RUNE = create("studiose_rune");
    public static final ResourceKey<TrimPattern> ARDENTER_RUNE = create("ardenter_rune");
    public static final ResourceKey<TrimPattern> NIMIS_RUNE = create("nimis_rune");

    public static final ResourceKey<TrimPattern> IRA_RUNE = create("ira_rune");
    public static final ResourceKey<TrimPattern> INVIDIA_RUNE = create("invidia_rune");
    public static final ResourceKey<TrimPattern> GULA_RUNE = create("gula_rune");

    public static final ResourceKey<TrimPattern> IGNAVIA_RUNE = create("ignavia_rune");
    public static final ResourceKey<TrimPattern> KENODOXIA_RUNE = create("kenodoxia_rune");
    public static final ResourceKey<TrimPattern> PHILARGYRIA_RUNE = create("philargyria_rune");

    private static ResourceKey<TrimPattern> create(String name) {
        return ResourceKey.create(Registries.TRIM_PATTERN, RubinatedNether.id(name));
    }

    public static void bootstrap(BootstrapContext<TrimPattern> context) {
        register(context, GREED_RUNE, RNItems.GREED_RUNE, "rune_tool");
        register(context, GLUTTONY_RUNE, RNItems.GLUTTONY_RUNE, "rune_tool");
        register(context, SLOTH_RUNE, RNItems.SLOTH_RUNE, "rune_tool");

        register(context, WRATH_RUNE, RNItems.WRATH_RUNE, "rune_weapon");
        register(context, ENVY_RUNE, RNItems.ENVY_RUNE, "rune_weapon");
        register(context, VAINGLORY_RUNE, RNItems.VAINGLORY_RUNE, "rune_weapon");

        register(context, PRIDE_RUNE, RNItems.PRIDE_RUNE, "rune_armor");
        register(context, ACEDIA_RUNE, RNItems.ACEDIA_RUNE, "rune_armor");
        register(context, LUXURIA_RUNE, RNItems.LUXURIA_RUNE, "rune_armor");

        register(context, INSIDIAE_RUNE, RNItems.INSIDIAE_RUNE, "rune_bow");
        register(context, SUPERBIA_RUNE, RNItems.SUPERBIA_RUNE, "rune_bow");
        register(context, TRISTIA_RUNE, RNItems.TRISTIA_RUNE, "rune_bow");

        register(context, STUDIOSE_RUNE, RNItems.STUDIOSE_RUNE, "rune_crossbow");
        register(context, ARDENTER_RUNE, RNItems.ARDENTER_RUNE, "rune_crossbow");
        register(context, NIMIS_RUNE, RNItems.NIMIS_RUNE, "rune_crossbow");

        register(context, IRA_RUNE, RNItems.IRA_RUNE, "rune_trident");
        register(context, INVIDIA_RUNE, RNItems.INVIDIA_RUNE, "rune_trident");
        register(context, GULA_RUNE, RNItems.GULA_RUNE, "rune_trident");

        register(context, IGNAVIA_RUNE, RNItems.IGNAVIA_RUNE, "rune_mace");
        register(context, KENODOXIA_RUNE, RNItems.KENODOXIA_RUNE, "rune_mace");
        register(context, PHILARGYRIA_RUNE, RNItems.PHILARGYRIA_RUNE, "rune_mace");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Item> void register(BootstrapContext<TrimPattern> context,
                                                  ResourceKey<TrimPattern> key,
                                                  DeferredItem<T> item,
                                                  String categoryAssetId) {
        TrimPattern trimPattern = new TrimPattern(
                RubinatedNether.id(categoryAssetId),
                (Holder<Item>) (Holder<?>) item.getDelegate(),
                Component.translatable(Util.makeDescriptionId("trim_pattern", key.location())),
                false
        );
        context.register(key, trimPattern);
    }
}