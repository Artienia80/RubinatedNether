package net.artienia.rubinated_nether.block.entity;

import net.artienia.rubinated_nether.ModTags;
import net.artienia.rubinated_nether.block.ModBlocks;
import net.artienia.rubinated_nether.block.RubyLaserBlock;
import net.artienia.rubinated_nether.platform.PlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BeaconBeamBlock;
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
    private float[] color;
    private boolean colored = false;
    private boolean silly = false;

    static {
        for(Direction direction : Direction.values()) {
            FACE_RANGES.put(direction, new AABB(0, 0, 0, 1, 1, 1)
                .expandTowards(Vec3.atLowerCornerOf(direction.getNormal().multiply(15))));
        }
    }

    public RubyLaserBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.RUBY_LASER.get(), pos, blockState);
    }

    public void handleBlockUpdate(BlockPos pos) {
        // TODO
    }

    public void tick() {
        if(level == null) return;

        Direction facing = getBlockState().getValue(RubyLaserBlock.FACING);

        // Only re check blocks ever 8th game tick
        if(blockRange == -1 || level.getGameTime() % 8 == 0) {
            BlockPos.MutableBlockPos mutableBlockPos = worldPosition.mutable();
            blockRange = 0;
            for (int i = 0; i <= 15; i++) {
                mutableBlockPos.move(facing);
                blockRange = i;
                if (level.getBlockState(mutableBlockPos).canOcclude()) break;
            }

            // Ignore what IDEA says its stupid
            BlockState state = level.getBlockState(worldPosition.relative(facing));
            silly = state.is(ModTags.Blocks.RUBY_GLASS);
            visible = silly || state.is(PlatformUtils.getGlassTag());

            if(visible && !silly && state.getBlock() instanceof BeaconBeamBlock) {
                DyeColor dye = ((BeaconBeamBlock) state.getBlock()).getColor();
                color = dye.getTextureDiffuseColors();
                colored = true;
            } else {
                colored = false;
            }
        }

        // Further processing only needs to be done server-side
        if(level.isClientSide) return;

        if(getBlockState().getValue(RubyLaserBlock.TINTED)) {
            powerLevel = Mth.clamp(15 - (blockRange), 0, 15);
            if(powerLevel != getBlockState().getValue(RubyLaserBlock.POWER)) {
                level.scheduleTick(getBlockPos(), ModBlocks.RUBY_LASER.get(), 2);
            }
            return;
        }

        Vec3i rangeVec = facing.getNormal().multiply(blockRange);
        AABB range = new AABB(0, 0, 0, 1, 1, 1)
            .expandTowards(rangeVec.getX(), rangeVec.getY(), rangeVec.getZ())
            .move(worldPosition.relative(facing));

        MutableDouble lastDistance = new MutableDouble(blockRange);
        level.getEntities().get(range, entity -> {
            double distance = Math.sqrt(entity.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())) - 1;
            if(distance < lastDistance.getValue()) lastDistance.setValue(distance);
        });

        int blockDistance = Mth.clamp(Mth.floor(lastDistance.getValue()), 0, 15);
        powerLevel = 15 - blockDistance;
        if(powerLevel != getBlockState().getValue(RubyLaserBlock.POWER)) {
            level.scheduleTick(getBlockPos(), ModBlocks.RUBY_LASER.get(), 2);
        }
    }

    // This overrides a forge thing
    public AABB getRenderBoundingBox() {
        return FACE_RANGES.get(getBlockState().getValue(RubyLaserBlock.FACING)).move(worldPosition);
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public int getBlockRange() {
        return (blockRange == -1) ? 15 : Mth.clamp(blockRange, 0, 15);
    }

    public boolean alwaysVisible() {
        return visible;
    }

    public boolean isColored() {
        return colored;
    }

    public boolean isSilly() {
        return silly;
    }

    public float[] getColor() {
        return color;
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
