package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNEffects;
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

    protected abstract void dropAllDeathLoot(DamageSource damageSource, ServerLevel p_level);

    private boolean suppressDrops = false;
    private boolean doubleLoot = false;
    private boolean isDoublingLoot = false; // Prevent recursive calls

    @Inject(method = "die", at = @At("HEAD"))
    private void checkRavagingCurse(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;

        suppressDrops = false;
        doubleLoot = false;
        isDoublingLoot = false; // Reset flag

        // Don't trigger blessed effect for wither boss
        if (thisEntity.getType() == net.minecraft.world.entity.EntityType.WITHER) {
            return;
        }

        if (damageSource.getEntity() instanceof LivingEntity killer) {
            ItemStack mainHandItem = killer.getMainHandItem();

            // Check for BLESSED effect with duration-based chance
            if (killer.hasEffect(RNEffects.BLESSED)) {
                int remainingDuration = killer.getEffect(RNEffects.BLESSED).getDuration();
                float chance = Math.min(1.0f, remainingDuration / 384000.0f); // Linear from 0 to 1

                if (thisEntity.level().getRandom().nextFloat() < chance) {
                    doubleLoot = true;
                }
            }

            if (!mainHandItem.isEmpty()) {
                try {
                    Holder<Enchantment> antiLootingCurse = thisEntity.level().registryAccess()
                            .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                            .getHolderOrThrow(RNEnchantments.RAVAGING_CURSE);

                    int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(antiLootingCurse, mainHandItem);

                    if (enchantmentLevel > 0) {
                        if (thisEntity.level().getRandom().nextFloat() < 0.5f) {
                            suppressDrops = true;
                            doubleLoot = false; // Override double loot if drops are suppressed
                        }
                    }
                } catch (Exception e) {
                    // Enchantment not found, continue normally
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

    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    private void doubleLootIfBlessed(ServerLevel p_level, DamageSource damageSource, CallbackInfo ci) {
        // Always set the flag first to prevent multiple rolls
        if (isDoublingLoot) {
            return; // Already processing, exit early
        }

        if (doubleLoot && !suppressDrops) {
            isDoublingLoot = true; // Set flag to prevent recursion and multiple rolls

            // Manually trigger another loot drop by calling the loot generation directly
            LivingEntity thisEntity = (LivingEntity) (Object) this;
            try {
                // Use reflection to call the protected method with correct signature
                java.lang.reflect.Method method = LivingEntity.class.getDeclaredMethod("dropAllDeathLoot", ServerLevel.class, DamageSource.class);
                method.setAccessible(true);
                method.invoke(thisEntity, p_level, damageSource);
            } catch (Exception e) {
                // If reflection fails, skip the double loot
            }

            isDoublingLoot = false; // Reset flag after call
        }
    }
}