package net.artienia.rubinated_nether.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.artienia.rubinated_nether.utils.PiglinCurrency;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {

	@ModifyReturnValue(
		method = "isBarterCurrency",
		at = @At("RETURN")
	)
	private static boolean barterRubies(boolean original, @Local(argsOnly = true) ItemStack stack) {
		return original || (stack.getItem() instanceof PiglinCurrency currency && currency.isPiglinCurrency(stack));
	}
}
