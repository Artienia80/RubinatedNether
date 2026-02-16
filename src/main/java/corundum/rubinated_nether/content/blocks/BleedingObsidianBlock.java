package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BleedingObsidianBlock extends Block {

    public BleedingObsidianBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, 20);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        checkDripstone(level, pos, random);
        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(5) == 0) {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);

            if (belowState.is(Blocks.POINTED_DRIPSTONE)) {
                Direction direction = belowState.getValue(PointedDripstoneBlock.TIP_DIRECTION);
                if (direction == Direction.DOWN) {
                    return;
                }
            }

            if (!belowState.isSolidRender(level, below)) {
                double x = (double)pos.getX() + random.nextDouble();
                double y = (double)pos.getY() - 0.05;
                double z = (double)pos.getZ() + random.nextDouble();

                level.addParticle(RNParticleTypes.BLOOD_DRIP.get(), x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }

    private void checkDripstone(ServerLevel level, BlockPos pos, RandomSource random) {
        if (RNConfig.moltenRubyCauldronNetherOnly && !level.dimension().equals(Level.NETHER)) {
            return;
        }

        if (random.nextDouble() >= RNConfig.getMoltenRubyCauldronFillProbability()) {
            return;
        }

        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);

        if (belowState.is(Blocks.POINTED_DRIPSTONE)) {
            Direction direction = belowState.getValue(PointedDripstoneBlock.TIP_DIRECTION);

            if (direction == Direction.DOWN) {
                BlockPos tipPos = findDripstoneTip(level, below, direction, 10);

                if (tipPos != null) {
                    BlockPos cauldronPos = findCauldronBelow(level, tipPos.below(), 10);

                    if (cauldronPos != null) {
                        BlockState cauldronState = level.getBlockState(cauldronPos);

                        if (cauldronState.is(Blocks.CAULDRON)) {
                            level.setBlockAndUpdate(cauldronPos, RNBlocks.MOLTEN_RUBY_CAULDRON.get().defaultBlockState());
                            level.levelEvent(1046, cauldronPos, 0);
                        }
                    }
                }
            }
        }
    }

    @javax.annotation.Nullable
    private BlockPos findDripstoneTip(ServerLevel level, BlockPos startPos, Direction direction, int maxIterations) {
        BlockPos currentPos = startPos;

        for (int i = 0; i < maxIterations; i++) {
            BlockState currentState = level.getBlockState(currentPos);

            if (!currentState.is(Blocks.POINTED_DRIPSTONE) ||
                    currentState.getValue(PointedDripstoneBlock.TIP_DIRECTION) != direction) {
                return null;
            }

            BlockPos nextPos = currentPos.relative(direction);
            BlockState nextState = level.getBlockState(nextPos);

            if (!nextState.is(Blocks.POINTED_DRIPSTONE) ||
                    nextState.getValue(PointedDripstoneBlock.TIP_DIRECTION) != direction) {
                return currentPos;
            }

            currentPos = nextPos;
        }

        return null;
    }

    @javax.annotation.Nullable
    private BlockPos findCauldronBelow(ServerLevel level, BlockPos startPos, int maxDistance) {
        BlockPos currentPos = startPos;

        for (int i = 0; i < maxDistance; i++) {
            BlockState currentState = level.getBlockState(currentPos);

            if (currentState.is(Blocks.CAULDRON)) {
                return currentPos;
            }

            if (!currentState.isAir()) {
                return null;
            }

            currentPos = currentPos.below();
        }

        return null;
    }
}