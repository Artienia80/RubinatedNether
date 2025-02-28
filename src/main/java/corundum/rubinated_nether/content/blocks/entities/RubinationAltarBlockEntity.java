package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class RubinationAltarBlockEntity extends BlockEntity implements Nameable {

    public RubinationAltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(RNBlockEntities.RUBINATION_ALTAR.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RubinationAltarBlockEntity altarBlockEntity){

    }

    @Override
    public Component getName() {
        return Component.translatable("gui.rubinated_nether.rubination_altar");
    }
}
