package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
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
import net.minecraft.world.item.component.BlockItemStateProperties;
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
