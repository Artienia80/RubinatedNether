package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.entities.BrazierBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class BrazierBlock extends BaseEntityBlock {
	public static final MapCodec<BrazierBlock> CODEC = simpleCodec(BrazierBlock::new);
	public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 9);

	protected static final VoxelShape SHAPE = Stream.of(
			box(0, 0, 0, 16, 3, 16),
			box(2, 2, 2, 14, 5, 14),
			box(2, 5, 2, 14, 8, 14),
			box(2, 6, 2, 14, 16, 14)
	).reduce(Shapes::or).get();

	protected static final VoxelShape COLLISION_SHAPE =
			Shapes.join(SHAPE, box(1.5, 6, 1.5, 14.5, 16, 14.5), BooleanOp.NOT_SAME);

	public BrazierBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(LEVEL);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return COLLISION_SHAPE;
	}

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int currentLevel = state.getValue(LEVEL);
        BlockEntity be = level.getBlockEntity(pos);

        if (!(be instanceof BrazierBlockEntity brazier)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Molten Ruby Block - fill from 0 to 9
        if (stack.is(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem())) {
            if (currentLevel == 0) {
                if (!level.isClientSide) {
                    brazier.setFuelForLevel(9); // Set fuel FIRST
                    level.setBlock(pos, state.setValue(LEVEL, 9), 3); // Then update level
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Molten Ruby - add one level
        if (stack.is(RNItems.MOLTEN_RUBY_ITEM.get())) {
            if (currentLevel < 9) {
                if (!level.isClientSide) {
                    brazier.addFuel(1); // Add fuel FIRST
                    level.setBlock(pos, state.setValue(LEVEL, currentLevel + 1), 3); // Then update level
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0F, 1.5F);
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BrazierBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		if (level.isClientSide) {
			return createTickerHelper(blockEntityType, RNBlockEntities.BRAZIER.get(), BrazierBlockEntity::clientTickStatic);
		}
		return createTickerHelper(blockEntityType, RNBlockEntities.BRAZIER.get(), BrazierBlockEntity::serverTickStatic);
	}
}