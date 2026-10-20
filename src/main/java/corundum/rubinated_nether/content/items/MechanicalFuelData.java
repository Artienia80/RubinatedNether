package corundum.rubinated_nether.content.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record MechanicalFuelData(ResourceLocation item, int count, int droplets) {
    private static final ResourceLocation AIR = ResourceLocation.withDefaultNamespace("air");

    public static final MechanicalFuelData EMPTY = new MechanicalFuelData(AIR, 0, 0);

    public static final Codec<MechanicalFuelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("item", AIR).forGetter(MechanicalFuelData::item),
            Codec.INT.optionalFieldOf("count", 0).forGetter(MechanicalFuelData::count),
            Codec.INT.optionalFieldOf("droplets", 0).forGetter(MechanicalFuelData::droplets)
    ).apply(instance, MechanicalFuelData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MechanicalFuelData> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, MechanicalFuelData::item,
            ByteBufCodecs.VAR_INT, MechanicalFuelData::count,
            ByteBufCodecs.VAR_INT, MechanicalFuelData::droplets,
            MechanicalFuelData::new
    );

    public static MechanicalFuelData of(Item item, int count, int droplets) {
        if (count <= 0 || item == Items.AIR) {
            return new MechanicalFuelData(AIR, 0, Math.max(droplets, 0));
        }
        return new MechanicalFuelData(BuiltInRegistries.ITEM.getKey(item), count, Math.max(droplets, 0));
    }

    public Item itemType() {
        return BuiltInRegistries.ITEM.get(item);
    }
}