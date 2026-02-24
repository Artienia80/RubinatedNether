package corundum.rubinated_nether.content.trim;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;

import java.util.Map;

public class RNTrimMaterials {
    public static final ResourceKey<TrimMaterial> BRONZE =
            ResourceKey.create(Registries.TRIM_MATERIAL, RubinatedNether.id("bronze"));
    public static final ResourceKey<TrimMaterial> DISCOLORED_BRONZE =
            ResourceKey.create(Registries.TRIM_MATERIAL, RubinatedNether.id("discolored_bronze"));
    public static final ResourceKey<TrimMaterial> CORRODED_BRONZE =
            ResourceKey.create(Registries.TRIM_MATERIAL, RubinatedNether.id("corroded_bronze"));
    public static final ResourceKey<TrimMaterial> TARNISHED_BRONZE =
            ResourceKey.create(Registries.TRIM_MATERIAL, RubinatedNether.id("tarnished_bronze"));
    public static final ResourceKey<TrimMaterial> CRYSTALLIZED_BRONZE =
            ResourceKey.create(Registries.TRIM_MATERIAL, RubinatedNether.id("crystallized_bronze"));

    public static void bootstrap(BootstrapContext<TrimMaterial> context) {
        register(context, BRONZE,             RNItems.BRONZE_SCRAP.get(),                Style.EMPTY.withColor(TextColor.parseColor("#bf8142").getOrThrow()), 0.6F);  // gold
        register(context, DISCOLORED_BRONZE,  RNItems.BRONZE_SCRAP.get(),                Style.EMPTY.withColor(TextColor.parseColor("#C77459").getOrThrow()), 0.5F);  // copper
        register(context, CORRODED_BRONZE,    RNItems.BRONZE_SCRAP.get(),                Style.EMPTY.withColor(TextColor.parseColor("#B25B4E").getOrThrow()), 0.4F);  // redstone
        register(context, TARNISHED_BRONZE,   RNItems.BRONZE_SCRAP.get(),                Style.EMPTY.withColor(TextColor.parseColor("#6F4A4F").getOrThrow()), 0.3F);  // netherite
        register(context, CRYSTALLIZED_BRONZE, RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL.get().asItem(), Style.EMPTY.withColor(TextColor.parseColor("#ACD1B0").getOrThrow()), 0.1F); // quartz
    }

    private static void register(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> key,
                                 Item item, Style style, float itemModelIndex) {
        TrimMaterial material = TrimMaterial.create(
                key.location().getPath(), item, itemModelIndex,
                Component.translatable(Util.makeDescriptionId("trim_material", key.location())).withStyle(style),
                Map.of());
        context.register(key, material);
    }
}