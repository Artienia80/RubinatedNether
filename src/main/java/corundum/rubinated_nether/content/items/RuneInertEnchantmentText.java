package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.client.render.RubinatedTextRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
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
        boolean isRubinatedCurse = enchantment.is(RUBINATED_CURSES);
        boolean isCurse = isRubinatedCurse || enchantment.is(EnchantmentTags.CURSE);

        TextColor color = isCurse ? INERT_CURSE_COLOR : TextColor.fromLegacyFormat(ChatFormatting.DARK_GRAY);
        Style baseStyle = Style.EMPTY.withColor(color).withItalic(true);

        if (isRubinatedCurse) {
            String fullText = ench.description().copy().getString();
            String prefix = "Curse of ";
            String curseName = fullText.startsWith(prefix) ? fullText.substring(prefix.length()) : fullText;

            MutableComponent result;
            if (RubinatedTextRenderer.useRubinatedLang()) {
                result = Component.empty()
                        .append(Component.literal(prefix).withStyle(baseStyle))
                        .append(RubinatedTextRenderer.applyRubin(curseName).withStyle(baseStyle));
            } else {
                result = Component.empty()
                        .append(Component.literal(prefix).withStyle(baseStyle))
                        .append(Component.literal(curseName).withStyle(baseStyle));
            }

            if (level != 1 || ench.getMaxLevel() != 1) {
                result.append(CommonComponents.SPACE)
                        .append(Component.translatable("enchantment.level." + level).withStyle(baseStyle));
            }

            return result;
        }

        MutableComponent mutablecomponent = ench.description().copy();
        ComponentUtils.mergeStyles(mutablecomponent, baseStyle);

        if (level != 1 || ench.getMaxLevel() != 1) {
            mutablecomponent.append(CommonComponents.SPACE)
                    .append(Component.translatable("enchantment.level." + level));
        }

        return mutablecomponent;
    }
}