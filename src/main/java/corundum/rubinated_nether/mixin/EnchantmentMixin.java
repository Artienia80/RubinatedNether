package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.utils.RubinatedText;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "getFullname", at = @At("HEAD"), cancellable = true)
    private static void rubinated_nether$modifyCurseColor(Holder<Enchantment> enchantment, int level, CallbackInfoReturnable<Component> cir) {
        if (!enchantment.is(RNTags.Enchantments.RUBINATED_CURSES)) {
            return;
        }

        Enchantment ench = enchantment.value();
        Style baseStyle = Style.EMPTY.withColor(ChatFormatting.DARK_RED);
        String fullText = ench.description().getString();
        String prefix = "Curse of ";

        MutableComponent result;
        if (RubinatedText.useRubinatedLang() && fullText.startsWith(prefix)) {
            result = Component.empty()
                    .append(Component.literal(prefix).withStyle(baseStyle))
                    .append(RubinatedText.applyRubin(fullText.substring(prefix.length())).withStyle(baseStyle));
        } else {
            result = ench.description().copy().withStyle(baseStyle);
        }

        if (level != 1 || ench.getMaxLevel() != 1) {
            result.append(CommonComponents.SPACE)
                    .append(Component.translatable("enchantment.level." + level).withStyle(baseStyle));
        }

        cir.setReturnValue(result);
    }
}
