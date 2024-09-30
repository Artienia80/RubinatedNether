package net.artienia.rubinated_nether.mixin.client;

import net.artienia.rubinated_nether.utils.UpdateListenerHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow private ClientLevel level;

    @Shadow @Final private Minecraft minecraft;

    @Inject(
        method = "handleBlockUpdate",
        at = @At("TAIL")
    )
    public void handleBlockUpdate(ClientboundBlockUpdatePacket packet, CallbackInfo ci) {
        ((UpdateListenerHolder) this.level).rn$handleBlockUpdate(packet.getPos());
    }

    @Inject(
        method = "handleChunkBlocksUpdate",
        at = @At("TAIL")
    )
    public void handleBlockUpdates(ClientboundSectionBlocksUpdatePacket packet, CallbackInfo ci) {
        packet.runUpdates((pos, state) -> ((UpdateListenerHolder) this.level).rn$handleBlockUpdate(pos));
    }

}
