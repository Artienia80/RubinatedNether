package corundum.rubinated_nether.content.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/** Shrine stone tier block items; can be put into a grindstone to make blank runes. */
public class ShrineStoneBlockItem extends BlockItem {
    public ShrineStoneBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canGrindstoneRepair(ItemStack stack) {
        return true;
    }
}
