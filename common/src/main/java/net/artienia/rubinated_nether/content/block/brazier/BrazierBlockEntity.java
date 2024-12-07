package net.artienia.rubinated_nether.content.block.brazier;

import net.artienia.rubinated_nether.config.RNConfig;
import net.artienia.rubinated_nether.content.RNParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import uwu.serenity.critter.utils.TickableBlockEntity;
import java.util.function.Predicate;

public class BrazierBlockEntity extends BlockEntity implements TickableBlockEntity {

	public BrazierBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	@Override
	public void tick() {

		if(level == null || level.getGameTime() % 40 != 0) return;

		int x = worldPosition.getX(), y = worldPosition.getY(), z = worldPosition.getZ();

		AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierRange);

		Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierRange)
			.and(EntitySelector.NO_SPECTATORS);

		for(ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
			player.addEffect(new MobEffectInstance(
				MobEffects.FIRE_RESISTANCE,
				Mth.floor(20 * RNConfig.brazierDuration),
				0, true, RNConfig.brazierEffectParticles, true
			));
		}
	}

	@Override
	public void clientTick() {

		if(level == null || RNConfig.Client.brazierParticleCount == 0) return;

		RandomSource random = level.random;
		int xPos = worldPosition.getX(), yPos = worldPosition.getY(), zPos = worldPosition.getZ();

		for(int i = 0; i < RNConfig.Client.brazierParticleCount; i++) {
			double x = Mth.clamp(xPos + random.nextGaussian() / 6.0, xPos - 0.4, xPos + 0.4);
			double z = Mth.clamp(zPos + random.nextGaussian() / 6.0, zPos - 0.4, zPos + 0.4);
			level.addParticle(RNParticleTypes.RUBY_AURA.get(), x + 0.5, yPos + 0.5, z + 0.5, 0, 0.02, 0);
		}
	}
}
