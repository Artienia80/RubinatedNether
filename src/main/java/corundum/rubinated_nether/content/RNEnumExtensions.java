package corundum.rubinated_nether.content;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.function.Supplier;

public class RNEnumExtensions {
	public static Object freezingSearchIcon(int idx, Class<?> type) {
		return type.cast(switch (idx) {
			case 0 -> (Supplier<List<ItemStack>>) () -> List.of(new ItemStack(Items.COMPASS));
			default -> throw new IllegalArgumentException("Unexpected parameter index: " + idx);
		});
	}

	public static Object freezingMiscIcon(int idx, Class<?> type) {
		return type.cast(switch (idx) {
			case 0 -> (Supplier<List<ItemStack>>) () -> List.of(new ItemStack(Blocks.ICE));
			default -> throw new IllegalArgumentException("Unexpected parameter index: " + idx);
		});
	}
}
