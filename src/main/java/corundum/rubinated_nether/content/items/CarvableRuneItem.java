package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.client.render.RubinatedTextRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CarvableRuneItem extends Item {

    public CarvableRuneItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        Component carvedName = RuneCarvingHelper.carvedDisplayName(stack);
        return carvedName != null ? carvedName : super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Rubination carving = RuneCarvingHelper.getCarving(stack);
        if (carving == Rubination.EMPTY) return;

        var level = Minecraft.getInstance().level;
        if (level == null) return;

        for (var enchant : carving.getEnchantments(level.registryAccess())) {
            if (enchant == null) continue;
            tooltipComponents.add(RuneInertEnchantmentText.getFullnameInert(enchant.enchantment, enchant.level));
        }
    }

    public static MutableComponent buildCarvedName(String rubinationName) {
        Style baseStyle = Style.EMPTY.withColor(ChatFormatting.WHITE);

        if (!RubinatedTextRenderer.useRubinatedLang()) {
            return Component.literal("Carved Rune of " + rubinationName).withStyle(baseStyle);
        }

        return Component.empty()
                .append(Component.literal("Carved Rune of ").withStyle(baseStyle))
                .append(RubinatedTextRenderer.applyRubin(rubinationName).withStyle(baseStyle));
    }
}