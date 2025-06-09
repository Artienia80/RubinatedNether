package corundum.rubinated_nether.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Enchantment.class)
public class MultishotSpreadMixin {

    @Inject(method = "modifyProjectileSpread", at = @At("TAIL"))
    private void modifyMultishotSpread(ServerLevel level, int enchantmentLevel, ItemStack tool, Entity entity, MutableFloat projectileSpread, CallbackInfo ci) {
        Enchantment enchantment = (Enchantment) (Object) this;

        var multishotHolder = level.registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.MULTISHOT);

        if (enchantment.equals(multishotHolder.value())) {
            projectileSpread.setValue(5.0F);
        }
    }
}