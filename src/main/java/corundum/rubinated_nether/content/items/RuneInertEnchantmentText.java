package corundum.rubinated_nether.content.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class RuneInertEnchantmentText {

    private static final TagKey<Enchantment> RUBINATED_CURSES = TagKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "rubinated_curses")
    );

    private static final TextColor INERT_CURSE_COLOR = TextColor.fromRgb(0x802A2A);

    private RuneInertEnchantmentText() {}

    public static Component getFullnameInert(Holder<Enchantment> enchantment, int level) {
        Enchantment ench = enchantment.value();
        MutableComponent mutablecomponent = ench.description().copy();

        boolean isCurse = enchantment.is(RUBINATED_CURSES) || enchantment.is(EnchantmentTags.CURSE);
        TextColor color = isCurse
                ? INERT_CURSE_COLOR
                : TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY);

        ComponentUtils.mergeStyles(mutablecomponent, Style.EMPTY.withColor(color).withItalic(true));

        if (level != 1 || ench.getMaxLevel() != 1) {
            mutablecomponent.append(CommonComponents.SPACE)
                    .append(Component.translatable("enchantment.level." + level));
        }

        return mutablecomponent;
    }
}