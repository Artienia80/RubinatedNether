package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.items.PocketwatchData;
import corundum.rubinated_nether.content.items.PocketwatchItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class PocketwatchRepairHandler {

    private static final int REPAIR_LEVEL_COST = 3;

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!(left.getItem() instanceof PocketwatchItem) || !right.is(RNTags.Items.GEARBOXES)) {
            return;
        }

        PocketwatchData data = left.getOrDefault(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);
        if (!data.broken()) {
            return;
        }

        ItemStack result = left.copyWithCount(1);
        result.set(RNDataComponents.POCKETWATCH_DATA, PocketwatchData.EMPTY);
        result.remove(DataComponents.CUSTOM_MODEL_DATA);

        String name = event.getName();
        if (name != null && !name.isBlank()) {
            result.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        }

        event.setOutput(result);
        event.setCost(REPAIR_LEVEL_COST);
        event.setMaterialCost(1);
    }
}