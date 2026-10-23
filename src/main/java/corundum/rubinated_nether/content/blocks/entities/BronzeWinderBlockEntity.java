package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BronzeWinderBlockEntity extends BlockEntity {

	private long lastInput = -1L;
	private boolean externallyLocked = false;

	public BronzeWinderBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.BRONZE_WINDER.get(), pos, blockState);
	}

	public long getLastInput() {
		return lastInput;
	}

	public void setLastInput(long lastInput) {
		this.lastInput = lastInput;
		this.setChanged();
	}

	/** True while an external redstone signal is holding this winder at its strength. */
	public boolean isExternallyLocked() {
		return externallyLocked;
	}

	public void setExternallyLocked(boolean externallyLocked) {
		if (this.externallyLocked != externallyLocked) {
			this.externallyLocked = externallyLocked;
			this.setChanged();
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putLong("last_input", lastInput);
		tag.putBoolean("externally_locked", externallyLocked);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		lastInput = tag.contains("last_input") ? tag.getLong("last_input") : -1L;
		externallyLocked = tag.getBoolean("externally_locked");
	}
}
