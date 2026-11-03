package corundum.rubinated_nether.data;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.loot.PomegranateSnifferLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class RNGlobalLootModifiers extends GlobalLootModifierProvider {
	public RNGlobalLootModifiers(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, RubinatedNether.MODID);
	}

	@Override
	protected void start() {
		this.add("pomegranate_sniffer_digging", new PomegranateSnifferLootModifier(new LootItemCondition[0]));
	}
}
