package net.artienia.rubinated_nether.content.block.brazier;

import net.artienia.rubinated_nether.content.RNBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import uwu.serenity.critter.utils.BEBlock;

import java.util.stream.Stream;

public class Brazier extends Block implements BEBlock<BrazierBlockEntity> {

    protected static final VoxelShape SHAPE = Stream.of(
        box(0, 0, 0, 16, 2, 16),
        box(2, 2, 2, 14, 5, 14),
        box(1, 5, 1, 15, 8, 15),
        box(1, 6, 1, 15, 16,15)
    ).reduce(Shapes::or).get();

    protected static final VoxelShape COLLISION_SHAPE =
        Shapes.join(SHAPE, box(2, 6, 2, 14, 16, 14), BooleanOp.NOT_SAME);

    public Brazier(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public BlockEntityType<? extends BrazierBlockEntity> getBlockEntityType() {
        return RNBlockEntities.BRAZIER.get();
    }

    @Override
    public Class<? extends BrazierBlockEntity> getBlockEntityClass() {
        return BrazierBlockEntity.class;
    }
}

