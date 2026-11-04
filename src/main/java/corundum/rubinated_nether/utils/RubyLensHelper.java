package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.compat.CuriosCompat;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.fml.ModList;

public class RubyLensHelper {
	public static final String CURIOS_ID = "curios";

	public static boolean isCuriosLoaded() {
		return ModList.get() != null && ModList.get().isLoaded(CURIOS_ID);
	}

	public static boolean isWearingInHead(LivingEntity entity) {
		return entity.getItemBySlot(EquipmentSlot.HEAD).is(RNItems.RUBY_LENS.get());
	}

	public static boolean isWearing(LivingEntity entity) {
		if (entity == null) return false;
		if (isWearingInHead(entity)) return true;
		return isCuriosLoaded() && CuriosCompat.hasLens(entity);
	}
}
