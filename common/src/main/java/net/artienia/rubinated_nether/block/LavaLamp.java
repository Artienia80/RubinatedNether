package net.artienia.rubinated_nether.block;

import net.minecraft.core.BlockPos;
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

public class LavaLamp extends RotatedPillarBlock {
   protected static final float AABB_MIN = 2.0F;
   protected static final float AABB_MAX = 14.0F;
   protected static final VoxelShape Y_AXIS_AABB = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 16.0D);
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);

   public LavaLamp(Properties properties) {
      super(properties);
   }

   public VoxelShape getShape(BlockState p_154346_, BlockGetter p_154347_, BlockPos p_154348_, CollisionContext p_154349_) {
      switch (p_154346_.getValue(AXIS)) {
         case X:
         default:
            return X_AXIS_AABB;
         case Z:
            return Z_AXIS_AABB;
         case Y:
            return Y_AXIS_AABB;
      }
   }

   public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
      super.playerDestroy(level, player, pos, state, blockEntity, tool);
      if(level.getRandom().nextInt(1_000_000) == 0) {
         level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
      }
   }
}