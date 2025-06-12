package corundum.rubinated_nether.content.entity.client;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import corundum.rubinated_nether.content.entity.layer.RNModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BronzeRenderer extends MobRenderer<BronzeEntity, BronzeModel<BronzeEntity>> {
    private static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/entity/bronze/bronze.png");
    public BronzeRenderer(EntityRendererProvider.Context context) {
            super(context, new BronzeModel<>(context.bakeLayer(RNModelLayers.BRONZE)), 0.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(BronzeEntity mosquito) {
        return LOCATION;
    }
}