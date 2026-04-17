package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GearboxBlockEntity extends BlockEntity {
    public GearboxBlockEntity(BlockPos pos, BlockState blockState) {
        super(RNBlockEntities.GEARBOX.get(), pos, blockState);
    }
}
