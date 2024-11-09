package net.artienia.rubinated_nether.forge.conditions;

import com.google.gson.JsonObject;
import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.config.RNConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.crafting.conditions.*;

public class RecipeConfigCondition implements ICondition {
    public static final RecipeConfigCondition INSTANCE = new RecipeConfigCondition();

    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(RubinatedNether.MOD_ID, "netherite_smithing_template_recipe");
    }

    @Override
    public boolean test(IContext iContext) {
        return RNConfig.netherite_smithing_template_recipe;
    }

    public static class Serializer implements IConditionSerializer<RecipeConfigCondition> {

        public static final RecipeConfigCondition.Serializer INSTANCE = new RecipeConfigCondition.Serializer();

        @Override
        public void write(JsonObject jsonObject, RecipeConfigCondition iCondition) { }

        @Override
        public RecipeConfigCondition read(JsonObject jsonObject) {
            return RecipeConfigCondition.INSTANCE;
        }

        @Override
        public ResourceLocation getID() {
            return new ResourceLocation(RubinatedNether.MOD_ID, "netherite_smithing_template_recipe");
        }
    }
}
