package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
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
        int originalArmor = cir.getReturnValue();

        boolean hasCurse = false;

        // Check each armor piece for the curse
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
                ItemStack armorPiece = entity.getItemBySlot(slot);

                if (!armorPiece.isEmpty()) {
                    // Get the enchantment holder
                    Holder<Enchantment> exposureCurse = entity.level().registryAccess()
                            .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                            .getHolderOrThrow(RNEnchantments.EXPOSURE_CURSE);

                    int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(exposureCurse, armorPiece);

                    if (enchantmentLevel > 0) {
                        hasCurse = true;
                        break;
                    }
                }
            }
        }

        if (hasCurse) {
            // Reduce total armor by 50%, minimum 1
            int modifiedArmor = Math.max(1, (int) Math.floor(originalArmor * 0.5));
            cir.setReturnValue(modifiedArmor);
        }
    }


}