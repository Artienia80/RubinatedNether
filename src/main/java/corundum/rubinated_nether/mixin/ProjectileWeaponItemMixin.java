package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.CrossbowRodType;
import corundum.rubinated_nether.content.entity.CrossbowRodProjectileEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponItemMixin {

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    private static void rn$drawRods(ItemStack weapon, ItemStack ammo, LivingEntity shooter, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!(weapon.getItem() instanceof CrossbowItem) || CrossbowRodType.of(ammo) == null) {
            return;
        }

        List<ItemStack> drawn = new ArrayList<>();

        if (!(shooter.level() instanceof ServerLevel serverLevel)) {
            drawn.add(rn$intangible(ammo));
            cir.setReturnValue(drawn);
            return;
        }

        int count = EnchantmentHelper.processProjectileCount(serverLevel, weapon, shooter, 1);
        boolean infinite = shooter.hasInfiniteMaterials();
        for (int i = 0; i < count; i++) {
            if (infinite) {
                drawn.add(rn$intangible(ammo));
            } else if (!ammo.isEmpty()) {
                drawn.add(ammo.split(1));
            }
        }

        if (ammo.isEmpty() && shooter instanceof Player player) {
            if (player.getMainHandItem() == ammo) {
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            } else if (player.getOffhandItem() == ammo) {
                player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
        }

        cir.setReturnValue(drawn);
    }

    @Inject(method = "createProjectile", at = @At("HEAD"), cancellable = true)
    private void rn$createRodProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean crit, CallbackInfoReturnable<Projectile> cir) {
        CrossbowRodType type = CrossbowRodType.of(ammo);
        if (type == null || !(weapon.getItem() instanceof CrossbowItem)) {
            return;
        }
        cir.setReturnValue(new CrossbowRodProjectileEntity(level, shooter, weapon, ammo, type, crit));
    }

    private static ItemStack rn$intangible(ItemStack ammo) {
        ItemStack copy = ammo.copyWithCount(1);
        copy.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
        return copy;
    }
}
