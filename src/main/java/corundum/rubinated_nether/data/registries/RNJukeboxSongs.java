package corundum.rubinated_nether.data.registries;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNSoundEvents;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.JukeboxSong;

public interface RNJukeboxSongs {
	public static final ResourceKey<JukeboxSong> SHIMMER = ResourceKey.create(
		Registries.JUKEBOX_SONG, 
		ResourceLocation.fromNamespaceAndPath(
			RubinatedNether.MODID,
			"shimmer"
		)
	);
	public static final ResourceKey<JukeboxSong> SINNER = ResourceKey.create(
			Registries.JUKEBOX_SONG,
			ResourceLocation.fromNamespaceAndPath(
					RubinatedNether.MODID,
					"sinner"
			)
	);

	public static void bootstap(BootstrapContext<JukeboxSong> context) {
		context.register(
			SHIMMER, 
			new JukeboxSong(
				RNSoundEvents.MUSIC_DISC_SHIMMER,
				Component.translatable(Util.makeDescriptionId("jukebox_song", SHIMMER.location())),
				229, 
				13
			)
		);
		context.register(
				SINNER,
				new JukeboxSong(
						RNSoundEvents.MUSIC_DISC_SINNER,
						Component.translatable(Util.makeDescriptionId("jukebox_song", SINNER.location())),
						163,
						14
				)
		);
	}
}
