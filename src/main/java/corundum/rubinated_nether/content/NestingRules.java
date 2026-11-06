package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.blocks.BronzeStrongboxBlock;
import corundum.rubinated_nether.content.blocks.BronzeVaseBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public final class NestingRules {
    private NestingRules() {
    }

    public static boolean isNestingBox(ItemStack stack) {
        Block block = Block.byItem(stack.getItem());
        return block instanceof ShulkerBoxBlock || block instanceof BronzeStrongboxBlock;
    }

    public static boolean isNestedContainer(ItemStack stack) {
        Block block = Block.byItem(stack.getItem());
        return block instanceof BronzeVaseBlock || block instanceof BronzeStrongboxBlock;
    }

    public static boolean vaseAccepts(ItemStack stack) {
        return !(Block.byItem(stack.getItem()) instanceof BronzeVaseBlock) && (stack.canFitInsideContainerItems() || isNestingBox(stack));
    }

    public static boolean strongboxAccepts(ItemStack stack) {
        return !isNestingBox(stack) && !isNestedContainer(stack) && stack.canFitInsideContainerItems();
    }
}
