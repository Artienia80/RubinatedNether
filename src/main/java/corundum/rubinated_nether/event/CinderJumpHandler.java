package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.CinderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class CinderJumpHandler {
	private CinderJumpHandler() {}

	@SubscribeEvent
	public static void onJump(LivingEvent.LivingJumpEvent event) {
		LivingEntity entity = event.getEntity();
		Level level = entity.level();
		if (level.isClientSide()) return;
		BlockPos pos = entity.getOnPos();
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof CinderBlock cinder) {
			cinder.trigger(level, pos);
		}
	}
}
