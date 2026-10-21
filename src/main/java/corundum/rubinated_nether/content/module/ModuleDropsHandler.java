package corundum.rubinated_nether.content.module;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class ModuleDropsHandler {
    private ModuleDropsHandler() {}

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        ItemStack tool = event.getTool();
        if (!ToolModules.isModdable(tool)) return;

        List<ResourceLocation> moduleIds = ToolModules.moduleIds(tool);
        if (moduleIds.isEmpty()) return;

        ServerLevel level = event.getLevel();
        for (ItemEntity drop : event.getDrops()) {
            for (ResourceLocation moduleId : moduleIds) {
                DropProcessor processor = DropProcessors.get(moduleId);
                if (processor == null) continue;

                Optional<ItemStack> result = processor.process(level, drop.getItem());
                if (result.isPresent()) {
                    drop.setItem(result.get());
                    break;
                }
            }
        }
    }
}
