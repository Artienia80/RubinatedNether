package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNBannerPatterns;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class RuneItem extends BannerPatternItem {
	private final Rubination rubination;
	private final RNBannerPatterns.BannerPatternEntry bannerPattern;

	public RuneItem(Rubination rubination, String tooltipKey, RNBannerPatterns.BannerPatternEntry bannerPattern, Properties properties) {
		super(bannerPattern.tag(), properties);
		this.rubination = rubination;
		this.bannerPattern = bannerPattern;
	}

	public Rubination getRubination() {
		return this.rubination;
	}

	public RNBannerPatterns.BannerPatternEntry getBannerPatternEntry() {
		return this.bannerPattern;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	@Override
	public void appendHoverText(
			ItemStack stack,
			Item.TooltipContext context,
			List<Component> tooltipComponents,
			TooltipFlag tooltipFlag
	) {
		Component patternDesc = Component.translatable(this.getDescriptionId() + ".desc")
				.withStyle(style -> style
						.withColor(net.minecraft.ChatFormatting.DARK_GRAY)
						.withItalic(true));
		tooltipComponents.add(patternDesc);

		var level = Minecraft.getInstance().level;
		if (level == null) return;

		for (var enchant : this.getRubination().getEnchantments(level.registryAccess())) {
			if (enchant == null) continue;

			tooltipComponents.add(Enchantment.getFullname(enchant.enchantment, enchant.level));
		}
	}
}