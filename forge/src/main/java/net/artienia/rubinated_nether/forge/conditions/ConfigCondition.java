package net.artienia.rubinated_nether.forge.conditions;

import com.google.gson.JsonObject;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.config.RNConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.checkerframework.checker.units.qual.C;

public class ConfigCondition implements ICondition {

    public static final IConditionSerializer<ConfigCondition> SERIALIZER = new Serializer();

    private static final ResourceLocation ID = RubinatedNether.id("netherite_smithing_template_recipe");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(IContext iContext) {
        return RNConfig.customSmithingRecipe;
    }

    private static class Serializer implements IConditionSerializer<ConfigCondition> {

        @Override
        public void write(JsonObject jsonObject, ConfigCondition iCondition) {

        }

        @Override
        public ConfigCondition read(JsonObject jsonObject) {
            return new ConfigCondition();
        }

        @Override
        public ResourceLocation getID() {
            return ID;
        }
    }
}
