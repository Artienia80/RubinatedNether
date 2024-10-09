package net.artienia.rubinated_nether.content.block.brazier;

import net.artienia.rubinated_nether.content.RNBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import uwu.serenity.critter.utils.BEBlock;

public class Brazier extends Block implements BEBlock<BrazierBlockEntity> {

    protected static final VoxelShape SHAPE = Block.box(0., 0., 0., 16.0, 16.0, 16.0);

    public Brazier(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
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

