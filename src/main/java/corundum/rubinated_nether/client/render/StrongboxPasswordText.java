package corundum.rubinated_nether.client.render;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.utils.RubinatedText;
import corundum.rubinated_nether.utils.StrongboxLock;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.ArrayList;
import java.util.List;


@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT)
public final class StrongboxPasswordText {
    private static final String GLYPHS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final long SCRAMBLE_MILLIS = 120L;

    private StrongboxPasswordText() {}

    public static boolean isSealed(ItemStack stack) {
        return StrongboxLock.isStrongbox(stack) && StrongboxLock.password(stack) != null;
    }

    public static MutableComponent displayName(String password) {
        return hasMatchingKey(password) ? Component.literal(password) : obscured(password);
    }

    public static Component screenTitle(Component title) {
        if (title.getContents() instanceof TranslatableContents contents
                && contents.getKey().equals(StrongboxLock.SEALED_NAME_KEY)
                && contents.getArgs().length > 0
                && contents.getArgs()[0] instanceof String password) {
            return displayName(password);
        }
        return title;
    }

    public static boolean hasMatchingKey(String password) {
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

    public static MutableComponent obscuredName(ItemStack stack) {
        String password = StrongboxLock.password(stack);
        return obscured(password == null ? "" : password);
    }

    public static MutableComponent obscured(String password) {

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
        for (int i = scrambled.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            String swap = scrambled.get(i);
            scrambled.set(i, scrambled.get(j));
            scrambled.set(j, swap);
        }

        return Component.literal(String.join(" ", scrambled))
                .withStyle(Style.EMPTY.withFont(RubinatedText.RUBIN_FONT));
    }

    public static MutableComponent styledName(ItemStack stack) {
        return Component.empty()
                .append(displayName(StrongboxLock.password(stack)))
                .withStyle(stack.getRarity().getStyleModifier())
                .withStyle(ChatFormatting.ITALIC);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        List<Component> lines = event.getToolTip();
        if (!lines.isEmpty() && isSealed(event.getItemStack())) {
            lines.set(0, styledName(event.getItemStack()));
        }
    }
}
