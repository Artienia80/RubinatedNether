package corundum.rubinated_nether.networking;

import corundum.rubinated_nether.content.entity.BronzeEntity;
import corundum.rubinated_nether.misc.RNAttachments;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientSidePayloadHandler {

    public static void updateBronzeData(final BronzeTarnishingData data, IPayloadContext context) {
        context.enqueueWork(() -> {
            var entity = Minecraft.getInstance().level.getEntity(data.id());
            if (!(entity instanceof BronzeEntity bronze))
                return;

            bronze.setData(RNAttachments.TARNISH_LEVEL, data.tarnishingLevel());
        });
    }
}
