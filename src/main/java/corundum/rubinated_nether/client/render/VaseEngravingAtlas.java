package corundum.rubinated_nether.client.render;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.trim.VaseEngravingMaterial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

public class VaseEngravingAtlas extends TextureAtlasHolder {
    public static final ResourceLocation ATLAS_LOCATION = RubinatedNether.id("textures/atlas/vase_engravings.png");
    public static final ResourceLocation ATLAS_INFO_LOCATION = RubinatedNether.id("vase_engravings");
    public static final VaseEngravingAtlas INSTANCE = new VaseEngravingAtlas();

    private VaseEngravingAtlas() {
        super(Minecraft.getInstance().getTextureManager(), ATLAS_LOCATION, ATLAS_INFO_LOCATION);
    }

    public TextureAtlasSprite getSprite(Rubination pattern, VaseEngravingMaterial material) {
        String category = Rubination.parseRubinationTextureName(pattern);
        return this.getSprite(RubinatedNether.id("trims/models/vase/rune_" + category + "_" + material.paletteName()));
    }
}