package corundum.rubinated_nether.content;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class RNRarity {
    public static Component formatRarity(String translationKey) {
        return Component.translatable(translationKey).withStyle(ChatFormatting.RED);
    }
}
