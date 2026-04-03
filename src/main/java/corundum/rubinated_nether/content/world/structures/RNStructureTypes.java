package corundum.rubinated_nether.content.world.structures;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNStructureTypes {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, RubinatedNether.MODID);

    //public static final DeferredHolder<StructureType<?>, StructureType<SacredShrineStructure>> SACRED_SHRINE = STRUCTURE_TYPES.register("sacred_shrine", () -> () -> SacredShrineStructure.CODEC);
}
