package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.RuneCarvingHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
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

			AdvancementHolder grinding = Advancement.Builder.advancement()
					.parent(complementaryOpposites)
					.display(RNItems.RUNE.get(),
							Component.translatable("advancements.rubinated_nether.grinding.title"),
							Component.translatable("advancements.rubinated_nether.grinding.description"),
							null,
							AdvancementType.TASK, true, true, false)
					.requirements(AdvancementRequirements.Strategy.AND)
					.addCriterion("blank_rune",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.addCriterion("runestone",
							PlayerTrigger.TriggerInstance.located(
									LocationPredicate.Builder.location()
											.setY(MinMaxBounds.Doubles.atMost(-250314))))
					.save(consumer, RubinatedNether.id("grinding"), existingFileHelper);

			AdvancementHolder stonecutting = Advancement.Builder.advancement()
					.parent(grinding)
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
					.parent(baptism)
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
					.parent(insertRune)
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
