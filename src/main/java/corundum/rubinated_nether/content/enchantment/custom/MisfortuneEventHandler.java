package corundum.rubinated_nether.content.enchantment.custom;

import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class MisfortuneEventHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        ItemStack tool = player.getMainHandItem();
        if (tool.isEmpty()) return;

        var enchantmentRegistry = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        var misfortuneCurseHolder = enchantmentRegistry.getHolder(RNEnchantments.MISFORTUNE_CURSE);

        if (misfortuneCurseHolder.isPresent()) {
            int enchantmentLevel = tool.getEnchantmentLevel(misfortuneCurseHolder.get());
            if (enchantmentLevel > 0) {
                if (player.getRandom().nextFloat() < 0.5f) {
                    event.setCanceled(true);
                    event.getLevel().destroyBlock(event.getPos(), false);
                    if (!player.getAbilities().instabuild) {
                        tool.hurtAndBreak(1, player, player.getEquipmentSlotForItem(tool));
                    }
                }
            }
        }
    }
}