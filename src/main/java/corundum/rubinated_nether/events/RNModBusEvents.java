package corundum.rubinated_nether.events;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.effect.renderer.BronzeDiseasedEffectOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;

//@EventBusSubscriber(modid = RubinatedNether.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RNModBusEvents {
    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, RNModBusEvents::onRenderGuiOverlay);
    }
    @SubscribeEvent
    public static void onRenderGuiOverlay(RenderGuiLayerEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player != null && player.hasEffect(RNEffects.BRONZE_DISEASED)) {
            BronzeDiseasedEffectOverlay.renderTextureOverlay(event.getGuiGraphics(), BronzeDiseasedEffectOverlay.PARANOIA_OVERLAY, 0.5F);
        }
    }

}
