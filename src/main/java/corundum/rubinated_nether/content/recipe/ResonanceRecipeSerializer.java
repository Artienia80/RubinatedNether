package corundum.rubinated_nether.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public class ResonanceRecipeSerializer implements RecipeSerializer<ResonanceRecipe> {

    private static final MapCodec<ResonanceRecipe.TriggerData> TRIGGER_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.DOUBLE.optionalFieldOf("radius", 10.0).forGetter(ResonanceRecipe.TriggerData::radius),
            Codec.INT.optionalFieldOf("attempts", 2500).forGetter(ResonanceRecipe.TriggerData::attempts)
    ).apply(i, ResonanceRecipe.TriggerData::new));

    public static final MapCodec<ResonanceRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "").forGetter(ResonanceRecipe::getGroup),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(ResonanceRecipe::getIngredient),
            BuiltInRegistries.ITEM.byNameCodec()
                    .xmap(ItemStack::new, ItemStack::getItem)
                    .fieldOf("result").forGetter(ResonanceRecipe::getResult),
            TRIGGER_CODEC.codec().optionalFieldOf("offering").forGetter(ResonanceRecipe::getOffering),
            TRIGGER_CODEC.codec().optionalFieldOf("key").forGetter(ResonanceRecipe::getKey),
            Codec.BOOL.optionalFieldOf("require_shrine_stone_neighbor", false).forGetter(ResonanceRecipe::requiresShrineStoneNeighbor)
    ).apply(instance, ResonanceRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ResonanceRecipe.TriggerData> TRIGGER_STREAM =
            StreamCodec.composite(
                    ByteBufCodecs.fromCodec(Codec.DOUBLE), ResonanceRecipe.TriggerData::radius,
                    ByteBufCodecs.INT, ResonanceRecipe.TriggerData::attempts,
                    ResonanceRecipe.TriggerData::new
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ResonanceRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, ResonanceRecipe::getGroup,
                    Ingredient.CONTENTS_STREAM_CODEC, ResonanceRecipe::getIngredient,
                    ByteBufCodecs.registry(BuiltInRegistries.ITEM.key())
                            .map(ItemStack::new, ItemStack::getItem), ResonanceRecipe::getResult,
                    ByteBufCodecs.optional(TRIGGER_STREAM), ResonanceRecipe::getOffering,
                    ByteBufCodecs.optional(TRIGGER_STREAM), ResonanceRecipe::getKey,
                    ByteBufCodecs.BOOL, ResonanceRecipe::requiresShrineStoneNeighbor,
                    ResonanceRecipe::new
            );

    @Override
    public MapCodec<ResonanceRecipe> codec() { return CODEC; }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ResonanceRecipe> streamCodec() { return STREAM_CODEC; }
}