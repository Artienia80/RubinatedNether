package corundum.rubinated_nether.utils;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.BooleanSupplier;

/**
 * Side-safe half of the Rubinated text system.
 * <p>
 * This class (and everything it imports) exists on both the client and the dedicated server, so it may be
 * referenced from common code and from common mixins.
 * Anything that needs client classes lives in {@code client.render.RubinatedTextRenderer}, which installs
 * a hook here via {@link #setClientCheck(BooleanSupplier)}. On a dedicated server no hook is installed,
 * so {@link #useRubinatedLang()} simply returns false.
 */
public final class RubinatedText {
    public static final ResourceLocation RUBIN_FONT = RubinatedNether.id("rubin");

    private static volatile BooleanSupplier clientCheck = null;

    private RubinatedText() {}

    /** Called by the client only. The supplier answers "should Rubinated lang be shown right now?" */
    public static void setClientCheck(BooleanSupplier check) {
        clientCheck = check;
    }

    public static boolean useRubinatedLang() {
        if (!RNConfig.rubinatedLanguage) return false;
        BooleanSupplier check = clientCheck;
        if (check == null) return false; // dedicated server / client not set up yet
        try {
            return check.getAsBoolean();
        } catch (Exception e) {
            return true;
        }
    }

    public static String applySubs(String text) {
        return text
                .replaceAll("(?i)(?<=\\s|^)the(?=\\s|$)", "\u00F0")
                .replaceAll("(?i)(?<=\\s|^)and(?=\\s|$)", "&")
                .replace("TH", "\u00DE")
                .replace("Th", "\u00DE")
                .replace("th", "\u00FE");
    }

    public static MutableComponent applyRubin(Component component) {
        MutableComponent result = Component.empty();
        component.visit((style, text) -> {
            result.append(Component.literal(applySubs(text)).withStyle(style.withFont(RUBIN_FONT)));
            return Optional.empty();
        }, Style.EMPTY);
        return result;
    }

    public static MutableComponent applyRubin(String text) {
        return Component.literal(applySubs(text)).withStyle(Style.EMPTY.withFont(RUBIN_FONT));
    }
}
