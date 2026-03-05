package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.RuneItem;
import net.minecraft.server.level.ServerPlayer;
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

    @Inject(method = "onTake", at = @At("HEAD"))
    private void onLoomTake(Player player, ItemStack stack, CallbackInfo ci) {
        Slot patternSlot = this.this$0.getPatternSlot();
        ItemStack pattern = patternSlot.getItem();
        if (pattern.getItem() instanceof RuneItem) {
            if (player instanceof ServerPlayer serverPlayer) {
                var advancementHolder = serverPlayer.server.getAdvancements()
                        .get(RubinatedNether.id("rubinated_banner"));
                if (advancementHolder != null) {
                    var progress = serverPlayer.getAdvancements().getOrStartProgress(advancementHolder);
                    if (!progress.isDone()) {
                        for (String criterion : progress.getRemainingCriteria()) {
                            serverPlayer.getAdvancements().award(advancementHolder, criterion);
                        }
                    }
                }
            }

            patternSlot.set(new ItemStack(RNItems.RUNE.get()));
        }
    }
}