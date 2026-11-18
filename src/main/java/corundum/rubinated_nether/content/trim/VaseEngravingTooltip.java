package corundum.rubinated_nether.content.trim;

import corundum.rubinated_nether.content.RNDataMaps;
import corundum.rubinated_nether.content.datamaps.VaseEngravingMaterialData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public final class VaseEngravingTooltip {

    private VaseEngravingTooltip() {}

    public static void appendHoverText(@Nullable VaseEngraving engraving, @Nullable HolderLookup.Provider registries, List<Component> tooltip) {
        if (engraving == null) return;

        Component materialDescription = resolveMaterialDescription(engraving.material(), registries);
        Style style = materialDescription.getStyle();

        String patternKey = "vase_engraving_pattern.rubinated_nether." + engraving.pattern().getSerializedName() + "_rune";
        tooltip.add(Component.translatable(patternKey).withStyle(style));
        tooltip.add(materialDescription);
    }

    private static Component resolveMaterialDescription(VaseEngravingMaterial material, @Nullable HolderLookup.Provider registries) {
        if (registries != null) {
            var found = registries.lookup(Registries.TRIM_MATERIAL)
                    .flatMap(reg -> reg.listElements()
                            .filter(holder -> holder.value().assetName().equals(material.paletteName()))
                            .findFirst());
            if (found.isPresent()) {
                Component source = found.get().value().description();
                return source.plainCopy().withStyle(source.getStyle());
            }
        }

        for (var entry : BuiltInRegistries.ITEM.getDataMap(RNDataMaps.VASE_ENGRAVING_MATERIALS).entrySet()) {
            VaseEngravingMaterialData data = entry.getValue();
            if (!data.palette().equals(material.paletteName())) continue;
            ResourceLocation itemId = entry.getKey().location();
            MutableComponent description = Component.translatable("vase_engraving_material." + itemId.getNamespace() + "." + data.palette());
            return data.color().map(color -> description.copy().withStyle(Style.EMPTY.withColor(color))).orElse(description);
        }

        return Component.literal(material.paletteName());
    }
}