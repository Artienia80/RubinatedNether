package corundum.rubinated_nether.mixin;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import corundum.rubinated_nether.content.RNTags;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @Inject(method = "createInputSlotDefinitions", at = @At("RETURN"), cancellable = true)
    private void modifySlotDefinitions(CallbackInfoReturnable<ItemCombinerMenuSlotDefinition> cir) {
        ItemCombinerMenuSlotDefinition modified = ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 27, 47, (stack) -> !stack.is(RNTags.Items.RUNES))
                .withSlot(1, 76, 47, (stack) -> !stack.is(RNTags.Items.RUNES))
                .withResultSlot(2, 134, 47)
                .build();

        cir.setReturnValue(modified);
    }
}