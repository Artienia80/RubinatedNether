package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.networking.BronzeTarnishingData;
import corundum.rubinated_nether.networking.ClientSidePayloadHandler;
import corundum.rubinated_nether.networking.ServerSidePayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handlers.ClientPayloadHandler;
import net.neoforged.neoforge.network.handlers.ServerPayloadHandler;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = RubinatedNether.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RNCommonModBusEvents {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                BronzeTarnishingData.TYPE,
                BronzeTarnishingData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientSidePayloadHandler::updateBronzeData,
                        ServerSidePayloadHandler::updateBronzeData
                )
        );
    }
}
