package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.client.render.RubinatedTextRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    private static final TagKey<Enchantment> RUBINATED_CURSES = TagKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "rubinated_curses")
    );

    @Inject(method = "getFullname", at = @At("HEAD"), cancellable = true)
    private static void modifyCurseColor(Holder<Enchantment> enchantment, int level, CallbackInfoReturnable<Component> cir) {
        Enchantment ench = enchantment.value();
        boolean isRubinatedCurse = enchantment.is(RUBINATED_CURSES);

        if (isRubinatedCurse) {
            Style baseStyle = Style.EMPTY.withColor(ChatFormatting.DARK_RED);

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

            cir.setReturnValue(result);
            return;
        }

        MutableComponent mutablecomponent = ench.description().copy();
        if (enchantment.is(EnchantmentTags.CURSE)) {
            ComponentUtils.mergeStyles(mutablecomponent, Style.EMPTY.withColor(ChatFormatting.RED));
        } else {
            ComponentUtils.mergeStyles(mutablecomponent, Style.EMPTY.withColor(ChatFormatting.GRAY));
        }

        if (level != 1 || ench.getMaxLevel() != 1) {
            mutablecomponent.append(CommonComponents.SPACE)
                    .append(Component.translatable("enchantment.level." + level));
        }

        cir.setReturnValue(mutablecomponent);
    }
}