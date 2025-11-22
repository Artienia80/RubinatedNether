package corundum.rubinated_nether.content.entity.client;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNModelLayers;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BronzeRenderer extends MobRenderer<BronzeEntity, BronzeModel<BronzeEntity>> {
    private static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/entity/bronze/bronze.png");
    private static final ResourceLocation DISCOLORED = ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/entity/bronze/bronze_discolored.png");
    private static final ResourceLocation CORRODED = ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/entity/bronze/bronze_corroded.png");
    private static final ResourceLocation TARNISHED = ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/entity/bronze/bronze_tarnished.png");
    private static final ResourceLocation CRYSTALLIZED = ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "textures/entity/bronze/bronze_crystallized.png");

    public BronzeRenderer(EntityRendererProvider.Context context) {
            super(context, new BronzeModel<>(context.bakeLayer(RNModelLayers.BRONZE)), 0.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(BronzeEntity entity) {
        return entity.getTarnishLevel() == 1 ? DISCOLORED : entity.getTarnishLevel() == 2 ? CORRODED : entity.getTarnishLevel() == 3 ? TARNISHED : entity.getTarnishLevel() == 4 ? CRYSTALLIZED : LOCATION;
    }
}