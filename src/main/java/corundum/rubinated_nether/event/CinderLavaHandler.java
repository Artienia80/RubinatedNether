package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.CinderBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class CinderLavaHandler {
	private static final double GRAVITY = 0.04;

	private CinderLavaHandler() {}

	@SubscribeEvent
	public static void stopInLava(EntityTickEvent.Pre event) {
		if (!(event.getEntity() instanceof FallingBlockEntity falling)) return;
		if (!(falling.getBlockState().getBlock() instanceof CinderBlock)) return;
		Level level = falling.level();
		if (level.isClientSide()) return;

		double nextDy = Math.min(falling.getDeltaMovement().y, 0.0) - GRAVITY;
		int startY = Mth.floor(falling.getY());
		int endY = Mth.floor(falling.getY() + nextDy);
		int x = Mth.floor(falling.getX());
		int z = Mth.floor(falling.getZ());

		for (int y = startY; y >= endY; y--) {
			BlockPos pos = new BlockPos(x, y, z);
			BlockState state = level.getBlockState(pos);
			if (state.getBlock() instanceof LiquidBlock && state.getFluidState().is(FluidTags.LAVA)) {
				level.setBlockAndUpdate(pos, CinderBlock.solidState());
				level.levelEvent(1501, pos, 0);
				falling.discard();
				return;
			}
			if (!state.getCollisionShape(level, pos).isEmpty()) return;
		}
	}
}
