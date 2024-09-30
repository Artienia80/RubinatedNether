//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.artienia.rubinated_nether.content;

import net.artienia.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import uwu.serenity.critter.api.entry.RegistryEntry;
import uwu.serenity.critter.api.generic.Registrar;
import uwu.serenity.critter.stdlib.extras.ExtraBuilders;

public final class RNSounds {

    public static final Registrar<SoundEvent> SOUNDS = RubinatedNether.REGISTRIES.getRegistrar(Registries.SOUND_EVENT);

    public static final RegistryEntry<SoundEvent> SHIMMER = ExtraBuilders.sound(SOUNDS, "shimmer_disc").register();

    public static void register() {
        SOUNDS.register();
    }

}
