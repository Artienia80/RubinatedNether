package corundum.rubinated_nether.content.trim;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public final class VaseEngravingTooltip {

    private VaseEngravingTooltip() {}

    // Fallback descriptions for palettes with no backing TrimMaterial registry entry.
    private static final Map<String, Component> CUSTOM_MATERIAL_DESCRIPTIONS = Map.of(
            "ruby", Component.translatable("vase_engraving_material.rubinated_nether.ruby")
                    .withStyle(Style.EMPTY.withColor(TextColor.parseColor("#C62B3B").getOrThrow())),
            "molten_ruby", Component.translatable("vase_engraving_material.rubinated_nether.molten_ruby")
                    .withStyle(Style.EMPTY.withColor(TextColor.parseColor("#FF6A2B").getOrThrow()))
    );

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
                return found.get().value().description();
            }
        }

        return CUSTOM_MATERIAL_DESCRIPTIONS.getOrDefault(
                material.paletteName(),
                Component.literal(material.paletteName())
        );
    }
}