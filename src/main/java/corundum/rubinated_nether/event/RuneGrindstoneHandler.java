package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.GrindstoneEvent;

import java.util.Set;

/** Grindstone: one shrine stone tier block in EACH slot becomes a blank rune. */
@EventBusSubscriber(modid = RubinatedNether.MODID)
public class RuneGrindstoneHandler {
    private static Set<Item> shrineTiers;

    private static Set<Item> shrineTiers() {
        if (shrineTiers == null) {
            shrineTiers = Set.of(
                    RNBlocks.SHRINE_STONE.get().asItem(),
                    RNBlocks.COBBLED_SHRINE_STONE.get().asItem(),
                    RNBlocks.POLISHED_SHRINE_STONE.get().asItem(),
                    RNBlocks.SHRINE_STONE_BRICKS.get().asItem(),
                    RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get().asItem(),
                    RNBlocks.SHRINE_STONE_TILES.get().asItem()
            );
        }
        return shrineTiers;
    }

    private static boolean isShrineStone(ItemStack stack) {
        return !stack.isEmpty() && shrineTiers().contains(stack.getItem());
    }

    private static boolean isValid(ItemStack top, ItemStack bottom) {
        return isShrineStone(top) && isShrineStone(bottom);
    }

    @SubscribeEvent
    public static void onPlace(GrindstoneEvent.OnPlaceItem e) {
        if (!isValid(e.getTopItem(), e.getBottomItem())) return;
        e.setOutput(new ItemStack(RNItems.RUNE.get()));
        e.setXp(0);
    }

    @SubscribeEvent
    public static void onTake(GrindstoneEvent.OnTakeItem e) {
        if (!isValid(e.getTopItem(), e.getBottomItem())) return;
        e.setXp(0);
        ItemStack top = e.getTopItem().copy();
        ItemStack bottom = e.getBottomItem().copy();
        top.shrink(1);
        bottom.shrink(1);
        e.setNewTopItem(top);
        e.setNewBottomItem(bottom);
    }
}
