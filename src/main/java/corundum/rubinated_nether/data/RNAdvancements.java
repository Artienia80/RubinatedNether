package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBannerPatterns;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.trim.RNTrimPatterns;
import corundum.rubinated_nether.utils.RNAdvancementLinks;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
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

			HolderGetter<BannerPattern> bannerPatterns = provider.lookupOrThrow(Registries.BANNER_PATTERN);
			ItemStack rubinatedBannerDisplay = new ItemStack(Items.BLACK_BANNER);
			rubinatedBannerDisplay.set(DataComponents.BANNER_PATTERNS,
					new BannerPatternLayers.Builder()
							.add(bannerPatterns.getOrThrow(RNBannerPatterns.RUNE_ARMOR.key()), DyeColor.RED)
							.build());

			HolderGetter<TrimPattern> trimPatterns = provider.lookupOrThrow(Registries.TRIM_PATTERN);
			HolderGetter<TrimMaterial> trimMaterials = provider.lookupOrThrow(Registries.TRIM_MATERIAL);
			ItemStack rubinatedTrimDisplay = new ItemStack(Items.NETHERITE_CHESTPLATE);
			rubinatedTrimDisplay.set(DataComponents.TRIM, new ArmorTrim(
					trimMaterials.getOrThrow(TrimMaterials.REDSTONE),
					trimPatterns.getOrThrow(RNTrimPatterns.STUDIOSE_RUNE)));

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

			AdvancementHolder blooddripping = Advancement.Builder.advancement()
					.parent(bleedingObsidian)
					.display(RNBlocks.MOLTEN_RUBY_CAULDRON.get(),
							Component.translatable("advancements.rubinated_nether.blood_dripping.title"),
							Component.translatable("advancements.rubinated_nether.blood_dripping.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("blood_dripping"), existingFileHelper);

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

			AdvancementHolder laserDetection = Advancement.Builder.advancement()
					.parent(rubyLens)
					.display(RNBlocks.BRONZE_LASER.get(),
							Component.translatable("advancements.rubinated_nether.laser_detection.title"),
							Component.translatable("advancements.rubinated_nether.laser_detection.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("laser_detection"), existingFileHelper);

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

			AdvancementHolder max_brazier = Advancement.Builder.advancement()
					.parent(bronzeRod)
					.display(RNBlocks.BRAZIER.get(),
							Component.translatable("advancements.rubinated_nether.max_brazier.title"),
							Component.translatable("advancements.rubinated_nether.max_brazier.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("max_brazier"), existingFileHelper);

			AdvancementHolder baptism = Advancement.Builder.advancement()
					.parent(max_brazier)
					.display(RNItems.RITUAL_OFFERING.get(),
							Component.translatable("advancements.rubinated_nether.baptism.title"),
							Component.translatable("advancements.rubinated_nether.baptism.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("baptism"), existingFileHelper);

			AdvancementHolder ritualOffering = Advancement.Builder.advancement()
					.parent(baptism)
					.display(RNBlocks.RUBINATION_ALTAR.get(),
							Component.translatable("advancements.rubinated_nether.offer_ritual_offering.title"),
							Component.translatable("advancements.rubinated_nether.offer_ritual_offering.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
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
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("divine_favor"), existingFileHelper);

			AdvancementHolder insertRune = Advancement.Builder.advancement()
					.parent(ritualOffering)
					.display(RNItems.PRIDE_RUNE.get(),
							Component.translatable("advancements.rubinated_nether.insert_rune.title"),
							Component.translatable("advancements.rubinated_nether.insert_rune.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("insert_rune"), existingFileHelper);

			AdvancementHolder rubinateItem = Advancement.Builder.advancement()
					.parent(insertRune)
					.display(RNItems.RUBY.get(),
							Component.translatable("advancements.rubinated_nether.rubinate_item.title"),
							Component.translatable("advancements.rubinated_nether.rubinate_item.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("rubinate_item"), existingFileHelper);

			AdvancementHolder rubinatedBanner = Advancement.Builder.advancement()
					.parent(rubinateItem)
					.display(rubinatedBannerDisplay,
							Component.translatable("advancements.rubinated_nether.rubinated_banner.title"),
							Component.translatable("advancements.rubinated_nether.rubinated_banner.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("rubinated_banner"), existingFileHelper);

			AdvancementHolder rubinatedTrim = Advancement.Builder.advancement()
					.parent(rubinateItem)
					.display(rubinatedTrimDisplay,
							Component.translatable("advancements.rubinated_nether.rubinated_trim.title"),
							Component.translatable("advancements.rubinated_nether.rubinated_trim.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("rubinated_trim"), existingFileHelper);

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
									Blocks.COPPER_BLOCK,
									Blocks.WAXED_COPPER_BLOCK
							).build(),
							ItemPredicate.Builder.item().of(
									Blocks.EXPOSED_COPPER,
									Blocks.WAXED_EXPOSED_COPPER
							).build(),
							ItemPredicate.Builder.item().of(
									Blocks.WEATHERED_COPPER,
									Blocks.WAXED_WEATHERED_COPPER
							).build(),
							ItemPredicate.Builder.item().of(
									Blocks.OXIDIZED_COPPER,
									Blocks.WAXED_OXIDIZED_COPPER
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
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
                    .save(consumer, RubinatedNether.id("pitfalls"), existingFileHelper);

			AdvancementHolder springs = Advancement.Builder.advancement()
					.parent(enterShrine)
					.display(RNBlocks.BRONZE_SPRING.get(),
							Component.translatable("advancements.rubinated_nether.springs.title"),
							Component.translatable("advancements.rubinated_nether.springs.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("impossible",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("springs"), existingFileHelper);

			// ---- Placeholder tab: "Rubinated Nether" (dowsing / shrine tree) ----
			// Layout test only: every advancement completes itself right away (tick trigger, no toast / chat message) so the whole tree
			// is visible without doing anything. Swap in the real criteria later (see placeholder()).
			// RUBINATE_ITEM and POCKETWATCH_SAVE have two parents in the design. They only have one real parent here (the one passed in);
			// the second line is drawn client-side and the node is centered between both parents, see RNAdvancementLinks / AdvancementWidgetMixin.
			// Sibling order (top to bottom) is defined by RNAdvancementLinks.CHILD_ORDER, not by the order below.
			AdvancementHolder phDowsing = Advancement.Builder.advancement()
					.display(Items.COMPASS,
							Component.translatable("advancements.rubinated_nether.placeholder.dowsing.title"),
							Component.translatable("advancements.rubinated_nether.placeholder.dowsing.description"),
							RubinatedNether.id("textures/gui/advancements/backgrounds/rubinated_nether.png"),
							AdvancementType.TASK, false, false, false)
					.addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
					.save(consumer, RNAdvancementLinks.DOWSING, existingFileHelper);

			AdvancementHolder phShrinestone = placeholder(consumer, existingFileHelper, phDowsing, RNAdvancementLinks.SHRINESTONE, RNBlocks.SHRINE_STONE.get(), AdvancementType.TASK);
			AdvancementHolder phFillBrazier = placeholder(consumer, existingFileHelper, phShrinestone, RNAdvancementLinks.FILL_BRAZIER, RNBlocks.BRAZIER.get(), AdvancementType.TASK);
			AdvancementHolder phRubinatedRune = placeholder(consumer, existingFileHelper, phFillBrazier, RNAdvancementLinks.RUBINATED_RUNE, RNItems.PRIDE_RUNE.get(), AdvancementType.TASK);
			AdvancementHolder phInsertRune = placeholder(consumer, existingFileHelper, phRubinatedRune, RNAdvancementLinks.INSERT_RUNE, RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get(), AdvancementType.TASK);
			AdvancementHolder phRubinateItem = placeholder(consumer, existingFileHelper, phInsertRune, RNAdvancementLinks.RUBINATE_ITEM, RNItems.RUBY.get(), AdvancementType.TASK);
			AdvancementHolder phBronzeRodOffering = placeholder(consumer, existingFileHelper, phFillBrazier, RNAdvancementLinks.BRONZE_ROD_OFFERING, RNItems.BRONZE_ROD.get(), AdvancementType.TASK);
			AdvancementHolder phGiveOffering = placeholder(consumer, existingFileHelper, phBronzeRodOffering, RNAdvancementLinks.GIVE_OFFERING, RNBlocks.RUBINATION_ALTAR.get(), AdvancementType.TASK);
			AdvancementHolder phPocketwatchSave = placeholder(consumer, existingFileHelper, phGiveOffering, RNAdvancementLinks.POCKETWATCH_SAVE, Items.TOTEM_OF_UNDYING, AdvancementType.TASK);
			AdvancementHolder phKillBronze = placeholder(consumer, existingFileHelper, phDowsing, RNAdvancementLinks.KILL_BRONZE, RNBlocks.BRONZE_BLOCK.get(), AdvancementType.TASK);
			AdvancementHolder phSummonWave = placeholder(consumer, existingFileHelper, phKillBronze, RNAdvancementLinks.SUMMON_WAVE, RNBlocks.BRONZE_SPRING.get(), AdvancementType.TASK);
			AdvancementHolder phCreatePocketwatch = placeholder(consumer, existingFileHelper, phSummonWave, RNAdvancementLinks.CREATE_POCKETWATCH, Items.CLOCK, AdvancementType.TASK);
			AdvancementHolder phAccumulateBlessing = placeholder(consumer, existingFileHelper, phCreatePocketwatch, RNAdvancementLinks.ACCUMULATE_BLESSING, RNItems.BLESSED_ICON.get(), AdvancementType.TASK);
			AdvancementHolder phMechanicalTools = placeholder(consumer, existingFileHelper, phSummonWave, RNAdvancementLinks.MECHANICAL_TOOLS, Items.IRON_PICKAXE, AdvancementType.TASK);
			AdvancementHolder phSmithModule = placeholder(consumer, existingFileHelper, phMechanicalTools, RNAdvancementLinks.SMITH_MODULE, Items.SMITHING_TABLE, AdvancementType.TASK);
			AdvancementHolder phKillVariants = placeholder(consumer, existingFileHelper, phKillBronze, RNAdvancementLinks.KILL_VARIANTS, RNBlocks.BRONZE_BLOCK.get(), AdvancementType.CHALLENGE);
			AdvancementHolder phBleedingObsidian = placeholder(consumer, existingFileHelper, phDowsing, RNAdvancementLinks.BLEEDING_OBSIDIAN, RNBlocks.BLEEDING_OBSIDIAN.get(), AdvancementType.TASK);
			AdvancementHolder phDrawingBlood = placeholder(consumer, existingFileHelper, phBleedingObsidian, RNAdvancementLinks.DRAWING_BLOOD, RNBlocks.MOLTEN_RUBY_CAULDRON.get(), AdvancementType.TASK);

		}
	}

	/** A placeholder advancement that completes immediately. Replace the criterion with the real one when the advancement gets implemented. */
	private static AdvancementHolder placeholder(Consumer<AdvancementHolder> consumer, ExistingFileHelper helper, AdvancementHolder parent, ResourceLocation id, ItemLike icon, AdvancementType type) {
		String key = "advancements." + id.getNamespace() + "." + id.getPath().replace('/', '.');
		return Advancement.Builder.advancement()
				.parent(parent)
				.display(icon,
						Component.translatable(key + ".title"),
						Component.translatable(key + ".description"),
						null,
						type, false, false, false)
				.addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
				.save(consumer, id, helper);
	}

	private static ItemUsedOnLocationTrigger.TriggerInstance itemUsedOnLocationCheckAbove(LocationPredicate.Builder location, LocationPredicate.Builder above, ItemPredicate.Builder item) {
		ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.create(LocationCheck.checkLocation(location).build(), LocationCheck.checkLocation(above, BlockPos.ZERO.above()).build(), MatchTool.toolMatches(item).build());
		return new ItemUsedOnLocationTrigger.TriggerInstance(Optional.empty(), Optional.of(contextawarepredicate));
	}

	public static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> itemUsedOnBlockCheckAbove(LocationPredicate.Builder location, LocationPredicate.Builder above, ItemPredicate.Builder item) {
		return CriteriaTriggers.ITEM_USED_ON_BLOCK.createCriterion(itemUsedOnLocationCheckAbove(location, above, item));
	}
}
