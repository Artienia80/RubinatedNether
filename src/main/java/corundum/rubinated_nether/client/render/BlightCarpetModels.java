package corundum.rubinated_nether.client.render;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Map;

@EventBusSubscriber(modid = RubinatedNether.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BlightCarpetModels {
    private BlightCarpetModels() {
    }

    private static ModelResourceLocation location(Direction facing, int tile) {
        return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(
                RubinatedNether.MODID,
                "block/blight_carpet/" + facing.getName() + "_" + tile / 6 + "_" + tile % 6
        ));
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        for (Direction facing : Direction.values()) {
            for (int tile = 0; tile < 60; tile++) {
                event.register(location(facing, tile));
            }
        }
    }

    @SubscribeEvent
    public static void modifyBaking(ModelEvent.ModifyBakingResult event) {
        Map<ModelResourceLocation, BakedModel> models = event.getModels();

        BakedModel[][] tiles = new BakedModel[6][60];
        for (Direction facing : Direction.values()) {
            for (int tile = 0; tile < 60; tile++) {
                BakedModel model = models.get(location(facing, tile));
                if (model == null) {
                    return;
                }
                tiles[facing.ordinal()][tile] = model;
            }
        }

        for (BlockState state : RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT.get().getStateDefinition().getPossibleStates()) {
            ModelResourceLocation key = BlockModelShaper.stateToModelLocation(state);
            BakedModel original = models.get(key);
            if (original != null) {
                models.put(key, new BlightCarpetModel(original, tiles));
            }
        }
    }
}
