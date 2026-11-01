package corundum.rubinated_nether.client.render;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.BronzeStrongboxBlock;
import corundum.rubinated_nether.utils.RubinatedText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * A strongbox item carrying a password (its custom name) hides that name: it is drawn like {@code §k} text, but in
 * the Rubin font, and the words keep being swapped around. Holding a Winding Key renamed to the password anywhere in
 * the inventory shows the real name again. Purely cosmetic and client side; the item's name is untouched.
 */
@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT)
public final class StrongboxPasswordText {
    private static final String GLYPHS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /** How long (ms) a scramble lasts before the letters are re-rolled and the words swapped again. */
    private static final long SCRAMBLE_MILLIS = 120L;

    private StrongboxPasswordText() {}

    /** True for a strongbox item with a password that the local player cannot read right now. */
    public static boolean shouldObscure(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof BronzeStrongboxBlock)) {
            return false;
        }
        Component name = stack.get(DataComponents.CUSTOM_NAME);
        if (name == null || name.getString().isEmpty()) {
            return false;
        }
        return !hasMatchingKey(name.getString());
    }

    private static boolean hasMatchingKey(String password) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        return player.getInventory().contains(stack -> {
            if (!stack.is(RNItems.WINDING_KEY.get())) {
                return false;
            }
            Component keyName = stack.get(DataComponents.CUSTOM_NAME);
            return keyName != null && keyName.getString().equals(password);
        });
    }

    /** The strongbox's name as scrambled Rubin letters, with the same word lengths as the real name. */
    public static MutableComponent obscuredName(ItemStack stack) {
        Component name = stack.get(DataComponents.CUSTOM_NAME);
        String password = name == null ? "" : name.getString();

        long slot = Util.getMillis() / SCRAMBLE_MILLIS;
        String[] words = password.trim().split("\\s+");
        RandomSource random = RandomSource.create(slot * 7919L + password.length());

        List<String> scrambled = new ArrayList<>();
        for (String word : words) {
            StringBuilder builder = new StringBuilder(word.length());
            for (int i = 0; i < word.length(); i++) {
                builder.append(GLYPHS.charAt(random.nextInt(GLYPHS.length())));
            }
            scrambled.add(builder.toString());
        }
        // Swap the words around, too, so even the shape of the name keeps changing.
        for (int i = scrambled.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            String swap = scrambled.get(i);
            scrambled.set(i, scrambled.get(j));
            scrambled.set(j, swap);
        }

        return Component.literal(String.join(" ", scrambled))
                .withStyle(Style.EMPTY.withFont(RubinatedText.RUBIN_FONT));
    }

    /** The full name line: scrambled, italic like any custom name, in the item's rarity colour. */
    public static MutableComponent styledName(ItemStack stack) {
        return Component.empty()
                .append(obscuredName(stack))
                .withStyle(stack.getRarity().getStyleModifier())
                .withStyle(ChatFormatting.ITALIC);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        List<Component> lines = event.getToolTip();
        if (!lines.isEmpty() && shouldObscure(event.getItemStack())) {
            lines.set(0, styledName(event.getItemStack()));
        }
    }
}
