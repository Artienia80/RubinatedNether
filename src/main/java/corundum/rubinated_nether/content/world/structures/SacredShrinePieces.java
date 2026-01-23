package corundum.rubinated_nether.content.world.structures;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallTorchBlock;

public class SacredShrinePieces {
        private static final int SMALL_DOOR_WIDTH = 3;
        private static final int SMALL_DOOR_HEIGHT = 3;
        private static final int MAX_DEPTH = 50;
        private static final int LOWEST_Y_POSITION = 10;
        private static final boolean CHECK_AIR = true;
        public static final int MAGIC_START_Y = 64;
        private static final SacredShrinePieces.PieceWeight[] STRONGHOLD_PIECE_WEIGHTS = new SacredShrinePieces.PieceWeight[]{
                new SacredShrinePieces.PieceWeight(SacredShrinePieces.Straight.class, 40, 0),
                new SacredShrinePieces.PieceWeight(SacredShrinePieces.PrisonHall.class, 5, 5),
                new SacredShrinePieces.PieceWeight(SacredShrinePieces.LeftTurn.class, 20, 0),
                new SacredShrinePieces.PieceWeight(SacredShrinePieces.RightTurn.class, 20, 0),
                new SacredShrinePieces.PieceWeight(SacredShrinePieces.RoomCrossing.class, 10, 6),
                new SacredShrinePieces.PieceWeight(SacredShrinePieces.StraightStairsDown.class, 5, 5),
                new SacredShrinePieces.PieceWeight(SacredShrinePieces.StairsDown.class, 5, 5),
                new SacredShrinePieces.PieceWeight(SacredShrinePieces.FiveCrossing.class, 5, 4),
                new SacredShrinePieces.PieceWeight(SacredShrinePieces.ChestCorridor.class, 5, 4),
                new SacredShrinePieces.PieceWeight(SacredShrinePieces.Library.class, 10, 2) {
                    @Override
                    public boolean doPlace(int p_229450_) {
                        return super.doPlace(p_229450_) && p_229450_ > 4;
                    }
                },
                new SacredShrinePieces.PieceWeight(SacredShrinePieces.PortalRoom.class, 20, 1) {
                    @Override
                    public boolean doPlace(int p_229456_) {
                        return super.doPlace(p_229456_) && p_229456_ > 5;
                    }
                }
        };
        private static List<SacredShrinePieces.PieceWeight> currentPieces;
        static Class<? extends SacredShrinePiece> imposedPiece;
        private static int totalWeight;
        static final SacredShrinePieces.SmoothStoneSelector SMOOTH_STONE_SELECTOR = new SacredShrinePieces.SmoothStoneSelector();

        public static void resetPieces() {
            currentPieces = Lists.newArrayList();

            for (SacredShrinePieces.PieceWeight strongholdpieces$pieceweight : STRONGHOLD_PIECE_WEIGHTS) {
                strongholdpieces$pieceweight.placeCount = 0;
                currentPieces.add(strongholdpieces$pieceweight);
            }

            imposedPiece = null;
        }

        private static boolean updatePieceWeight() {
            boolean flag = false;
            totalWeight = 0;

            for (SacredShrinePieces.PieceWeight strongholdpieces$pieceweight : currentPieces) {
                if (strongholdpieces$pieceweight.maxPlaceCount > 0 && strongholdpieces$pieceweight.placeCount < strongholdpieces$pieceweight.maxPlaceCount) {
                    flag = true;
                }

                totalWeight = totalWeight + strongholdpieces$pieceweight.weight;
            }

            return flag;
        }

        private static SacredShrinePiece findAndCreatePieceFactory(
                Class<? extends SacredShrinePiece> pieceClass,
                StructurePieceAccessor pieces,
                RandomSource random,
                int x,
                int y,
                int z,
                @Nullable Direction direction,
                int genDepth
        ) {
            SacredShrinePiece strongholdpieces$strongholdpiece = null;
            if (pieceClass == SacredShrinePieces.Straight.class) {
                strongholdpieces$strongholdpiece = SacredShrinePieces.Straight.createPiece(
                        pieces, random, x, y, z, direction, genDepth
                );
            } else if (pieceClass == SacredShrinePieces.PrisonHall.class) {
                strongholdpieces$strongholdpiece = SacredShrinePieces.PrisonHall.createPiece(
                        pieces, random, x, y, z, direction, genDepth
                );
            } else if (pieceClass == SacredShrinePieces.LeftTurn.class) {
                strongholdpieces$strongholdpiece = SacredShrinePieces.LeftTurn.createPiece(
                        pieces, random, x, y, z, direction, genDepth
                );
            } else if (pieceClass == SacredShrinePieces.RightTurn.class) {
                strongholdpieces$strongholdpiece = SacredShrinePieces.RightTurn.createPiece(
                        pieces, random, x, y, z, direction, genDepth
                );
            } else if (pieceClass == SacredShrinePieces.RoomCrossing.class) {
                strongholdpieces$strongholdpiece = SacredShrinePieces.RoomCrossing.createPiece(
                        pieces, random, x, y, z, direction, genDepth
                );
            } else if (pieceClass == SacredShrinePieces.StraightStairsDown.class) {
                strongholdpieces$strongholdpiece = SacredShrinePieces.StraightStairsDown.createPiece(
                        pieces, random, x, y, z, direction, genDepth
                );
            } else if (pieceClass == SacredShrinePieces.StairsDown.class) {
                strongholdpieces$strongholdpiece = SacredShrinePieces.StairsDown.createPiece(
                        pieces, random, x, y, z, direction, genDepth
                );
            } else if (pieceClass == SacredShrinePieces.FiveCrossing.class) {
                strongholdpieces$strongholdpiece = SacredShrinePieces.FiveCrossing.createPiece(
                        pieces, random, x, y, z, direction, genDepth
                );
            } else if (pieceClass == SacredShrinePieces.ChestCorridor.class) {
                strongholdpieces$strongholdpiece = SacredShrinePieces.ChestCorridor.createPiece(
                        pieces, random, x, y, z, direction, genDepth
                );
            } else if (pieceClass == SacredShrinePieces.Library.class) {
                strongholdpieces$strongholdpiece = SacredShrinePieces.Library.createPiece(pieces, random, x, y, z, direction, genDepth);
            } else if (pieceClass == SacredShrinePieces.PortalRoom.class) {
                strongholdpieces$strongholdpiece = SacredShrinePieces.PortalRoom.createPiece(pieces, x, y, z, direction, genDepth);
            }

            return strongholdpieces$strongholdpiece;
        }

        private static SacredShrinePiece generatePieceFromSmallDoor(
                SacredShrinePieces.StartPiece piece,
                StructurePieceAccessor pieces,
                RandomSource random,
                int x,
                int y,
                int z,
                Direction direction,
                int genDepth
        ) {
            if (!updatePieceWeight()) {
                return null;
            } else {
                if (imposedPiece != null) {
                    SacredShrinePiece strongholdpieces$strongholdpiece = findAndCreatePieceFactory(
                            imposedPiece, pieces, random, x, y, z, direction, genDepth
                    );
                    imposedPiece = null;
                    if (strongholdpieces$strongholdpiece != null) {
                        return strongholdpieces$strongholdpiece;
                    }
                }

                int j = 0;

                while (j < 5) {
                    j++;
                    int i = random.nextInt(totalWeight);

                    for (SacredShrinePieces.PieceWeight strongholdpieces$pieceweight : currentPieces) {
                        i -= strongholdpieces$pieceweight.weight;
                        if (i < 0) {
                            if (!strongholdpieces$pieceweight.doPlace(genDepth) || strongholdpieces$pieceweight == piece.previousPiece) {
                                break;
                            }

                            SacredShrinePiece strongholdpieces$strongholdpiece1 = findAndCreatePieceFactory(
                                    strongholdpieces$pieceweight.pieceClass, pieces, random, x, y, z, direction, genDepth
                            );
                            if (strongholdpieces$strongholdpiece1 != null) {
                                strongholdpieces$pieceweight.placeCount++;
                                piece.previousPiece = strongholdpieces$pieceweight;
                                if (!strongholdpieces$pieceweight.isValid()) {
                                    currentPieces.remove(strongholdpieces$pieceweight);
                                }

                                return strongholdpieces$strongholdpiece1;
                            }
                        }
                    }
                }

                BoundingBox boundingbox = SacredShrinePieces.FillerCorridor.findPieceBox(pieces, random, x, y, z, direction);
                return boundingbox != null && boundingbox.minY() > 1 ? new SacredShrinePieces.FillerCorridor(genDepth, boundingbox, direction) : null;
            }
        }

        static StructurePiece generateAndAddPiece(
                SacredShrinePieces.StartPiece piece,
                StructurePieceAccessor pieces,
                RandomSource random,
                int x,
                int y,
                int z,
                @Nullable Direction direction,
                int genDepth
        ) {
            if (genDepth > 50) {
                return null;
            } else if (Math.abs(x - piece.getBoundingBox().minX()) <= 112 && Math.abs(z - piece.getBoundingBox().minZ()) <= 112) {
                StructurePiece structurepiece = generatePieceFromSmallDoor(
                        piece, pieces, random, x, y, z, direction, genDepth + 1
                );
                if (structurepiece != null) {
                    pieces.addPiece(structurepiece);
                    piece.pendingChildren.add(structurepiece);
                }

                return structurepiece;
            } else {
                return null;
            }
        }

        public static class ChestCorridor extends SacredShrinePiece {
            private static final int WIDTH = 5;
            private static final int HEIGHT = 5;
            private static final int DEPTH = 7;
            private boolean hasPlacedChest;

            public ChestCorridor(int genDepth, RandomSource random, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, genDepth, box);
                this.setOrientation(orientation);
                this.entryDoor = this.randomSmallDoor(random);
            }

            public ChestCorridor(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, tag);
                this.hasPlacedChest = tag.getBoolean("Chest");
            }

            @Override
            protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
                super.addAdditionalSaveData(context, tag);
                tag.putBoolean("Chest", this.hasPlacedChest);
            }

            @Override
            public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
                this.generateSmallDoorChildForward((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 1);
            }

            public static SacredShrinePieces.ChestCorridor createPiece(
                    StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction orientation, int genDepth
            ) {
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, 7, orientation);
                return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null
                        ? new SacredShrinePieces.ChestCorridor(genDepth, random, boundingbox, orientation)
                        : null;
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                this.generateBox(level, box, 0, 0, 0, 4, 4, 6, true, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateSmallDoor(level, random, box, this.entryDoor, 1, 1, 0);
                this.generateSmallDoor(level, random, box, SacredShrinePiece.SmallDoorType.OPENING, 1, 1, 6);
                this.generateBox(level, box, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.defaultBlockState(), Blocks.STONE_BRICKS.defaultBlockState(), false);
                this.placeBlock(level, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 1, box);
                this.placeBlock(level, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 5, box);
                this.placeBlock(level, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 2, box);
                this.placeBlock(level, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 4, box);

                for (int i = 2; i <= 4; i++) {
                    this.placeBlock(level, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2, 1, i, box);
                }

                if (!this.hasPlacedChest && box.isInside(this.getWorldPos(3, 2, 3))) {
                    this.hasPlacedChest = true;
                    this.createChest(level, box, random, 3, 2, 3, BuiltInLootTables.STRONGHOLD_CORRIDOR);
                }
            }
        }

        public static class FillerCorridor extends SacredShrinePiece {
            private final int steps;

            public FillerCorridor(int genDepth, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, genDepth, box);
                this.setOrientation(orientation);
                this.steps = orientation != Direction.NORTH && orientation != Direction.SOUTH ? box.getXSpan() : box.getZSpan();
            }

            public FillerCorridor(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, tag);
                this.steps = tag.getInt("Steps");
            }

            @Override
            protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
                super.addAdditionalSaveData(context, tag);
                tag.putInt("Steps", this.steps);
            }

            public static BoundingBox findPieceBox(
                    StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction orientation
            ) {
                int i = 3;
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, 4, orientation);
                StructurePiece structurepiece = pieces.findCollisionPiece(boundingbox);
                if (structurepiece == null) {
                    return null;
                } else {
                    if (structurepiece.getBoundingBox().minY() == boundingbox.minY()) {
                        for (int j = 2; j >= 1; j--) {
                            boundingbox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, j, orientation);
                            if (!structurepiece.getBoundingBox().intersects(boundingbox)) {
                                return BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, j + 1, orientation);
                            }
                        }
                    }

                    return null;
                }
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                for (int i = 0; i < this.steps; i++) {
                    this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 0, 0, i, box);
                    this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 1, 0, i, box);
                    this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 2, 0, i, box);
                    this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 3, 0, i, box);
                    this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 4, 0, i, box);

                    for (int j = 1; j <= 3; j++) {
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 0, j, i, box);
                        this.placeBlock(level, Blocks.CAVE_AIR.defaultBlockState(), 1, j, i, box);
                        this.placeBlock(level, Blocks.CAVE_AIR.defaultBlockState(), 2, j, i, box);
                        this.placeBlock(level, Blocks.CAVE_AIR.defaultBlockState(), 3, j, i, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 4, j, i, box);
                    }

                    this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 0, 4, i, box);
                    this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, i, box);
                    this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, i, box);
                    this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 3, 4, i, box);
                    this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 4, 4, i, box);
                }
            }
        }

        public static class FiveCrossing extends SacredShrinePiece {
            protected static final int WIDTH = 10;
            protected static final int HEIGHT = 9;
            protected static final int DEPTH = 11;
            private final boolean leftLow;
            private final boolean leftHigh;
            private final boolean rightLow;
            private final boolean rightHigh;

            public FiveCrossing(int genDepth, RandomSource random, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, genDepth, box);
                this.setOrientation(orientation);
                this.entryDoor = this.randomSmallDoor(random);
                this.leftLow = random.nextBoolean();
                this.leftHigh = random.nextBoolean();
                this.rightLow = random.nextBoolean();
                this.rightHigh = random.nextInt(3) > 0;
            }

            public FiveCrossing(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, tag);
                this.leftLow = tag.getBoolean("leftLow");
                this.leftHigh = tag.getBoolean("leftHigh");
                this.rightLow = tag.getBoolean("rightLow");
                this.rightHigh = tag.getBoolean("rightHigh");
            }

            @Override
            protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
                super.addAdditionalSaveData(context, tag);
                tag.putBoolean("leftLow", this.leftLow);
                tag.putBoolean("leftHigh", this.leftHigh);
                tag.putBoolean("rightLow", this.rightLow);
                tag.putBoolean("rightHigh", this.rightHigh);
            }

            @Override
            public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
                int i = 3;
                int j = 5;
                Direction direction = this.getOrientation();
                if (direction == Direction.WEST || direction == Direction.NORTH) {
                    i = 8 - i;
                    j = 8 - j;
                }

                this.generateSmallDoorChildForward((SacredShrinePieces.StartPiece)piece, pieces, random, 5, 1);
                if (this.leftLow) {
                    this.generateSmallDoorChildLeft((SacredShrinePieces.StartPiece)piece, pieces, random, i, 1);
                }

                if (this.leftHigh) {
                    this.generateSmallDoorChildLeft((SacredShrinePieces.StartPiece)piece, pieces, random, j, 7);
                }

                if (this.rightLow) {
                    this.generateSmallDoorChildRight((SacredShrinePieces.StartPiece)piece, pieces, random, i, 1);
                }

                if (this.rightHigh) {
                    this.generateSmallDoorChildRight((SacredShrinePieces.StartPiece)piece, pieces, random, j, 7);
                }
            }

            public static SacredShrinePieces.FiveCrossing createPiece(
                    StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction orientation, int genDepth
            ) {
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -4, -3, 0, 10, 9, 11, orientation);
                return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null
                        ? new SacredShrinePieces.FiveCrossing(genDepth, random, boundingbox, orientation)
                        : null;
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                this.generateBox(level, box, 0, 0, 0, 9, 8, 10, true, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateSmallDoor(level, random, box, this.entryDoor, 4, 3, 0);
                if (this.leftLow) {
                    this.generateBox(level, box, 0, 3, 1, 0, 5, 3, CAVE_AIR, CAVE_AIR, false);
                }

                if (this.rightLow) {
                    this.generateBox(level, box, 9, 3, 1, 9, 5, 3, CAVE_AIR, CAVE_AIR, false);
                }

                if (this.leftHigh) {
                    this.generateBox(level, box, 0, 5, 7, 0, 7, 9, CAVE_AIR, CAVE_AIR, false);
                }

                if (this.rightHigh) {
                    this.generateBox(level, box, 9, 5, 7, 9, 7, 9, CAVE_AIR, CAVE_AIR, false);
                }

                this.generateBox(level, box, 5, 1, 10, 7, 3, 10, CAVE_AIR, CAVE_AIR, false);
                this.generateBox(level, box, 1, 2, 1, 8, 2, 6, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 4, 1, 5, 4, 4, 9, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 8, 1, 5, 8, 4, 9, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 1, 4, 7, 3, 4, 9, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 1, 3, 5, 3, 3, 6, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(
                        level, box, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false
                );
                this.generateBox(
                        level, box, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false
                );
                this.generateBox(level, box, 5, 1, 7, 7, 1, 8, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(
                        level, box, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false
                );
                this.generateBox(
                        level, box, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false
                );
                this.generateBox(
                        level, box, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false
                );
                this.generateBox(
                        level, box, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false
                );
                this.generateBox(
                        level,
                        box,
                        5,
                        5,
                        7,
                        7,
                        5,
                        9,
                        Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE),
                        Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE),
                        false
                );
                this.placeBlock(level, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, box);
            }
        }

        public static class LeftTurn extends SacredShrinePieces.Turn {
            public LeftTurn(int genDepth, RandomSource random, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_LEFT_TURN, genDepth, box);
                this.setOrientation(orientation);
                this.entryDoor = this.randomSmallDoor(random);
            }

            public LeftTurn(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_LEFT_TURN, tag);
            }

            @Override
            public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
                Direction direction = this.getOrientation();
                if (direction != Direction.NORTH && direction != Direction.EAST) {
                    this.generateSmallDoorChildRight((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 1);
                } else {
                    this.generateSmallDoorChildLeft((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 1);
                }
            }

            public static SacredShrinePieces.LeftTurn createPiece(
                    StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction orientation, int genDepth
            ) {
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, 5, orientation);
                return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null
                        ? new SacredShrinePieces.LeftTurn(genDepth, random, boundingbox, orientation)
                        : null;
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                this.generateBox(level, box, 0, 0, 0, 4, 4, 4, true, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateSmallDoor(level, random, box, this.entryDoor, 1, 1, 0);
                Direction direction = this.getOrientation();
                if (direction != Direction.NORTH && direction != Direction.EAST) {
                    this.generateBox(level, box, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
                } else {
                    this.generateBox(level, box, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
                }
            }
        }

        public static class Library extends SacredShrinePiece {
            protected static final int WIDTH = 14;
            protected static final int HEIGHT = 6;
            protected static final int TALL_HEIGHT = 11;
            protected static final int DEPTH = 15;
            private final boolean isTall;

            public Library(int genDepth, RandomSource random, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_LIBRARY, genDepth, box);
                this.setOrientation(orientation);
                this.entryDoor = this.randomSmallDoor(random);
                this.isTall = box.getYSpan() > 6;
            }

            public Library(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_LIBRARY, tag);
                this.isTall = tag.getBoolean("Tall");
            }

            @Override
            protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
                super.addAdditionalSaveData(context, tag);
                tag.putBoolean("Tall", this.isTall);
            }

            public static SacredShrinePieces.Library createPiece(
                    StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction orientation, int genDepth
            ) {
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -4, -1, 0, 14, 11, 15, orientation);
                if (!isOkBox(boundingbox) || pieces.findCollisionPiece(boundingbox) != null) {
                    boundingbox = BoundingBox.orientBox(x, y, z, -4, -1, 0, 14, 6, 15, orientation);
                    if (!isOkBox(boundingbox) || pieces.findCollisionPiece(boundingbox) != null) {
                        return null;
                    }
                }

                return new SacredShrinePieces.Library(genDepth, random, boundingbox, orientation);
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                int i = 11;
                if (!this.isTall) {
                    i = 6;
                }

                this.generateBox(level, box, 0, 0, 0, 13, i - 1, 14, true, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateSmallDoor(level, random, box, this.entryDoor, 4, 1, 0);
                this.generateMaybeBox(
                        level, box, random, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.defaultBlockState(), Blocks.COBWEB.defaultBlockState(), false, false
                );
                int j = 1;
                int k = 12;

                for (int l = 1; l <= 13; l++) {
                    if ((l - 1) % 4 == 0) {
                        this.generateBox(
                                level, box, 1, 1, l, 1, 4, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false
                        );
                        this.generateBox(
                                level, box, 12, 1, l, 12, 4, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false
                        );
                        this.placeBlock(level, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 2, 3, l, box);
                        this.placeBlock(level, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 11, 3, l, box);
                        if (this.isTall) {
                            this.generateBox(
                                    level, box, 1, 6, l, 1, 9, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false
                            );
                            this.generateBox(
                                    level, box, 12, 6, l, 12, 9, l, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false
                            );
                        }
                    } else {
                        this.generateBox(level, box, 1, 1, l, 1, 4, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                        this.generateBox(
                                level, box, 12, 1, l, 12, 4, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false
                        );
                        if (this.isTall) {
                            this.generateBox(
                                    level, box, 1, 6, l, 1, 9, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false
                            );
                            this.generateBox(
                                    level, box, 12, 6, l, 12, 9, l, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false
                            );
                        }
                    }
                }

                for (int l1 = 3; l1 < 12; l1 += 2) {
                    this.generateBox(level, box, 3, 1, l1, 4, 3, l1, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                    this.generateBox(level, box, 6, 1, l1, 7, 3, l1, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                    this.generateBox(level, box, 9, 1, l1, 10, 3, l1, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                }

                if (this.isTall) {
                    this.generateBox(level, box, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    this.generateBox(level, box, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    this.generateBox(level, box, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    this.generateBox(level, box, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    this.placeBlock(level, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 11, box);
                    this.placeBlock(level, Blocks.OAK_PLANKS.defaultBlockState(), 8, 5, 11, box);
                    this.placeBlock(level, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 10, box);
                    BlockState blockstate5 = Blocks.OAK_FENCE
                            .defaultBlockState()
                            .setValue(FenceBlock.WEST, Boolean.valueOf(true))
                            .setValue(FenceBlock.EAST, Boolean.valueOf(true));
                    BlockState blockstate = Blocks.OAK_FENCE
                            .defaultBlockState()
                            .setValue(FenceBlock.NORTH, Boolean.valueOf(true))
                            .setValue(FenceBlock.SOUTH, Boolean.valueOf(true));
                    this.generateBox(level, box, 3, 6, 3, 3, 6, 11, blockstate, blockstate, false);
                    this.generateBox(level, box, 10, 6, 3, 10, 6, 9, blockstate, blockstate, false);
                    this.generateBox(level, box, 4, 6, 2, 9, 6, 2, blockstate5, blockstate5, false);
                    this.generateBox(level, box, 4, 6, 12, 7, 6, 12, blockstate5, blockstate5, false);
                    this.placeBlock(
                            level,
                            Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)),
                            3,
                            6,
                            2,
                            box
                    );
                    this.placeBlock(
                            level,
                            Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true)),
                            3,
                            6,
                            12,
                            box
                    );
                    this.placeBlock(
                            level,
                            Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)),
                            10,
                            6,
                            2,
                            box
                    );

                    for (int i1 = 0; i1 <= 2; i1++) {
                        this.placeBlock(
                                level,
                                Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, Boolean.valueOf(true)).setValue(FenceBlock.WEST, Boolean.valueOf(true)),
                                8 + i1,
                                6,
                                12 - i1,
                                box
                        );
                        if (i1 != 2) {
                            this.placeBlock(
                                    level,
                                    Blocks.OAK_FENCE
                                            .defaultBlockState()
                                            .setValue(FenceBlock.NORTH, Boolean.valueOf(true))
                                            .setValue(FenceBlock.EAST, Boolean.valueOf(true)),
                                    8 + i1,
                                    6,
                                    11 - i1,
                                    box
                            );
                        }
                    }

                    BlockState blockstate6 = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
                    this.placeBlock(level, blockstate6, 10, 1, 13, box);
                    this.placeBlock(level, blockstate6, 10, 2, 13, box);
                    this.placeBlock(level, blockstate6, 10, 3, 13, box);
                    this.placeBlock(level, blockstate6, 10, 4, 13, box);
                    this.placeBlock(level, blockstate6, 10, 5, 13, box);
                    this.placeBlock(level, blockstate6, 10, 6, 13, box);
                    this.placeBlock(level, blockstate6, 10, 7, 13, box);
                    int j1 = 7;
                    int k1 = 7;
                    BlockState blockstate1 = Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, Boolean.valueOf(true));
                    this.placeBlock(level, blockstate1, 6, 9, 7, box);
                    BlockState blockstate2 = Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, Boolean.valueOf(true));
                    this.placeBlock(level, blockstate2, 7, 9, 7, box);
                    this.placeBlock(level, blockstate1, 6, 8, 7, box);
                    this.placeBlock(level, blockstate2, 7, 8, 7, box);
                    BlockState blockstate3 = blockstate.setValue(FenceBlock.WEST, Boolean.valueOf(true)).setValue(FenceBlock.EAST, Boolean.valueOf(true));
                    this.placeBlock(level, blockstate3, 6, 7, 7, box);
                    this.placeBlock(level, blockstate3, 7, 7, 7, box);
                    this.placeBlock(level, blockstate1, 5, 7, 7, box);
                    this.placeBlock(level, blockstate2, 8, 7, 7, box);
                    this.placeBlock(level, blockstate1.setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 6, 7, 6, box);
                    this.placeBlock(level, blockstate1.setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 6, 7, 8, box);
                    this.placeBlock(level, blockstate2.setValue(FenceBlock.NORTH, Boolean.valueOf(true)), 7, 7, 6, box);
                    this.placeBlock(level, blockstate2.setValue(FenceBlock.SOUTH, Boolean.valueOf(true)), 7, 7, 8, box);
                    BlockState blockstate4 = Blocks.TORCH.defaultBlockState();
                    this.placeBlock(level, blockstate4, 5, 8, 7, box);
                    this.placeBlock(level, blockstate4, 8, 8, 7, box);
                    this.placeBlock(level, blockstate4, 6, 8, 6, box);
                    this.placeBlock(level, blockstate4, 6, 8, 8, box);
                    this.placeBlock(level, blockstate4, 7, 8, 6, box);
                    this.placeBlock(level, blockstate4, 7, 8, 8, box);
                }

                this.createChest(level, box, random, 3, 3, 5, BuiltInLootTables.STRONGHOLD_LIBRARY);
                if (this.isTall) {
                    this.placeBlock(level, CAVE_AIR, 12, 9, 1, box);
                    this.createChest(level, box, random, 12, 8, 1, BuiltInLootTables.STRONGHOLD_LIBRARY);
                }
            }
        }

        static class PieceWeight {
            public final Class<? extends SacredShrinePiece> pieceClass;
            public final int weight;
            public int placeCount;
            public final int maxPlaceCount;

            public PieceWeight(Class<? extends SacredShrinePiece> pieceClass, int weight, int maxPlaceCount) {
                this.pieceClass = pieceClass;
                this.weight = weight;
                this.maxPlaceCount = maxPlaceCount;
            }

            public boolean doPlace(int genDepth) {
                return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
            }

            public boolean isValid() {
                return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
            }
        }

        public static class PortalRoom extends SacredShrinePiece {
            protected static final int WIDTH = 11;
            protected static final int HEIGHT = 8;
            protected static final int DEPTH = 16;
            private boolean hasPlacedSpawner;

            public PortalRoom(int genDepth, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, genDepth, box);
                this.setOrientation(orientation);
            }

            public PortalRoom(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, tag);
                this.hasPlacedSpawner = tag.getBoolean("Mob");
            }

            @Override
            protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
                super.addAdditionalSaveData(context, tag);
                tag.putBoolean("Mob", this.hasPlacedSpawner);
            }

            @Override
            public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
                if (piece != null) {
                    ((SacredShrinePieces.StartPiece)piece).portalRoomPiece = this;
                }
            }

            public static SacredShrinePieces.PortalRoom createPiece(
                    StructurePieceAccessor pieces, int x, int y, int z, Direction orientation, int genDepth
            ) {
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -4, -1, 0, 11, 8, 16, orientation);
                return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null
                        ? new SacredShrinePieces.PortalRoom(genDepth, boundingbox, orientation)
                        : null;
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                this.generateBox(level, box, 0, 0, 0, 10, 7, 15, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateSmallDoor(level, random, box, SacredShrinePiece.SmallDoorType.GRATES, 4, 1, 0);
                int i = 6;
                this.generateBox(level, box, 1, 6, 1, 1, 6, 14, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 9, 6, 1, 9, 6, 14, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 2, 6, 1, 8, 6, 2, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 2, 6, 14, 8, 6, 14, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 1, 1, 1, 2, 1, 4, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 8, 1, 1, 9, 1, 4, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 1, 1, 1, 1, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
                this.generateBox(level, box, 9, 1, 1, 9, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
                this.generateBox(level, box, 3, 1, 8, 7, 1, 12, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 4, 1, 9, 6, 1, 11, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
                BlockState blockstate = Blocks.IRON_BARS
                        .defaultBlockState()
                        .setValue(IronBarsBlock.NORTH, Boolean.valueOf(true))
                        .setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true));
                BlockState blockstate1 = Blocks.IRON_BARS
                        .defaultBlockState()
                        .setValue(IronBarsBlock.WEST, Boolean.valueOf(true))
                        .setValue(IronBarsBlock.EAST, Boolean.valueOf(true));

                for (int j = 3; j < 14; j += 2) {
                    this.generateBox(level, box, 0, 3, j, 0, 4, j, blockstate, blockstate, false);
                    this.generateBox(level, box, 10, 3, j, 10, 4, j, blockstate, blockstate, false);
                }

                for (int i1 = 2; i1 < 9; i1 += 2) {
                    this.generateBox(level, box, i1, 3, 15, i1, 4, 15, blockstate1, blockstate1, false);
                }

                BlockState blockstate5 = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
                this.generateBox(level, box, 4, 1, 5, 6, 1, 7, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 4, 2, 6, 6, 2, 7, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 4, 3, 7, 6, 3, 7, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);

                for (int k = 4; k <= 6; k++) {
                    this.placeBlock(level, blockstate5, k, 1, 4, box);
                    this.placeBlock(level, blockstate5, k, 2, 5, box);
                    this.placeBlock(level, blockstate5, k, 3, 6, box);
                }

                BlockState blockstate6 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.NORTH);
                BlockState blockstate2 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.SOUTH);
                BlockState blockstate3 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.EAST);
                BlockState blockstate4 = Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.WEST);
                boolean flag = true;
                boolean[] aboolean = new boolean[12];

                for (int l = 0; l < aboolean.length; l++) {
                    aboolean[l] = random.nextFloat() > 0.9F;
                    flag &= aboolean[l];
                }

                this.placeBlock(level, blockstate6.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[0])), 4, 3, 8, box);
                this.placeBlock(level, blockstate6.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[1])), 5, 3, 8, box);
                this.placeBlock(level, blockstate6.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[2])), 6, 3, 8, box);
                this.placeBlock(level, blockstate2.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[3])), 4, 3, 12, box);
                this.placeBlock(level, blockstate2.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[4])), 5, 3, 12, box);
                this.placeBlock(level, blockstate2.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[5])), 6, 3, 12, box);
                this.placeBlock(level, blockstate3.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[6])), 3, 3, 9, box);
                this.placeBlock(level, blockstate3.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[7])), 3, 3, 10, box);
                this.placeBlock(level, blockstate3.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[8])), 3, 3, 11, box);
                this.placeBlock(level, blockstate4.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[9])), 7, 3, 9, box);
                this.placeBlock(level, blockstate4.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[10])), 7, 3, 10, box);
                this.placeBlock(level, blockstate4.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(aboolean[11])), 7, 3, 11, box);
                if (flag) {
                    BlockState blockstate7 = Blocks.END_PORTAL.defaultBlockState();
                    this.placeBlock(level, blockstate7, 4, 3, 9, box);
                    this.placeBlock(level, blockstate7, 5, 3, 9, box);
                    this.placeBlock(level, blockstate7, 6, 3, 9, box);
                    this.placeBlock(level, blockstate7, 4, 3, 10, box);
                    this.placeBlock(level, blockstate7, 5, 3, 10, box);
                    this.placeBlock(level, blockstate7, 6, 3, 10, box);
                    this.placeBlock(level, blockstate7, 4, 3, 11, box);
                    this.placeBlock(level, blockstate7, 5, 3, 11, box);
                    this.placeBlock(level, blockstate7, 6, 3, 11, box);
                }

                if (!this.hasPlacedSpawner) {
                    BlockPos blockpos = this.getWorldPos(5, 3, 6);
                    if (box.isInside(blockpos)) {
                        this.hasPlacedSpawner = true;
                        level.setBlock(blockpos, Blocks.SPAWNER.defaultBlockState(), 2);
                        if (level.getBlockEntity(blockpos) instanceof SpawnerBlockEntity spawnerblockentity) {
                            spawnerblockentity.setEntityId(EntityType.SILVERFISH, random);
                        }
                    }
                }
            }
        }

        public static class PrisonHall extends SacredShrinePiece {
            protected static final int WIDTH = 9;
            protected static final int HEIGHT = 5;
            protected static final int DEPTH = 11;

            public PrisonHall(int genDepth, RandomSource random, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_PRISON_HALL, genDepth, box);
                this.setOrientation(orientation);
                this.entryDoor = this.randomSmallDoor(random);
            }

            public PrisonHall(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_PRISON_HALL, tag);
            }

            @Override
            public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
                this.generateSmallDoorChildForward((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 1);
            }

            public static SacredShrinePieces.PrisonHall createPiece(
                    StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction orientation, int genDepth
            ) {
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 9, 5, 11, orientation);
                return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null
                        ? new SacredShrinePieces.PrisonHall(genDepth, random, boundingbox, orientation)
                        : null;
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                this.generateBox(level, box, 0, 0, 0, 8, 4, 10, true, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateSmallDoor(level, random, box, this.entryDoor, 1, 1, 0);
                this.generateBox(level, box, 1, 1, 10, 3, 3, 10, CAVE_AIR, CAVE_AIR, false);
                this.generateBox(level, box, 4, 1, 1, 4, 3, 1, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 4, 1, 3, 4, 3, 3, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 4, 1, 7, 4, 3, 7, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateBox(level, box, 4, 1, 9, 4, 3, 9, false, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);

                for (int i = 1; i <= 3; i++) {
                    this.placeBlock(
                            level,
                            Blocks.IRON_BARS
                                    .defaultBlockState()
                                    .setValue(IronBarsBlock.NORTH, Boolean.valueOf(true))
                                    .setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)),
                            4,
                            i,
                            4,
                            box
                    );
                    this.placeBlock(
                            level,
                            Blocks.IRON_BARS
                                    .defaultBlockState()
                                    .setValue(IronBarsBlock.NORTH, Boolean.valueOf(true))
                                    .setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true))
                                    .setValue(IronBarsBlock.EAST, Boolean.valueOf(true)),
                            4,
                            i,
                            5,
                            box
                    );
                    this.placeBlock(
                            level,
                            Blocks.IRON_BARS
                                    .defaultBlockState()
                                    .setValue(IronBarsBlock.NORTH, Boolean.valueOf(true))
                                    .setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)),
                            4,
                            i,
                            6,
                            box
                    );
                    this.placeBlock(
                            level,
                            Blocks.IRON_BARS
                                    .defaultBlockState()
                                    .setValue(IronBarsBlock.WEST, Boolean.valueOf(true))
                                    .setValue(IronBarsBlock.EAST, Boolean.valueOf(true)),
                            5,
                            i,
                            5,
                            box
                    );
                    this.placeBlock(
                            level,
                            Blocks.IRON_BARS
                                    .defaultBlockState()
                                    .setValue(IronBarsBlock.WEST, Boolean.valueOf(true))
                                    .setValue(IronBarsBlock.EAST, Boolean.valueOf(true)),
                            6,
                            i,
                            5,
                            box
                    );
                    this.placeBlock(
                            level,
                            Blocks.IRON_BARS
                                    .defaultBlockState()
                                    .setValue(IronBarsBlock.WEST, Boolean.valueOf(true))
                                    .setValue(IronBarsBlock.EAST, Boolean.valueOf(true)),
                            7,
                            i,
                            5,
                            box
                    );
                }

                this.placeBlock(
                        level,
                        Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true)).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)),
                        4,
                        3,
                        2,
                        box
                );
                this.placeBlock(
                        level,
                        Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(true)).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(true)),
                        4,
                        3,
                        8,
                        box
                );
                BlockState blockstate1 = Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST);
                BlockState blockstate = Blocks.IRON_DOOR
                        .defaultBlockState()
                        .setValue(DoorBlock.FACING, Direction.WEST)
                        .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
                this.placeBlock(level, blockstate1, 4, 1, 2, box);
                this.placeBlock(level, blockstate, 4, 2, 2, box);
                this.placeBlock(level, blockstate1, 4, 1, 8, box);
                this.placeBlock(level, blockstate, 4, 2, 8, box);
            }
        }

        public static class RightTurn extends SacredShrinePieces.Turn {
            public RightTurn(int genDepth, RandomSource random, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_RIGHT_TURN, genDepth, box);
                this.setOrientation(orientation);
                this.entryDoor = this.randomSmallDoor(random);
            }

            public RightTurn(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_RIGHT_TURN, tag);
            }

            @Override
            public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
                Direction direction = this.getOrientation();
                if (direction != Direction.NORTH && direction != Direction.EAST) {
                    this.generateSmallDoorChildLeft((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 1);
                } else {
                    this.generateSmallDoorChildRight((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 1);
                }
            }

            public static SacredShrinePieces.RightTurn createPiece(
                    StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction orientation, int genDepth
            ) {
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, 5, orientation);
                return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null
                        ? new SacredShrinePieces.RightTurn(genDepth, random, boundingbox, orientation)
                        : null;
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                this.generateBox(level, box, 0, 0, 0, 4, 4, 4, true, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateSmallDoor(level, random, box, this.entryDoor, 1, 1, 0);
                Direction direction = this.getOrientation();
                if (direction != Direction.NORTH && direction != Direction.EAST) {
                    this.generateBox(level, box, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
                } else {
                    this.generateBox(level, box, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
                }
            }
        }

        public static class RoomCrossing extends SacredShrinePiece {
            protected static final int WIDTH = 11;
            protected static final int HEIGHT = 7;
            protected static final int DEPTH = 11;
            protected final int type;

            public RoomCrossing(int genDepth, RandomSource random, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, genDepth, box);
                this.setOrientation(orientation);
                this.entryDoor = this.randomSmallDoor(random);
                this.type = random.nextInt(5);
            }

            public RoomCrossing(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, tag);
                this.type = tag.getInt("Type");
            }

            @Override
            protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
                super.addAdditionalSaveData(context, tag);
                tag.putInt("Type", this.type);
            }

            @Override
            public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
                this.generateSmallDoorChildForward((SacredShrinePieces.StartPiece)piece, pieces, random, 4, 1);
                this.generateSmallDoorChildLeft((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 4);
                this.generateSmallDoorChildRight((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 4);
            }

            public static SacredShrinePieces.RoomCrossing createPiece(
                    StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction orientation, int genDepth
            ) {
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -4, -1, 0, 11, 7, 11, orientation);
                return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null
                        ? new SacredShrinePieces.RoomCrossing(genDepth, random, boundingbox, orientation)
                        : null;
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                this.generateBox(level, box, 0, 0, 0, 10, 6, 10, true, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateSmallDoor(level, random, box, this.entryDoor, 4, 1, 0);
                this.generateBox(level, box, 4, 1, 10, 6, 3, 10, CAVE_AIR, CAVE_AIR, false);
                this.generateBox(level, box, 0, 1, 4, 0, 3, 6, CAVE_AIR, CAVE_AIR, false);
                this.generateBox(level, box, 10, 1, 4, 10, 3, 6, CAVE_AIR, CAVE_AIR, false);
                switch (this.type) {
                    case 0:
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, box);
                        this.placeBlock(level, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, box);
                        this.placeBlock(level, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, box);
                        this.placeBlock(level, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, box);
                        this.placeBlock(level, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, box);
                        this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 4, box);
                        this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 5, box);
                        this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 6, box);
                        this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 4, box);
                        this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 5, box);
                        this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 6, box);
                        this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 4, box);
                        this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 6, box);
                        break;
                    case 1:
                        for (int i1 = 0; i1 < 5; i1++) {
                            this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 3, 1, 3 + i1, box);
                            this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 7, 1, 3 + i1, box);
                            this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 3 + i1, 1, 3, box);
                            this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 3 + i1, 1, 7, box);
                        }

                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, box);
                        this.placeBlock(level, Blocks.WATER.defaultBlockState(), 5, 4, 5, box);
                        break;
                    case 2:
                        for (int i = 1; i <= 9; i++) {
                            this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 1, 3, i, box);
                            this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 9, 3, i, box);
                        }

                        for (int j = 1; j <= 9; j++) {
                            this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), j, 3, 1, box);
                            this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), j, 3, 9, box);
                        }

                        this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 4, box);
                        this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 6, box);
                        this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 4, box);
                        this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 6, box);
                        this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 4, 1, 5, box);
                        this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 6, 1, 5, box);
                        this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 4, 3, 5, box);
                        this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 6, 3, 5, box);

                        for (int k = 1; k <= 3; k++) {
                            this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 4, k, 4, box);
                            this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 6, k, 4, box);
                            this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 4, k, 6, box);
                            this.placeBlock(level, Blocks.COBBLESTONE.defaultBlockState(), 6, k, 6, box);
                        }

                        this.placeBlock(level, Blocks.WALL_TORCH.defaultBlockState(), 5, 3, 5, box);

                        for (int l = 2; l <= 8; l++) {
                            this.placeBlock(level, Blocks.OAK_PLANKS.defaultBlockState(), 2, 3, l, box);
                            this.placeBlock(level, Blocks.OAK_PLANKS.defaultBlockState(), 3, 3, l, box);
                            if (l <= 3 || l >= 7) {
                                this.placeBlock(level, Blocks.OAK_PLANKS.defaultBlockState(), 4, 3, l, box);
                                this.placeBlock(level, Blocks.OAK_PLANKS.defaultBlockState(), 5, 3, l, box);
                                this.placeBlock(level, Blocks.OAK_PLANKS.defaultBlockState(), 6, 3, l, box);
                            }

                            this.placeBlock(level, Blocks.OAK_PLANKS.defaultBlockState(), 7, 3, l, box);
                            this.placeBlock(level, Blocks.OAK_PLANKS.defaultBlockState(), 8, 3, l, box);
                        }

                        BlockState blockstate = Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST);
                        this.placeBlock(level, blockstate, 9, 1, 3, box);
                        this.placeBlock(level, blockstate, 9, 2, 3, box);
                        this.placeBlock(level, blockstate, 9, 3, 3, box);
                        this.createChest(level, box, random, 3, 4, 8, BuiltInLootTables.STRONGHOLD_CROSSING);
                }
            }
        }

        static class SmoothStoneSelector extends StructurePiece.BlockSelector {
            @Override
            public void next(RandomSource p_229749_, int p_229750_, int p_229751_, int p_229752_, boolean p_229753_) {
                if (p_229753_) {
                    float f = p_229749_.nextFloat();
                    if (f < 0.2F) {
                        this.next = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
                    } else if (f < 0.5F) {
                        this.next = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
                    } else if (f < 0.55F) {
                        this.next = Blocks.INFESTED_STONE_BRICKS.defaultBlockState();
                    } else {
                        this.next = Blocks.STONE_BRICKS.defaultBlockState();
                    }
                } else {
                    this.next = Blocks.CAVE_AIR.defaultBlockState();
                }
            }
        }

        public static class StairsDown extends SacredShrinePiece {
            private static final int WIDTH = 5;
            private static final int HEIGHT = 11;
            private static final int DEPTH = 5;
            private final boolean isSource;

            public StairsDown(StructurePieceType type, int genDepth, int x, int z, Direction orientation) {
                super(type, genDepth, makeBoundingBox(x, 64, z, orientation, 5, 11, 5));
                this.isSource = true;
                this.setOrientation(orientation);
                this.entryDoor = SacredShrinePiece.SmallDoorType.OPENING;
            }

            public StairsDown(int genDepth, RandomSource random, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_STAIRS_DOWN, genDepth, box);
                this.isSource = false;
                this.setOrientation(orientation);
                this.entryDoor = this.randomSmallDoor(random);
            }

            public StairsDown(StructurePieceType type, CompoundTag tag) {
                super(type, tag);
                this.isSource = tag.getBoolean("Source");
            }

            public StairsDown(CompoundTag tag) {
                this(StructurePieceType.STRONGHOLD_STAIRS_DOWN, tag);
            }

            @Override
            protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
                super.addAdditionalSaveData(context, tag);
                tag.putBoolean("Source", this.isSource);
            }

            @Override
            public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
                if (this.isSource) {
                    SacredShrinePieces.imposedPiece = SacredShrinePieces.FiveCrossing.class;
                }

                this.generateSmallDoorChildForward((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 1);
            }

            public static SacredShrinePieces.StairsDown createPiece(
                    StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction orientation, int genDepth
            ) {
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -1, -7, 0, 5, 11, 5, orientation);
                return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null
                        ? new SacredShrinePieces.StairsDown(genDepth, random, boundingbox, orientation)
                        : null;
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                this.generateBox(level, box, 0, 0, 0, 4, 10, 4, true, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateSmallDoor(level, random, box, this.entryDoor, 1, 7, 0);
                this.generateSmallDoor(level, random, box, SacredShrinePiece.SmallDoorType.OPENING, 1, 1, 4);
                this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 2, 6, 1, box);
                this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 1, box);
                this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 6, 1, box);
                this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 2, box);
                this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, 3, box);
                this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 5, 3, box);
                this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, 3, box);
                this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 3, box);
                this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 4, 3, box);
                this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 2, box);
                this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 3, 2, 1, box);
                this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 3, 1, box);
                this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 2, 2, 1, box);
                this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 1, box);
                this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 2, 1, box);
                this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 2, box);
                this.placeBlock(level, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 1, 3, box);
            }
        }

        public static class StartPiece extends SacredShrinePieces.StairsDown {
            public SacredShrinePieces.PieceWeight previousPiece;
            @Nullable
            public SacredShrinePieces.PortalRoom portalRoomPiece;
            public final List<StructurePiece> pendingChildren = Lists.newArrayList();

            public StartPiece(RandomSource random, int x, int z) {
                super(StructurePieceType.STRONGHOLD_START, 0, x, z, getRandomHorizontalDirection(random));
            }

            public StartPiece(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_START, tag);
            }

            @Override
            public BlockPos getLocatorPosition() {
                return this.portalRoomPiece != null ? this.portalRoomPiece.getLocatorPosition() : super.getLocatorPosition();
            }
        }

        public static class Straight extends SacredShrinePiece {
            private static final int WIDTH = 5;
            private static final int HEIGHT = 5;
            private static final int DEPTH = 7;
            private final boolean leftChild;
            private final boolean rightChild;

            public Straight(int genDepth, RandomSource random, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_STRAIGHT, genDepth, box);
                this.setOrientation(orientation);
                this.entryDoor = this.randomSmallDoor(random);
                this.leftChild = random.nextInt(2) == 0;
                this.rightChild = random.nextInt(2) == 0;
            }

            public Straight(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_STRAIGHT, tag);
                this.leftChild = tag.getBoolean("Left");
                this.rightChild = tag.getBoolean("Right");
            }

            @Override
            protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
                super.addAdditionalSaveData(context, tag);
                tag.putBoolean("Left", this.leftChild);
                tag.putBoolean("Right", this.rightChild);
            }

            @Override
            public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
                this.generateSmallDoorChildForward((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 1);
                if (this.leftChild) {
                    this.generateSmallDoorChildLeft((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 2);
                }

                if (this.rightChild) {
                    this.generateSmallDoorChildRight((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 2);
                }
            }

            public static SacredShrinePieces.Straight createPiece(
                    StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction direction, int genDepth
            ) {
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -1, -1, 0, 5, 5, 7, direction);
                return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null
                        ? new SacredShrinePieces.Straight(genDepth, random, boundingbox, direction)
                        : null;
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                this.generateBox(level, box, 0, 0, 0, 4, 4, 6, true, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateSmallDoor(level, random, box, this.entryDoor, 1, 1, 0);
                this.generateSmallDoor(level, random, box, SacredShrinePiece.SmallDoorType.OPENING, 1, 1, 6);
                BlockState blockstate = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST);
                BlockState blockstate1 = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST);
                this.maybeGenerateBlock(level, box, random, 0.1F, 1, 2, 1, blockstate);
                this.maybeGenerateBlock(level, box, random, 0.1F, 3, 2, 1, blockstate1);
                this.maybeGenerateBlock(level, box, random, 0.1F, 1, 2, 5, blockstate);
                this.maybeGenerateBlock(level, box, random, 0.1F, 3, 2, 5, blockstate1);
                if (this.leftChild) {
                    this.generateBox(level, box, 0, 1, 2, 0, 3, 4, CAVE_AIR, CAVE_AIR, false);
                }

                if (this.rightChild) {
                    this.generateBox(level, box, 4, 1, 2, 4, 3, 4, CAVE_AIR, CAVE_AIR, false);
                }
            }
        }

        public static class StraightStairsDown extends SacredShrinePiece {
            private static final int WIDTH = 5;
            private static final int HEIGHT = 11;
            private static final int DEPTH = 8;

            public StraightStairsDown(int genDepth, RandomSource random, BoundingBox box, Direction orientation) {
                super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, genDepth, box);
                this.setOrientation(orientation);
                this.entryDoor = this.randomSmallDoor(random);
            }

            public StraightStairsDown(CompoundTag tag) {
                super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, tag);
            }

            @Override
            public void addChildren(StructurePiece piece, StructurePieceAccessor pieces, RandomSource random) {
                this.generateSmallDoorChildForward((SacredShrinePieces.StartPiece)piece, pieces, random, 1, 1);
            }

            public static SacredShrinePieces.StraightStairsDown createPiece(
                    StructurePieceAccessor pieces, RandomSource random, int x, int y, int z, Direction orientation, int genDepth
            ) {
                BoundingBox boundingbox = BoundingBox.orientBox(x, y, z, -1, -7, 0, 5, 11, 8, orientation);
                return isOkBox(boundingbox) && pieces.findCollisionPiece(boundingbox) == null
                        ? new SacredShrinePieces.StraightStairsDown(genDepth, random, boundingbox, orientation)
                        : null;
            }

            @Override
            public void postProcess(
                    WorldGenLevel level,
                    StructureManager structureManager,
                    ChunkGenerator generator,
                    RandomSource random,
                    BoundingBox box,
                    ChunkPos chunkPos,
                    BlockPos pos
            ) {
                this.generateBox(level, box, 0, 0, 0, 4, 10, 7, true, random, SacredShrinePieces.SMOOTH_STONE_SELECTOR);
                this.generateSmallDoor(level, random, box, this.entryDoor, 1, 7, 0);
                this.generateSmallDoor(level, random, box, SacredShrinePiece.SmallDoorType.OPENING, 1, 1, 7);
                BlockState blockstate = Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);

                for (int i = 0; i < 6; i++) {
                    this.placeBlock(level, blockstate, 1, 6 - i, 1 + i, box);
                    this.placeBlock(level, blockstate, 2, 6 - i, 1 + i, box);
                    this.placeBlock(level, blockstate, 3, 6 - i, 1 + i, box);
                    if (i < 5) {
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5 - i, 1 + i, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 2, 5 - i, 1 + i, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), 3, 5 - i, 1 + i, box);
                    }
                }
            }
        }

        abstract static class SacredShrinePiece extends StructurePiece {
            protected SacredShrinePiece.SmallDoorType entryDoor = SacredShrinePiece.SmallDoorType.OPENING;

            protected SacredShrinePiece(StructurePieceType type, int genDepth, BoundingBox boundingBox) {
                super(type, genDepth, boundingBox);
            }

            public SacredShrinePiece(StructurePieceType type, CompoundTag tag) {
                super(type, tag);
                this.entryDoor = SacredShrinePiece.SmallDoorType.valueOf(tag.getString("EntryDoor"));
            }

            @Override
            protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
                tag.putString("EntryDoor", this.entryDoor.name());
            }

            protected void generateSmallDoor(
                    WorldGenLevel level,
                    RandomSource random,
                    BoundingBox box,
                    SacredShrinePiece.SmallDoorType type,
                    int x,
                    int y,
                    int z
            ) {
                switch (type) {
                    case OPENING:
                        this.generateBox(
                                level, box, x, y, z, x + 3 - 1, y + 3 - 1, z, CAVE_AIR, CAVE_AIR, false
                        );
                        break;
                    case WOOD_DOOR:
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x, y, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x, y + 1, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x, y + 2, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x + 1, y + 2, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x + 2, y + 2, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x + 2, y + 1, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x + 2, y, z, box);
                        this.placeBlock(level, Blocks.OAK_DOOR.defaultBlockState(), x + 1, y, z, box);
                        this.placeBlock(
                                level,
                                Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER),
                                x + 1,
                                y + 1,
                                z,
                                box
                        );
                        break;
                    case GRATES:
                        this.placeBlock(level, Blocks.CAVE_AIR.defaultBlockState(), x + 1, y, z, box);
                        this.placeBlock(level, Blocks.CAVE_AIR.defaultBlockState(), x + 1, y + 1, z, box);
                        this.placeBlock(
                                level,
                                Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true)),
                                x,
                                y,
                                z,
                                box
                        );
                        this.placeBlock(
                                level,
                                Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, Boolean.valueOf(true)),
                                x,
                                y + 1,
                                z,
                                box
                        );
                        this.placeBlock(
                                level,
                                Blocks.IRON_BARS
                                        .defaultBlockState()
                                        .setValue(IronBarsBlock.EAST, Boolean.valueOf(true))
                                        .setValue(IronBarsBlock.WEST, Boolean.valueOf(true)),
                                x,
                                y + 2,
                                z,
                                box
                        );
                        this.placeBlock(
                                level,
                                Blocks.IRON_BARS
                                        .defaultBlockState()
                                        .setValue(IronBarsBlock.EAST, Boolean.valueOf(true))
                                        .setValue(IronBarsBlock.WEST, Boolean.valueOf(true)),
                                x + 1,
                                y + 2,
                                z,
                                box
                        );
                        this.placeBlock(
                                level,
                                Blocks.IRON_BARS
                                        .defaultBlockState()
                                        .setValue(IronBarsBlock.EAST, Boolean.valueOf(true))
                                        .setValue(IronBarsBlock.WEST, Boolean.valueOf(true)),
                                x + 2,
                                y + 2,
                                z,
                                box
                        );
                        this.placeBlock(
                                level,
                                Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true)),
                                x + 2,
                                y + 1,
                                z,
                                box
                        );
                        this.placeBlock(
                                level,
                                Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, Boolean.valueOf(true)),
                                x + 2,
                                y,
                                z,
                                box
                        );
                        break;
                    case IRON_DOOR:
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x, y, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x, y + 1, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x, y + 2, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x + 1, y + 2, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x + 2, y + 2, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x + 2, y + 1, z, box);
                        this.placeBlock(level, Blocks.STONE_BRICKS.defaultBlockState(), x + 2, y, z, box);
                        this.placeBlock(level, Blocks.IRON_DOOR.defaultBlockState(), x + 1, y, z, box);
                        this.placeBlock(
                                level,
                                Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER),
                                x + 1,
                                y + 1,
                                z,
                                box
                        );
                        this.placeBlock(
                                level,
                                Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.NORTH),
                                x + 2,
                                y + 1,
                                z + 1,
                                box
                        );
                        this.placeBlock(
                                level,
                                Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.SOUTH),
                                x + 2,
                                y + 1,
                                z - 1,
                                box
                        );
                }
            }

            protected SacredShrinePiece.SmallDoorType randomSmallDoor(RandomSource random) {
                int i = random.nextInt(5);
                switch (i) {
                    case 0:
                    case 1:
                    default:
                        return SacredShrinePiece.SmallDoorType.OPENING;
                    case 2:
                        return SacredShrinePiece.SmallDoorType.WOOD_DOOR;
                    case 3:
                        return SacredShrinePiece.SmallDoorType.GRATES;
                    case 4:
                        return SacredShrinePiece.SmallDoorType.IRON_DOOR;
                }
            }

            @Nullable
            protected StructurePiece generateSmallDoorChildForward(
                    SacredShrinePieces.StartPiece startPiece, StructurePieceAccessor pieces, RandomSource random, int offsetX, int offsetY
            ) {
                Direction direction = this.getOrientation();
                if (direction != null) {
                    switch (direction) {
                        case NORTH:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.minX() + offsetX,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.minZ() - 1,
                                    direction,
                                    this.getGenDepth()
                            );
                        case SOUTH:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.minX() + offsetX,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.maxZ() + 1,
                                    direction,
                                    this.getGenDepth()
                            );
                        case WEST:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.minX() - 1,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.minZ() + offsetX,
                                    direction,
                                    this.getGenDepth()
                            );
                        case EAST:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.maxX() + 1,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.minZ() + offsetX,
                                    direction,
                                    this.getGenDepth()
                            );
                    }
                }

                return null;
            }

            @Nullable
            protected StructurePiece generateSmallDoorChildLeft(
                    SacredShrinePieces.StartPiece startPiece, StructurePieceAccessor pieces, RandomSource random, int offsetY, int offsetX
            ) {
                Direction direction = this.getOrientation();
                if (direction != null) {
                    switch (direction) {
                        case NORTH:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.minX() - 1,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.minZ() + offsetX,
                                    Direction.WEST,
                                    this.getGenDepth()
                            );
                        case SOUTH:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.minX() - 1,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.minZ() + offsetX,
                                    Direction.WEST,
                                    this.getGenDepth()
                            );
                        case WEST:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.minX() + offsetX,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.minZ() - 1,
                                    Direction.NORTH,
                                    this.getGenDepth()
                            );
                        case EAST:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.minX() + offsetX,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.minZ() - 1,
                                    Direction.NORTH,
                                    this.getGenDepth()
                            );
                    }
                }

                return null;
            }

            @Nullable
            protected StructurePiece generateSmallDoorChildRight(
                    SacredShrinePieces.StartPiece startPiece, StructurePieceAccessor pieces, RandomSource random, int offsetY, int offsetX
            ) {
                Direction direction = this.getOrientation();
                if (direction != null) {
                    switch (direction) {
                        case NORTH:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.maxX() + 1,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.minZ() + offsetX,
                                    Direction.EAST,
                                    this.getGenDepth()
                            );
                        case SOUTH:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.maxX() + 1,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.minZ() + offsetX,
                                    Direction.EAST,
                                    this.getGenDepth()
                            );
                        case WEST:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.minX() + offsetX,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.maxZ() + 1,
                                    Direction.SOUTH,
                                    this.getGenDepth()
                            );
                        case EAST:
                            return SacredShrinePieces.generateAndAddPiece(
                                    startPiece,
                                    pieces,
                                    random,
                                    this.boundingBox.minX() + offsetX,
                                    this.boundingBox.minY() + offsetY,
                                    this.boundingBox.maxZ() + 1,
                                    Direction.SOUTH,
                                    this.getGenDepth()
                            );
                    }
                }

                return null;
            }

            protected static boolean isOkBox(BoundingBox box) {
                return box != null && box.minY() > 10;
            }

            protected static enum SmallDoorType {
                OPENING,
                WOOD_DOOR,
                GRATES,
                IRON_DOOR;
            }
        }

        public abstract static class Turn extends SacredShrinePiece {
            protected static final int WIDTH = 5;
            protected static final int HEIGHT = 5;
            protected static final int DEPTH = 5;

            protected Turn(StructurePieceType p_229930_, int p_229931_, BoundingBox p_229932_) {
                super(p_229930_, p_229931_, p_229932_);
            }

            public Turn(StructurePieceType p_229934_, CompoundTag p_229935_) {
                super(p_229934_, p_229935_);
            }
        }
    }

