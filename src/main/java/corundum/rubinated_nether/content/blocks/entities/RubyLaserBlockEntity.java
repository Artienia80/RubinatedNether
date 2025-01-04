package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.mixin.accessors.LevelAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

import org.apache.commons.lang3.mutable.MutableDouble;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.blocks.RubyLaserBlock;
import corundum.rubinated_nether.utils.BlockUpdateListener;
import corundum.rubinated_nether.utils.ShapeUtils;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import corundum.rubinated_nether.utils.UpdateListenerHolder;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class RubyLaserBlockEntity extends BlockEntity implements BlockUpdateListener, TickableBlockEntity {

	// Shapes representing a 1 block long beam segment
	private static final Map<Direction, VoxelShape> BEAM_SEGMENT_SHAPES = ShapeUtils.allDirections(
		Shapes.box(.4, 0, .4, .6, 1, .6)
	);

	private int powerLevel;
	private int blockRange = -1;
	private int currentLaserRange = 15;
	private double rangeRemnant;
	private boolean visible = false;
	private Optional<Integer> color;
	private boolean silly = false;

	public RubyLaserBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.RUBY_LASER.get(), pos, blockState);
	}


	@Override
	public void setLevel(Level level) {
		if(!hasLevel()) UpdateListenerHolder.addUpdateListener(level, this);
		super.setLevel(level);
	}

	@Override
	public void clientTick() {
		//if(blockRange == -1)
			handleBlockUpdate(level, worldPosition, getBlockState());
	}

	@Override
	public void tick() {
		//if(blockRange == -1)
			handleBlockUpdate(level, worldPosition, getBlockState());

		if(getBlockState().getValue(RubyLaserBlock.TINTED)) return;

		Direction facing = getBlockState().getValue(RubyLaserBlock.FACING);

		Vec3i rangeVec = facing.getNormal().multiply(blockRange);
		AABB range = new AABB(0, 0, 0, 1, 1, 1)
				.expandTowards(rangeVec.getX(), rangeVec.getY(), rangeVec.getZ())
				.move(worldPosition.relative(facing));

		MutableDouble lastDistance = new MutableDouble(blockRange);
		((LevelAccessor) level).invokeGetEntities().get(range, entity -> {
			double distance = Math.sqrt(entity.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ())) - 1;
			if(distance < lastDistance.getValue()) lastDistance.setValue(distance);
		});


		int blockDistance = Mth.clamp(Mth.floor(lastDistance.getValue()), 0, currentLaserRange);
		powerLevel = currentLaserRange - blockDistance;
		if(powerLevel != getBlockState().getValue(RubyLaserBlock.POWER)) {
			level.scheduleTick(getBlockPos(), RNBlocks.RUBY_LASER.get(), 2);
		}
	}

	@SuppressWarnings({"DataFlowIssue"})
	@Override
	public void handleBlockUpdate(Level view, BlockPos pos, BlockState bs) {
		this.currentLaserRange = 15;
		Direction facing = getBlockState().getValue(RubyLaserBlock.FACING);
		// BlockPos that is being checked
		BlockPos.MutableBlockPos mutableBlockPos = worldPosition.mutable();

		// Iterating the range of the Laser to check each position
		blockRange = 0;
		for (int i = 0; i <= 15; i++) {
			mutableBlockPos.move(facing);
			blockRange = i;

			BlockState state = level.getBlockState(mutableBlockPos);

			System.out.println("Before states checks");
			boolean blocks = state.is(RNTags.Blocks.RUBY_LASER_NO_SIGNAL);
			if (!blocks && state.is(RNTags.Blocks.RUBY_LASER_TRANSPARENT)) continue;

			VoxelShape shape = Shapes.join(state.getCollisionShape(level, mutableBlockPos), BEAM_SEGMENT_SHAPES.get(facing), BooleanOp.AND);
			System.out.println("Before isEmpty");

			if(!shape.isEmpty()) {
				if(level.isClientSide) {
					Direction.Axis axis = facing.getAxis();
					rangeRemnant = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? shape.min(axis) : 1.0 - shape.max(axis);
				}
				// In case of Tinted Glass the laser range is shortened
				if(blocks) this.currentLaserRange = blockRange;
				System.out.println("Darksonic: Block " + facing + " at " + mutableBlockPos + ". Range: " + this.blockRange);
				break;
			}
		}

		// Ignore what IDEA says its stupid
		//
		BlockState state = level.getBlockState(worldPosition.relative(facing));
		silly = state.is(RNTags.Blocks.RUBY_GLASS);
		visible = silly || state.is(Tags.Blocks.GLASS_BLOCKS);

		if (visible && !silly && state.getBlock() instanceof BeaconBeamBlock) {
			DyeColor dye = ((BeaconBeamBlock) state.getBlock()).getColor();
			color = Optional.of(dye.getTextureDiffuseColor());
		} else {
			color = Optional.empty();
		}

		if(getBlockState().getValue(RubyLaserBlock.TINTED)) {
			powerLevel = Mth.clamp(currentLaserRange - blockRange, 0, 15);
			if (powerLevel != getBlockState().getValue(RubyLaserBlock.POWER)) {
				level.scheduleTick(getBlockPos(), RNBlocks.RUBY_LASER.get(), 2);
			}
		}
	}

	@Override
	public boolean shouldRemove() {
		return this.isRemoved();
	}

	@Override
	public Stream<BlockPos> getListenedPositions() {
		Vec3i offset = getBlockState().getValue(RubyLaserBlock.FACING).getNormal().multiply(15);
		return BlockPos.betweenClosedStream(worldPosition, worldPosition.offset(offset));
	}

	// This overrides a forge thing
	public AABB getRenderBoundingBox() {
		Direction facing = getBlockState().getValue(RubyLaserBlock.FACING);
		Vec3i end = facing.getNormal().multiply(currentLaserRange + 1);
		return new AABB(worldPosition).expandTowards(end.getX(), end.getY(), end.getZ());
	}

	public int getPowerLevel() {
		return powerLevel;
	}

	public int getBlockRange() {
		return (blockRange == -1) ? currentLaserRange : Mth.clamp(blockRange, 0, currentLaserRange);
	}

	public double getRenderRange() {
		return getBlockRange() + rangeRemnant;
	}

	public boolean alwaysVisible() {
		return visible;
	}

	public boolean isColored() {
		return color.isPresent();
	}

	public boolean isSilly() {
		return silly;
	}

	public Optional<Integer> getColor() {
		return color;
	}

	@Override
	public BlockEntityType<?> getType() {
		return RNBlockEntities.RUBY_LASER.get();
	}
}
