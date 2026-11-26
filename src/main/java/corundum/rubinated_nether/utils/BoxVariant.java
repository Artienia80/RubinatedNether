package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.content.blocks.BronzeStrongboxBlock;
import corundum.rubinated_nether.content.blocks.GearboxBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;

public final class BoxVariant {
    public static final String CONSTRUCTED_KEY = "item.rubinated_nether.constructed";

    private BoxVariant() {}

    public static boolean isBox(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && isBox(blockItem.getBlock());
    }

    public static boolean isBox(Block block) {
        return block instanceof GearboxBlock || block instanceof BronzeStrongboxBlock;
    }

    public static boolean isGenerated(ItemStack stack) {
        BlockItemStateProperties properties = stack.get(DataComponents.BLOCK_STATE);
        return properties != null && "true".equals(properties.properties().get("natural"));
    }

    public static Component name(ItemStack stack, Component base) {
        if (!(stack.getItem() instanceof BlockItem blockItem) || isGenerated(stack)) {
            return base;
        }
        if (isBox(blockItem.getBlock())) {
            return Component.translatable(CONSTRUCTED_KEY, base);
        }
        return base;
    }
}
