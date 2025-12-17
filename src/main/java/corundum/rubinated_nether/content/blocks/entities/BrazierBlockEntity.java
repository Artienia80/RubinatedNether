package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.content.blocks.BrazierBlock;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BrazierBlockEntity extends BlockEntity implements WorldlyContainer {
	private static final int TICKS_PER_SECOND = 20;
	private static final int[] SLOTS_FOR_UP = new int[]{0};
	private static final int[] SLOTS_FOR_DOWN = new int[]{0};
	private static final int[] SLOTS_FOR_SIDES = new int[]{0};

	private int remainingFuelSeconds = 0;
	private int tickCounter = 0;
	private boolean levelJustChanged = false;
	private ItemStack virtualSlot = ItemStack.EMPTY;

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

		levelJustChanged = false;

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

					levelJustChanged = true;

					level.setBlock(pos, state.setValue(BrazierBlock.LEVEL, Math.max(0, expectedLevel)), 3);

					int x = pos.getX(), y = pos.getY(), z = pos.getZ();
					AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierEffectRange);
					Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierEffectRange)
							.and(EntitySelector.NO_SPECTATORS);

					int fuelDurationTicks = Math.max(20, remainingFuelSeconds * TICKS_PER_SECOND);
					int newAmplifier = expectedLevel - 1;

					for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
						MobEffectInstance oldEffect = player.getEffect(RNEffects.BRAZIER_POWER);

						if (oldEffect != null) {
							System.out.println("DEBUG: Player " + player.getName().getString() +
									" had amplifier " + oldEffect.getAmplifier() + ", removing...");

							player.removeEffect(RNEffects.BRAZIER_POWER);

							System.out.println("DEBUG: Effect removed, now has effect? " + player.hasEffect(RNEffects.BRAZIER_POWER));
						}

						System.out.println("DEBUG: Applying new amplifier " + newAmplifier);

						MobEffectInstance newEffect = new MobEffectInstance(
								RNEffects.BRAZIER_POWER,
								fuelDurationTicks,
								newAmplifier,
								true,
								RNConfig.brazierEffectParticles,
								true
						);

						boolean applied = player.addEffect(newEffect);
						System.out.println("DEBUG: Applied? " + applied);

						MobEffectInstance afterEffect = player.getEffect(RNEffects.BRAZIER_POWER);
						System.out.println("DEBUG: After apply, player has amplifier: " +
								(afterEffect != null ? afterEffect.getAmplifier() : "null"));
					}
				}
			}

			if (!levelJustChanged) {
				updatePlayersInRange(level, pos, currentLevel);
			}
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

				// Update if amplifier changed OR if duration differs by more than 1 second worth of ticks
				if (currentAmplifier != targetAmplifier || Math.abs(currentDuration - fuelDurationTicks) > TICKS_PER_SECOND) {
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
		if (RNConfig.brazierParticleCount == 0 || !RNConfig.brazierEffectParticles) return;

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

	public void addFuelWithBonus(int levels, float multiplier) {
		int baseSeconds = levels * RNConfig.getBrazierSecondsPerLevel();
		remainingFuelSeconds += (int)(baseSeconds * multiplier);
		setChanged();
	}

	public void addFuelNuggets(int nuggets) {
		int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();
		int secondsPerNugget = secondsPerLevel / 9;
		remainingFuelSeconds += nuggets * secondsPerNugget;
		setChanged();
	}

	public void setFuelForLevel(int level) {
		remainingFuelSeconds = level * RNConfig.getBrazierSecondsPerLevel();
		setChanged();
	}

	public ItemStack extractFuel() {
		int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();

		if (remainingFuelSeconds > secondsPerLevel * 9) {
			remainingFuelSeconds = 0;
			setChanged();
			return new ItemStack(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem(), 1);
		} else if (remainingFuelSeconds >= secondsPerLevel * 9) {
			remainingFuelSeconds -= secondsPerLevel * 9;
			setChanged();
			return new ItemStack(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem(), 1);
		} else if (remainingFuelSeconds >= secondsPerLevel) {
			remainingFuelSeconds -= secondsPerLevel;
			setChanged();
			return new ItemStack(RNItems.MOLTEN_RUBY_ITEM.get(), 1);
		} else if (remainingFuelSeconds > 0) {
			int secondsPerNugget = secondsPerLevel / 9;
			int nuggetCount = remainingFuelSeconds / secondsPerNugget;

			if (nuggetCount > 0) {
				int secondsToRemove = nuggetCount * secondsPerNugget;
				remainingFuelSeconds -= secondsToRemove;
				setChanged();
				return new ItemStack(RNItems.MOLTEN_RUBY_NUGGET_ITEM.get(), nuggetCount);
			} else {
				remainingFuelSeconds = 0;
				setChanged();
				return ItemStack.EMPTY;
			}
		}

		return ItemStack.EMPTY;
	}

	public int calculateLevelFromFuel() {
		int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();
		return (remainingFuelSeconds + secondsPerLevel - 1) / secondsPerLevel;
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

	public void removeEffectFromAllPlayersInRange(Level level, BlockPos excludePos) {
		int x = worldPosition.getX(), y = worldPosition.getY(), z = worldPosition.getZ();
		AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierEffectRange);

		Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierEffectRange)
				.and(EntitySelector.NO_SPECTATORS);

		for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
			if (player.hasEffect(RNEffects.BRAZIER_POWER)) {
				if (!isPlayerInRangeOfAnyBrazierExcluding(level, player, excludePos)) {
					player.removeEffect(RNEffects.BRAZIER_POWER);
					System.out.println("DEBUG: Removed effect from " + player.getName().getString() + " due to brazier being broken");
				}
			}
		}
	}

	private boolean isPlayerInRangeOfAnyBrazierExcluding(Level level, ServerPlayer player, BlockPos excludePos) {
		BlockPos playerPos = player.blockPosition();
		int searchRadius = RNConfig.brazierEffectRange + 1;

		for (BlockPos pos : BlockPos.betweenClosed(
				playerPos.offset(-searchRadius, -searchRadius, -searchRadius),
				playerPos.offset(searchRadius, searchRadius, searchRadius))) {

			if (pos.equals(excludePos)) {
				continue;
			}

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

	// WorldlyContainer implementation for hopper compatibility
	@Override
	public int[] getSlotsForFace(Direction direction) {
		if (direction == Direction.DOWN) {
			return SLOTS_FOR_DOWN;
		} else if (direction == Direction.UP) {
			return SLOTS_FOR_UP;
		} else {
			return SLOTS_FOR_SIDES;
		}
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
		if (index != 0) return false;

		if (!stack.is(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem()) &&
				!stack.is(RNItems.MOLTEN_RUBY_ITEM.get()) &&
				!stack.is(RNItems.MOLTEN_RUBY_NUGGET_ITEM.get())) {
			return false;
		}

		int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();
		int maxSeconds = secondsPerLevel * 9;

		// Check based on actual fuel seconds
		if (stack.is(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem())) {
			// Blocks only get bonus when brazier is empty
			if (remainingFuelSeconds == 0) {
				// Empty brazier - allow with bonus (9 * 1.05 = 9.45 levels worth)
				int bonusSeconds = (int)(secondsPerLevel * 9 * 1.05f);
				return bonusSeconds <= maxSeconds; // This will be true since 9.45 > 9, but we allow it
			} else {
				// Partially filled - check without bonus
				return remainingFuelSeconds + (secondsPerLevel * 9) <= maxSeconds;
			}
		} else if (stack.is(RNItems.MOLTEN_RUBY_ITEM.get())) {
			return remainingFuelSeconds + secondsPerLevel <= maxSeconds;
		} else {
			// Nuggets
			int secondsPerNugget = secondsPerLevel / 9;
			return remainingFuelSeconds + secondsPerNugget <= maxSeconds;
		}
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return direction == Direction.DOWN && index == 0;
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return virtualSlot.isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot == 0 ? virtualSlot : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		if (slot != 0 || amount <= 0) {
			return ItemStack.EMPTY;
		}

		BlockState state = level.getBlockState(worldPosition);
		int currentLevel = state.getValue(BrazierBlock.LEVEL);

		if (currentLevel <= 0) {
			return ItemStack.EMPTY;
		}

		ItemStack extracted = extractFuel();

		if (!extracted.isEmpty()) {
			int newLevel = calculateLevelFromFuel();
			level.setBlock(worldPosition, state.setValue(BrazierBlock.LEVEL, newLevel), 3);

			int secondsRemoved = 0;
			int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();

			if (extracted.is(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem())) {
				secondsRemoved = secondsPerLevel * 9;
			} else if (extracted.is(RNItems.MOLTEN_RUBY_ITEM.get())) {
				secondsRemoved = secondsPerLevel;
			} else if (extracted.is(RNItems.MOLTEN_RUBY_NUGGET_ITEM.get())) {
				int secondsPerNugget = secondsPerLevel / 9;
				secondsRemoved = extracted.getCount() * secondsPerNugget;
			}

			updatePlayerEffectDurations(secondsRemoved);

			return extracted;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack result = virtualSlot;
		virtualSlot = ItemStack.EMPTY;
		return result;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot != 0 || stack.isEmpty()) {
			return;
		}

		int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();
		int maxSeconds = secondsPerLevel * 9;

		boolean added = false;
		if (stack.is(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem())) {
			// Only apply bonus if brazier is completely empty
			if (remainingFuelSeconds == 0) {
				addFuelWithBonus(9, 1.05f);
				stack.shrink(1);
				added = true;
			} else if (remainingFuelSeconds + (secondsPerLevel * 9) <= maxSeconds) {
				// Partially filled - no bonus
				addFuel(9);
				stack.shrink(1);
				added = true;
			}
		} else if (stack.is(RNItems.MOLTEN_RUBY_ITEM.get())) {
			if (remainingFuelSeconds + secondsPerLevel <= maxSeconds) {
				addFuel(1);
				stack.shrink(1);
				added = true;
			}
		} else if (stack.is(RNItems.MOLTEN_RUBY_NUGGET_ITEM.get())) {
			int secondsPerNugget = secondsPerLevel / 9;
			if (remainingFuelSeconds + secondsPerNugget <= maxSeconds) {
				addFuelNuggets(1);
				stack.shrink(1);
				added = true;
			}
		}

		if (added) {
			int newLevel = calculateLevelFromFuel();
			BlockState state = level.getBlockState(worldPosition);
			level.setBlock(worldPosition, state.setValue(BrazierBlock.LEVEL, Math.min(9, newLevel)), 3);
			setChanged();
		}

		virtualSlot = stack.isEmpty() ? ItemStack.EMPTY : stack;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		virtualSlot = ItemStack.EMPTY;
	}

	private void updatePlayerEffectDurations(int secondsRemoved) {
		if (secondsRemoved <= 0) return;

		int x = worldPosition.getX(), y = worldPosition.getY(), z = worldPosition.getZ();
		AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierEffectRange);
		Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierEffectRange)
				.and(EntitySelector.NO_SPECTATORS);

		int ticksToReduce = secondsRemoved * 20;

		for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
			MobEffectInstance currentEffect = serverPlayer.getEffect(RNEffects.BRAZIER_POWER);
			if (currentEffect != null) {
				int currentDuration = currentEffect.getDuration();
				int newDuration = Math.max(0, currentDuration - ticksToReduce);

				if (newDuration > 0) {
					serverPlayer.removeEffect(RNEffects.BRAZIER_POWER);
					serverPlayer.addEffect(new MobEffectInstance(
							RNEffects.BRAZIER_POWER,
							newDuration,
							currentEffect.getAmplifier(),
							currentEffect.isAmbient(),
							currentEffect.isVisible(),
							currentEffect.showIcon()
					));
				} else {
					serverPlayer.removeEffect(RNEffects.BRAZIER_POWER);
				}
			}
		}
	}

	public IItemHandler getItemHandler(Direction side) {
		return new IItemHandler() {
			@Override
			public int getSlots() {
				return 1;
			}

			@Override
			public @NotNull ItemStack getStackInSlot(int slot) {
				return ItemStack.EMPTY;
			}

			@Override
			public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				if (stack.isEmpty() || slot != 0) {
					return stack;
				}

				if (!canPlaceItemThroughFace(0, stack, side)) {
					return stack;
				}

				if (!simulate) {
					setItem(0, stack.copy());
				}

				return ItemStack.EMPTY;
			}

			@Override
			public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
				if (slot != 0 || side != Direction.DOWN) {
					return ItemStack.EMPTY;
				}

				if (!simulate) {
					return removeItem(0, amount);
				}

				BlockState state = level.getBlockState(worldPosition);
				int currentLevel = state.getValue(BrazierBlock.LEVEL);

				if (currentLevel <= 0) {
					return ItemStack.EMPTY;
				}

				int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();
				if (remainingFuelSeconds >= secondsPerLevel * 9) {
					return new ItemStack(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem(), 1);
				} else if (remainingFuelSeconds >= secondsPerLevel) {
					return new ItemStack(RNItems.MOLTEN_RUBY_ITEM.get(), 1);
				} else if (remainingFuelSeconds > 0) {
					int secondsPerNugget = secondsPerLevel / 9;
					int nuggetCount = remainingFuelSeconds / secondsPerNugget;
					if (nuggetCount > 0) {
						return new ItemStack(RNItems.MOLTEN_RUBY_NUGGET_ITEM.get(), nuggetCount);
					}
				}

				return ItemStack.EMPTY;
			}

			@Override
			public int getSlotLimit(int slot) {
				return 64;
			}

			@Override
			public boolean isItemValid(int slot, @NotNull ItemStack stack) {
				return canPlaceItemThroughFace(0, stack, side);
			}
		};
	}
}