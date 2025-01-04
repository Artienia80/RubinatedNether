package corundum.rubinated_nether.mixin.accessors;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Gui.class)
public interface GuiAccessor {
    @Invoker("renderTextureOverlay")
    void invokeRenderTextureOverlay(GuiGraphics guiGraphics, ResourceLocation shaderLocation, float alpha);
}
