package corundum.rubinated_nether.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public class RNClientEnumExtensions {
    public static final EnumProxy<Gui.HeartType> BRONZE_DISEASED_HEART_PROXY = new EnumProxy<>(
            Gui.HeartType.class,
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_full"),
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_full_blinking"),
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_half"),
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_half_blinking"),
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_hardcore_full"),
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_hardcore_full_blinking"),
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_hardcore_half"),
            ResourceLocation.fromNamespaceAndPath("rubinated_nether", "hud/heart/bronze_diseased_hardcore_half_blinking")
    );

    public static Gui.HeartType bronzeDiseasedHeartType() {
        return BRONZE_DISEASED_HEART_PROXY.getValue();
    }
}