package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.CrossbowRodType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingGetProjectileEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class CrossbowRodHandler {

    public static float loadedRodModelValue(ItemStack crossbow) {
        ChargedProjectiles charged = crossbow.get(DataComponents.CHARGED_PROJECTILES);
        if (charged == null || charged.isEmpty()) {
            return 0.0F;
        }
        for (ItemStack projectile : charged.getItems()) {
            CrossbowRodType type = CrossbowRodType.of(projectile);
            if (type != null) {
                return type.modelValue();
            }
        }
        return 0.0F;
    }

    @SubscribeEvent
    public static void onGetProjectile(LivingGetProjectileEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!(event.getProjectileWeaponItemStack().getItem() instanceof CrossbowItem)) {
            return;
        }

        ItemStack otherHand = player.getMainHandItem().getItem() instanceof CrossbowItem
                ? player.getOffhandItem()
                : player.getMainHandItem();
        if (CrossbowRodType.of(otherHand) != null) {
            event.setProjectileItemStack(otherHand);
        }
    }
}
