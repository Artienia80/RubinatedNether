package corundum.rubinated_nether.event;

import corundum.rubinated_nether.client.render.PocketwatchSaveAnimation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PocketwatchSaveHandler {

    public static void handle(PocketwatchSavePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> PocketwatchSaveAnimation.start());
    }
}