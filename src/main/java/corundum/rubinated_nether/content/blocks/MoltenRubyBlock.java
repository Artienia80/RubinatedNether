package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MoltenRubyBlock extends RotatedPillarBlock implements BucketPickup {

    public MoltenRubyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        level.playSound(null, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
        return new ItemStack(corundum.rubinated_nether.content.RNItems.MOLTEN_RUBY_BUCKET.get());
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL_LAVA);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (shouldConvertToBleedingObsidian(level, pos)) {
            convertToBleedingObsidian(level, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (shouldConvertToBleedingObsidian(level, pos)) {
            convertToBleedingObsidian(level, pos);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (shouldConvertToBleedingObsidian(level, pos)) {
            convertToBleedingObsidian(level, pos);
        }
    }

    private boolean shouldConvertToBleedingObsidian(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            FluidState fluidState = level.getFluidState(neighborPos);
            if (fluidState.is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    private void convertToBleedingObsidian(Level level, BlockPos pos) {
        level.setBlock(pos, RNBlocks.BLEEDING_OBSIDIAN.get().defaultBlockState(), 3);
        level.levelEvent(1501, pos, 0);
    }
}