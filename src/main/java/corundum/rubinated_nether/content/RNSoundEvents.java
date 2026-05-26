package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class RNSoundEvents {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(
		BuiltInRegistries.SOUND_EVENT, 
		RubinatedNether.MODID
	);

	public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_SHIMMER = SOUNDS.register(
		"shimmer", 
		() -> SoundEvent.createVariableRangeEvent(
			ResourceLocation.fromNamespaceAndPath(
				RubinatedNether.MODID, 
				"shimmer"
			)
		)
	);

	public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_SINNER = SOUNDS.register(
			"sinner",
			() -> SoundEvent.createVariableRangeEvent(
					ResourceLocation.fromNamespaceAndPath(
							RubinatedNether.MODID,
							"sinner"
					)
			)
	);
}
