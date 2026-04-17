package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNDamageTypes;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.bases.TarnishingBronze;
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
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class TarnishingChandelierBlock extends ChandelierBlock implements BEBlock<TarnishingChandelierBlock.ChandelierBlockEntity>, Fallable, TarnishingBronze {
    public static final MapCodec<TarnishingChandelierBlock> CODEC = RecordCodecBuilder.mapCodec(
            blockInstance -> blockInstance.group(
                            TarnishStage.CODEC
                                    .fieldOf("tarnishing_state")
                                    .forGetter(ChangeOverTimeBlock::getAge),
                            propertiesCodec()
                    )
                    .apply(blockInstance, TarnishingChandelierBlock::new)
    );

    @Override
    public MapCodec<TarnishingChandelierBlock> codec() {
        return CODEC;
    }

    public TarnishingChandelierBlock(TarnishStage tarnishStage, Properties properties) {
        super(tarnishStage, properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.onTarnishTick(state, level, pos, random);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return TarnishingBronze.canCrystallize(state.getBlock());
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChandelierBlockEntity(pos, state);
    }

    @Override
    public TarnishStage getAge() {
        return this.tarnishStage;
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

            if (!(pState.getBlock() instanceof TarnishingChandelierBlock chandelier) || chandelier.getAge().ordinal() != 4) {
                return;
            }

            boolean hasUnobstructedLineOfSight = true;
            boolean entityDetected = false;
            var currentPos = pPos.below();

            while (hasUnobstructedLineOfSight && !entityDetected) {
                BlockState blockState = pLevel.getBlockState(currentPos);

                if (blockState.getBlock() != Blocks.AIR) {
                    hasUnobstructedLineOfSight = false;
                    break;
                }

                List<LivingEntity> entities = pLevel.getEntitiesOfClass(LivingEntity.class, new AABB(currentPos));
                for (LivingEntity entity : entities) {
                    if (!(entity instanceof BronzeEntity)) {
                        entityDetected = true;
                        break;
                    }
                }

                currentPos = currentPos.below();

                if (currentPos.getY() < pLevel.getMinBuildHeight()) {
                    break;
                }
            }

            if (entityDetected && hasUnobstructedLineOfSight) {
                spawnFallingChandelier(pState, (ServerLevel) pLevel, pPos);
            }
        }
    }
}