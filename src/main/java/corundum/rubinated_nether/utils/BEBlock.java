package corundum.rubinated_nether.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * More convenient interface for making blocks with block entities
 * @param <BE> Type of the block entity
 */
public interface BEBlock<BE extends BlockEntity> extends EntityBlock {

    /**
     * @return The type of this block's entity
     */
    BlockEntityType<? extends BE> getBlockEntityType();

    /**
     * @return The class of this block's entity
     */
    Class<? extends BE> getBlockEntityClass();

    @Nullable
    @Override
    default BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntityType().create(pos, state);
    }

    @Nullable
    @Override
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if(Arrays.asList(getBlockEntityClass().getInterfaces()).contains(TickableBlockEntity.class)) {
            return (beLevel, blockPos, blockState, blockEntity) -> {
                if(!blockEntity.hasLevel()) {
                    blockEntity.setLevel(beLevel);
                }
                ((TickableBlockEntity) blockEntity).tick(beLevel.isClientSide);
            };
        }

        return EntityBlock.super.getTicker(level, state, blockEntityType);
    }

    /**
     * More convenient method for getting a block entity from this block
     * @param access BlockGetter to get the BE from (usually a {@link Level})
     * @param pos Block entity's position
     * @return Block entity with the type specified by this interface
     */
    @Nullable
    @SuppressWarnings("unchecked")
    default BE getBlockEntity(BlockGetter access, BlockPos pos) {
        BlockEntity blockEntity = access.getBlockEntity(pos);
        Class<?> expectedClass = getBlockEntityClass();

        if (!expectedClass.isInstance(blockEntity) || blockEntity.getType() != getBlockEntityType())
            return null;

        return (BE) blockEntity;
    }
}
