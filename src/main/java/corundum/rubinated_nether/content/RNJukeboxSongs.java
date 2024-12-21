package corundum.rubinated_nether.content;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.JukeboxSong;

public interface RNJukeboxSongs {
    ResourceKey<JukeboxSong> SHIMMER = create("shimmer");


    private static ResourceKey<JukeboxSong> create(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.withDefaultNamespace(name));
    }

    private static void register(
            BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, Holder.Reference<SoundEvent> soundEvent, int lengthInSeconds, int comparatorOutput
    ) {
        context.register(
                key,
                new JukeboxSong(soundEvent, Component.translatable(Util.makeDescriptionId("jukebox_song", key.location())), (float)lengthInSeconds, comparatorOutput)
        );
    }

    static void bootstrap(BootstrapContext<JukeboxSong> context) {
        register(context, SHIMMER, RNSoundEvents.MUSIC_DISC_SHIMMER, 229, 13);
    }
}
