package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.blocks.RunestoneBlock;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.RuneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.jetbrains.annotations.Nullable;

public class RunestoneBlockEntity extends BlockEntity implements ContainerSingleItem.BlockContainerSingleItem, WorldlyContainer {
	private static final int[] SLOTS = new int[]{0};
	private static final int[] NO_SLOTS = new int[0];

	private ItemStack item;

	public RunestoneBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.RUNESTONE.get(), pos, blockState);
		this.item = ItemStack.EMPTY;
	}

	public static boolean isValidRune(ItemStack stack) {
		return stack.is(RNTags.Items.RUNES) && stack.getItem() instanceof RuneItem;
	}

	private static Rubination rubinationOf(ItemStack stack) {
		return stack.getItem() instanceof RuneItem runeItem ? runeItem.getRubination() : Rubination.EMPTY;
	}

	private boolean isLowerHalf() {
		var state = this.getBlockState();
		return !state.hasProperty(RunestoneBlock.HALF) || state.getValue(RunestoneBlock.HALF) == DoubleBlockHalf.LOWER;
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains("RuneItem"))
			this.item = ItemStack.parse(registries, tag.getCompound("RuneItem")).orElse(ItemStack.EMPTY);
		else
			this.item = ItemStack.EMPTY;
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (!this.getTheItem().isEmpty())
			tag.put("RuneItem", this.getTheItem().save(registries));
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.saveWithoutMetadata(registries);
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public ItemStack getTheItem() {
		return this.item;
	}

	@Override
	public ItemStack splitTheItem(int amount) {
		if (this.item.isEmpty()) return ItemStack.EMPTY;

		ItemStack itemstack = this.item;
		this.item = ItemStack.EMPTY;
		this.onItemChanged();
		return itemstack;
	}

	@Override
	public void setTheItem(ItemStack item) {
		this.item = item;
		this.onItemChanged();
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public BlockEntity getContainerBlockEntity() {
		return this;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot == 0 && this.isLowerHalf() && this.item.isEmpty() && isValidRune(stack);
	}

	@Override
	public boolean canTakeItem(Container target, int slot, ItemStack stack) {
		return target.hasAnyMatching(ItemStack::isEmpty);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return this.isLowerHalf() ? SLOTS : NO_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
		return this.canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return slot == 0 && this.isLowerHalf() && !this.item.isEmpty();
	}

	public void popOutTheItem() {
		if (this.level == null || this.level.isClientSide) return;

		var itemstack = this.item;
		if (itemstack.isEmpty()) return;

		this.item = ItemStack.EMPTY;
		this.setChanged();

		var vec3 = Vec3.atLowerCornerWithOffset(this.getBlockPos(), 0.5F, 1.01, 0.5F)
			.offsetRandom(this.level.random, 0.7F);
		var itementity = new ItemEntity(this.level, vec3.x(), vec3.y(), vec3.z(), itemstack.copy());
		itementity.setDefaultPickUpDelay();
		this.level.addFreshEntity(itementity);
	}

	private void onItemChanged() {
		this.setChanged();

		if (this.level == null || this.level.isClientSide || !this.isLowerHalf()) return;

		var state = this.getBlockState();
		if (!state.is(RNBlocks.RUNESTONE.get())) return;

		if (state.getBlock() instanceof RunestoneBlock runestoneBlock)
			runestoneBlock.syncRuneValue(this.level, this.worldPosition, rubinationOf(this.item));

		this.level.sendBlockUpdated(this.worldPosition, state, this.getBlockState(), UPDATE_ALL);
	}

	private static final int UPDATE_ALL = 3;
}
