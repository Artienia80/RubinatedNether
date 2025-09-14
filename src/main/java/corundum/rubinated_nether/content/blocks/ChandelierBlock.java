package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChandelierBlock extends TarnishingBronzeBlock {
	protected static final VoxelShape SHAPE_BOTTOM = Block.box(2.0, -2.0, 2.0, 14.0, 5.0, 14.0);
	protected static final VoxelShape SHAPE_TOP = Block.box(-8.0, 5.0, -8.0, 24.0, 10.0, 24.0);
	protected static final VoxelShape SHAPE = Shapes.or(SHAPE_BOTTOM, SHAPE_TOP);

	public ChandelierBlock(TarnishState tarnishState, Properties properties) {
		super(tarnishState, properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		pLevel.scheduleTick(pCurrentPos, this, 2);
		return pState;
	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!this.canSurvive(pState, pLevel, pPos))
			spawnFallingChandelier(pState, pLevel, pPos);

        boolean blockCheck = false;
        boolean playerCheck = false;
        var currentPos = pPos.below();
        while(!blockCheck && !playerCheck){
            if(pLevel.getBlockState(currentPos).getBlock() == Blocks.AIR){
                playerCheck = !pLevel.getEntitiesOfClass(Player.class, new AABB(currentPos)).isEmpty();
                currentPos = currentPos.below();
                continue;
            }
            blockCheck = true;
        }

        if(!pState.getValue(WAXED) && playerCheck)
            spawnFallingChandelier(pState, pLevel, pPos);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return Block.canSupportCenter(level, pos.above(), Direction.UP);
	}

	private static void spawnFallingChandelier(BlockState pState, ServerLevel pLevel, BlockPos pPos) {
		BlockPos.MutableBlockPos blockpos$mutableblockpos = pPos.mutable();
  
		BlockState blockstate = pLevel.getBlockState(blockpos$mutableblockpos);
		FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(pLevel, blockpos$mutableblockpos, blockstate);

		int i = Math.max(1 + pPos.getY() - blockpos$mutableblockpos.getY(), 6);
        var chandelier = (ChandelierBlock) blockstate.getBlock();
		float f = (RNConfig.chandelierStateMultiplierIncrease * (float)i) * tarnishingDamageMultiplier(chandelier.getAge());
	
		fallingblockentity.setHurtsEntities(f, RNConfig.chandelierDefaultDamage);
	}

    private static float tarnishingDamageMultiplier(TarnishState tarnishState)  {
        var multiplier = RNConfig.chandelierStateMultiplierIncrease;
        return tarnishState.ordinal() != 4 ? multiplier * (tarnishState.ordinal() + 2) : multiplier;
    }
}
