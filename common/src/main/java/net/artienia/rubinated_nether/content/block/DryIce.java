package net.artienia.rubinated_nether.content.block;

import net.artienia.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DryIce extends Block {

	protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

	public DryIce(Properties properties) {
		super(properties);
	}

	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		checkAndDrop(state, level, pos);
	}

	// Custom method to check support and drop the block if no valid block is below
	private void checkAndDrop(BlockState state, Level level, BlockPos pos) {
		if (!canSurvive(state, level, pos)) {
			level.destroyBlock(pos, false); // Destroy the block
		}
	}

	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (!(level instanceof Level levelInstance)) {
			// If level is not a Level instance, return false
			return false;
		}

		BlockState blockState = level.getBlockState(pos.below());

		// Check if in the Nether
		if (levelInstance.dimension() == Level.NETHER) {
			// Iterate over horizontal directions to find ectoplasm fluid
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				FluidState fluidState = level.getFluidState(pos.below().relative(direction));
				if (fluidState.is(BuiltInRegistries.FLUID.get(new ResourceLocation("netherexp", "ectoplasm")))) {
					// In Nether, block below must be Coldest Ice and ectoplasm must be nearby
					return blockState.is(RNTags.Blocks.COLDEST_ICE);
				}
			}
			// No ectoplasm found, return false
			return false;
		}

		// Check if in Aether or End dimensions
		ResourceKey<Level> aetherDimension = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("aether:the_aether"));
		if (levelInstance.dimension() == Level.END || levelInstance.dimension() == aetherDimension) {
			// In Aether or End, full block below must exist
			return Block.isFaceFull(blockState.getCollisionShape(level, pos.below()), Direction.UP);
		}

		// For other dimensions (including Overworld), block below must be Coldest Ice
		return blockState.is(RNTags.Blocks.COLDEST_ICE);
	}



}


