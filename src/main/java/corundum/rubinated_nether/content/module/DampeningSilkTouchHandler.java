package corundum.rubinated_nether.content.module;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class DampeningSilkTouchHandler {

    @SubscribeEvent
    public static void dampeningActsAsSilkTouch(GetEnchantmentLevelEvent event) {
        if (!event.isTargetting(Enchantments.SILK_TOUCH)) return;
        if (!ToolModules.has(event.getStack(), ToolModules.DAMPENING)) return;

        event.getHolder(Enchantments.SILK_TOUCH).ifPresent(silkTouch -> event.getEnchantments().upgrade(silkTouch, 1));
    }
}