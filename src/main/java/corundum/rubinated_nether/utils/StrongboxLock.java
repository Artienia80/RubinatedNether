package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.BronzeStrongboxBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Side-safe helpers for strongbox passwords on items. The password lives in its own component; the strongbox's
 * official name is always "Sealed Strongbox", so no other mod ever sees the password. A Winding Key renamed to the
 * password is the key that fits. Used by the anvil rules (server and client).
 */
public final class StrongboxLock {
    public static final String SEALED_NAME_KEY = "container.rubinated_nether.sealed_strongbox";

    private StrongboxLock() {}

    public static MutableComponent sealedName() {
        return Component.translatable(SEALED_NAME_KEY);
    }

    public static MutableComponent sealedTitle(String password) {
        return Component.translatable(SEALED_NAME_KEY, password);
    }

    public static boolean isStrongbox(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BronzeStrongboxBlock;
    }

    public static boolean isKey(ItemStack stack) {
        return stack.is(RNItems.WINDING_KEY.get());
    }

    /** The password of the strongbox item, or null when it has none. */
    @Nullable
    public static String password(ItemStack stack) {
        String password = stack.get(RNDataComponents.STRONGBOX_PASSWORD.get());
        return password == null || password.isEmpty() ? null : password;
    }

    /** The custom name of a key, or null when it has none (or an empty one). */
    @Nullable
    public static String keyName(ItemStack key) {
        Component name = key.get(DataComponents.CUSTOM_NAME);
        if (name == null || name.getString().isEmpty()) {
            return null;
        }
        return name.getString();
    }

    /** The name the anvil's text field starts with: the password if there is one, else the item's own name. */
    public static String editableName(ItemStack stack) {
        String password = password(stack);
        return password != null ? password : stack.getHoverName().getString();
    }

    /** Whether the key fits the strongbox item: any key for one without a password, else a key named like it. */
    public static boolean opens(ItemStack strongbox, ItemStack key) {
        if (!isKey(key)) {
            return false;
        }
        String password = password(strongbox);
        return password == null || password.equals(keyName(key));
    }

    /** A copy of the strongbox with the given password and the sealed name, or with neither when {@code password} is null. */
    public static ItemStack withPassword(ItemStack stack, @Nullable String password) {
        ItemStack copy = stack.copy();
        if (password == null || password.isBlank()) {
            copy.remove(RNDataComponents.STRONGBOX_PASSWORD.get());
            copy.remove(DataComponents.CUSTOM_NAME);
        } else {
            copy.set(RNDataComponents.STRONGBOX_PASSWORD.get(), password);
            copy.set(DataComponents.CUSTOM_NAME, sealedName());
        }
        return copy;
    }

    /** A copy of the key with the given custom name, or without a custom name when {@code name} is null. */
    public static ItemStack renamedKey(ItemStack key, @Nullable String name) {
        ItemStack copy = key.copy();
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
        return password(left) != null ? AnvilState.LOCKED : AnvilState.NEEDS_KEY;
    }
}
