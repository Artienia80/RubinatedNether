package corundum.rubinated_nether.content.trim;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;

import java.util.Map;

public class RNTrimMaterials {
    public static final ResourceKey<TrimMaterial> BRONZE =
            ResourceKey.create(Registries.TRIM_MATERIAL, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "bronze"));

    public static void bootstrap(BootstrapContext<TrimMaterial> context) {
        System.out.println("[RNTrimMaterials] bootstrap() called!");
        register(context, BRONZE, RNItems.BRONZE_SCRAP.get(), Style.EMPTY.withColor(TextColor.parseColor("#bf8142").getOrThrow()), 0.5F);
        System.out.println("[RNTrimMaterials] Registered BRONZE trim material with index 0.5");
    }

    private static void register(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> trimKey, Item item,
                                 Style style, float itemModelIndex) {
        TrimMaterial trimmaterial = TrimMaterial.create(trimKey.location().getPath(), item, itemModelIndex,
                Component.translatable(Util.makeDescriptionId("trim_material", trimKey.location())).withStyle(style), Map.of());
        context.register(trimKey, trimmaterial);
    }
}