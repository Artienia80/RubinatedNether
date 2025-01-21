package corundum.rubinated_nether.misc;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.recipe.fuel.FreezerFuel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public final class DatapackRegistry {
	public static final ResourceKey<Registry<FreezerFuel>> FREEZER_FUELS = ResourceKey.createRegistryKey(
		RubinatedNether.id("freezer_fuel")
	);

	public static void datapackRegistry(final DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(FREEZER_FUELS, FreezerFuel.FREEZER_FUEL_CODEC);
	}
}
