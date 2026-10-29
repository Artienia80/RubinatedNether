package corundum.rubinated_nether.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class BaptismRecipeSerializer implements RecipeSerializer<BaptismRecipe> {

    public static final MapCodec<BaptismRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(BaptismRecipe::getIngredient),
            ItemStack.CODEC.fieldOf("result").forGetter(BaptismRecipe::getResult),
            Codec.intRange(1, 9).optionalFieldOf("fuel_levels", 1).forGetter(BaptismRecipe::getFuelLevels)
    ).apply(instance, BaptismRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BaptismRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, BaptismRecipe::getIngredient,
                    ItemStack.STREAM_CODEC, BaptismRecipe::getResult,
                    ByteBufCodecs.VAR_INT, BaptismRecipe::getFuelLevels,
                    BaptismRecipe::new
            );

    @Override
    public MapCodec<BaptismRecipe> codec() { return CODEC; }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BaptismRecipe> streamCodec() { return STREAM_CODEC; }
}
