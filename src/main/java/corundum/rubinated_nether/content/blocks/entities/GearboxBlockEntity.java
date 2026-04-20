package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GearboxBlockEntity extends BlockEntity implements TickableBlockEntity {
    public int crankCount;

    public GearboxBlockEntity(BlockPos pos, BlockState blockState) {
        super(RNBlockEntities.GEARBOX.get(), pos, blockState);
        this.crankCount = 0;
    }

/*
    public void increaseCrankCount() {
        crankCount = Mth.clamp(++crankCount, 0, 20);
    }

    public void decreaseCrankCount() {
        crankCount = Mth.clamp(--crankCount, 0, 20);
    }
*/

    @Override
    public void tick() {

    }
}
