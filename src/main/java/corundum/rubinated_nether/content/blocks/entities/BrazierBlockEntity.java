package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.content.blocks.BrazierBlock;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
					levelJustChanged = true;
					level.setBlock(pos, state.setValue(BrazierBlock.LEVEL, Math.max(0, expectedLevel)), 3);
					updateEffectAmplifiersForLevelChange(level, pos, expectedLevel);
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

	public void updateEffectAmplifiersForLevelChange(Level level, BlockPos pos, int newLevel) {
		int x = pos.getX(), y = pos.getY(), z = pos.getZ();
		AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierEffectRange);
		Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierEffectRange)
				.and(EntitySelector.NO_SPECTATORS);

		int fuelDurationTicks = Math.max(20, remainingFuelSeconds * TICKS_PER_SECOND);
		int newAmplifier = newLevel - 1;

		for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
			MobEffectInstance oldEffect = player.getEffect(RNEffects.BRAZIER_POWER);

			if (oldEffect != null) {
				player.removeEffect(RNEffects.BRAZIER_POWER);
			}

			MobEffectInstance newEffect = new MobEffectInstance(
					RNEffects.BRAZIER_POWER,
					fuelDurationTicks,
					newAmplifier,
					true,
					RNConfig.brazierEffectParticles,
					true
			);

			player.addEffect(newEffect);
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

		// Calculate surface Y position based on fill level
		double surfaceY = yPos + 0.3125 + (fillLevel * 0.0625);

		// Check if block above is air
		BlockPos blockAbove = pos.above();
		if (level.getBlockState(blockAbove).isAir() && !level.getBlockState(blockAbove).isSolidRender(level, blockAbove)) {
			// Pop particles - scales with fill level
			int popChance = Math.max(10, 40 - (fillLevel * 3));
			if (random.nextInt(popChance) == 0) {
				double d0 = xPos + 0.2 + random.nextDouble() * 0.6;
				double d1 = surfaceY + 0.1;
				double d2 = zPos + 0.2 + random.nextDouble() * 0.6;
				level.addParticle(RNParticleTypes.RUBY_AURA.get(), d0, d1, d2, 0.0, 0.0, 0.0);
				level.playLocalSound(d0, d1, d2, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}

			// Ambient sound
			if (random.nextInt(200) == 0) {
				level.playLocalSound(xPos, surfaceY, zPos, SoundEvents.LAVA_AMBIENT, SoundSource.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
			}
		}

		// Ruby Spirit particles - frequency scales with level
		// Level 1 = 1x frequency (1/100 chance)
		// Level 9 = 2x frequency (1/50 chance)
		// Linear interpolation: chance = 100 - (fillLevel - 1) * 6.25
		if (fillLevel > 0) {
			// Calculate spawn chance: lerp from 100 at level 1 to 50 at level 9
			float levelNormalized = (fillLevel - 1) / 8.0f; // 0.0 at level 1, 1.0 at level 9
			int spawnChance = (int)(100 - (levelNormalized * 50)); // 100 to 50

			if (random.nextInt(spawnChance) == 0) {
				double sx = xPos + 0.3 + random.nextDouble() * 0.4;
				double sz = zPos + 0.3 + random.nextDouble() * 0.4;
				double spawnY = surfaceY + 0.1;

				net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
				if (mc != null && mc.particleEngine != null) {
					mc.particleEngine.createParticle(
							RNParticleTypes.RUBY_SPIRIT.get(),
							sx, spawnY, sz,
							0.0, 0.0, 0.0
					);
				}
			}
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
			return new ItemStack(RNItems.MOLTEN_RUBY.get(), 1);
		} else if (remainingFuelSeconds > 0) {
			int secondsPerNugget = secondsPerLevel / 9;
			int nuggetCount = remainingFuelSeconds / secondsPerNugget;

			if (nuggetCount > 0) {
				int secondsToRemove = nuggetCount * secondsPerNugget;
				remainingFuelSeconds -= secondsToRemove;
				setChanged();
				return new ItemStack(RNItems.MOLTEN_RUBY_NUGGET.get(), nuggetCount);
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

	private boolean isGreatFuel(ItemStack stack) {
		return stack.is(RNTags.Items.GREAT_BRAZIER_FUEL);
	}

	private boolean isStandardFuel(ItemStack stack) {
		return stack.is(RNTags.Items.STANDARD_BRAZIER_FUEL);
	}

	private boolean isSmallFuel(ItemStack stack) {
		return stack.is(RNTags.Items.SMALL_BRAZIER_FUEL);
	}

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

		if (!isGreatFuel(stack) && !isStandardFuel(stack) && !isSmallFuel(stack)) {
			return false;
		}

		int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();
		int maxSeconds = secondsPerLevel * 9;

		if (isGreatFuel(stack)) {
			if (remainingFuelSeconds == 0) {
				int bonusSeconds = (int)(secondsPerLevel * 9 * 1.05f);
				return bonusSeconds <= maxSeconds;
			} else {
				return remainingFuelSeconds + (secondsPerLevel * 9) <= maxSeconds;
			}
		} else if (isStandardFuel(stack)) {
			return remainingFuelSeconds + secondsPerLevel <= maxSeconds;
		} else {
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

			if (isGreatFuel(extracted)) {
				secondsRemoved = secondsPerLevel * 9;
			} else if (isStandardFuel(extracted)) {
				secondsRemoved = secondsPerLevel;
			} else if (isSmallFuel(extracted)) {
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
		if (isGreatFuel(stack)) {
			if (remainingFuelSeconds == 0) {
				addFuelWithBonus(9, 1.05f);
				stack.shrink(1);
				added = true;
			} else if (remainingFuelSeconds + (secondsPerLevel * 9) <= maxSeconds) {
				addFuel(9);
				stack.shrink(1);
				added = true;
			}
		} else if (isStandardFuel(stack)) {
			if (remainingFuelSeconds + secondsPerLevel <= maxSeconds) {
				addFuel(1);
				stack.shrink(1);
				added = true;
			}
		} else if (isSmallFuel(stack)) {
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
					return new ItemStack(RNItems.MOLTEN_RUBY.get(), 1);
				} else if (remainingFuelSeconds > 0) {
					int secondsPerNugget = secondsPerLevel / 9;
					int nuggetCount = remainingFuelSeconds / secondsPerNugget;
					if (nuggetCount > 0) {
						return new ItemStack(RNItems.MOLTEN_RUBY_NUGGET.get(), nuggetCount);
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