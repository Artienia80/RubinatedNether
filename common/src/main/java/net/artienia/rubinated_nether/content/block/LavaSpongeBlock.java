package net.artienia.rubinated_nether.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class LavaSpongeBlock extends Block{
        public static final int MAX_DEPTH = 6;
        public static final int MAX_COUNT = 64;
        private static final Direction[] ALL_DIRECTIONS = Direction.values();

        public LavaSpongeBlock(BlockBehaviour.Properties properties) {
            super(properties);
        }

        public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
            if (!oldState.is(state.getBlock())) {
                this.tryAbsorbLava(level, pos);
            }
        }

        public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
            this.tryAbsorbLava(level, pos);
            super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        }

        protected void tryAbsorbLava(Level level, BlockPos pos) {
            if (this.removeLavaBreadthFirstSearch(level, pos)) {
                level.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
                level.levelEvent(2001, pos, Block.getId(Blocks.LAVA.defaultBlockState()));
            }

        }

        private boolean removeLavaBreadthFirstSearch(Level level, BlockPos pos) {
            return BlockPos.breadthFirstTraversal(pos, 6, 65, (blockPos, consumer) -> {
                Direction[] var2 = ALL_DIRECTIONS;
                int var3 = var2.length;

                for(int var4 = 0; var4 < var3; ++var4) {
                    Direction direction = var2[var4];
                    consumer.accept(blockPos.relative(direction));
                }

            }, (blockPos2) -> {
                if (blockPos2.equals(pos)) {
                    return true;
                } else {
                    BlockState blockState = level.getBlockState(blockPos2);
                    FluidState fluidState = level.getFluidState(blockPos2);
                    if (!fluidState.is(FluidTags.LAVA)) {
                        return false;
                    } else {
                        Block block = blockState.getBlock();
                        if (block instanceof BucketPickup) {
                            BucketPickup bucketPickup = (BucketPickup)block;
                            if (!bucketPickup.pickupBlock(level, blockPos2, blockState).isEmpty()) {
                                return true;
                            }
                        }

                        if (blockState.getBlock() instanceof LiquidBlock) {
                            level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
                        } else {
                            if (!blockState.is(Blocks.KELP) && !blockState.is(Blocks.KELP_PLANT) && !blockState.is(Blocks.SEAGRASS) && !blockState.is(Blocks.TALL_SEAGRASS)) {
                                return false;
                            }

                            BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos2) : null;
                            dropResources(blockState, level, blockPos2, blockEntity);
                            level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
                        }

                        return true;
                    }
                }
            }) > 1;
        }
    }

