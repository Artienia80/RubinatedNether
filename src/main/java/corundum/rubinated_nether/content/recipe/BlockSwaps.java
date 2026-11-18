package corundum.rubinated_nether.content.recipe;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class BlockSwaps {
    private BlockSwaps() {
    }

    public static <T extends BlockSwapRecipe> Map<Block, Block> map(Level level, RecipeType<T> type) {
        Map<Block, Block> map = new HashMap<>();
        for (RecipeHolder<T> holder : level.getRecipeManager().getAllRecipesFor(type)) {
            map.put(holder.value().getSource(), holder.value().getResultBlock());
        }
        return map;
    }

    public static <T extends BlockSwapRecipe> Optional<Block> result(Level level, RecipeType<T> type, BlockState state) {
        BlockSwapRecipe.Input input = new BlockSwapRecipe.Input(state);
        for (RecipeHolder<T> holder : level.getRecipeManager().getAllRecipesFor(type)) {
            if (holder.value().matches(input, level)) {
                return Optional.of(holder.value().getResultBlock());
            }
        }
        return Optional.empty();
    }
}
