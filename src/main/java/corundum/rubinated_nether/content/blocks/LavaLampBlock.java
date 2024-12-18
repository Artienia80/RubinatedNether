package corundum.rubinated_nether.content.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import corundum.rubinated_nether.utils.ShapeUtils;

import java.util.Map;

public class LavaLampBlock extends RotatedPillarBlock {

   protected static final Map<Direction.Axis, VoxelShape> AXIS_SHAPES = ShapeUtils.allAxis(Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D));

   public LavaLampBlock(Properties properties) {
	  super(properties);
   }

   public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
	  return AXIS_SHAPES.get(state.getValue(AXIS));
   }

   public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
	  super.playerDestroy(level, player, pos, state, blockEntity, tool);
	  if(level.getRandom().nextInt(1_000_000) == 0) {
		 level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
	  }
   }
}