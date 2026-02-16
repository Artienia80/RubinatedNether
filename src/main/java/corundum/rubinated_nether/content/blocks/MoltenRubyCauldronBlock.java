package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import corundum.rubinated_nether.content.RNCauldronInteraction;

public class MoltenRubyCauldronBlock extends AbstractCauldronBlock {
    public static final MapCodec<MoltenRubyCauldronBlock> CODEC = simpleCodec(MoltenRubyCauldronBlock::new);

    @Override
    public MapCodec<MoltenRubyCauldronBlock> codec() {
        return CODEC;
    }

    public MoltenRubyCauldronBlock(BlockBehaviour.Properties properties) {
        super(properties, RNCauldronInteraction.MOLTEN_RUBY);
    }

    @Override
    protected double getContentHeight(BlockState state) {
        return 0.9375;
    }

    @Override
    public boolean isFull(BlockState state) {
        return true;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (this.isEntityInsideContent(state, pos, entity)) {
            entity.lavaHurt();
        }
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return 3;
    }
}