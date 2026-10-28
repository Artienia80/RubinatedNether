package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class CrookedShotHandler {
    private static final double EXTRA_SPREAD = 0.0172275 * 4.0;

    @SubscribeEvent
    public static void onArrowJoin(EntityJoinLevelEvent event) {
        Level level = event.getLevel();
        if (level.isClientSide || event.loadedFromDisk()) {
            return;
        }
        if (!(event.getEntity() instanceof AbstractArrow arrow)) {
            return;
        }

        ItemStack weapon = arrow.getWeaponItem();
        if (weapon == null || !(weapon.getItem() instanceof CrossbowItem)) {
            return;
        }

        var holder = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(RNEnchantments.CROOKED_SHOT_CURSE);
        if (holder.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(holder.get(), weapon) <= 0) {
            return;
        }

        Vec3 motion = arrow.getDeltaMovement();
        double speed = motion.length();
        if (speed < 1.0E-6) {
            return;
        }

        RandomSource random = level.random;
        Vec3 velocity = motion.normalize().add(
                random.triangle(0.0, EXTRA_SPREAD),
                random.triangle(0.0, EXTRA_SPREAD),
                random.triangle(0.0, EXTRA_SPREAD)
        ).scale(speed);

        arrow.setDeltaMovement(velocity);
        double horizontal = velocity.horizontalDistance();
        arrow.setYRot((float) (Mth.atan2(velocity.x, velocity.z) * (180.0 / Math.PI)));
        arrow.setXRot((float) (Mth.atan2(velocity.y, horizontal) * (180.0 / Math.PI)));
        arrow.yRotO = arrow.getYRot();
        arrow.xRotO = arrow.getXRot();
    }
}
