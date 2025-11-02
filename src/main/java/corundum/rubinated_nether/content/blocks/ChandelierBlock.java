package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNDamageTypes;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import corundum.rubinated_nether.utils.BEBlock;
import corundum.rubinated_nether.utils.RNConfig;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ChandelierBlock extends TarnishingBronzeBlock implements BEBlock<ChandelierBlock.ChandelierBlockEntity>, Fallable {
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
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.above(), Direction.UP);
    }

    private static void spawnFallingChandelier(BlockState pState, ServerLevel pLevel, BlockPos pPos) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = pPos.mutable();

        BlockState blockstate = pLevel.getBlockState(blockpos$mutableblockpos);
        if(!(blockstate.getBlock() instanceof ChandelierBlock chandelier)) return;

        FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(pLevel, blockpos$mutableblockpos, blockstate);

        int i = Math.max(1 + pPos.getY() - blockpos$mutableblockpos.getY(), 6);
        float f = (RNConfig.chandelierStateMultiplierIncrease * (float) i) * tarnishingDamageMultiplier(chandelier.getAge());
        fallingblockentity.setHurtsEntities(f, RNConfig.chandelierDefaultDamage);
    }

    private static float tarnishingDamageMultiplier(TarnishState tarnishState)  {
        var multiplier = RNConfig.chandelierStateMultiplierIncrease;
        return tarnishState.ordinal() != 4 ? multiplier * (tarnishState.ordinal() + 2) : multiplier;
    }

    @Override
    public BlockEntityType<? extends ChandelierBlockEntity> getBlockEntityType() {
        return RNBlockEntities.CHANDELIER.get();
    }

    @Override
    public Class<? extends ChandelierBlockEntity> getBlockEntityClass() {
        return ChandelierBlockEntity.class;
    }

    @Override
    public DamageSource getFallDamageSource(Entity entity) {
        return entity.damageSources().generic();
    }

    private static void inflictDisease(LivingEntity player) {
        player.addEffect(new MobEffectInstance(RNEffects.BRONZE_DISEASED, 1000));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChandelierBlockEntity(pos, state);
    }

    public static class ChandelierBlockEntity extends BlockEntity implements TickableBlockEntity {
        public ChandelierBlockEntity(BlockPos pos, BlockState blockState) {
            super(RNBlockEntities.CHANDELIER.get(), pos, blockState);
        }

        @Override
        public void tick() {
            var pLevel = this.getLevel();
            var pState = this.getBlockState();
            var pPos = this.getBlockPos();

            if (!(pState.getBlock() instanceof ChandelierBlock chandelier) || chandelier.getAge().ordinal() != 4) {
                return;
            }

            boolean hasUnobstructedLineOfSight = true;
            boolean entityDetected = false;
            var currentPos = pPos.below();

            // Check for unobstructed line of sight and entities
            while (hasUnobstructedLineOfSight && !entityDetected) {
                BlockState blockState = pLevel.getBlockState(currentPos);

                // If we hit a solid block, no line of sight
                if (blockState.getBlock() != Blocks.AIR) {
                    hasUnobstructedLineOfSight = false;
                    break;
                }

                // Check for entities at this position (excluding BronzeEntity)
                List<Entity> entities = pLevel.getEntitiesOfClass(Entity.class, new AABB(currentPos));
                for (Entity entity : entities) {
                    if (!(entity instanceof BronzeEntity)) {
                        entityDetected = true;
                        break;
                    }
                }

                // Move to next block below
                currentPos = currentPos.below();

                // Stop if we've gone too far down (prevent infinite loop)
                if (currentPos.getY() < pLevel.getMinBuildHeight()) {
                    break;
                }
            }

            if (!pState.getValue(WAXED) && entityDetected && hasUnobstructedLineOfSight) {
                spawnFallingChandelier(pState, (ServerLevel) pLevel, pPos);
            }
        }
    }
}