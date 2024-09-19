package net.artienia.rubinated_nether.block.entity;

import net.artienia.rubinated_nether.block.ModBlocks;
import net.artienia.rubinated_nether.block.RubyLaserBlock;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableDouble;
import java.util.EnumMap;
import java.util.Map;

public class RubyLaserBlockEntity extends BlockEntity {

    private static final Map<Direction, AABB> FACE_RANGES = new EnumMap<>(Direction.class);
    private static final String VISIBLE_KEY = "alwaysVisible";

    private int powerLevel;
    private int blockRange = -1;
    private boolean visible = false;

    static {
        for(Direction direction : Direction.values()) {
            FACE_RANGES.put(direction, new AABB(0, 0, 0, 1, 1, 1)
                .expandTowards(Vec3.atLowerCornerOf(direction.getNormal().multiply(15))));
        }
    }

    public RubyLaserBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.RUBY_LASER.get(), pos, blockState);
    }

    public void tick() {
        if(level == null) return;

        Direction facing = getBlockState().getValue(RubyLaserBlock.FACING);

        // Only re check blocks ever 8th game tick
        if(blockRange == -1 || level.getGameTime() % 8 == 0) {
            BlockPos.MutableBlockPos mutableBlockPos = worldPosition.mutable();
            blockRange = 0;
            for (int i = 0; i < 15; i++) {
                mutableBlockPos.move(facing);
                if (level.getBlockState(mutableBlockPos).canOcclude()) break;
                blockRange = i;
            }
        }

        AABB range = new AABB(0, 0, 0, 1, 1, 1)
            .expandTowards(Vec3.atLowerCornerOf(facing.getNormal().multiply(blockRange)))
            .move(worldPosition.relative(facing));

        MutableDouble lastDistance = new MutableDouble(blockRange + 1);
        level.getEntities().get(range, entity -> {
            double distance = Math.sqrt(entity.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())) - 1;
            if(distance < lastDistance.getValue()) lastDistance.setValue(distance);
        });

        int blockDistance = Mth.clamp(Mth.floor(lastDistance.getValue()), 0, 15);
        powerLevel = 15 - blockDistance;
        if(powerLevel != getBlockState().getValue(RubyLaserBlock.POWER) && (!level.isClientSide)) {
            level.scheduleTick(getBlockPos(), ModBlocks.RUBY_LASER.get(), 2);
        }
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public int getBlockRange() {
        return (blockRange == -1) ? 15 : Mth.clamp(blockRange, 0, 15);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        setChanged();

        if(level != null && !level.isClientSide) {
            // TODO: Sync to client if necessary
        }
    }

    public boolean alwaysVisible() {
        return level.getBlockState(getBlockPos().relative(getBlockState().getValue(RubyLaserBlock.FACING))).is(Blocks.GLASS);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putBoolean(VISIBLE_KEY, visible);
    }

    @Override
    public void load(CompoundTag tag) {
        visible = tag.getBoolean(VISIBLE_KEY);
    }
}
