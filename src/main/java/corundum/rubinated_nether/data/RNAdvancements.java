package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
                    .save(consumer, ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "bleeding_obsidian"), existingFileHelper);
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