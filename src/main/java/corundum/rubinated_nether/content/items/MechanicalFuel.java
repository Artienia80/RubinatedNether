package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public final class MechanicalFuel {
    public static final int DROPLETS_PER_NUGGET = 9;
    public static final int DROPLETS_PER_MOLTEN_RUBY = DROPLETS_PER_NUGGET * 9;
    public static final int DROPLETS_PER_MOLTEN_RUBY_BLOCK = DROPLETS_PER_MOLTEN_RUBY * 9;
    public static final int TICKS_PER_DROPLET = 20;

    private static final int BAR_COLOR = 0xE8254B;
    private static final int FALLBACK_STACK_SIZE = 64;

    private MechanicalFuel() {}

    public static int fuelValue(Item item) {
        if (item == RNItems.MOLTEN_RUBY_NUGGET.get()) return DROPLETS_PER_NUGGET;
        if (item == RNItems.MOLTEN_RUBY.get()) return DROPLETS_PER_MOLTEN_RUBY;
        if (item == RNBlocks.MOLTEN_RUBY_BLOCK.asItem()) return DROPLETS_PER_MOLTEN_RUBY_BLOCK;
        return 0;
    }

    public static MechanicalFuelData data(ItemStack tool) {
        return tool.getOrDefault(RNDataComponents.MECHANICAL_FUEL, MechanicalFuelData.EMPTY);
    }

    public static int totalDroplets(ItemStack tool) {
        MechanicalFuelData data = data(tool);
        return data.droplets() + data.count() * fuelValue(data.itemType());
    }

    public static boolean hasFuel(ItemStack tool) {
        return totalDroplets(tool) > 0;
    }

    public static boolean burn(ItemStack tool) {
        MechanicalFuelData data = data(tool);
        Item item = data.itemType();
        int count = data.count();
        int droplets = data.droplets();

        if (droplets <= 0) {
            int value = fuelValue(item);
            if (count <= 0 || value <= 0) return false;
            count--;
            droplets = value;
        }

        tool.set(RNDataComponents.MECHANICAL_FUEL, MechanicalFuelData.of(item, count, droplets - 1));
        return true;
    }

    public static int insert(ItemStack tool, ItemStack source) {
        if (source.isEmpty() || fuelValue(source.getItem()) <= 0) return 0;

        MechanicalFuelData data = data(tool);
        Item item = source.getItem();
        if (data.count() > 0 && data.itemType() != item) return 0;

        int room = source.getMaxStackSize() - data.count();
        int moved = Math.min(room, source.getCount());
        if (moved <= 0) return 0;

        source.shrink(moved);
        tool.set(RNDataComponents.MECHANICAL_FUEL, MechanicalFuelData.of(item, data.count() + moved, data.droplets()));
        return moved;
    }

    public static ItemStack extract(ItemStack tool) {
        MechanicalFuelData data = data(tool);
        if (data.count() <= 0) return ItemStack.EMPTY;

        ItemStack out = new ItemStack(data.itemType(), data.count());
        tool.set(RNDataComponents.MECHANICAL_FUEL, MechanicalFuelData.of(Items.AIR, 0, data.droplets()));
        return out;
    }

    public static boolean onStackedOnOther(ItemStack tool, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) return false;

        ItemStack target = slot.getItem();
        if (target.isEmpty()) {
            ItemStack out = extract(tool);
            if (!out.isEmpty()) {
                playRemove(player);
                ItemStack leftover = slot.safeInsert(out);
                if (!leftover.isEmpty()) {
                    insert(tool, leftover);
                }
            }
            return true;
        }

        if (fuelValue(target.getItem()) <= 0 || !slot.mayPickup(player)) return false;

        if (insert(tool, target) > 0) {
            playInsert(player);
            slot.setChanged();
        }
        return true;
    }

    public static boolean onOtherStackedOnMe(ItemStack tool, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

        if (other.isEmpty()) {
            ItemStack out = extract(tool);
            if (!out.isEmpty()) {
                playRemove(player);
                access.set(out);
            }
            return true;
        }

        if (fuelValue(other.getItem()) <= 0) return false;

        if (insert(tool, other) > 0) {
            playInsert(player);
        }
        return true;
    }

    public static void appendTooltip(ItemStack tool, List<Component> tooltip) {
        int total = totalDroplets(tool);
        if (total <= 0) {
            tooltip.add(Component.literal("Fuel: empty").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        tooltip.add(Component.literal("Fuel: " + (total / 60) + "m " + (total % 60) + "s").withStyle(ChatFormatting.RED));
    }

    public static boolean isBarVisible(ItemStack tool) {
        return totalDroplets(tool) > 0;
    }

    public static int getBarWidth(ItemStack tool) {
        MechanicalFuelData data = data(tool);
        Item item = data.count() > 0 ? data.itemType() : RNItems.MOLTEN_RUBY_NUGGET.get();
        int stackSize = data.count() > 0 ? item.getDefaultMaxStackSize() : FALLBACK_STACK_SIZE;
        int capacity = Math.max(1, fuelValue(item) * stackSize);
        return Mth.clamp(Math.round(13.0F * totalDroplets(tool) / capacity), 1, 13);
    }

    public static int getBarColor(ItemStack tool) {
        return BAR_COLOR;
    }

    private static void playInsert(Player player) {
        player.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playRemove(Player player) {
        player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
    }
}