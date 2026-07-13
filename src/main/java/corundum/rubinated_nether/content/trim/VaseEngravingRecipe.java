package corundum.rubinated_nether.content.trim;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.items.RuneItem;
import corundum.rubinated_nether.content.recipe.RNRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public class VaseEngravingRecipe implements SmithingRecipe {

    public static final MapCodec<VaseEngravingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("template").forGetter(r -> r.template),
            Ingredient.CODEC.fieldOf("base").forGetter(r -> r.base),
            Ingredient.CODEC.fieldOf("addition").forGetter(r -> r.addition)
    ).apply(instance, VaseEngravingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VaseEngravingRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.template,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.base,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.addition,
            VaseEngravingRecipe::new
    );

    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;

    public VaseEngravingRecipe(Ingredient template, Ingredient base, Ingredient addition) {
        this.template = template;
        this.base = base;
        this.addition = addition;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ItemStack templateStack = input.template();
        ItemStack baseStack = input.base();
        ItemStack additionStack = input.addition();

        if (!(templateStack.getItem() instanceof RuneItem runeItem)) {
            return ItemStack.EMPTY;
        }

        var material = VaseEngravingMaterial.fromItem(additionStack.getItem(), registries);
        if (material.isEmpty() || baseStack.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = baseStack.copyWithCount(1);
        result.set(RNDataComponents.VASE_ENGRAVING.get(), new VaseEngraving(runeItem.getRubination(), material.get()));
        return result;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) { return this.template.test(stack); }
    @Override
    public boolean isBaseIngredient(ItemStack stack) { return this.base.test(stack); }
    @Override
    public boolean isAdditionIngredient(ItemStack stack) { return this.addition.test(stack); }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        boolean t = this.template.test(input.template());
        boolean b = this.base.test(input.base());
        boolean a = this.addition.test(input.addition());
        return t && b && a;
    }

    @Override
    public RecipeSerializer<VaseEngravingRecipe> getSerializer() {
        return RNRecipeSerializers.VASE_ENGRAVING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    public static class Serializer implements RecipeSerializer<VaseEngravingRecipe> {
        @Override public MapCodec<VaseEngravingRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, VaseEngravingRecipe> streamCodec() { return STREAM_CODEC; }
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(this.template);
        list.add(this.base);
        list.add(this.addition);
        return list;
    }
}