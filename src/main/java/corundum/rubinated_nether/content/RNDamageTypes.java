package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNDamageTypes {
    public static final DeferredRegister<DamageType> DAMAGE_TYPES = DeferredRegister.create(
            Registries.DAMAGE_TYPE,
            RubinatedNether.MODID
    );

    public static final DeferredHolder<DamageType, DamageType> CHANDELIER = DAMAGE_TYPES.register("chandelier",
            () -> new DamageType("chandelier", DamageScaling.ALWAYS, 0.0F, DamageEffects.HURT, DeathMessageType.FALL_VARIANTS));
}
