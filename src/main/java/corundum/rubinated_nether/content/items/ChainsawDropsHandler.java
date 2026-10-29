package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.module.ToolModules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

/**
 * The chainsaw has shears speed (and abilities) built in, which makes leaves drop themselves.
 * Unless it has Dampening (or real Silk Touch), it should drop what an axe would instead.
 */
@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class ChainsawDropsHandler {
    private ChainsawDropsHandler() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void axeDropsInsteadOfShearsDrops(BlockDropsEvent event) {
        ItemStack tool = event.getTool();
        if (!(tool.getItem() instanceof ChainsawItem)) return;

        BlockState state = event.getState();
        if (!state.is(RNTags.Blocks.CHAINSAW_NO_SELF_DROP)) return;

        ServerLevel level = event.getLevel();
        if (ToolModules.has(tool, ToolModules.DAMPENING)) return;
        // Silk Touch on the stack (Dampening is covered above and would also show up through this check)
        if (level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .get(net.minecraft.world.item.enchantment.Enchantments.SILK_TOUCH)
                .map(tool::getEnchantmentLevel).orElse(0) > 0) return;

        // Roll the block's loot again with an axe that carries the chainsaw's enchantments (e.g. Fortune)
        ItemStack axe = new ItemStack(Items.IRON_AXE);
        axe.set(DataComponents.ENCHANTMENTS, tool.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));

        BlockPos pos = event.getPos();
        event.getDrops().clear();
        for (ItemStack stack : Block.getDrops(state, level, pos, event.getBlockEntity(), event.getBreaker(), axe)) {
            if (stack.isEmpty()) continue;
            // Same placement as Block.popResource
            double halfHeight = EntityType.ITEM.getHeight() / 2.0;
            double x = pos.getX() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
            double y = pos.getY() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25) - halfHeight;
            double z = pos.getZ() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
            ItemEntity entity = new ItemEntity(level, x, y, z, stack);
            entity.setDefaultPickUpDelay();
            event.getDrops().add(entity);
        }
    }
}
