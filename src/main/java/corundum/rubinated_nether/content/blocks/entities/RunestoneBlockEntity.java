package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;

public class RunestoneBlockEntity extends BlockEntity implements ContainerSingleItem.BlockContainerSingleItem {
	private ItemStack item;

	public RunestoneBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.RUNESTONE.get(), pos, blockState);
		this.item = ItemStack.EMPTY;
	}

	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains("RuneItem"))
			this.item = ItemStack.parse(registries, tag.getCompound("RuneItem")).orElse(ItemStack.EMPTY);
		else
			this.item = ItemStack.EMPTY;

	}

	public void popOutTheItem() {
		if (this.level == null || this.level.isClientSide) return;

		var itemstack = this.getTheItem();
		if (itemstack.isEmpty()) return;

		this.removeTheItem();
		var vec3 = Vec3.atLowerCornerWithOffset(this.getBlockPos(), 0.5F, 1.01, 0.5F)
			.offsetRandom(this.level.random, 0.7F);
		var itemstackCopy = itemstack.copy();
		var itementity = new ItemEntity(this.level, vec3.x(), vec3.y(), vec3.z(), itemstackCopy);
		itementity.setDefaultPickUpDelay();
		this.level.addFreshEntity(itementity);
	}

	public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (!this.getTheItem().isEmpty())
			tag.put("RuneItem", this.getTheItem().save(registries));
	}

	public ItemStack getTheItem() {
		return this.item;
	}

	public ItemStack splitTheItem(int amount) {
		ItemStack itemstack = this.item;
		this.setTheItem(ItemStack.EMPTY);
		return itemstack;
	}

	public void setTheItem(ItemStack item) {
		this.item = item;
	}

	public int getMaxStackSize() {
		return 1;
	}

	public BlockEntity getContainerBlockEntity() {
		return this;
	}

	public boolean canPlaceItem(int slot, ItemStack stack) {
		return stack.is(RNTags.Items.RUNES) && this.getItem(slot).isEmpty();
	}

	public boolean canTakeItem(Container target, int slot, ItemStack stack) {
		return target.hasAnyMatching(ItemStack::isEmpty);
	}
}
