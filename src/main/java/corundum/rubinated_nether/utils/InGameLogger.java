package corundum.rubinated_nether.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLLoader;

public class InGameLogger {

    public static void info(String message) {
        if(FMLLoader.isProduction()) return;

        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.sendSystemMessage(
                Component.literal("INFO: " + message).withStyle(ChatFormatting.BLUE)
        );
    }

    public static void warn(String message) {
        if(FMLLoader.isProduction()) return;

        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.sendSystemMessage(
                Component.literal("WARNING: " + message).withStyle(ChatFormatting.GOLD)
        );
    }

    public static void error(String message) {
        if(FMLLoader.isProduction()) return;

        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.sendSystemMessage(
                Component.literal("ERROR: " + message).withStyle(ChatFormatting.RED)
        );
    }
}
