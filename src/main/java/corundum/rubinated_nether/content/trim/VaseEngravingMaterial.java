package corundum.rubinated_nether.content.trim;

import com.mojang.serialization.Codec;
import corundum.rubinated_nether.content.RNDataMaps;
import corundum.rubinated_nether.content.datamaps.VaseEngravingMaterialData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

import java.util.Optional;

public record VaseEngravingMaterial(String paletteName) {

    public static final Codec<VaseEngravingMaterial> CODEC =
            Codec.STRING.xmap(VaseEngravingMaterial::new, VaseEngravingMaterial::paletteName);
    public static final StreamCodec<RegistryFriendlyByteBuf, VaseEngravingMaterial> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(VaseEngravingMaterial::new, VaseEngravingMaterial::paletteName).cast();

    public static Optional<VaseEngravingMaterial> fromItem(Item item, HolderLookup.Provider registries) {
        VaseEngravingMaterialData data = item.builtInRegistryHolder().getData(RNDataMaps.VASE_ENGRAVING_MATERIALS);
        if (data != null) return Optional.of(new VaseEngravingMaterial(data.palette()));

        return registries.lookup(Registries.TRIM_MATERIAL)
                .flatMap(reg -> reg.listElements()
                        .filter(holder -> holder.value().ingredient().value() == item)
                        .findFirst())
                .map(holder -> new VaseEngravingMaterial(holder.value().assetName()));
    }
}
