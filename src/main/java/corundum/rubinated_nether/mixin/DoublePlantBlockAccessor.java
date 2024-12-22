package corundum.rubinated_nether.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DoublePlantBlock.class)
public interface DoublePlantBlockAccessor {
    @Invoker
    static void invokePreventDropFromBottomPart(Level level, BlockPos pos, BlockState state, Player player) {
        throw new AssertionError();
    }
}
