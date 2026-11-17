package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.RNEntityCreator;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public class PetrifiedChestBoat extends ChestBoat {
	public PetrifiedChestBoat(EntityType<? extends Boat> type, Level level) {
		super(type, level);
	}

	public PetrifiedChestBoat(Level level, double x, double y, double z) {
		this(RNEntityCreator.PETRIFIED_CHEST_BOAT.get(), level);
		this.setPos(x, y, z);
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	@Override
	public Item getDropItem() {
		return RNItems.PETRIFIED_CHEST_BOAT.get();
	}

	@Override
	public boolean canBoatInFluid(FluidState state) {
		if (RNConfig.petrifiedBoatLavaMode) {
			return state.is(FluidTags.LAVA);
		}
		return super.canBoatInFluid(state);
	}

	@Override
	public boolean fireImmune() {
		return RNConfig.petrifiedBoatLavaMode || super.fireImmune();
	}
}
