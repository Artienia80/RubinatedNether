package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.RuneItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.inventory.LoomMenu$6")
public class LoomMenuMixin {

    @Shadow
    private LoomMenu this$0;

    // EXTREMELY FUCKED UP WAY TO SOLVE THIS - BUT MOJANG MADE ONTAKE ANONYMOUS!
    // Either I am a dumb fuck or mojang is, this is the only way i could figure this out
    // this hacky bullshit can break at basically any second, implement this in a better way ASAP!
    // TODO: Rewrite this cursed hell of an implementation as soon as humanly possible.
    @Inject(method = "onTake", at = @At("HEAD"))
    private void onLoomTake(Player player, ItemStack stack, CallbackInfo ci) {
        if (player.isCreative()) return;

        Slot patternSlot = this.this$0.getPatternSlot();
        ItemStack pattern = patternSlot.getItem();
        if (pattern.getItem() instanceof RuneItem) {
            patternSlot.set(new ItemStack(RNItems.RUNE.get()));
        }
    }
}