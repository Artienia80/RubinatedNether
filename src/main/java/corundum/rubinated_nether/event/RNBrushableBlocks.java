package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RNBrushableBlocks {

	@SubscribeEvent
	public static void addValidBlocks(BlockEntityTypeAddBlocksEvent event) {
		event.modify(BlockEntityType.BRUSHABLE_BLOCK, RNBlocks.SUSPICIOUS_SHRINE_STONE_RUBBLE.get());
	}
}
