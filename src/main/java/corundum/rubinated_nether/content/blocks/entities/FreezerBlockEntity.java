package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNRecipes;
import corundum.rubinated_nether.content.menu.FreezerMenu;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class FreezerBlockEntity extends AbstractFreezerBlockEntity {
	private static final Object2IntMap<Item> freezingMap = new Object2IntLinkedOpenHashMap<>();
	private static final Object2IntMap<TagKey<Item>> tagFreezingMap = new Object2IntLinkedOpenHashMap<>();

	public FreezerBlockEntity(BlockPos pos, BlockState state) {
		super(RNBlockEntities.FREEZER.get(), pos, state, RNRecipes.FREEZING.get());
	}

	@Override
	public Component getDefaultName() {
		return Component.translatable("menu." + RubinatedNether.MODID + ".freezer");
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
		return new FreezerMenu(id, playerInventory, this, this.dataAccess);
	}

	@Override
	public int getBurnDuration(ItemStack fuelStack) {
		if (fuelStack.isEmpty() || !getFreezingMap().containsKey(fuelStack.getItem())) {
			return 0;
		} else {
			return getFreezingMap().getInt(fuelStack.getItem());
		}
	}

	public IItemHandler getItemHandler(Direction side) {
		return new IItemHandler() {
			@Override
			public int getSlots() {
				return 3;
			}

			@Override
			public @NotNull ItemStack getStackInSlot(int slot) {
				if (slot < 0 || slot >= 3) return ItemStack.EMPTY;
				return FreezerBlockEntity.this.getItem(slot);
			}

			@Override
			public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
				if (stack.isEmpty() || slot < 0 || slot >= 3) {
					return stack;
				}

				if (!isSlotAccessibleForInsertion(slot, side)) {
					return stack;
				}

				if (!canPlaceItemThroughFace(slot, stack, side)) {
					return stack;
				}

				ItemStack existing = getStackInSlot(slot);
				int maxStackSize = Math.min(getSlotLimit(slot), stack.getMaxStackSize());

				if (existing.isEmpty()) {
					int toInsert = Math.min(stack.getCount(), maxStackSize);
					if (!simulate) {
						FreezerBlockEntity.this.setItem(slot, stack.copyWithCount(toInsert));
						FreezerBlockEntity.this.setChanged();
					}
					return stack.getCount() > toInsert ? stack.copyWithCount(stack.getCount() - toInsert) : ItemStack.EMPTY;
				}

				if (!ItemStack.isSameItemSameComponents(existing, stack)) {
					return stack;
				}

				int spaceLeft = maxStackSize - existing.getCount();
				if (spaceLeft <= 0) {
					return stack;
				}

				int toInsert = Math.min(stack.getCount(), spaceLeft);
				if (!simulate) {
					existing.grow(toInsert);
					FreezerBlockEntity.this.setChanged();
				}

				return stack.getCount() > toInsert ? stack.copyWithCount(stack.getCount() - toInsert) : ItemStack.EMPTY;
			}

			@Override
			public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
				if (amount <= 0 || slot < 0 || slot >= 3) {
					return ItemStack.EMPTY;
				}

				if (!isSlotAccessibleForExtraction(slot, side)) {
					return ItemStack.EMPTY;
				}

				ItemStack existing = getStackInSlot(slot);
				if (existing.isEmpty()) {
					return ItemStack.EMPTY;
				}

				if (!canTakeItemThroughFace(slot, existing, side)) {
					return ItemStack.EMPTY;
				}

				int toExtract = Math.min(amount, existing.getCount());
				ItemStack extracted = existing.copyWithCount(toExtract);

				if (!simulate) {
					existing.shrink(toExtract);
					if (existing.isEmpty()) {
						FreezerBlockEntity.this.setItem(slot, ItemStack.EMPTY);
					}
					FreezerBlockEntity.this.setChanged();
				}

				return extracted;
			}

			@Override
			public int getSlotLimit(int slot) {
				return 64;
			}

			@Override
			public boolean isItemValid(int slot, @NotNull ItemStack stack) {
				if (slot < 0 || slot >= 3) return false;
				return canPlaceItemThroughFace(slot, stack, side);
			}

			private boolean isSlotAccessibleForInsertion(int slot, Direction direction) {
				if (direction == Direction.UP) {
					return slot == 0; // Input slot only from top
				} else if (direction == Direction.DOWN) {
					return false; // No insertion from bottom
				} else {
					return slot == 1; // Fuel slot only from sides
				}
			}

			private boolean isSlotAccessibleForExtraction(int slot, Direction direction) {
				if (direction == Direction.DOWN) {
					return slot == 2 || slot == 0; // Output and input from bottom
				} else if (direction == Direction.UP) {
					return slot == 0; // Input from top
				} else {
					return slot == 2 || slot == 1; // Output and fuel from sides
				}
			}
		};
	}

	public static Object2IntMap<Item> getFreezingMap() {
		// No need to do expensive iterations if there aren't any registered tags
		if (tagFreezingMap.isEmpty()) return Object2IntMaps.unmodifiable(freezingMap);

		// Make a copy of the items map
		Object2IntMap<Item> allItems = new Object2IntOpenHashMap<>(freezingMap);

		// Iterate the tags and put items into copied map
		tagFreezingMap.object2IntEntrySet().stream()
				.flatMap(entry -> streamTagFuels(entry.getKey(), entry.getIntValue()))
				.forEach(pair -> allItems.put(pair.key(), pair.valueInt()));

		return Object2IntMaps.unmodifiable(allItems);
	}

	private static Stream<ObjectIntPair<Item>> streamTagFuels(TagKey<Item> tag, int freezeTime) {
		return BuiltInRegistries.ITEM.getTag(tag)
				.map(set -> set.stream().map(h -> ObjectIntPair.of(h.value(), freezeTime)))
				.orElseGet(Stream::empty);
	}

	public static void addItemFreezingTime(ItemLike itemProvider, int burnTime) {
		Item item = itemProvider.asItem();
		freezingMap.put(item, burnTime);
	}

	public static void addItemsFreezingTime(ItemLike[] itemProviders, int burnTime) {
		Stream.of(itemProviders).map(ItemLike::asItem).forEach((item) -> freezingMap.put(item, burnTime));
	}

	public static void addItemTagFreezingTime(TagKey<Item> itemTag, int burnTime) {
		tagFreezingMap.put(itemTag, burnTime);
	}

	public static void cleanFreezingTimes() {
		freezingMap.clear();
		tagFreezingMap.clear();
	}

	public static void removeItemFreezingTime(ItemLike itemProvider) {
		Item item = itemProvider.asItem();
		freezingMap.removeInt(item);
	}

	public static void removeItemsFreezingTime(ItemLike[] itemProviders) {
		Stream.of(itemProviders).map(ItemLike::asItem).forEach(freezingMap::removeInt);
	}

	public static void removeItemTagFreezingTime(TagKey<Item> itemTag) {
		tagFreezingMap.removeInt(itemTag);
	}
}