package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.effect.BlessedEffect;
import corundum.rubinated_nether.content.effect.BronzeDiseasedEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, RubinatedNether.MODID);

    public static final Holder<MobEffect>  BRONZE_DISEASED = MOB_EFFECTS.register("bronze_diseased",
            () -> new BronzeDiseasedEffect());

    public static final Holder<MobEffect>  BLESSED = MOB_EFFECTS.register("blessed",
            () -> new BlessedEffect());


    public static void register(IEventBus bus){
        MOB_EFFECTS.register(bus);
    }
}
