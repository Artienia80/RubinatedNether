package corundum.rubinated_nether.content.world.cinder;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNFeatures {
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, RubinatedNether.MODID);

	public static final DeferredHolder<Feature<?>, CinderOverhangFeature> CINDER_OVERHANG = FEATURES.register(
			"cinder_overhang",
			() -> new CinderOverhangFeature(NoneFeatureConfiguration.CODEC)
	);

	public static final DeferredHolder<Feature<?>, CinderBridgeFeature> CINDER_BRIDGE = FEATURES.register(
			"cinder_bridge",
			() -> new CinderBridgeFeature(NoneFeatureConfiguration.CODEC)
	);
}
