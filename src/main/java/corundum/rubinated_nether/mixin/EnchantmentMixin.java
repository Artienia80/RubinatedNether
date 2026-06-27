package corundum.rubinated_nether.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Unique
    private static final TagKey<Enchantment> RUBINATED_CURSES = TagKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "rubinated_curses")
    );

    @Inject(method = "getFullname", at = @At("TAIL"), cancellable = true)
    private static void modifyCurseColor(Holder<Enchantment> enchantment, int level, CallbackInfoReturnable<Component> cir) {
        Enchantment ench = enchantment.value();
        MutableComponent mutablecomponent = ench.description().copy();

        if (enchantment.is(RUBINATED_CURSES)) {
            ComponentUtils.mergeStyles(mutablecomponent, Style.EMPTY.withColor(ChatFormatting.DARK_RED));
        }

        cir.setReturnValue(mutablecomponent);
    }
}