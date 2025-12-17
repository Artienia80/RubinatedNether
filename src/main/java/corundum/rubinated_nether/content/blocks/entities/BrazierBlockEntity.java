package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.content.blocks.BrazierBlock;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
	private static final int TICKS_PER_SECOND = 20;

	private int remainingFuelSeconds = 0;
	private int tickCounter = 0;

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
		int currentLevel = state.getValue(BrazierBlock.LEVEL);

		if (currentLevel > 0 && remainingFuelSeconds > 0) {
			tickCounter++;

			if (tickCounter >= TICKS_PER_SECOND) {
				tickCounter = 0;
				remainingFuelSeconds--;
				setChanged();

				int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();
				int expectedLevel = (remainingFuelSeconds + secondsPerLevel - 1) / secondsPerLevel;

				if (expectedLevel < currentLevel) {
					System.out.println("DEBUG: Level dropping from " + currentLevel + " to " + expectedLevel);

					int x = pos.getX(), y = pos.getY(), z = pos.getZ();
					AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierEffectRange);
					Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierEffectRange)
							.and(EntitySelector.NO_SPECTATORS);

					for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
						if (player.hasEffect(RNEffects.BRAZIER_POWER)) {
							MobEffectInstance oldEffect = player.getEffect(RNEffects.BRAZIER_POWER);
							System.out.println("DEBUG: Player " + player.getName().getString() +
									" had amplifier " + oldEffect.getAmplifier() + ", removing...");

							player.removeEffect(RNEffects.BRAZIER_POWER);

							int fuelDurationTicks = Math.max(20, remainingFuelSeconds * TICKS_PER_SECOND);
							int newAmplifier = expectedLevel - 1;

							System.out.println("DEBUG: Applying new amplifier " + newAmplifier);

							player.addEffect(new MobEffectInstance(
									RNEffects.BRAZIER_POWER,
									fuelDurationTicks,
									newAmplifier,
									true,
									RNConfig.brazierEffectParticles,
									true
							));
						}
					}

					level.setBlock(pos, state.setValue(BrazierBlock.LEVEL, Math.max(0, expectedLevel)), 3);
				}
			}

			updatePlayersInRange(level, pos, currentLevel);
		} else if (currentLevel > 0 && remainingFuelSeconds <= 0) {
			level.setBlock(pos, state.setValue(BrazierBlock.LEVEL, 0), 3);
		}

		if (level.getGameTime() % TICKS_PER_SECOND == 0) {
			removeEffectFromPlayersOutOfRange(level, pos);
		}
	}

	private void updatePlayersInRange(Level level, BlockPos pos, int currentLevel) {
		int x = pos.getX(), y = pos.getY(), z = pos.getZ();
		AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierEffectRange);

		Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierEffectRange)
				.and(EntitySelector.NO_SPECTATORS);

		int fuelDurationTicks = Math.max(20, remainingFuelSeconds * TICKS_PER_SECOND);
		int targetAmplifier = currentLevel - 1;

		for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
			MobEffectInstance currentEffect = player.getEffect(RNEffects.BRAZIER_POWER);

			boolean shouldUpdate = false;

			if (currentEffect == null) {
				shouldUpdate = true;
			} else {
				int currentAmplifier = currentEffect.getAmplifier();
				int currentDuration = currentEffect.getDuration();

				if (currentAmplifier != targetAmplifier) {
					shouldUpdate = true;
				} else if (Math.abs(currentDuration - fuelDurationTicks) > 40) {
					shouldUpdate = true;
				}
			}

			if (shouldUpdate) {
				player.addEffect(new MobEffectInstance(
						RNEffects.BRAZIER_POWER,
						fuelDurationTicks,
						targetAmplifier,
						true,
						RNConfig.brazierEffectParticles,
						true
				));
			}
		}
	}

	private void removeEffectFromPlayersOutOfRange(Level level, BlockPos pos) {
		int x = pos.getX(), y = pos.getY(), z = pos.getZ();
		double rangeSquared = RNConfig.brazierEffectRange * RNConfig.brazierEffectRange;

		for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
			if (player.level() == level && player.hasEffect(RNEffects.BRAZIER_POWER)) {
				double distanceSq = player.distanceToSqr(x + 0.5, y + 0.5, z + 0.5);
				if (distanceSq > rangeSquared) {
					if (!isPlayerInRangeOfAnyBrazier(level, player)) {
						player.removeEffect(RNEffects.BRAZIER_POWER);
					}
				}
			}
		}
	}

	private boolean isPlayerInRangeOfAnyBrazier(Level level, ServerPlayer player) {
		BlockPos playerPos = player.blockPosition();
		int searchRadius = RNConfig.brazierEffectRange + 1;

		for (BlockPos pos : BlockPos.betweenClosed(
				playerPos.offset(-searchRadius, -searchRadius, -searchRadius),
				playerPos.offset(searchRadius, searchRadius, searchRadius))) {

			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof BrazierBlockEntity brazier) {
				BlockState state = level.getBlockState(pos);
				int brazierLevel = state.getValue(BrazierBlock.LEVEL);

				if (brazierLevel > 0 && brazier.remainingFuelSeconds > 0) {
					double distSq = player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
					if (distSq <= RNConfig.brazierEffectRange * RNConfig.brazierEffectRange) {
						return true;
					}
				}
			}
		}
		return false;
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

	public void addFuel(int levels) {
		remainingFuelSeconds += levels * RNConfig.getBrazierSecondsPerLevel();
		setChanged();
	}

	public void setFuelForLevel(int level) {
		remainingFuelSeconds = level * RNConfig.getBrazierSecondsPerLevel();
		setChanged();
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("RemainingFuelSeconds", remainingFuelSeconds);
		tag.putInt("TickCounter", tickCounter);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		remainingFuelSeconds = tag.getInt("RemainingFuelSeconds");
		tickCounter = tag.getInt("TickCounter");
	}

	public int getRemainingFuelSeconds() {
		return remainingFuelSeconds;
	}
}