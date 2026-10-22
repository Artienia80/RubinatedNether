package corundum.rubinated_nether.content.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

public final class ToolStatsTooltip {
    private ToolStatsTooltip() {}

    public static String fmt(double v) {
        String s = String.format(Locale.ROOT, "%.2f", v);
        if (s.contains(".")) {
            s = s.replaceAll("0+$", "").replaceAll("\\.$", "");
        }
        return s;
    }

    private static void line(List<Component> tooltip, String value, String label) {
        tooltip.add(Component.literal(" " + value + " " + label).withStyle(ChatFormatting.DARK_GREEN));
    }

    public static void breaking(List<Component> tooltip, double strength, double perSecond) {
        line(tooltip, fmt(strength), "Breaking Strength");
        line(tooltip, fmt(perSecond) + "/s", "Breaking Acceleration");
    }

    public static void shattering(List<Component> tooltip, double strengthPct, double spreadPct) {
        line(tooltip, fmt(strengthPct) + "%", "Shattering Strength");
        line(tooltip, fmt(spreadPct) + "%", "Shattering Spread");
    }
}