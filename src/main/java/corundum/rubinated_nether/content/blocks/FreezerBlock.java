package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;


import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FreezerBlock extends AbstractFurnaceBlock {
	public static final MapCodec<FreezerBlock> CODEC = simpleCodec(FreezerBlock::new);

	public FreezerBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	protected MapCodec<? extends AbstractFurnaceBlock> codec() {
		return CODEC;
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FreezerBlockEntity(pos, state);
	}

	@Override
	protected void openContainer(Level level, BlockPos blockPos, Player player) {
		if (level.isClientSide()) 
			return;

		BlockEntity entity = level.getBlockEntity(blockPos);

		if(entity instanceof FreezerBlockEntity freezer)
			player.openMenu(freezer);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (state.getValue(LIT)) {
			double x = pos.getX() + 0.5;
			double y = pos.getY() + 1.0 + (random.nextFloat() * 6.0) / 16.0;
			double z = pos.getZ() + 0.5;
			level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, 0.0, 0.0, 0.0);
		}
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return createTicker(level, blockEntityType, RNBlockEntities.FREEZER.get());
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> serverType, BlockEntityType<? extends FreezerBlockEntity> clientType) {
		return level.isClientSide() ? null : createTickerHelper(serverType, clientType, FreezerBlockEntity::serverTick);
	}
}





