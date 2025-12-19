package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
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

			AdvancementHolder powderSnow = Advancement.Builder.advancement()
					.parent(freezer)
					.display(Blocks.POWDER_SNOW,
							Component.translatable("advancements.rubinated_nether.obtain_powder_snow.title"),
							Component.translatable("advancements.rubinated_nether.obtain_powder_snow.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.OR)
					.addCriterion("powder_snow", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.POWDER_SNOW))
					.save(consumer, RubinatedNether.id( "obtain_powder_snow"), existingFileHelper);

			AdvancementHolder frostedIce = Advancement.Builder.advancement()
					.parent(powderSnow)
					.display(Blocks.FROSTED_ICE,
							Component.translatable("advancements.rubinated_nether.obtain_frosted_ice.title"),
							Component.translatable("advancements.rubinated_nether.obtain_frosted_ice.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.OR)
					.addCriterion("frosted_ice", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.FROSTED_ICE))
					.save(consumer, RubinatedNether.id( "obtain_frosted_ice"), existingFileHelper);

			AdvancementHolder dryIce = Advancement.Builder.advancement()
					.parent(frostedIce)
					.display(RNBlocks.DRY_ICE,
							Component.translatable("advancements.rubinated_nether.obtain_dry_ice.title"),
							Component.translatable("advancements.rubinated_nether.obtain_dry_ice.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.OR)
					.addCriterion("dry_ice", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.DRY_ICE))
					.save(consumer, RubinatedNether.id( "obtain_dry_ice"), existingFileHelper);

			AdvancementHolder moltenRuby = Advancement.Builder.advancement()
					.parent(freezer)
					.display(RNItems.MOLTEN_RUBY,
							Component.translatable("advancements.rubinated_nether.obtain_molten_ruby.title"),
							Component.translatable("advancements.rubinated_nether.obtain_molten_ruby.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.OR)
					.addCriterion("molten_ruby", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.MOLTEN_RUBY))
					.save(consumer, RubinatedNether.id( "obtain_molten_ruby"), existingFileHelper);

			AdvancementHolder ruby = Advancement.Builder.advancement()
					.parent(moltenRuby)
					.display(RNItems.RUBY.get(),
							Component.translatable("advancements.rubinated_nether.obtain_ruby.title"),
							Component.translatable("advancements.rubinated_nether.obtain_ruby.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.OR)
					.addCriterion("ruby", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.RUBY))
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

			AdvancementHolder enterShrine = Advancement.Builder.advancement()
					.parent(AdvancementSubProvider.createPlaceholder("nether/root"))
					.display(RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get(),
							Component.translatable("advancements.rubinated_nether.enter_shrine.title"),
							Component.translatable("advancements.rubinated_nether.enter_shrine.description"),
							null,
							AdvancementType.TASK, true, true, true)
					.requirements(AdvancementRequirements.Strategy.OR)
					.addCriterion("shrine_stone", InventoryChangeTrigger.TriggerInstance.hasItems(RNBlocks.SHRINE_STONE.get()))
					.save(consumer, RubinatedNether.id( "enter_shrine"), existingFileHelper);

			AdvancementHolder bronzeRod = Advancement.Builder.advancement()
					.parent(enterShrine)
					.display(RNItems.BRONZE_ROD.get(),
							Component.translatable("advancements.rubinated_nether.bronze_rod.title"),
							Component.translatable("advancements.rubinated_nether.bronze_rod.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.OR)
					.addCriterion("bronze_rod", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.BRONZE_ROD.get()))
					.save(consumer, RubinatedNether.id( "obtain_bronze_rod"), existingFileHelper);

			AdvancementHolder ritualOffering = Advancement.Builder.advancement()
					.parent(bronzeRod)
					.display(RNItems.RITUAL_OFFERING.get(),
							Component.translatable("advancements.rubinated_nether.offer_ritual_offering.title"),
							Component.translatable("advancements.rubinated_nether.offer_ritual_offering.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-65536))))
					.save(consumer, RubinatedNether.id("offer_ritual_offering"), existingFileHelper);

			AdvancementHolder divineFavor = Advancement.Builder.advancement()
					.parent(ritualOffering)
					.display(RNItems.BLESSED_ICON.get(),
							Component.translatable("advancements.rubinated_nether.divine_favor.title"),
							Component.translatable("advancements.rubinated_nether.divine_favor.description"),
							null,
							AdvancementType.CHALLENGE, true, true, true)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-65536))))
					.save(consumer, RubinatedNether.id("divine_favor"), existingFileHelper);

			AdvancementHolder inscribeRune = Advancement.Builder.advancement()
					.parent(ritualOffering)
					.display(RNItems.RUNE.get(),
							Component.translatable("advancements.rubinated_nether.inscribe_rune.title"),
							Component.translatable("advancements.rubinated_nether.inscribe_rune.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-65536))))
					.save(consumer, RubinatedNether.id("inscribe_rune"), existingFileHelper);

			AdvancementHolder insertRune = Advancement.Builder.advancement()
					.parent(inscribeRune)
					.display(RNItems.PRIDE_RUNE.get(),
							Component.translatable("advancements.rubinated_nether.insert_rune.title"),
							Component.translatable("advancements.rubinated_nether.insert_rune.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-65536))))
					.save(consumer, RubinatedNether.id("insert_rune"), existingFileHelper);

			AdvancementHolder rubinateItem = Advancement.Builder.advancement()
					.parent(insertRune)
					.display(RNItems.WINDING_KEY.get(),
							Component.translatable("advancements.rubinated_nether.rubinate_item.title"),
							Component.translatable("advancements.rubinated_nether.rubinate_item.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-65536))))
					.save(consumer, RubinatedNether.id("rubinate_item"), existingFileHelper);

			AdvancementHolder heavyBurden = Advancement.Builder.advancement()
					.parent(enterShrine)
					.display(RNBlocks.BRONZE_BLOCK.get(),
							Component.translatable("advancements.rubinated_nether.heavy_burden.title"),
							Component.translatable("advancements.rubinated_nether.heavy_burden.description"),
							null,
							AdvancementType.CHALLENGE, true, true, true)
					.addCriterion("all_blocks", InventoryChangeTrigger.TriggerInstance.hasItems(
							ItemPredicate.Builder.item().of(
									RNBlocks.BRONZE_BLOCK,
									RNRecipeProvider.waxed(RNBlocks.BRONZE_BLOCK)
							).build(),
							ItemPredicate.Builder.item().of(
									RNBlocks.DISCOLORED_BRONZE_BLOCK,
									RNRecipeProvider.waxed(RNBlocks.DISCOLORED_BRONZE_BLOCK)
							).build(),
							ItemPredicate.Builder.item().of(
									RNBlocks.CORRODED_BRONZE_BLOCK,
									RNRecipeProvider.waxed(RNBlocks.CORRODED_BRONZE_BLOCK)
							).build(),
							ItemPredicate.Builder.item().of(
									RNBlocks.TARNISHED_BRONZE_BLOCK,
									RNRecipeProvider.waxed(RNBlocks.TARNISHED_BRONZE_BLOCK)
							).build(),
							ItemPredicate.Builder.item().of(
									RNBlocks.CRYSTALLIZED_BRONZE_BLOCK,
									RNRecipeProvider.waxed(RNBlocks.CRYSTALLIZED_BRONZE_BLOCK)
							).build(),
							ItemPredicate.Builder.item().of(
									net.minecraft.world.level.block.Blocks.COPPER_BLOCK,
									net.minecraft.world.level.block.Blocks.WAXED_COPPER_BLOCK
							).build(),
							ItemPredicate.Builder.item().of(
									net.minecraft.world.level.block.Blocks.EXPOSED_COPPER,
									net.minecraft.world.level.block.Blocks.WAXED_EXPOSED_COPPER
							).build(),
							ItemPredicate.Builder.item().of(
									net.minecraft.world.level.block.Blocks.WEATHERED_COPPER,
									net.minecraft.world.level.block.Blocks.WAXED_WEATHERED_COPPER
							).build(),
							ItemPredicate.Builder.item().of(
									net.minecraft.world.level.block.Blocks.OXIDIZED_COPPER,
									net.minecraft.world.level.block.Blocks.WAXED_OXIDIZED_COPPER
							).build()))
					.save(consumer, RubinatedNether.id("heavy_burden"), existingFileHelper);

			AdvancementHolder brightenUp = Advancement.Builder.advancement()
					.parent(enterShrine)
					.display(RNBlocks.BRONZE_LAMP.get(),
							Component.translatable("advancements.rubinated_nether.brighten_up.title"),
							Component.translatable("advancements.rubinated_nether.brighten_up.description"),
							null,
							AdvancementType.CHALLENGE, true, true, true)
					.addCriterion("all_lights", InventoryChangeTrigger.TriggerInstance.hasItems(
							ItemPredicate.Builder.item().of(
									RNBlocks.BRONZE_LANTERN,
									RNRecipeProvider.waxed(RNBlocks.BRONZE_LANTERN),
									RNBlocks.DISCOLORED_BRONZE_LANTERN,
									RNRecipeProvider.waxed(RNBlocks.DISCOLORED_BRONZE_LANTERN),
									RNBlocks.CORRODED_BRONZE_LANTERN,
									RNRecipeProvider.waxed(RNBlocks.CORRODED_BRONZE_LANTERN),
									RNBlocks.TARNISHED_BRONZE_LANTERN,
									RNRecipeProvider.waxed(RNBlocks.TARNISHED_BRONZE_LANTERN),
									RNBlocks.CRYSTALLIZED_BRONZE_LANTERN,
									RNRecipeProvider.waxed(RNBlocks.CRYSTALLIZED_BRONZE_LANTERN)
							).build(),
							ItemPredicate.Builder.item().of(
									RNBlocks.BRONZE_CHANDELIER,
									RNRecipeProvider.waxed(RNBlocks.BRONZE_CHANDELIER),
									RNBlocks.DISCOLORED_BRONZE_CHANDELIER,
									RNRecipeProvider.waxed(RNBlocks.DISCOLORED_BRONZE_CHANDELIER),
									RNBlocks.CORRODED_BRONZE_CHANDELIER,
									RNRecipeProvider.waxed(RNBlocks.CORRODED_BRONZE_CHANDELIER),
									RNBlocks.TARNISHED_BRONZE_CHANDELIER,
									RNRecipeProvider.waxed(RNBlocks.TARNISHED_BRONZE_CHANDELIER),
									RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER,
									RNRecipeProvider.waxed(RNBlocks.CRYSTALLIZED_BRONZE_CHANDELIER)
							).build(),
							ItemPredicate.Builder.item().of(
									RNBlocks.BRONZE_LAMP,
									RNRecipeProvider.waxed(RNBlocks.BRONZE_LAMP),
									RNBlocks.DISCOLORED_BRONZE_LAMP,
									RNRecipeProvider.waxed(RNBlocks.DISCOLORED_BRONZE_LAMP),
									RNBlocks.CORRODED_BRONZE_LAMP,
									RNRecipeProvider.waxed(RNBlocks.CORRODED_BRONZE_LAMP),
									RNBlocks.TARNISHED_BRONZE_LAMP,
									RNRecipeProvider.waxed(RNBlocks.TARNISHED_BRONZE_LAMP),
									RNBlocks.CRYSTALLIZED_BRONZE_LAMP,
									RNRecipeProvider.waxed(RNBlocks.CRYSTALLIZED_BRONZE_LAMP)
							).build(),
							ItemPredicate.Builder.item().of(
									RNBlocks.BRONZE_BULB,
									RNRecipeProvider.waxed(RNBlocks.BRONZE_BULB),
									RNBlocks.DISCOLORED_BRONZE_BULB,
									RNRecipeProvider.waxed(RNBlocks.DISCOLORED_BRONZE_BULB),
									RNBlocks.CORRODED_BRONZE_BULB,
									RNRecipeProvider.waxed(RNBlocks.CORRODED_BRONZE_BULB),
									RNBlocks.TARNISHED_BRONZE_BULB,
									RNRecipeProvider.waxed(RNBlocks.TARNISHED_BRONZE_BULB),
									RNBlocks.CRYSTALLIZED_BRONZE_BULB,
									RNRecipeProvider.waxed(RNBlocks.CRYSTALLIZED_BRONZE_BULB)
							).build()))
					.save(consumer, RubinatedNether.id("brighten_up"), existingFileHelper);

            AdvancementHolder pitfalls = Advancement.Builder.advancement()
                    .parent(enterShrine)
                    .display(RNBlocks.BRONZE_GRATE.get(),
                            Component.translatable("advancements.rubinated_nether.pitfalls.title"),
                            Component.translatable("advancements.rubinated_nether.pitfalls.description"),
                            null,
                            AdvancementType.TASK, true, true, false)
                    .requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-65536))))
                    .save(consumer, RubinatedNether.id("pitfalls"), existingFileHelper);

            AdvancementHolder laserDetection = Advancement.Builder.advancement()
					.parent(enterShrine)
					.display(RNBlocks.BRONZE_LASER.get(),
							Component.translatable("advancements.rubinated_nether.laser_detection.title"),
							Component.translatable("advancements.rubinated_nether.laser_detection.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-65536))))
					.save(consumer, RubinatedNether.id("laser_detection"), existingFileHelper);
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
