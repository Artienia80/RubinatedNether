//package corundum.rubinated_nether.content.world.structures.shrine;
//
//import com.mojang.serialization.MapCodec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import corundum.rubinated_nether.content.world.structures.RNStructureTypes;
//import net.minecraft.core.BlockPos;
//import net.minecraft.util.RandomSource;
//import net.minecraft.world.level.block.Rotation;
//import net.minecraft.world.level.levelgen.structure.Structure;
//import net.minecraft.world.level.levelgen.structure.StructurePiece;
//import net.minecraft.world.level.levelgen.structure.StructureType;
//import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
//import net.minecraft.world.level.levelgen.structure.structures.StrongholdPieces;
//import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
//
//import java.util.List;
//import java.util.Optional;
//
//public class SacredShrineStructure extends Structure {
//
//    public static final MapCodec<SacredShrineStructure> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
//            settingsCodec(builder),
//            BrassProcessorSettings.CODEC.fieldOf("processor_settings").forGetter(o -> o.processors)
//    ).apply(builder, SacredShrineStructure::new));
//
//    protected SacredShrineStructure(StructureSettings settings) {
//        super(settings);
//    }
//
//    private static void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
//        int i = 0;
//
//        SacredShrinePieces.StartPiece startPiece;
//        do {
//            builder.clear();
//            context.random().setLargeFeatureSeed(context.seed() + (long)(i++), context.chunkPos().x, context.chunkPos().z);
//            StrongholdPieces.resetPieces();
//            startPiece = new SacredShrinePieces.StartPiece(context.random(), context.chunkPos().getBlockX(2), context.chunkPos().getBlockZ(2));
//            builder.addPiece(startPiece);
//            startPiece.addChildren(startPiece, builder, context.random());
//            List<StructurePiece> list = startPiece.pendingChildren;
//
//            while(!list.isEmpty()) {
//                int j = context.random().nextInt(list.size());
//                StructurePiece structurepiece = list.remove(j);
//                structurepiece.addChildren(startPiece, builder, context.random());
//            }
//
//            builder.moveBelowSeaLevel(context.chunkGenerator().getSeaLevel(), context.chunkGenerator().getMinY(), context.random(), 10);
//        } while(builder.isEmpty() || startPiece.portalRoomPiece == null);
//
//    }
//
//    @Override
//    protected Optional<GenerationStub> findGenerationPoint(GenerationContext generationContext) {
//        return Optional.empty();
//    }
//
//    @Override
//    public StructureType<SacredShrineStructure> type() {
//        return RNStructureTypes.SACRED_SHRINE.get();
//    }
//
//    private String getRandomRoomType(RandomSource random) {
//        int num = random.nextInt(77);
//
//        if(num <= 25)
//            return "brass_dungeon_room_4"; //Empty
//        else if(num <=45)
//            return "brass_dungeon_room_0"; //Interior
//        else if(num <=55)
//            return "brass_dungeon_room_2"; //Garden
//        else if(num <= 70)
//            return "brass_dungeon_room_1"; //Library
//        else return "brass_dungeon_room_3"; //Infested
//    }
//
//    private void createBossRoom(RandomSource random, StructurePiecesBuilder builder, BlockPos pos, Rotation rotation, StructureTemplateManager templateManager, boolean parent) {
//        String room = this.getRandomRoomType(random);
//        if (parent)
//            builder.addPiece(new BrassRoom.BossRoom(templateManager, room + "_boss", pos, rotation, this.processors.bossSettings()));
//        else builder.addPiece(new BrassRoom(templateManager, room, pos, rotation, this.processors.roomSettings()));
//    }
//}
