package corundum.rubinated_nether.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.recipe.FreezingRecipe;
import corundum.rubinated_nether.content.recipe.RNBookCategory;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;

import java.util.Objects;

/**
 * [CODE COPY] - {@link SimpleCookingSerializer}.<br><br>
 * Cleaned up.
 */
public class FreezerCookingSerializer<T extends FreezingRecipe> implements RecipeSerializer<T> {
    private final FreezerCookingSerializer.CookieBaker<T> factory;
    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public FreezerCookingSerializer(FreezerCookingSerializer.CookieBaker<T> factory, int defaultCookingTime) {
        this.factory = factory;
        this.codec = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(AbstractCookingRecipe::getGroup),
                RNBookCategory.CODEC.fieldOf("category").forGetter(FreezingRecipe::modCategory),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((recipe) -> recipe.getIngredients().getFirst()),
                ItemStack.CODEC.fieldOf("result").forGetter(FreezingRecipe::getResult),
                Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(AbstractCookingRecipe::getExperience),
                Codec.INT.fieldOf("cookingtime").orElse(defaultCookingTime).forGetter(AbstractCookingRecipe::getCookingTime)
        ).apply(instance, factory::create));
        this.streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);
    }

    @Override
    public MapCodec<T> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return this.streamCodec;
    }

    public T fromNetwork(RegistryFriendlyByteBuf buffer) {
        String group = buffer.readUtf();
        RNBookCategory RNBookCategory = buffer.readEnum(RNBookCategory.class);
        Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
        float experience = buffer.readFloat();
        int cookingTime = buffer.readVarInt();
        return this.factory.create(group, RNBookCategory, ingredient, result, experience, cookingTime);
    }

    public void toNetwork(RegistryFriendlyByteBuf buffer, T recipe) {
        buffer.writeUtf(recipe.getGroup());
        buffer.writeEnum(Objects.requireNonNullElse(recipe.modCategory(), RNBookCategory.UNKNOWN));
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getIngredients().getFirst());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.getResult());
        buffer.writeFloat(recipe.getExperience());
        buffer.writeVarInt(recipe.getCookingTime());
    }

    public interface CookieBaker<T extends FreezingRecipe> {
        T create(String group, RNBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime);
    }
}