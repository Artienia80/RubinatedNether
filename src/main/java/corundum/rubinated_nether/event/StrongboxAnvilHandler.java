package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.utils.StrongboxLock;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

import java.util.Objects;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class StrongboxAnvilHandler {
    private static final int RENAME_LEVEL_COST = 1;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        var left = event.getLeft();
        if (!StrongboxLock.isStrongbox(left)) {
            return;
        }

        if (StrongboxLock.anvilState(left, event.getRight()) != StrongboxLock.AnvilState.FREE) {
            event.setCanceled(true);
            return;
        }

        String typed = event.getName();
        if (typed == null) {
            event.setCanceled(true);
            return;
        }

        String newName = typed.isBlank() ? null : typed;
        String current = StrongboxLock.password(left);
        if (Objects.equals(newName, current) || (current == null && typed.equals(left.getHoverName().getString()))) {
            event.setCanceled(true);
            return;
        }

        event.setOutput(StrongboxLock.withPassword(left, newName));
        event.setCost(RENAME_LEVEL_COST);
        event.setMaterialCost(0);
    }
}
