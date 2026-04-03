package corundum.rubinated_nether.content.world.structures.shrine;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.function.Function;

public class SacredShrinePiece extends TemplateStructurePiece {
    public SacredShrinePiece(StructurePieceType type, int genDepth, StructureTemplateManager structureTemplateManager, String templateName, StructurePlaceSettings placeSettings, BlockPos templatePosition) {
        super(type, genDepth, structureTemplateManager, makeLocation(templateName), templateName, placeSettings, templatePosition);
    }

    public SacredShrinePiece(StructurePieceType type, CompoundTag tag, StructureTemplateManager structureTemplateManager, Function<ResourceLocation, StructurePlaceSettings> placeSettingsFactory) {
        super(type, tag, structureTemplateManager, placeSettingsFactory);
    }

    @Override
    protected void handleDataMarker(String s, BlockPos blockPos, ServerLevelAccessor serverLevelAccessor, RandomSource randomSource, BoundingBox boundingBox) {

    }

    protected static ResourceLocation makeLocation(String name) {
        return RubinatedNether.id("sacred_shrine/" + name);
    }
}
