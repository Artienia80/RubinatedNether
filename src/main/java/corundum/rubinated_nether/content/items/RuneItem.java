package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RuneItem extends Item {
    private final Rubination rubination;

    public RuneItem(Properties properties, Rubination rubination, String tooltipKey) {
        super(properties);
        this.rubination = rubination;
    }

    public Rubination getRubination() {
        return this.rubination;
    }
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.rubinated_nether.rune." + rubination.name().toLowerCase())
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

    }


}
