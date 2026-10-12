package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.client.render.RubinatedTextRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow
    private ItemStack lastToolHighlight;

    @ModifyVariable(
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private MutableComponent rubinated_nether$restyleItemName(MutableComponent component) {
        if (!RubinatedTextRenderer.useRubinatedLang()) return component;
        if (!RubinatedTextRenderer.isRubinated(lastToolHighlight)) return component;
        if (lastToolHighlight.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME)) return component;
        return RubinatedTextRenderer.restyleWithEnglishName(component, lastToolHighlight);
    }
}