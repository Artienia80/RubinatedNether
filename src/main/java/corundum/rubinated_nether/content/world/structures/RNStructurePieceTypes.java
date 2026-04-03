package corundum.rubinated_nether.content.world.structures;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;

public class RNStructurePieceTypes {
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECE_TYPES = DeferredRegister.create(Registries.STRUCTURE_PIECE, RubinatedNether.MODID);

    //public static final DeferredHolder<StructurePieceType, StructurePieceType> BRASS_BOSS_ROOM = register("BBossRoom", BrassRoom.BossRoom::new);
    //public static final DeferredHolder<StructurePieceType, StructurePieceType> BRASS_ROOM = register("BRoom", BrassRoom::new);

    private static DeferredHolder<StructurePieceType, StructurePieceType> register(String name, StructurePieceType structurePieceType) {
        return STRUCTURE_PIECE_TYPES.register(name.toLowerCase(Locale.ROOT), () -> structurePieceType);
    }
}
