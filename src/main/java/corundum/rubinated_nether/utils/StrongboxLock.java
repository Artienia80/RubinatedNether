package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.BronzeStrongboxBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Side-safe helpers for strongbox passwords on items: a strongbox item's custom name is its password and a
 * Winding Key renamed to the same text is the key that fits. Used by the anvil rules (server and client).
 */
public final class StrongboxLock {
    private StrongboxLock() {}

    public static boolean isStrongbox(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BronzeStrongboxBlock;
    }

    public static boolean isKey(ItemStack stack) {
        return stack.is(RNItems.WINDING_KEY.get());
    }

    /** The custom name of the stack, or null when it has none (or an empty one). */
    @Nullable
    public static String customName(ItemStack stack) {
        Component name = stack.get(DataComponents.CUSTOM_NAME);
        if (name == null || name.getString().isEmpty()) {
            return null;
        }
        return name.getString();
    }

    /** Whether the key fits the strongbox item: any key for one without a password, else a key named like it. */
    public static boolean opens(ItemStack strongbox, ItemStack key) {
        if (!isKey(key)) {
            return false;
        }
        String password = customName(strongbox);
        return password == null || password.equals(customName(key));
    }

    /** A copy of the stack with the given custom name, or without a custom name when {@code name} is null. */
    public static ItemStack renamedCopy(ItemStack stack, @Nullable String name) {
        ItemStack copy = stack.copy();
        if (name == null || name.isBlank()) {
            copy.remove(DataComponents.CUSTOM_NAME);
        } else {
            copy.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        }
        return copy;
    }

    /** What the anvil's name field does for a strongbox in the first slot and the given item in the second. */
    public enum AnvilState {
        /** Not a strongbox, or the right key is in: the anvil works (a strongbox is then renamed together with the key). */
        FREE,
        /** An unnamed strongbox without a key: it cannot be renamed on its own. */
        NEEDS_KEY,
        /** A strongbox with a password and no matching key: the name stays hidden and cannot be edited. */
        LOCKED
    }

    public static AnvilState anvilState(ItemStack left, ItemStack right) {
        if (!isStrongbox(left)) {
            return AnvilState.FREE;
        }
        if (opens(left, right)) {
            return AnvilState.FREE;
        }
        return customName(left) != null ? AnvilState.LOCKED : AnvilState.NEEDS_KEY;
    }
}
