package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNRarity;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.items.MechanicalFuel;
import corundum.rubinated_nether.content.items.MechanicalFuelData;
import corundum.rubinated_nether.content.items.PocketwatchItem;
import corundum.rubinated_nether.content.blocks.BronzeStrongboxBlock;
import corundum.rubinated_nether.content.blocks.GearboxBlock;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.RuneCarvingHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class RNAdvancements extends AdvancementProvider {
	public RNAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
		super(output, registries, helper, List.of(new RNAdvancement()));
	}

	public static class RNAdvancement implements AdvancementGenerator {
		private static ItemStack glintTool(net.minecraft.world.item.Item item) {
			ItemStack stack = new ItemStack(item);
			stack.set(RNDataComponents.MECHANICAL_FUEL.get(), MechanicalFuelData.of(Items.AIR, 0, 1));
			MechanicalFuel.syncRarity(stack);
			return stack;
		}

		@SuppressWarnings("unused")
		@Override
		public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {

			Holder<Structure> rubinationShrine = Holder.Reference.createStandAlone(
					provider.lookupOrThrow(Registries.STRUCTURE),
					ResourceKey.create(Registries.STRUCTURE, RubinatedNether.id("rubination_shrine")));

			AdvancementHolder complementaryOpposites = Advancement.Builder.advancement()
					.display(RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get(),
							Component.translatable("advancements.rubinated_nether.complementary_opposites.title"),
							Component.translatable("advancements.rubinated_nether.complementary_opposites.description"),
							RubinatedNether.id("textures/block/shrine_stone/shrine_stone_bricks.png"),
							AdvancementType.TASK, true, false, false)
					.addCriterion("enter_shrine", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(rubinationShrine)))
					.save(consumer, RubinatedNether.id("complementary_opposites"), existingFileHelper);

			ItemStack naturalGearbox = new ItemStack(RNBlocks.BRONZE_GEARBOX.get());
			naturalGearbox.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(GearboxBlock.NATURAL, true));

			AdvancementHolder witness = Advancement.Builder.advancement()
					.parent(complementaryOpposites)
					.display(naturalGearbox,
							Component.translatable("advancements.rubinated_nether.witness.title"),
							Component.translatable("advancements.rubinated_nether.witness.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("witness",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("witness"), existingFileHelper);

			ItemStack naturalStrongbox = new ItemStack(RNBlocks.BRONZE_STRONGBOX.get());
			naturalStrongbox.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(BronzeStrongboxBlock.NATURAL, true));

			AdvancementHolder windingKey = Advancement.Builder.advancement()
					.parent(witness)
					.display(RNItems.WINDING_KEY.get(),
							Component.translatable("advancements.rubinated_nether.winding_key.title"),
							Component.translatable("advancements.rubinated_nether.winding_key.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("winding_key", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.WINDING_KEY.get()))
					.save(consumer, RubinatedNether.id("winding_key"), existingFileHelper);

			AdvancementHolder strongbox = Advancement.Builder.advancement()
					.parent(windingKey)
					.display(naturalStrongbox,
							Component.translatable("advancements.rubinated_nether.strongbox.title"),
							Component.translatable("advancements.rubinated_nether.strongbox.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("strongbox",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("strongbox"), existingFileHelper);

			AdvancementHolder cogwheel = Advancement.Builder.advancement()
					.parent(strongbox)
					.display(RNItems.COGWHEEL.get(),
							Component.translatable("advancements.rubinated_nether.cogwheel.title"),
							Component.translatable("advancements.rubinated_nether.cogwheel.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("cogwheel", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.COGWHEEL.get()))
					.save(consumer, RubinatedNether.id("cogwheel"), existingFileHelper);

			AdvancementHolder constructGearbox = Advancement.Builder.advancement()
					.parent(cogwheel)
					.display(RNBlocks.BRONZE_GEARBOX.get(),
							Component.translatable("advancements.rubinated_nether.construct_gearbox.title"),
							Component.translatable("advancements.rubinated_nether.construct_gearbox.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("construct_gearbox", RecipeCraftedTrigger.TriggerInstance.craftedItem(RubinatedNether.id("bronze_gearbox")))
					.save(consumer, RubinatedNether.id("construct_gearbox"), existingFileHelper);

			AdvancementHolder constructStrongbox = Advancement.Builder.advancement()
					.parent(cogwheel)
					.display(RNBlocks.BRONZE_STRONGBOX.get(),
							Component.translatable("advancements.rubinated_nether.construct_strongbox.title"),
							Component.translatable("advancements.rubinated_nether.construct_strongbox.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("construct_strongbox", RecipeCraftedTrigger.TriggerInstance.craftedItem(RubinatedNether.id("bronze_strongbox")))
					.save(consumer, RubinatedNether.id("construct_strongbox"), existingFileHelper);

			ItemStack chargedWatchIcon = new ItemStack(RNItems.POCKETWATCH.get());
			chargedWatchIcon.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));

			ItemStack overchargedWatchIcon = new ItemStack(RNItems.POCKETWATCH.get());
			overchargedWatchIcon.set(DataComponents.RARITY, RNRarity.RUBINATED_NETHER_RUBY.get());

			ItemStack rewindWatchIcon = new ItemStack(RNItems.POCKETWATCH.get());
			rewindWatchIcon.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(PocketwatchItem.REWIND_MODEL_VALUE));

			ItemStack brokenWatchIcon = new ItemStack(RNItems.POCKETWATCH.get());
			brokenWatchIcon.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(PocketwatchItem.BROKEN_MODEL_VALUE));

			AdvancementHolder craftPocketwatch = Advancement.Builder.advancement()
					.parent(constructGearbox)
					.display(RNItems.POCKETWATCH.get(),
							Component.translatable("advancements.rubinated_nether.craft_pocketwatch.title"),
							Component.translatable("advancements.rubinated_nether.craft_pocketwatch.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("craft_pocketwatch",
							RecipeCraftedTrigger.TriggerInstance.craftedItem(RubinatedNether.id("pocketwatch")))
					.save(consumer, RubinatedNether.id("craft_pocketwatch"), existingFileHelper);

			AdvancementHolder chargePocketwatch = Advancement.Builder.advancement()
					.parent(craftPocketwatch)
					.display(chargedWatchIcon,
							Component.translatable("advancements.rubinated_nether.charge_pocketwatch.title"),
							Component.translatable("advancements.rubinated_nether.charge_pocketwatch.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("charge_pocketwatch",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("charge_pocketwatch"), existingFileHelper);

			AdvancementHolder overchargePocketwatch = Advancement.Builder.advancement()
					.parent(chargePocketwatch)
					.display(overchargedWatchIcon,
							Component.translatable("advancements.rubinated_nether.overcharge_pocketwatch.title"),
							Component.translatable("advancements.rubinated_nether.overcharge_pocketwatch.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("overcharge_pocketwatch",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("overcharge_pocketwatch"), existingFileHelper);

			AdvancementHolder escapeDeath = Advancement.Builder.advancement()
					.parent(overchargePocketwatch)
					.display(rewindWatchIcon,
							Component.translatable("advancements.rubinated_nether.escape_death.title"),
							Component.translatable("advancements.rubinated_nether.escape_death.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("escape_death",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("escape_death"), existingFileHelper);

			AdvancementHolder repairPocketwatch = Advancement.Builder.advancement()
					.parent(escapeDeath)
					.display(brokenWatchIcon,
							Component.translatable("advancements.rubinated_nether.repair_pocketwatch.title"),
							Component.translatable("advancements.rubinated_nether.repair_pocketwatch.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("repair_pocketwatch",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("repair_pocketwatch"), existingFileHelper);

			ItemStack drillGlintIcon = glintTool(RNItems.BRONZE_DRILL.get());
			ItemStack jackhammerGlintIcon = glintTool(RNItems.BRONZE_JACKHAMMER.get());
			ItemStack chainsawGlintIcon = glintTool(RNItems.BRONZE_CHAINSAW.get());

			AdvancementHolder summonWave = Advancement.Builder.advancement()
					.parent(constructGearbox)
					.display(RNBlocks.BRONZE_GEARBOX.get(),
							Component.translatable("advancements.rubinated_nether.summon_wave.title"),
							Component.translatable("advancements.rubinated_nether.summon_wave.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("summon_wave",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("summon_wave"), existingFileHelper);

			AdvancementHolder keyBreak = Advancement.Builder.advancement()
					.parent(summonWave)
					.display(RNItems.WINDING_KEY.get(),
							Component.translatable("advancements.rubinated_nether.key_break.title"),
							Component.translatable("advancements.rubinated_nether.key_break.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("key_break",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("key_break"), existingFileHelper);

			AdvancementHolder createDrill = Advancement.Builder.advancement()
					.parent(constructGearbox)
					.display(RNItems.BRONZE_DRILL.get(),
							Component.translatable("advancements.rubinated_nether.create_drill.title"),
							Component.translatable("advancements.rubinated_nether.create_drill.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("create_drill",
							RecipeCraftedTrigger.TriggerInstance.craftedItem(RubinatedNether.id("bronze_drill")))
					.save(consumer, RubinatedNether.id("create_drill"), existingFileHelper);

			AdvancementHolder createJackhammer = Advancement.Builder.advancement()
					.parent(constructGearbox)
					.display(RNItems.BRONZE_JACKHAMMER.get(),
							Component.translatable("advancements.rubinated_nether.create_jackhammer.title"),
							Component.translatable("advancements.rubinated_nether.create_jackhammer.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("create_jackhammer",
							RecipeCraftedTrigger.TriggerInstance.craftedItem(RubinatedNether.id("bronze_jackhammer")))
					.save(consumer, RubinatedNether.id("create_jackhammer"), existingFileHelper);

			AdvancementHolder createChainsaw = Advancement.Builder.advancement()
					.parent(constructGearbox)
					.display(RNItems.BRONZE_CHAINSAW.get(),
							Component.translatable("advancements.rubinated_nether.create_chainsaw.title"),
							Component.translatable("advancements.rubinated_nether.create_chainsaw.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("create_chainsaw",
							RecipeCraftedTrigger.TriggerInstance.craftedItem(RubinatedNether.id("bronze_chainsaw")))
					.save(consumer, RubinatedNether.id("create_chainsaw"), existingFileHelper);

			AdvancementHolder drillEndurance = Advancement.Builder.advancement()
					.parent(createDrill)
					.display(drillGlintIcon,
							Component.translatable("advancements.rubinated_nether.drill_endurance.title"),
							Component.translatable("advancements.rubinated_nether.drill_endurance.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("drill_endurance",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("drill_endurance"), existingFileHelper);

			AdvancementHolder jackhammerObsidian = Advancement.Builder.advancement()
					.parent(createJackhammer)
					.display(jackhammerGlintIcon,
							Component.translatable("advancements.rubinated_nether.jackhammer_obsidian.title"),
							Component.translatable("advancements.rubinated_nether.jackhammer_obsidian.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("jackhammer_obsidian",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("jackhammer_obsidian"), existingFileHelper);

			AdvancementHolder chainsawTimber = Advancement.Builder.advancement()
					.parent(createChainsaw)
					.display(chainsawGlintIcon,
							Component.translatable("advancements.rubinated_nether.chainsaw_timber.title"),
							Component.translatable("advancements.rubinated_nether.chainsaw_timber.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("chainsaw_timber",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("chainsaw_timber"), existingFileHelper);

			AdvancementHolder addModule = Advancement.Builder.advancement()
					.parent(chainsawTimber)
					.display(Items.NETHERITE_SCRAP,
							Component.translatable("advancements.rubinated_nether.add_module.title"),
							Component.translatable("advancements.rubinated_nether.add_module.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("add_module",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("add_module"), existingFileHelper);

			AdvancementHolder fuelUp = Advancement.Builder.advancement()
					.parent(addModule)
					.display(RNBlocks.MOLTEN_RUBY_BLOCK.get(),
							Component.translatable("advancements.rubinated_nether.fuel_up.title"),
							Component.translatable("advancements.rubinated_nether.fuel_up.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("fuel_up",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("fuel_up"), existingFileHelper);

			AdvancementHolder bronzeRod = Advancement.Builder.advancement()
					.parent(witness)
					.display(RNItems.BRONZE_ROD.get(),
							Component.translatable("advancements.rubinated_nether.bronze_rod.title"),
							Component.translatable("advancements.rubinated_nether.bronze_rod.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("bronze_rod", InventoryChangeTrigger.TriggerInstance.hasItems(RNItems.BRONZE_ROD.get()))
					.save(consumer, RubinatedNether.id("bronze_rod"), existingFileHelper);

			AdvancementHolder baptiseRod = Advancement.Builder.advancement()
					.parent(bronzeRod)
					.display(RNBlocks.BRAZIER.get(),
							Component.translatable("advancements.rubinated_nether.baptise_rod.title"),
							Component.translatable("advancements.rubinated_nether.baptise_rod.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("baptised_rod",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("baptise_rod"), existingFileHelper);

			AdvancementHolder runestone = Advancement.Builder.advancement()
					.parent(complementaryOpposites)
					.display(RNBlocks.RUNESTONE.get(),
							Component.translatable("advancements.rubinated_nether.runestone.title"),
							Component.translatable("advancements.rubinated_nether.runestone.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("runestone",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("runestone"), existingFileHelper);

			AdvancementHolder blankSlate = Advancement.Builder.advancement()
					.parent(complementaryOpposites)
					.display(RNItems.RUNE.get(),
							Component.translatable("advancements.rubinated_nether.blank_slate.title"),
							Component.translatable("advancements.rubinated_nether.blank_slate.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("blank_rune",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("blank_slate"), existingFileHelper);

			AdvancementHolder placeRunestone = Advancement.Builder.advancement()
					.parent(runestone)
					.display(RNBlocks.RUBINATION_ALTAR.get(),
							Component.translatable("advancements.rubinated_nether.place_runestone.title"),
							Component.translatable("advancements.rubinated_nether.place_runestone.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("place_runestone",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("place_runestone"), existingFileHelper);

			AdvancementHolder stonecutting = Advancement.Builder.advancement()
					.parent(blankSlate)
					.display(RuneCarvingHelper.withCarving(new ItemStack(RNItems.RUNE.get()), Rubination.SLOTH),
							Component.translatable("advancements.rubinated_nether.stonecutting.title"),
							Component.translatable("advancements.rubinated_nether.stonecutting.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("carved_rune",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("stonecutting"), existingFileHelper);

			AdvancementHolder baptism = Advancement.Builder.advancement()
					.parent(stonecutting)
					.display(RNItems.SLOTH_RUNE.get(),
							Component.translatable("advancements.rubinated_nether.baptism.title"),
							Component.translatable("advancements.rubinated_nether.baptism.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("baptised_rune",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("baptism"), existingFileHelper);

			AdvancementHolder insertRune = Advancement.Builder.advancement()
					.parent(placeRunestone)
					.display(RNBlocks.RUNESTONE.get(),
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

			AdvancementHolder ritualOffering = Advancement.Builder.advancement()
					.parent(baptism)
					.display(RNItems.RITUAL_OFFERING.get(),
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

			AdvancementHolder ready = Advancement.Builder.advancement()
					.parent(ritualOffering)
					.display(RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get(),
							Component.translatable("advancements.rubinated_nether.ready.title"),
							Component.translatable("advancements.rubinated_nether.ready.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("ready",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("ready"), existingFileHelper);

			AdvancementHolder rubinateItem = Advancement.Builder.advancement()
					.parent(ready)
					.display(RNBlocks.RUBINATION_ALTAR.get(),
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
		}
	}
}
