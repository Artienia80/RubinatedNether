package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class RNCapabilities {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RNCapabilities::onRegisterCapabilities);
    }

    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                RNBlockEntities.FREEZER.get(),
                FreezerBlockEntity::getItemHandler
        );
    }
}