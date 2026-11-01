package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.utils.StrongboxLock;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

import java.util.Objects;

/**
 * A strongbox can only be renamed in the anvil together with a winding key, and the key is renamed with it
 * (see {@code AnvilMenuMixin} for the key coming back). A strongbox that already has a password only accepts the
 * key renamed to that password.
 */
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

        // A blank name removes the password (and the key's name along with it).
        String newName = typed.isBlank() ? null : typed;
        if (Objects.equals(newName, StrongboxLock.customName(left))) {
            event.setCanceled(true);
            return;
        }

        event.setOutput(StrongboxLock.renamedCopy(left, newName));
        event.setCost(RENAME_LEVEL_COST);
        event.setMaterialCost(0);
    }
}
