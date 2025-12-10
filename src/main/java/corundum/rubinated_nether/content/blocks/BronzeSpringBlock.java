package corundum.rubinated_nether.content.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BronzeSpringBlock extends TarnishingBronzeBlock {
    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;

    private static final double BASE_LAUNCH_VELOCITY = 0.5;

    private static final VoxelShape SQUISHED_SHAPE = Block.box(2, 0, 2, 14, 16, 14);
    private static final VoxelShape EXTENDED_SHAPE = Block.box(2, 0, 2, 14, 24, 14);

    public BronzeSpringBlock(TarnishState tarnishState, BlockBehaviour.Properties properties) {
        super(tarnishState, properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(EXTENDED, false)
                .setValue(WAXED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EXTENDED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(EXTENDED) ? EXTENDED_SHAPE : SQUISHED_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(EXTENDED) ? EXTENDED_SHAPE : SQUISHED_SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        Level level = context.getLevel();
        BlockPos abovePos = blockPos.above();

        if (blockPos.getY() < level.getMaxBuildHeight() - 1) {
            BlockState aboveState = level.getBlockState(abovePos);
            if (aboveState.canBeReplaced(context) || aboveState.getBlock() instanceof BronzeSpringBlock) {
                return super.getStateForPlacement(context);
            }
        }

        return null;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                     LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.UP) {
            if (state.getValue(EXTENDED)) {
                if (!facingState.isAir() && !facingState.canBeReplaced() && !(facingState.getBlock() instanceof BronzeSpringBlock)) {
                    return state.setValue(EXTENDED, false);
                }
            }

            if (facingState.getBlock() instanceof BronzeSpringBlock && state.getValue(EXTENDED)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);

        boolean hasSupport = blockstate.isFaceSturdy(level, blockpos, Direction.UP) ||
                blockstate.getBlock() instanceof BronzeSpringBlock;

        if (state.getValue(EXTENDED)) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            return hasSupport && (aboveState.isAir() || aboveState.canBeReplaced() || aboveState.getBlock() instanceof BronzeSpringBlock);
        }

        return hasSupport;
    }

    private int getContractionDelay(BlockState state) {
        TarnishState tarnishState = this.getAge();

        return switch (tarnishState) {
            case UNAFFECTED -> 10;
            case DISCOLORED -> 20;
            case CORRODED -> 40;
            case TARNISHED -> 80;
            case CRYSTALLIZED -> 5;
        };
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (level.isClientSide) {
            super.fallOn(level, state, pos, entity, fallDistance);
            return;
        }

        entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());

        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (belowState.getBlock() instanceof BronzeSpringBlock && belowState.getValue(EXTENDED)) {
            if (entity instanceof LivingEntity livingEntity && fallDistance > 0.1f) {
                ((BronzeSpringBlock) belowState.getBlock()).launchEntity(livingEntity, belowState);
            }
            return;
        }

        if (state.getValue(EXTENDED)) {
            if (entity instanceof LivingEntity livingEntity) {
                launchEntity(livingEntity, state);
            }
            return;
        }

        if (fallDistance > 0.5f && entity instanceof LivingEntity livingEntity) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (aboveState.isAir() || aboveState.canBeReplaced()) {
                level.setBlock(pos, state.setValue(EXTENDED, true), 3);
                level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, 1.0F);

                launchEntity(livingEntity, state);

                level.scheduleTick(pos, this, getContractionDelay(state));
            }
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;

        TarnishState tarnishState = this.getAge();

        if (tarnishState == TarnishState.CRYSTALLIZED) {
            double relativeX = entity.getX() - pos.getX();
            double relativeZ = entity.getZ() - pos.getZ();

            double entityRadius = entity.getBbWidth() / 2.0;
            double minX = 2.0 / 16.0;
            double maxX = 14.0 / 16.0;
            double minZ = 2.0 / 16.0;
            double maxZ = 14.0 / 16.0;

            boolean xOverlap = (relativeX + entityRadius > minX) && (relativeX - entityRadius < maxX);
            boolean zOverlap = (relativeZ + entityRadius > minZ) && (relativeZ - entityRadius < maxZ);

            if (xOverlap && zOverlap) {
                launchEntity(livingEntity, state);
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;

        double relativeX = entity.getX() - pos.getX();
        double relativeZ = entity.getZ() - pos.getZ();

        double entityRadius = entity.getBbWidth() / 2.0;
        double minX = 2.0 / 16.0;
        double maxX = 14.0 / 16.0;
        double minZ = 2.0 / 16.0;
        double maxZ = 14.0 / 16.0;

        boolean xOverlap = (relativeX + entityRadius > minX) && (relativeX - entityRadius < maxX);
        boolean zOverlap = (relativeZ + entityRadius > minZ) && (relativeZ - entityRadius < maxZ);

        if (!xOverlap || !zOverlap) {
            return;
        }

        TarnishState tarnishState = this.getAge();

        if (tarnishState == TarnishState.CRYSTALLIZED) {
            launchEntity(livingEntity, state);
            return;
        }

        if (state.getValue(EXTENDED)) {
            if (entity.onGround() && entity.fallDistance > 0.0f) {
                launchEntity(livingEntity, state);
                entity.fallDistance = 0;
                return;
            }
        }
    }

    private void launchEntity(LivingEntity entity, BlockState state) {
        TarnishState tarnishState = this.getAge();

        double velocityMultiplier = switch (tarnishState) {
            case UNAFFECTED -> 1.0;
            case DISCOLORED -> 2.0;
            case CORRODED -> 4.0;
            case TARNISHED -> 8.0;
            case CRYSTALLIZED -> 1.5;
        };

        double launchVelocity = BASE_LAUNCH_VELOCITY * velocityMultiplier;

        Vec3 velocity = entity.getDeltaMovement();
        entity.setDeltaMovement(velocity.x, launchVelocity, velocity.z);
        entity.hurtMarked = true;
        entity.resetFallDistance();

        entity.level().playSound(null, entity.blockPosition(),
                SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.3F, 1.5F);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;

        boolean hasSignal = level.hasNeighborSignal(pos);
        boolean isExtended = state.getValue(EXTENDED);

        if (hasSignal && !isExtended) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (aboveState.isAir() || aboveState.canBeReplaced()) {
                double minY = pos.getY() + 1.0;
                double maxY = pos.getY() + 1.5;

                level.getEntities(null, new net.minecraft.world.phys.AABB(
                        pos.getX(), minY, pos.getZ(),
                        pos.getX() + 1, maxY, pos.getZ() + 1
                )).forEach(entity -> {
                    if (entity instanceof LivingEntity livingEntity && entity.onGround()) {
                        launchEntity(livingEntity, state);
                    }
                });

                level.setBlock(pos, state.setValue(EXTENDED, true), 3);
                level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, 1.2F);
            }
        } else if (!hasSignal && isExtended) {
            if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
                level.setBlock(pos, state.setValue(EXTENDED, false), 3);
                level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(EXTENDED) && !level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.setValue(EXTENDED, false), 3);
            level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(WAXED) && TarnishingBronze.canCrystallize(state.getBlock());
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(WAXED)) return;
        this.changeOverTime(state, level, pos, random);
    }
}