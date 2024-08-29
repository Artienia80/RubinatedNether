package net.artienia.rubinated_nether.block;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

public class Chandelier extends Block {
    protected static final VoxelShape SHAPE_BOTTOM = Block.box(2.0, -2.0, 2.0, 14.0, 5.0, 14.0);
    protected static final VoxelShape SHAPE_TOP = Block.box(-8.0, 5.0, -8.0, 24.0, 10.0, 24.0);
    protected static final VoxelShape SHAPE = Shapes.or(SHAPE_BOTTOM, SHAPE_TOP);

    public Chandelier(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

}

