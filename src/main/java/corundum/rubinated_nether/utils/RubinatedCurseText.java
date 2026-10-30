package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.content.RNTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Display name for Rubinated curses (dark red, optional Rubin font). Replaces the former {@code EnchantmentMixin}.
 * <p>
 * Our own code calls {@link #fullName} instead of {@link Enchantment#getFullname}. Vanilla tooltip lines are
 * rewritten in {@code RubinatedTextRenderer#onItemTooltip}. Side-safe like {@link RubinatedText}.
 */
public final class RubinatedCurseText {
    private static final String CURSE_PREFIX = "Curse of ";

    private RubinatedCurseText() {}

    /** Same as {@link Enchantment#getFullname}, but Rubinated curses get the Rubinated styling. */
    public static Component fullName(Holder<Enchantment> enchantment, int level) {
        if (!enchantment.is(RNTags.Enchantments.RUBINATED_CURSES)) {
            return Enchantment.getFullname(enchantment, level);
        }

        Enchantment ench = enchantment.value();
        Style baseStyle = Style.EMPTY.withColor(ChatFormatting.DARK_RED);
        String fullText = ench.description().getString();

        MutableComponent result;
        if (RubinatedText.useRubinatedLang() && fullText.startsWith(CURSE_PREFIX)) {
            result = Component.empty()
                    .append(Component.literal(CURSE_PREFIX).withStyle(baseStyle))
                    .append(RubinatedText.applyRubin(fullText.substring(CURSE_PREFIX.length())).withStyle(baseStyle));
        } else {
            result = ench.description().copy().withStyle(baseStyle);
        }

        if (level != 1 || ench.getMaxLevel() != 1) {
            result.append(CommonComponents.SPACE)
                    .append(Component.translatable("enchantment.level." + level).withStyle(baseStyle));
        }

        return result;
    }
}
