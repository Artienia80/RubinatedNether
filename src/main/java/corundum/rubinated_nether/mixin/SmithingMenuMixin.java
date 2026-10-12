package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.RuneCarvingHelper;
import corundum.rubinated_nether.content.items.RuneItem;
import corundum.rubinated_nether.content.items.Rubination;
import net.minecraft.server.level.ServerPlayer;
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

    @Unique
    private Rubination capturedRubination = null;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void captureTemplate(Player player, ItemStack stack, CallbackInfo ci) {
        SmithingMenu menu = (SmithingMenu)(Object)this;
        ItemStack templateStack = menu.getSlot(SmithingMenu.TEMPLATE_SLOT).getItem();

        capturedRubination = templateStack.getItem() instanceof RuneItem runeItem
                ? runeItem.getRubination()
                : null;

        if (capturedRubination != null && player instanceof ServerPlayer serverPlayer) {
            var advancementHolder = serverPlayer.server.getAdvancements()
                    .get(RubinatedNether.id("rubinated_trim"));
            if (advancementHolder != null) {
                var progress = serverPlayer.getAdvancements().getOrStartProgress(advancementHolder);
                if (!progress.isDone()) {
                    for (String criterion : progress.getRemainingCriteria()) {
                        serverPlayer.getAdvancements().award(advancementHolder, criterion);
                    }
                }
            }
        }
    }

    @Inject(method = "onTake", at = @At("TAIL"))
    private void restoreCarvedRune(Player player, ItemStack stack, CallbackInfo ci) {
        if (capturedRubination != null) {
            SmithingMenu menu = (SmithingMenu)(Object)this;
            ItemStack carved = RuneCarvingHelper.withCarving(new ItemStack(RNItems.RUNE.get()), capturedRubination);
            menu.getSlot(SmithingMenu.TEMPLATE_SLOT).set(carved);
            capturedRubination = null;
        }
    }
}