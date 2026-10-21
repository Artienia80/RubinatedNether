package corundum.rubinated_nether.content.module;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

import java.util.List;
import java.util.Optional;

public final class BuiltinDropProcessors {
    public static final TagKey<Item> TO_GRAVEL = TagKey.create(Registries.ITEM, RubinatedNether.id("processing/to_gravel"));
    public static final TagKey<Item> TO_SAND = TagKey.create(Registries.ITEM, RubinatedNether.id("processing/to_sand"));

    private BuiltinDropProcessors() {}

    public static Optional<ItemStack> smelt(ServerLevel level, ItemStack drop) {
        SingleRecipeInput input = new SingleRecipeInput(drop);
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, input, level)
                .map(holder -> holder.value().assemble(input, level.registryAccess()))
                .filter(result -> !result.isEmpty())
                .map(result -> scaled(result, drop.getCount()));
    }

    public static Optional<ItemStack> process(ServerLevel level, ItemStack drop) {
        if (drop.is(ItemTags.LOGS)) return planks(level, drop);
        if (drop.is(TO_GRAVEL)) return Optional.of(new ItemStack(Items.GRAVEL, drop.getCount()));
        if (drop.is(TO_SAND)) return Optional.of(new ItemStack(Items.SAND, drop.getCount()));
        return Optional.empty();
    }

    private static Optional<ItemStack> planks(ServerLevel level, ItemStack drop) {
        CraftingInput input = CraftingInput.of(1, 1, List.of(drop));
        return level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level)
                .map(holder -> holder.value().assemble(input, level.registryAccess()))
                .filter(result -> result.is(ItemTags.PLANKS))
                .map(result -> scaled(result, drop.getCount()));
    }

    private static ItemStack scaled(ItemStack result, int multiplier) {
        ItemStack copy = result.copy();
        copy.setCount(result.getCount() * multiplier);
        return copy;
    }
}
