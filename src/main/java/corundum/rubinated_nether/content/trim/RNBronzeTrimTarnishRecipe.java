package corundum.rubinated_nether.content.trim;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.recipe.RNRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public class RNBronzeTrimTarnishRecipe implements SmithingRecipe {

    public static final MapCodec<RNBronzeTrimTarnishRecipe> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ResourceKey.codec(Registries.TRIM_MATERIAL).fieldOf("input_material")
                            .forGetter(r -> r.inputMaterial),
                    ResourceKey.codec(Registries.TRIM_MATERIAL).fieldOf("output_material")
                            .forGetter(r -> r.outputMaterial)
            ).apply(instance, RNBronzeTrimTarnishRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RNBronzeTrimTarnishRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC.map(
                            loc -> ResourceKey.create(Registries.TRIM_MATERIAL, loc),
                            ResourceKey::location),
                    r -> r.inputMaterial,
                    ResourceLocation.STREAM_CODEC.map(
                            loc -> ResourceKey.create(Registries.TRIM_MATERIAL, loc),
                            ResourceKey::location),
                    r -> r.outputMaterial,
                    RNBronzeTrimTarnishRecipe::new);

    private final ResourceKey<TrimMaterial> inputMaterial;
    private final ResourceKey<TrimMaterial> outputMaterial;

    public RNBronzeTrimTarnishRecipe(ResourceKey<TrimMaterial> inputMaterial,
                                     ResourceKey<TrimMaterial> outputMaterial) {
        this.inputMaterial = inputMaterial;
        this.outputMaterial = outputMaterial;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        // No template needed — template slot must be empty
        return stack.isEmpty();
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return stack.is(ItemTags.TRIMMABLE_ARMOR);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return stack.is(corundum.rubinated_nether.content.RNItems.BRONZE_SCRAP.get());
    }

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        if (!isTemplateIngredient(input.template())) return false;
        if (!isBaseIngredient(input.base())) return false;
        if (!isAdditionIngredient(input.addition())) return false;

        ArmorTrim trim = input.base().get(DataComponents.TRIM);
        if (trim == null) return false;

        // Must have the expected input material to upgrade from
        return trim.material().is(inputMaterial);
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider registries) {
        ArmorTrim existing = input.base().get(DataComponents.TRIM);
        if (existing == null) return ItemStack.EMPTY;

        var materialHolder = registries.lookupOrThrow(Registries.TRIM_MATERIAL)
                .get(outputMaterial).orElse(null);
        if (materialHolder == null) return ItemStack.EMPTY;

        ItemStack result = input.base().copy();
        result.set(DataComponents.TRIM, new ArmorTrim(materialHolder, existing.pattern()));
        return result;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RNRecipeSerializers.BRONZE_TRIM_UPGRADE.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    public static class Serializer implements RecipeSerializer<RNBronzeTrimTarnishRecipe> {
        @Override public MapCodec<RNBronzeTrimTarnishRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, RNBronzeTrimTarnishRecipe> streamCodec() { return STREAM_CODEC; }
    }
}