package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class RavagingCurseMixin {

    private boolean suppressDrops = false;

    @Inject(method = "die", at = @At("HEAD"))
    private void checkRavagingCurse(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;

        suppressDrops = false;

        if (damageSource.getEntity() instanceof LivingEntity killer) {
            ItemStack mainHandItem = killer.getMainHandItem();

            if (!mainHandItem.isEmpty()) {
                try {
                    Holder<Enchantment> antiLootingCurse = thisEntity.level().registryAccess()
                            .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                            .getHolderOrThrow(RNEnchantments.RAVAGING_CURSE);

                    int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(antiLootingCurse, mainHandItem);

                    if (enchantmentLevel > 0) {
                        if (thisEntity.level().getRandom().nextFloat() < 0.5f) {
                            suppressDrops = true;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    @Inject(method = "dropAllDeathLoot", at = @At("HEAD"), cancellable = true)
    private void suppressDropsIfNeeded(ServerLevel p_level, DamageSource damageSource, CallbackInfo ci) {
        if (suppressDrops) {
            ci.cancel();
        }
    }
}