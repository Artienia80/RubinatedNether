package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

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
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        for(var enchant : this.getRubination().getEnchantments(Minecraft.getInstance().level.registryAccess())) {
            stack.enchant(enchant.enchantment, enchant.level);
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
