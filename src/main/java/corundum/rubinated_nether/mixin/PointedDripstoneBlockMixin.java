package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PointedDripstoneBlock.class)
public class PointedDripstoneBlockMixin {

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void onAnimateTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (PointedDripstoneBlock.canDrip(state)) {
            if (hasBleedingObsidianAbove(level, pos, state)) {
                if (random.nextFloat() < 0.12F && random.nextInt(5) == 0) {
                    double x = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
                    double y = (double)pos.getY() + 1.0 - 0.6875 - 0.0625;
                    double z = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.3;
                    level.addParticle(RNParticleTypes.BLOOD_DRIP.get(), x, y, z, 0.0, 0.0, 0.0);
                }
                ci.cancel();
            }
        }
    }

    private boolean hasBleedingObsidianAbove(Level level, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(PointedDripstoneBlock.TIP_DIRECTION);
        if (direction != Direction.DOWN) {
            return false;
        }

        BlockPos checkPos = pos;
        for (int i = 0; i < 11; i++) {
            checkPos = checkPos.above();
            BlockState checkState = level.getBlockState(checkPos);

            if (checkState.is(RNBlocks.BLEEDING_OBSIDIAN.get())) {
                return true;
            }

            if (!checkState.is(net.minecraft.world.level.block.Blocks.POINTED_DRIPSTONE)) {
                return false;
            }
        }

        return false;
    }
}