package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.blocks.LaserMode;
import corundum.rubinated_nether.utils.BeamScanCache;
import corundum.rubinated_nether.utils.ShapeUtils;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractLaserBlockEntity extends BlockEntity implements TickableBlockEntity {

	public record BeamSegment(double start, Optional<Integer> color, boolean silly) {}

	// Shapes representing a 1 block long beam segment
	private static final Map<Direction, VoxelShape> BEAM_SEGMENT_SHAPES = ShapeUtils.allDirections(
			Shapes.box(.4, 0, .4, .6, 1, .6)
	);

	protected int powerLevel;
	protected int blockRange = -1;
	protected int currentRange;
	private double rangeRemnant;
	private double visibleStart = -1;
	private List<BeamSegment> beamSegments = List.of();
	private final BeamScanCache beamCache = new BeamScanCache();

	protected AbstractLaserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
		this.currentRange = getMaxRange();
	}

	protected abstract int getMaxRange();

	public abstract LaserMode getMode();

	public abstract void handleBlockUpdate(Level view, BlockPos pos, BlockState bs);

	@Override
	public void clientTick() {
		handleBlockUpdate(level, worldPosition, getBlockState());
	}

	public Direction getFacing() {
		return getBlockState().getValue(DirectionalBlock.FACING);
	}

	protected void refreshBeam() {
		int maxRange = getMaxRange();
		Direction facing = getFacing();

		if (!beamCache.isValid(level, worldPosition, facing, maxRange)) {
			rescanBeam(facing, maxRange);
		}
	}

	private void rescanBeam(Direction facing, int maxRange) {
		beamCache.begin(level, facing, maxRange);
		this.currentRange = maxRange;

		// BlockPos that is being checked
		BlockPos.MutableBlockPos mutableBlockPos = worldPosition.mutable();

		// Reset block range
		blockRange = -1;
		rangeRemnant = 0;
		visibleStart = -1;

		List<BeamSegment> segments = new ArrayList<>();
		Optional<Integer> tint = Optional.empty();
		boolean silly = false;

		// Block detection - all modes detect blocks for obstruction/rendering
		// Iterating the range of the Laser to check each position
		int scanned = 0;
		for (int i = 0; i < maxRange; i++) {
			mutableBlockPos.move(facing);
			int currentDistance = i + 1; // We're now at distance i+1 from laser
			scanned = currentDistance;

			BlockState state = level.getBlockState(mutableBlockPos);
			beamCache.record(i, state);

			if (isBeamMedium(state)) {
				double entryY = currentDistance;
				if (level.isClientSide) {
					entryY += getNearOffset(Shapes.join(state.getCollisionShape(level, mutableBlockPos), BEAM_SEGMENT_SHAPES.get(facing), BooleanOp.AND), facing);
				}
				if (visibleStart == -1) {
					visibleStart = entryY;
					segments.add(new BeamSegment(entryY, tint, silly));
				}
				if (!silly) {
					if (state.is(RNTags.Blocks.SILLY_LASER)) {
						silly = true;
						tint = Optional.empty();
						pushSegment(segments, new BeamSegment(entryY, tint, true));
					} else if (state.getBlock() instanceof BeaconBeamBlock beaconBeamBlock) {
						int dye = beaconBeamBlock.getColor().getTextureDiffuseColor() & 0xFFFFFF;
						int mixed = tint.map(current -> mixColors(current, dye)).orElse(dye);
						if (tint.isEmpty() || tint.get() != mixed) {
							tint = Optional.of(mixed);
							pushSegment(segments, new BeamSegment(entryY, tint, false));
						}
					}
				}
			}

			boolean blockCheck = state.is(RNTags.Blocks.LASER_NO_SIGNAL);
			if (!blockCheck && state.is(RNTags.Blocks.LASER_TRANSPARENT)) continue;

			VoxelShape shape = Shapes.join(state.getCollisionShape(level, mutableBlockPos), BEAM_SEGMENT_SHAPES.get(facing), BooleanOp.AND);

			if(!shape.isEmpty()) {
				if(level.isClientSide) {
					rangeRemnant = getNearOffset(shape, facing);
				}
				// In case of Tinted Glass the laser range is shortened
				if(blockCheck) this.currentRange = currentDistance;
				blockRange = currentDistance;
				break;
			}
		}
		this.beamSegments = List.copyOf(segments);
		beamCache.finish(scanned);
	}

	private static double getNearOffset(VoxelShape shape, Direction facing) {
		if (shape.isEmpty()) return 0;
		Direction.Axis axis = facing.getAxis();
		return facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? shape.min(axis) : 1.0 - shape.max(axis);
	}

	private static void pushSegment(List<BeamSegment> segments, BeamSegment segment) {
		int last = segments.size() - 1;
		if (last >= 0 && segments.get(last).start() == segment.start()) {
			segments.set(last, segment);
		} else {
			segments.add(segment);
		}
	}

	private static int mixColors(int a, int b) {
		int r = (((a >> 16) & 0xFF) + ((b >> 16) & 0xFF)) / 2;
		int g = (((a >> 8) & 0xFF) + ((b >> 8) & 0xFF)) / 2;
		int bl = ((a & 0xFF) + (b & 0xFF)) / 2;
		return (r << 16) | (g << 8) | bl;
	}

	protected boolean isNoSignalBlock(int range) {
		BlockPos obstructionPos = worldPosition.relative(getFacing(), range);
		BlockState obstructionState = level.getBlockState(obstructionPos);
		return obstructionState.is(RNTags.Blocks.LASER_NO_SIGNAL);
	}

	protected double getEntityBlockDistance(Entity entity, Direction facing) {
		// Calculate distance in blocks from laser position to entity
		// Use the laser's facing direction to get the correct axis distance
		double axisDistance;
		switch (facing.getAxis()) {
			case X -> axisDistance = Math.abs(entity.getX() - (worldPosition.getX() + 0.5));
			case Y -> axisDistance = Math.abs(entity.getY() - (worldPosition.getY() + 0.5));
			case Z -> axisDistance = Math.abs(entity.getZ() - (worldPosition.getZ() + 0.5));
			default -> axisDistance = 0;
		}

		// Convert to block distance (round up to next block boundary)
		return Math.ceil(axisDistance);
	}

	// This overrides a forge thing
	public AABB getRenderBoundingBox() {
		Direction facing = getFacing();
		Vec3i end = facing.getNormal().multiply(currentRange + 1);
		return new AABB(worldPosition).expandTowards(end.getX(), end.getY(), end.getZ());
	}

	protected AABB getLaserRangeAABB(BlockPos worldPosition, Direction facing) {
		int effectiveRange = (blockRange == -1) ? currentRange : Math.min(blockRange, currentRange);
		Vec3i rangeVec = facing.getNormal().multiply(effectiveRange);
		return new AABB(0, 0, 0, 1, 1, 1)
				.expandTowards(rangeVec.getX(), rangeVec.getY(), rangeVec.getZ())
				.move(worldPosition.relative(facing));
	}

	protected void grantLaserDetectionAdvancement(Entity entity) {
		if (entity instanceof ServerPlayer serverPlayer) {
			var advancementHolder = serverPlayer.server.getAdvancements()
					.get(RubinatedNether.id("laser_detection"));

			if (advancementHolder != null) {
				var progress = serverPlayer.getAdvancements().getOrStartProgress(advancementHolder);
				if (!progress.isDone()) {
					for (String criterion : progress.getRemainingCriteria()) {
						serverPlayer.getAdvancements().award(advancementHolder, criterion);
					}
				}
			}
		}
	}

	public int getPowerLevel() {
		return powerLevel;
	}

	public int getBlockRange() {
		return (blockRange == -1) ? currentRange : Mth.clamp(blockRange, 0, currentRange);
	}

	public int getCurrentRange() {
		return currentRange;
	}

	public double getRenderRange() {
		if (blockRange == -1) return getBlockRange();
		return getBlockRange() - 1 + rangeRemnant;
	}

	private static boolean isBeamMedium(BlockState state) {
		return state.is(RNTags.Blocks.SILLY_LASER) || state.is(Tags.Blocks.GLASS_BLOCKS)
				|| state.is(Tags.Blocks.GLASS_BLOCKS_TINTED) || state.is(Tags.Blocks.GLASS_PANES);
	}

	public boolean alwaysVisible() {
		return visibleStart != -1;
	}

	public double getVisibleStart() {
		return visibleStart == -1 ? 1 : visibleStart;
	}

	public List<BeamSegment> getBeamSegments() {
		return beamSegments;
	}
}
