package net.artienia.rubinated_nether.fabric.conditions;

import com.google.gson.JsonObject;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.config.RNConfig;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.resources.ResourceLocation;

public class RNResourceConditionTypes {
    private static final ResourceLocation RECIPE_CONFIG = new ResourceLocation(RubinatedNether.MOD_ID, "netherite_smithing_template_recipe");

    public static ConditionJsonProvider recipe_config(ConditionJsonProvider value) {
        return new ConditionJsonProvider() {
            @Override
            public void writeParameters(JsonObject object) {
            }

            @Override
            public ResourceLocation getConditionId() {
                return RECIPE_CONFIG;
            }
        };
    }

    public static void init() {
        // init static
    }

    static {
        ResourceConditions.register(RECIPE_CONFIG, object -> RNConfig.netherite_smithing_template_recipe);
    }
}
