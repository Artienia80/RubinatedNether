package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.GrindstoneEvent;

/** Grindstone: one shrine stone in either slot becomes a blank rune; one in EACH slot becomes a runestone. */
@EventBusSubscriber(modid = RubinatedNether.MODID)
public class RuneGrindstoneHandler {

    private static boolean isShrineStone(ItemStack stack) {
        return stack.is(RNBlocks.SHRINE_STONE.get().asItem());
    }

    private static ItemStack result(ItemStack top, ItemStack bottom) {
        boolean topStone = isShrineStone(top);
        boolean bottomStone = isShrineStone(bottom);
        if (topStone && bottomStone) {
            return new ItemStack(RNBlocks.RUNESTONE.get());
        }
        if ((topStone && bottom.isEmpty()) || (bottomStone && top.isEmpty())) {
            return new ItemStack(RNItems.RUNE.get());
        }
        return ItemStack.EMPTY;
    }

    @SubscribeEvent
    public static void onPlace(GrindstoneEvent.OnPlaceItem e) {
        ItemStack output = result(e.getTopItem(), e.getBottomItem());
        if (output.isEmpty()) return;
        e.setOutput(output);
        e.setXp(0);
    }

    @SubscribeEvent
    public static void onTake(GrindstoneEvent.OnTakeItem e) {
        if (result(e.getTopItem(), e.getBottomItem()).isEmpty()) return;
        e.setXp(0);
        ItemStack top = e.getTopItem().copy();
        ItemStack bottom = e.getBottomItem().copy();
        if (!top.isEmpty()) top.shrink(1);
        if (!bottom.isEmpty()) bottom.shrink(1);
        e.setNewTopItem(top);
        e.setNewBottomItem(bottom);
    }
}
