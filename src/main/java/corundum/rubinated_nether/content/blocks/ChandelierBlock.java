package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNDamageTypes;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.bases.TarnishingBronzeBlock;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import corundum.rubinated_nether.utils.BEBlock;
import corundum.rubinated_nether.utils.RNConfig;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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

public class ChandelierBlock extends Block implements Fallable {
    protected static final VoxelShape SHAPE_BOTTOM = Block.box(2.0, -2.0, 2.0, 14.0, 5.0, 14.0);
    protected static final VoxelShape SHAPE_TOP = Block.box(-8.0, 5.0, -8.0, 24.0, 10.0, 24.0);
    protected static final VoxelShape SHAPE = Shapes.or(SHAPE_BOTTOM, SHAPE_TOP);

    protected TarnishStage tarnishStage;

    public ChandelierBlock(TarnishStage tarnishStage, Properties properties) {
        super(properties);
        this.tarnishStage = tarnishStage;
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

    static void spawnFallingChandelier(BlockState pState, ServerLevel pLevel, BlockPos pPos) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = pPos.mutable();

        BlockState blockstate = pLevel.getBlockState(blockpos$mutableblockpos);
        if(!(blockstate.getBlock() instanceof ChandelierBlock chandelier)) return;

        FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(pLevel, blockpos$mutableblockpos, blockstate);

        int fallDistance = Math.max(1 + pPos.getY() - blockpos$mutableblockpos.getY(), 6);
        float f = (RNConfig.chandelierStateMultiplierIncrease * (float) fallDistance) * tarnishingDamageMultiplier(chandelier.getAge());

        fallingblockentity.setHurtsEntities(f, RNConfig.chandelierDefaultDamage);

        var nbt = fallingblockentity.saveWithoutId(new net.minecraft.nbt.CompoundTag());
        nbt.putInt("ChandelierTarnishState", chandelier.tarnishStage.ordinal());
        nbt.putInt("ChandelierFallDistance", fallDistance);
        fallingblockentity.load(nbt);
    }

    private static float tarnishingDamageMultiplier(TarnishStage tarnishStage)  {
        var multiplier = RNConfig.chandelierStateMultiplierIncrease;
        return tarnishStage.ordinal() != 4 ? multiplier * (tarnishStage.ordinal() + 2) : multiplier;
    }

    @Override
    public DamageSource getFallDamageSource(Entity entity) {
        DamageSource source = new DamageSource(
                entity.level().registryAccess()
                        .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(RNDamageTypes.CHANDELIER),
                entity
        );

        if (entity instanceof FallingBlockEntity fallingBlock) {
            var nbt = fallingBlock.saveWithoutId(new net.minecraft.nbt.CompoundTag());
            int tarnishOrdinal = nbt.getInt("ChandelierTarnishState");
            int fallDistance = nbt.getInt("ChandelierFallDistance");

            fallingBlock.getPersistentData().putInt("ChandelierTarnish", tarnishOrdinal);
            fallingBlock.getPersistentData().putInt("ChandelierFallDistance", fallDistance);
        }

        return source;
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
    }
}