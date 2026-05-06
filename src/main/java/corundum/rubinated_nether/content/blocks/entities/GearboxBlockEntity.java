package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.blocks.GearboxBlock;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GearboxBlockEntity extends BlockEntity implements TickableBlockEntity {
    private int oCrankVal;

    public GearboxBlockEntity(BlockPos pos, BlockState blockState) {
        super(RNBlockEntities.GEARBOX.get(), pos, blockState);
    }

    @Override
    public void tick() {
        int crankVal = this.getBlockState().getValue(GearboxBlock.CRANK_LEVEL);
        if(this.oCrankVal != crankVal) {
            this.turnCrank(this.oCrankVal - crankVal);
        }
    }

    private void turnCrank(int dir) {

    }
}
