package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
                    .save(consumer, RubinatedNether.id( "obtain_bleeding_obsidian"), existingFileHelper);

            AdvancementHolder freezer = Advancement.Builder.advancement()
                    .parent(AdvancementSubProvider.createPlaceholder("nether/root"))
                    .display(RNBlocks.FREEZER.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("freezer", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.FREEZER.get()))
                    .save(consumer, RubinatedNether.id( "obtain_freezer"), existingFileHelper);

            AdvancementHolder frostedIce = Advancement.Builder.advancement()
                    .parent(freezer)
                    .display(Blocks.FROSTED_ICE,
                            Component.translatable("advancements.rubinated_nether.obtain_frosted_ice.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_frosted_ice.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("frosted_ice", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.FROSTED_ICE))
                    .save(consumer, RubinatedNether.id( "obtain_frosted_ice"), existingFileHelper);

            AdvancementHolder rubinatedBlackstone = Advancement.Builder.advancement()
                    .parent(AdvancementSubProvider.createPlaceholder("nether/find_bastion"))
                    .display(RNBlocks.RUBINATED_BLACKSTONE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_rubinated_blackstone.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_rubinated_blackstone.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("rubinated_blackstone", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.RUBINATED_BLACKSTONE.get()))
                    .save(consumer, RubinatedNether.id( "obtain_rubinated_blackstone"), existingFileHelper);

            AdvancementHolder moltenRuby = Advancement.Builder.advancement()
                    .parent(freezer)
                    .display(RNItems.MOLTEN_RUBY_ITEM,
                            Component.translatable("advancements.rubinated_nether.obtain_molten_ruby.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_molten_ruby.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("molten_ruby", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.MOLTEN_RUBY_ITEM))
                    .save(consumer, RubinatedNether.id( "obtain_molten_ruby"), existingFileHelper);

            AdvancementHolder ruby = Advancement.Builder.advancement()
                    .parent(moltenRuby)
                    .display(RNItems.RUBY_ITEM.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("ruby", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.RUBY_ITEM))
                    .save(consumer, RubinatedNether.id( "obtain_ruby"), existingFileHelper);

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
                    .save(consumer, RubinatedNether.id( "obtain_ruby_glass"), existingFileHelper);

            AdvancementHolder rubyLaser = Advancement.Builder.advancement()
                    .parent(rubyGlass)
                    .display(RNBlocks.RUBY_LASER.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby_laser.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_ruby_laser.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("ruby_laser", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.RUBY_LASER.get()))
                    .save(consumer, RubinatedNether.id( "obtain_ruby_laser"), existingFileHelper);

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
                    .save(consumer, RubinatedNether.id( "obtain_ruby_lights"), existingFileHelper);

            AdvancementHolder brazier = Advancement.Builder.advancement()
                    .parent(moltenRuby)
                    .display(RNBlocks.BRAZIER.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_brazier.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_brazier.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("brazier", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.BRAZIER.get()))
                    .save(consumer, RubinatedNether.id( "obtain_brazier"), existingFileHelper);

            AdvancementHolder rubyLens = Advancement.Builder.advancement()
                    .parent(rubyGlass)
                    .display(RNItems.RUBY_LENS.get(),
                            Component.translatable("advancements.rubinated_nether.wear_lens.title"),
                            Component.translatable("advancements.rubinated_nether.wear_lens.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("ruby_lens", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.RUBY_LENS.get()))
                    .save(consumer, RubinatedNether.id( "obtain_ruby_lens"), existingFileHelper);

            AdvancementHolder sacredShrine = Advancement.Builder.advancement()
                    .parent(AdvancementSubProvider.createPlaceholder("nether/root"))
                    .display(RNBlocks.SHRINE_STONE_BRICKS.get(),
                            Component.translatable("advancements.rubinated_nether.enter_shrine.title"),
                            Component.translatable("advancements.rubinated_nether.enter_shrine.description"),
                            null,
                            AdvancementType.TASK, true, true, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("shrine_stone", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.SHRINE_STONE.get()))
                    .save(consumer, RubinatedNether.id( "enter_shrine"), existingFileHelper);

            AdvancementHolder rubinousRitual = Advancement.Builder.advancement()
                    .parent(sacredShrine)
                    .display(RNItems.RUBY_ITEM.get(),
                            Component.translatable("advancements.rubinated_nether.rubinous_ritual.title"),
                            Component.translatable("advancements.rubinated_nether.rubinous_ritual.description"),
                            null,
                            AdvancementType.TASK, true, true, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("placeholder", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.RUNESTONE.get()))
                    .save(consumer, RubinatedNether.id( "rubinous_ritual"), existingFileHelper);


            AdvancementHolder bronzeRod = Advancement.Builder.advancement()
                    .parent(sacredShrine)
                    .display(RNItems.BRONZE_ROD.get(),
                            Component.translatable("advancements.rubinated_nether.bronze_rod.title"),
                            Component.translatable("advancements.rubinated_nether.bronze_rod.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("bronze_rod", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.BRONZE_ROD.get()))
                    .save(consumer, RubinatedNether.id( "obtain_bronze_rod"), existingFileHelper);

            AdvancementHolder bronzeBlock = Advancement.Builder.advancement()
                    .parent(bronzeRod)
                    .display(RNBlocks.BRONZE_BLOCK.get(),
                            Component.translatable("advancements.rubinated_nether.bronze_block.title"),
                            Component.translatable("advancements.rubinated_nether.bronze_block.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("bronze_block", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.BRONZE_BLOCK.get()))
                    .save(consumer, RubinatedNether.id( "obtain_bronze_block"), existingFileHelper);

            AdvancementHolder shrineSentinel = Advancement.Builder.advancement()
                    .parent(bronzeRod)
                    .display(RNItems.BRONZE_SCRAP.get(),
                            Component.translatable("advancements.rubinated_nether.shrine_sentinel.title"),
                            Component.translatable("advancements.rubinated_nether.shrine_sentinel.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("placeholder", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.BRONZE_SCRAP.get()))
                    .save(consumer, RubinatedNether.id( "obtain_bronze_statue"), existingFileHelper);

            // Runes

            AdvancementHolder runeSloth = Advancement.Builder.recipeAdvancement()
                    .display(RNItems.SLOTH_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("sloth", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.SLOTH_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "sloth"), existingFileHelper);

            AdvancementHolder runeGluttony = Advancement.Builder.advancement()
                    .display(RNItems.GLUTTONY_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("gluttony", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.GLUTTONY_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "gluttony"), existingFileHelper);

            AdvancementHolder runeGreed = Advancement.Builder.advancement()
                    .display(RNItems.GREED_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("greed", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.GREED_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "greed"), existingFileHelper);

            AdvancementHolder runeVainglory = Advancement.Builder.advancement()
                    .display(RNItems.VAINGLORY_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("vainglory", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.VAINGLORY_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "vainglory"), existingFileHelper);

            AdvancementHolder runeWrath = Advancement.Builder.advancement()
                    .display(RNItems.WRATH_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("wrath", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.WRATH_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "wrath"), existingFileHelper);

            AdvancementHolder runeEnvy = Advancement.Builder.advancement()
                    .display(RNItems.ENVY_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("envy", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.ENVY_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "envy"), existingFileHelper);

            AdvancementHolder runePride = Advancement.Builder.advancement()
                    .display(RNItems.PRIDE_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("pride", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.PRIDE_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "pride"), existingFileHelper);

            AdvancementHolder runeAcedia = Advancement.Builder.advancement()
                    .display(RNItems.ACEDIA_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("acedia", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.ACEDIA_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "acedia"), existingFileHelper);

            AdvancementHolder runeLuxuria = Advancement.Builder.advancement()
                    .display(RNItems.LUXURIA_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("luxuria", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.LUXURIA_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "luxuria"), existingFileHelper);

            AdvancementHolder runeInsidiae = Advancement.Builder.advancement()
                    .display(RNItems.INSIDIAE_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("insidiae", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.INSIDIAE_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "insidiae"), existingFileHelper);

            AdvancementHolder runeSuperbia = Advancement.Builder.advancement()
                    .display(RNItems.SUPERBIA_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("superbia", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.SUPERBIA_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "superbia"), existingFileHelper);

            AdvancementHolder runeTristia = Advancement.Builder.advancement()
                    .display(RNItems.TRISTIA_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("tristia", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.TRISTIA_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "tristia"), existingFileHelper);

            AdvancementHolder runeStudiose = Advancement.Builder.advancement()
                    .display(RNItems.STUDIOSE_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("studiose", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.STUDIOSE_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "studiose"), existingFileHelper);

            AdvancementHolder runeArdenter = Advancement.Builder.advancement()
                    .display(RNItems.ARDENTER_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("ardenter", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.ARDENTER_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "ardenter"), existingFileHelper);

            AdvancementHolder runeNimis = Advancement.Builder.advancement()
                    .display(RNItems.NIMIS_RUNE.get(),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.title"),
                            Component.translatable("advancements.rubinated_nether.obtain_freezer.description"),
                            null,
                            AdvancementType.TASK, false, false, true)
                    .requirements(AdvancementRequirements.Strategy.OR)
                    .addCriterion("nimis", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.NIMIS_RUNE.get()))
                    .save(consumer, RubinatedNether.id( "nimis"), existingFileHelper);

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