package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RNAdvancements extends AdvancementProvider {
    public RNAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
        super(output, registries, helper, List.of(new RNAdvancement()));
    }

    public static class RNAdvancement implements AdvancementGenerator {
        @SuppressWarnings("unused")
        @Override
        public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {

            AdvancementHolder bleedingObsidian = Advancement.Builder.advancement()
                    .parent(AdvancementSubProvider.createPlaceholder("nether/obtain_crying_obsidian"))
                    .display(RNBlocks.BLEEDING_OBSIDIAN.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_bleeding_obsidian.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_bleeding_obsidian.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("bleeding_obsidian", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.BLEEDING_OBSIDIAN.get()))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_bleeding_obsidian"), existingFileHelper);

            AdvancementHolder freezer = Advancement.Builder.advancement()
                    .parent(AdvancementSubProvider.createPlaceholder("nether/root"))
                    .display(RNBlocks.FREEZER.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("freezer", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.FREEZER.get()))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_freezer"), existingFileHelper);

            AdvancementHolder frostedIce = Advancement.Builder.advancement()
                    .parent(freezer)
                    .display(Blocks.FROSTED_ICE,
                            Component.translatable("advancements.rubinated_nether.obtain_frosted_ice.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_frosted_ice.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("frosted_ice", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.FROSTED_ICE))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_frosted_ice"), existingFileHelper);

            AdvancementHolder rubinatedBlackstone = Advancement.Builder.advancement()
                    .parent(AdvancementSubProvider.createPlaceholder("nether/find_bastion"))
                    .display(RNBlocks.RUBINATED_BLACKSTONE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_rubinated_blackstone.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_rubinated_blackstone.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("rubinated_blackstone", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.RUBINATED_BLACKSTONE.get()))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_rubinated_blackstone"), existingFileHelper);

            AdvancementHolder moltenRuby = Advancement.Builder.advancement()
                    .parent(freezer)
                    .display(RNItems.MOLTEN_RUBY_ITEM,
                            Component.translatable("advancements.rubinated_nether.obtain_molten_ruby.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_molten_ruby.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("molten_ruby", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.MOLTEN_RUBY_ITEM))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_molten_ruby"), existingFileHelper);

            AdvancementHolder ruby = Advancement.Builder.advancement()
                    .parent(moltenRuby)
                    .display(RNItems.RUBY_ITEM.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("ruby", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.RUBY_ITEM))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_ruby"), existingFileHelper);

            AdvancementHolder rubyGlass = Advancement.Builder.advancement()
                    .parent(ruby)
                    .display(RNBlocks.RUBY_GLASS.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby_glass.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby_glass.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("ruby_glass", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.RUBY_GLASS.get()))
                    .addCriterion("ruby_glass_pane", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.RUBY_GLASS_PANE.get()))
                    .addCriterion("molten_ruby_glass", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.MOLTEN_RUBY_GLASS.get()))
                    .addCriterion("molten_ruby_glass_pane", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.MOLTEN_RUBY_GLASS_PANE.get()))
                    .addCriterion("ornate_ruby_glass", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.ORNATE_RUBY_GLASS.get()))
                    .addCriterion("ornate_ruby_glass_pane", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.ORNATE_RUBY_GLASS_PANE.get()))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_ruby_glass"), existingFileHelper);

            AdvancementHolder rubyLaser = Advancement.Builder.advancement()
                    .parent(rubyGlass)
                    .display(RNBlocks.RUBY_LASER.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby_laser.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby_laser.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("ruby_laser", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.RUBY_LASER.get()))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_ruby_laser"), existingFileHelper);

            AdvancementHolder rubyLights = Advancement.Builder.advancement()
                    .parent(moltenRuby)
                    .display(RNBlocks.LAVA_LAMP.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby_lights.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby_lights.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.AND)
                    .addCriterion("ruby_lantern", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.RUBY_LANTERN.get()))
                    .addCriterion("ruby_chandelier", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.CHANDELIER.get()))
                    .addCriterion("ruby_lamp", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.LAVA_LAMP.get()))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_ruby_lights"), existingFileHelper);

            AdvancementHolder brazier = Advancement.Builder.advancement()
                    .parent(moltenRuby)
                    .display(RNBlocks.BRAZIER.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_brazier.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_brazier.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("brazier", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.BRAZIER.get()))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_brazier"), existingFileHelper);

            AdvancementHolder rubyLens = Advancement.Builder.advancement()
                    .parent(rubyGlass)
                    .display(RNItems.RUBY_LENS.get(),
                            Component.translatable("advancements.rubinated_nether.wear_lens.title"),
                            Component.translatable("advancements.rubinated_nether.wear_lens.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("ruby_lens", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.RUBY_LENS.get()))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_ruby_lens"), existingFileHelper);

            AdvancementHolder sacredShrine = Advancement.Builder.advancement()
                    .parent(AdvancementSubProvider.createPlaceholder("nether/root"))
                    .display(RNBlocks.SHRINE_STONE_BRICKS.get(),
                            Component.translatable("advancements.rubinated_nether.enter_shrine.title"),
                            Component.translatable("advancements.rubinated_nether.enter_shrine.description"),
                            null,
                            AdvancementType.TASK, true, true, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("shrine_stone", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.SHRINE_STONE.get()))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "enter_shrine"), existingFileHelper);

            AdvancementHolder bronzeRod = Advancement.Builder.advancement()
                    .parent(sacredShrine)
                    .display(RNItems.BRONZE_ROD.get(),
                            Component.translatable("advancements.rubinated_nether.bronze_rod.title"),
                            Component.translatable("advancements.rubinated_nether.bronze_rod.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("bronze_rod", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.BRONZE_ROD.get()))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_bronze_rod"), existingFileHelper);

            AdvancementHolder bronzeBlock = Advancement.Builder.advancement()
                    .parent(bronzeRod)
                    .display(RNBlocks.BRONZE_BLOCK.get(),
                            Component.translatable("advancements.rubinated_nether.bronze_block.title"),
                            Component.translatable("advancements.rubinated_nether.bronze_block.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("bronze_block", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.BRONZE_BLOCK.get()))
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "obtain_bronze_block"), existingFileHelper);
        }
    }

    private static ItemUsedOnLocationTrigger.TriggerInstance itemUsedOnLocationCheckAbove(LocationPredicate.Builder location, LocationPredicate.Builder above, ItemPredicate.Builder item) {
        ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.create(LocationCheck.checkLocation(location).build(), LocationCheck.checkLocation(above, BlockPos.ZERO.above()).build(), MatchTool.toolMatches(item).build());
        return new ItemUsedOnLocationTrigger.TriggerInstance(Optional.empty(), Optional.of(contextawarepredicate));
    }

    public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> itemUsedOnBlockCheckAbove(LocationPredicate.Builder location, LocationPredicate.Builder above, ItemPredicate.Builder item) {
        return CriteriaTriggers.ITEM_USED_ON_BLOCK.createCriterion(itemUsedOnLocationCheckAbove(location, above, item));
    }
}