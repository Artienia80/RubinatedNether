package net.artienia.rubinated_nether.integrations.emi;

import dev.emi.emi.api.render.EmiTexture;
import net.artienia.rubinated_nether.content.screen.FreezerScreen;

import java.text.DecimalFormat;

public class EMIStuff {

    public static final DecimalFormat TEXT_FORMAT = new DecimalFormat("0.##");

    public static final EmiTexture FREEZE_ICON = new EmiTexture(FreezerScreen.FREEZER_GUI_TEXTURES, 176, 0, 16, 16, 14, 14, 256, 256);

    public static final EmiTexture FULL_FREEZE = new EmiTexture(FreezerScreen.FREEZER_GUI_TEXTURES, 176, 0, 14, 14);

    public static final EmiTexture EMPTY_FREEZE = new EmiTexture(FreezerScreen.FREEZER_GUI_TEXTURES, 57, 36, 14, 14);
}
