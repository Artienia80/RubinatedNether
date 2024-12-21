package corundum.rubinated_nether.content;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;


public class RNSoundEvents {
	public static final Holder.Reference<SoundEvent> MUSIC_DISC_SHIMMER = registerForHolder("music_disc.shimmer");

	private static Holder.Reference<SoundEvent> registerForHolder(String name) {
		return registerForHolder(ResourceLocation.withDefaultNamespace(name));
	}

	private static Holder.Reference<SoundEvent> registerForHolder(ResourceLocation name) {
		return registerForHolder(name, name);
	}

	private static Holder.Reference<SoundEvent> registerForHolder(ResourceLocation name, ResourceLocation location) {
		return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, name, SoundEvent.createVariableRangeEvent(location));
	}
}
