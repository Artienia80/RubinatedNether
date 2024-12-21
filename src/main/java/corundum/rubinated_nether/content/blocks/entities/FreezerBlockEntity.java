package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNRecipes;
import corundum.rubinated_nether.content.menu.FreezerMenu;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;

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

	public static Object2IntMap<Item> getFreezingMap() {
		// No need to do expensive iterations if there aren't any registered tags
		if(tagFreezingMap.isEmpty()) return Object2IntMaps.unmodifiable(freezingMap);

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
