package corundum.rubinated_nether.content.entity.client;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.living.BronzeTypeEntity;
import corundum.rubinated_nether.content.entity.layer.RNModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class BronzeRenderer extends MobRenderer<BronzeTypeEntity, BronzeModel<BronzeTypeEntity>> {
    private static final ResourceLocation BRONZE_LOCATION =
            RubinatedNether.id("textures/entity/bronze/bronze.png");
    private static final ResourceLocation DISCOLORED_LOCATION =
            RubinatedNether.id("textures/entity/bronze/discolored.png");
    private static final ResourceLocation CORRODED_LOCATION =
            RubinatedNether.id("textures/entity/bronze/corroded.png");
    private static final ResourceLocation TARNISHED_LOCATION =
            RubinatedNether.id("textures/entity/bronze/tarnished.png");
    private static final ResourceLocation CRYSTALLIZED_LOCATION =
            RubinatedNether.id("textures/entity/bronze/crystallized.png");

    public BronzeRenderer(EntityRendererProvider.Context context) {
            super(context, new BronzeModel<>(context.bakeLayer(RNModelLayers.BRONZE)), 0.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(BronzeTypeEntity bronzeEntity) {
        return switch (bronzeEntity.getTarnishingLevel()) {
            case BRONZE -> BRONZE_LOCATION;
            case DISCOLORED -> DISCOLORED_LOCATION;
            case CORRODED -> CORRODED_LOCATION;
            case TARNISHED -> TARNISHED_LOCATION;
            case CRYSTALLIZED -> CRYSTALLIZED_LOCATION;
        };
    }
}