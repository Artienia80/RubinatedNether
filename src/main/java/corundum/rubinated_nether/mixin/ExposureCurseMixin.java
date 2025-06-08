package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ExposureCurseMixin {

    @Inject(method = "getArmorValue", at = @At("RETURN"), cancellable = true)
    private void modifyArmorValue(CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        Holder<Enchantment> exposureCurse = entity.level().registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getHolderOrThrow(RNEnchantments.EXPOSURE_CURSE);

        int totalModifiedArmor = 0;
        boolean anyCursed = false;

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack armorPiece = entity.getItemBySlot(slot);

            if (!armorPiece.isEmpty() && armorPiece.getItem() instanceof ArmorItem armorItem) {
                int baseArmorValue = armorItem.getDefense();
                int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(exposureCurse, armorPiece);

                if (enchantmentLevel > 0) {
                    int modifiedArmor = Math.max(1, (int) Math.floor(baseArmorValue * 0.5));
                    totalModifiedArmor += modifiedArmor;
                    anyCursed = true;
                } else {
                    totalModifiedArmor += baseArmorValue;
                }
            }
        }

        if (anyCursed) {
            cir.setReturnValue(totalModifiedArmor);
        }
    }
}