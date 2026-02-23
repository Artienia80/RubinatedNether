package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.RuneItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public class SmithingMenuMixin {

    // This is fucking stupid but it works, alr?
    // im trying my best man

    @Unique
    private boolean runeWasInTemplateSlot = false;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void captureTemplate(Player player, ItemStack stack, CallbackInfo ci) {
        if (player.isCreative()) return;
        SmithingMenu menu = (SmithingMenu)(Object)this;
        runeWasInTemplateSlot = menu.getSlot(SmithingMenu.TEMPLATE_SLOT).getItem().getItem() instanceof RuneItem;
    }

    @Inject(method = "onTake", at = @At("TAIL"))
    private void restoreBlankRune(Player player, ItemStack stack, CallbackInfo ci) {
        if (player.isCreative()) return;
        if (runeWasInTemplateSlot) {
            SmithingMenu menu = (SmithingMenu)(Object)this;
            menu.getSlot(SmithingMenu.TEMPLATE_SLOT).set(new ItemStack(RNItems.RUNE.get()));
            runeWasInTemplateSlot = false;
        }
    }
}