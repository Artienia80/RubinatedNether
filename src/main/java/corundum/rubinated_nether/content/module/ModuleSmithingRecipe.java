package corundum.rubinated_nether.content.module;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.recipe.RNRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public class ModuleSmithingRecipe implements SmithingRecipe {
    public static final MapCodec<ModuleSmithingRecipe> CODEC = MapCodec.unit(ModuleSmithingRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ModuleSmithingRecipe> STREAM_CODEC =
            StreamCodec.of((buf, recipe) -> {}, buf -> new ModuleSmithingRecipe());

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return stack.is(RNItems.COGWHEEL.get());
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return ToolModules.isModdable(stack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return ToolModules.isAnyCatalyst(stack);
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return isTemplateIngredient(input.template())
                && isBaseIngredient(input.base())
                && ToolModules.moduleForCatalyst(input.base(), input.addition()) != null;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ToolModule module = ToolModules.moduleForCatalyst(input.base(), input.addition());
        if (module == null) return ItemStack.EMPTY;
        return ToolModules.withModule(input.base(), module);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RNRecipeSerializers.MODULE_APPLY.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    public static class Serializer implements RecipeSerializer<ModuleSmithingRecipe> {
        @Override public MapCodec<ModuleSmithingRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, ModuleSmithingRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
