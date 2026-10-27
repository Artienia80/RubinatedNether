package corundum.rubinated_nether.content.enchantment.custom;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import java.util.ArrayList;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class ExposureCurseEventHandler {

    @SubscribeEvent
    public static void halveCursedArmor(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof ArmorItem)) return;
        if (!hasExposureCurse(stack)) return;

        for (ItemAttributeModifiers.Entry entry : new ArrayList<>(event.getModifiers())) {
            AttributeModifier modifier = entry.modifier();
            if (!entry.attribute().equals(Attributes.ARMOR)) continue;
            if (modifier.operation() != AttributeModifier.Operation.ADD_VALUE || modifier.amount() <= 0) continue;

            double halved = Math.max(1.0, Math.floor(modifier.amount() * 0.5));
            event.replaceModifier(entry.attribute(), new AttributeModifier(modifier.id(), halved, modifier.operation()), entry.slot());
        }
    }

    private static boolean hasExposureCurse(ItemStack stack) {
        ItemEnchantments enchantments = stack.get(DataComponents.ENCHANTMENTS);
        if (enchantments == null || enchantments.isEmpty()) return false;

        for (Holder<Enchantment> holder : enchantments.keySet()) {
            if (holder.is(RNEnchantments.EXPOSURE_CURSE)) return true;
        }
        return false;
    }
}