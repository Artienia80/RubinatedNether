//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.artienia.rubinated_nether.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY;
    public static final RegistryObject<SoundEvent> SHIMMER_DISC;

    public ModSounds() {
    }

    static {
        REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "rubinated_nether");

        SHIMMER_DISC = REGISTRY.register("shimmer_disc", () -> {
            return SoundEvent.createVariableRangeEvent(new ResourceLocation("rubinated_nether", "shimmer_disc"));
        });
    }
}
