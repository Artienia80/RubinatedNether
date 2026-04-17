package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.bases.TarnishingBronze;
import corundum.rubinated_nether.content.blocks.bases.TarnishingBronzeBlock;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import static net.minecraft.world.level.block.FallingBlock.isFree;

public class TarnishingBronzeGrateBlock extends BronzeGrateBlock implements TarnishingBronze {
    private static final int MAX_REDSTONE_PROPAGATION = 256;
    private static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final int CASCADE_DELAY = 5; // Base delay for cascade

    private TarnishStage tarnishStage;

    public TarnishingBronzeGrateBlock(TarnishStage tarnishStage, BlockBehaviour.Properties properties) {
        super(properties);
        this.tarnishStage = tarnishStage;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return TarnishingBronze.canCrystallize(state.getBlock());
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide) return;
        if (entity instanceof ItemEntity) return;

        // Don't trigger falling if a BronzeEntity is stepping on it
        if (entity instanceof BronzeEntity) return;

        if (hasImmediateRedstoneSignal(level, pos)) return;

        TarnishStage tarnishStage = getAgeFromBlock(state);

        // Handle all tarnish states, not just crystallized
        int delay;
        if (tarnishStage == TarnishStage.CRYSTALLIZED) {
            delay = 5; // Fast fall for crystallized (cascade behavior)
        } else {
            delay = getDelayForTarnishState(tarnishStage); // Normal delays for other states
        }

        if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
            // Only schedule and grant advancement if the grate will actually fall
            // Check if there's a block below or if it's already supported
            if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
                level.scheduleTick(pos, this, delay);

                // Grant advancement to player after the delay
                if (entity instanceof ServerPlayer serverPlayer) {
                    scheduleAdvancementGrant(serverPlayer, (ServerLevel) level, delay);
                }
            }
        }
    }

    private void scheduleAdvancementGrant(ServerPlayer player, ServerLevel level, int delay) {
        level.getServer().tell(new TickTask(
                level.getServer().getTickCount() + delay,
                () -> {
                    var advancementHolder = player.server.getAdvancements()
                            .get(RubinatedNether.id("pitfalls"));

                    if (advancementHolder != null) {
                        var progress = player.getAdvancements().getOrStartProgress(advancementHolder);
                        if (!progress.isDone()) {
                            for (String criterion : progress.getRemainingCriteria()) {
                                player.getAdvancements().award(advancementHolder, criterion);
                            }
                        }
                    }
                }
        ));
    }

    // Add this method back from the original code
    private int getDelayForTarnishState(TarnishStage state) {
        return switch (state) {
            case CRYSTALLIZED -> 5; // 0.25 second
            case UNAFFECTED   -> 20; // 1 second
            case DISCOLORED   -> 40; // 2 seconds
            case CORRODED     -> 60; // 3 seconds
            case TARNISHED    -> 80; // 4 seconds
        };
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isConnectedToRedstone(level, pos)) return;

        if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
            boolean isCrystallized = getAgeFromBlock(state) == TarnishStage.CRYSTALLIZED;

            FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, pos, state);
            this.falling(fallingBlock);

            // Only trigger immediate neighbors when this block falls
            if (isCrystallized) {
                triggerImmediateNeighbors(level, pos);
            }
        }
    }

    private void triggerImmediateNeighbors(ServerLevel level, BlockPos fallenPos) {
        // Check horizontal neighbors
        for (Direction direction : HORIZONTAL_DIRECTIONS) {
            BlockPos neighborPos = fallenPos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (!(neighborState.getBlock() instanceof TarnishingBronzeGrateBlock)) continue;

            TarnishingBronzeGrateBlock neighborGrate = (TarnishingBronzeGrateBlock) neighborState.getBlock();

            // Check if neighbor is crystallized and should fall
            if (neighborGrate.getAgeFromBlock(neighborState) != TarnishStage.CRYSTALLIZED) continue;
            if (neighborGrate.hasImmediateRedstoneSignal(level, neighborPos)) continue;
            if (level.getBlockTicks().hasScheduledTick(neighborPos, neighborGrate)) continue;

            // Only schedule this immediate neighbor
            level.scheduleTick(neighborPos, neighborGrate, CASCADE_DELAY);
        }

        // Check block above
        BlockPos abovePos = fallenPos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (aboveState.getBlock() instanceof TarnishingBronzeGrateBlock) {
            TarnishingBronzeGrateBlock aboveGrate = (TarnishingBronzeGrateBlock) aboveState.getBlock();

            // Check if the grate above can fall (any tarnish state)
            if (!aboveGrate.hasImmediateRedstoneSignal(level, abovePos) &&
                    !level.getBlockTicks().hasScheduledTick(abovePos, aboveGrate)) {

                TarnishStage aboveTarnish = aboveGrate.getAgeFromBlock(aboveState);
                int delay = aboveTarnish == TarnishStage.CRYSTALLIZED ? CASCADE_DELAY : getDelayForTarnishState(aboveTarnish);
                level.scheduleTick(abovePos, aboveGrate, delay);
            }
        }
    }

    private boolean hasImmediateRedstoneSignal(Level level, BlockPos pos) {
        return level.hasNeighborSignal(pos);
    }

    protected void falling(FallingBlockEntity entity) {

    }

    private TarnishStage getAgeFromBlock(BlockState state) {
        return ((TarnishingBronzeBlock) state.getBlock()).getAge();
    }

    private boolean isConnectedToRedstone(Level level, BlockPos origin) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toCheck = new ArrayDeque<>();
        toCheck.add(origin);

        while (!toCheck.isEmpty() && visited.size() < MAX_REDSTONE_PROPAGATION) {
            BlockPos current = toCheck.poll();
            if (!visited.add(current)) continue;

            if (level.hasNeighborSignal(current)) {
                return true;
            }

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (!visited.contains(neighbor)) {
                    BlockState neighborState = level.getBlockState(neighbor);
                    if (neighborState.getBlock() instanceof TarnishingBronzeGrateBlock) {
                        toCheck.add(neighbor);
                    }
                }
            }
        }

        return false;
    }

    @Override
    public TarnishStage getAge() {
        return this.tarnishStage;
    }
}