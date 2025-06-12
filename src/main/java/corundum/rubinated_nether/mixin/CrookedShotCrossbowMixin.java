package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CrossbowItem.class)
public class CrookedShotCrossbowMixin {

    @Redirect(
            method = "shootProjectile",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/Projectile;shoot(DDDFF)V")
    )
    private void redirectProjectileShoot(Projectile projectile, double x, double y, double z,
                                         float velocity, float inaccuracy,
                                         LivingEntity shooter, Projectile projectile2, int index,
                                         float velocity2, float inaccuracy2, float angle,
                                         LivingEntity target) {

        // Get the crossbow item from the shooter's hands
        ItemStack crossbow = ItemStack.EMPTY;
        if (shooter.getMainHandItem().getItem() instanceof CrossbowItem) {
            crossbow = shooter.getMainHandItem();
        } else if (shooter.getOffhandItem().getItem() instanceof CrossbowItem) {
            crossbow = shooter.getOffhandItem();
        }

        float modifiedInaccuracy = inaccuracy;

        if (!crossbow.isEmpty()) {
            // Check if the crossbow has the Crooked Shot curse
            Holder<Enchantment> crookedShotHolder = shooter.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(RNEnchantments.CROOKED_SHOT_CURSE);

            int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(crookedShotHolder, crossbow);

            if (enchantmentLevel > 0) {
                // Multiply inaccuracy by 5 (or scale by enchantment level)
                modifiedInaccuracy = inaccuracy * 5.0F;
            }
        }

        // Call the original shoot method with potentially modified inaccuracy
        projectile.shoot(x, y, z, velocity, modifiedInaccuracy);
    }
}