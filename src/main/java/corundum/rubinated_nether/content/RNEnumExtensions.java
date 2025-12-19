package corundum.rubinated_nether.content;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

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

	public static final EnumProxy<Rarity> RUBY_RARITY_ENUM_PROXY = new EnumProxy<>(
			Rarity.class, 444, "rubinated_nether:ruby", ChatFormatting.DARK_RED
	);

	public static final EnumProxy<Gui.HeartType> BRONZE_DISEASED_HEART_PROXY = new EnumProxy<>(
			Gui.HeartType.class,
			ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_full"),
			ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_full_blinking"),
			ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_half"),
			ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_half_blinking"),
			ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_hardcore_full"),
			ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_hardcore_full_blinking"),
			ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_hardcore_half"),
			ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_hardcore_half_blinking")
	);
}
