package corundum.rubinated_nether.content.dowsing;

import corundum.rubinated_nether.RubinatedNether;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class DowsingNetworking {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(
                DowsingTargetPayload.TYPE,
                DowsingTargetPayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> DowsingClient.setTarget(payload.entityId(), payload.found(), payload.pos()))
        );
    }
}
