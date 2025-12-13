package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.content.blocks.BrazierBlock;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

public class BrazierBlockEntity extends BlockEntity {

	public BrazierBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.BRAZIER.get(), pos, blockState);
	}

	public static void serverTickStatic(Level level, BlockPos pos, BlockState state, BrazierBlockEntity blockEntity) {
		blockEntity.serverTick(level, pos, state);
	}

	public static void clientTickStatic(Level level, BlockPos pos, BlockState state, BrazierBlockEntity blockEntity) {
		blockEntity.clientTick(level, pos, state);
	}

	private void serverTick(Level level, BlockPos pos, BlockState state) {
		if (level.getGameTime() % 40 != 0) return;

		// Only apply effects if brazier has fuel
		int fillLevel = state.getValue(BrazierBlock.LEVEL);
		if (fillLevel == 0) return;

		int x = pos.getX(), y = pos.getY(), z = pos.getZ();

		AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierEffectRange);

		Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierEffectRange)
				.and(EntitySelector.NO_SPECTATORS);

		for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
			player.addEffect(new MobEffectInstance(
					RNEffects.BRAZIER_POWER,
					Mth.floor(20 * RNConfig.brazierEffectDuration),
					0, true, RNConfig.brazierEffectParticles, true
			));
		}
	}

	private void clientTick(Level level, BlockPos pos, BlockState state) {
		if (RNConfig.brazierParticleCount == 0) return;

		int fillLevel = state.getValue(BrazierBlock.LEVEL);
		if (fillLevel == 0) return;

		RandomSource random = level.random;
		int xPos = pos.getX(), yPos = pos.getY(), zPos = pos.getZ();

		int scaledParticleCount = Math.max(1, (RNConfig.brazierParticleCount * fillLevel) / 9);

		for (int i = 0; i < scaledParticleCount; i++) {
			double x = Mth.clamp(xPos + random.nextGaussian() / 6.0, xPos - 0.4, xPos + 0.4);
			double z = Mth.clamp(zPos + random.nextGaussian() / 6.0, zPos - 0.4, zPos + 0.4);
			level.addParticle(RNParticleTypes.RUBY_AURA.get(), x + 0.5, yPos + 0.5, z + 0.5, 0, 0.02, 0);
		}
	}
}