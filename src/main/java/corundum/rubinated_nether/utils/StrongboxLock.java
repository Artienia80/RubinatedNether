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

public final class StrongboxLock {
    public static final String SEALED_NAME_KEY = "container.rubinated_nether.sealed_strongbox";

    private StrongboxLock() {}

    public static MutableComponent sealedName(Component strongboxName) {
        return Component.translatable(SEALED_NAME_KEY, "", strongboxName);
    }

    public static MutableComponent sealedName(ItemStack stack) {
        return sealedName(Component.translatable(stack.getItem().getDescriptionId(stack)));
    }

    public static MutableComponent sealedTitle(String password, Component strongboxName) {
        return Component.translatable(SEALED_NAME_KEY, password, strongboxName);
    }

    public static boolean isStrongbox(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BronzeStrongboxBlock;
    }

    public static boolean isKey(ItemStack stack) {
        return stack.is(RNItems.WINDING_KEY.get());
    }

    @Nullable
    public static String password(ItemStack stack) {
        String password = stack.get(RNDataComponents.STRONGBOX_PASSWORD.get());
        return password == null || password.isEmpty() ? null : password;
    }

    @Nullable
    public static String keyName(ItemStack key) {
        Component name = key.get(DataComponents.CUSTOM_NAME);
        if (name == null || name.getString().isEmpty()) {
            return null;
        }
        return name.getString();
    }

    public static String editableName(ItemStack stack) {
        String password = password(stack);
        return password != null ? password : stack.getHoverName().getString();
    }

    public static boolean opens(ItemStack strongbox, ItemStack key) {
        if (!isKey(key)) {
            return false;
        }
        String password = password(strongbox);
        return password == null || password.equals(keyName(key));
    }

    public static ItemStack withPassword(ItemStack stack, @Nullable String password) {
        ItemStack copy = stack.copy();
        if (password == null || password.isBlank()) {
            copy.remove(RNDataComponents.STRONGBOX_PASSWORD.get());
            copy.remove(DataComponents.CUSTOM_NAME);
        } else {
            copy.set(RNDataComponents.STRONGBOX_PASSWORD.get(), password);
            copy.set(DataComponents.CUSTOM_NAME, sealedName(stack));
        }
        return copy;
    }

    public static ItemStack renamedKey(ItemStack key, @Nullable String name) {
        ItemStack copy = key.copy();
        if (name == null || name.isBlank()) {
            copy.remove(DataComponents.CUSTOM_NAME);
        } else {
            copy.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        }
        return copy;
    }

    public enum AnvilState {
        FREE,
        NEEDS_KEY,
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
