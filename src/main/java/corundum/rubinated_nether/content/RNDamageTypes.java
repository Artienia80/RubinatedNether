package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class RNDamageTypes {

    public static final ResourceKey<DamageType> CHANDELIER = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            RubinatedNether.id("chandelier")
    );
}