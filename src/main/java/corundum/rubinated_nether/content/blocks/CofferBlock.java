package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.blocks.entities.CofferBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CofferBlock extends AbstractChestBlock<CofferBlockEntity> implements SimpleWaterloggedBlock {
    public static final MapCodec<CofferBlock> CODEC = simpleCodec(p_304364_ -> new CofferBlock(p_304364_, () -> RNBlockEntities.COFFER.get()));
    public CofferBlock(BlockBehaviour.Properties properties, Supplier<BlockEntityType<? extends CofferBlockEntity>> blockEntityType) {
        super(properties, blockEntityType);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        return new CofferBlockEntity(pos, state);
    }

    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            MenuProvider menuprovider = this.getMenuProvider(state, level, pos);
            if (menuprovider != null) {
                player.openMenu(menuprovider);
                PiglinAi.angerNearbyPiglins(player, true);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public MapCodec<? extends CofferBlock> codec() {
        return CODEC;
    }

    @Override
    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState blockState, Level level, BlockPos blockPos, boolean b) {
        return null;
    }
}
