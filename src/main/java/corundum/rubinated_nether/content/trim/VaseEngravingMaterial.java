package corundum.rubinated_nether.content.trim;

import com.mojang.serialization.Codec;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public record VaseEngravingMaterial(String paletteName) {

    public static final Codec<VaseEngravingMaterial> CODEC =
            Codec.STRING.xmap(VaseEngravingMaterial::new, VaseEngravingMaterial::paletteName);
    public static final StreamCodec<RegistryFriendlyByteBuf, VaseEngravingMaterial> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(VaseEngravingMaterial::new, VaseEngravingMaterial::paletteName).cast();

    private static final Map<Item, String> CUSTOM_PALETTES = new HashMap<>();

    public static void registerCustomPalette(Supplier<? extends Item> item, String paletteName) {
        CUSTOM_PALETTES.put(item.get(), paletteName);
    }

    public static Optional<VaseEngravingMaterial> fromItem(Item item, HolderLookup.Provider registries) {
        String custom = CUSTOM_PALETTES.get(item);
        if (custom != null) return Optional.of(new VaseEngravingMaterial(custom));

        return registries.lookup(Registries.TRIM_MATERIAL)
                .flatMap(reg -> reg.listElements()
                        .filter(holder -> holder.value().ingredient().value() == item)
                        .findFirst())
                .map(holder -> new VaseEngravingMaterial(holder.value().assetName()));
    }

    public static void bootstrap() {
        registerCustomPalette(RNItems.BRONZE_SCRAP, "bronze");
        registerCustomPalette(RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL::asItem, "crystallized_bronze");
        registerCustomPalette(RNItems.RUBY, "ruby");
        registerCustomPalette(RNItems.MOLTEN_RUBY, "molten_ruby");
    }
}