package corundum.rubinated_nether.content.items;

import net.minecraft.network.chat.Component;
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
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        RuneCarvingHelper.appendCarvingTooltip(stack, tooltipComponents);
    }
}