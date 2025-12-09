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

    private static final double BASE_LAUNCH_VELOCITY = 0.5; // meters/s

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
        // Keep collision shape at 16 blocks high so entities can land on top
        return SQUISHED_SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        Level level = context.getLevel();
        BlockPos abovePos = blockPos.above();

        // Check if there's enough space above (when extended, spring goes into block above)
        if (blockPos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(abovePos).canBeReplaced(context)) {
            return super.getStateForPlacement(context);
        }

        return null;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                     LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        // If block above is no longer replaceable and spring is extended, prevent placement
        if (facing == Direction.UP && state.getValue(EXTENDED)) {
            if (!facingState.isAir() && !facingState.canBeReplaced()) {
                // Contract the spring if block above becomes solid
                return state.setValue(EXTENDED, false);
            }
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);

        // Check if block below can support this spring
        boolean hasSupport = blockstate.isFaceSturdy(level, blockpos, Direction.UP);

        // If extended, also check if space above is available
        if (state.getValue(EXTENDED)) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            return hasSupport && (aboveState.isAir() || aboveState.canBeReplaced());
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

        if (state.getValue(EXTENDED)) {
            if (entity instanceof LivingEntity livingEntity) {
                launchEntity(livingEntity, state);
            }
            return;
        }

        // Spring is squished - check if fall distance warrants a launch
        if (fallDistance > 0.5f && entity instanceof LivingEntity livingEntity) {
            // Check if space above is clear before extending
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (aboveState.isAir() || aboveState.canBeReplaced()) {
                // Extend spring
                level.setBlock(pos, state.setValue(EXTENDED, true), 3);
                level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, 1.0F);

                // Launch entity
                launchEntity(livingEntity, state);

                // Schedule squish back down with tarnish-based delay
                level.scheduleTick(pos, this, getContractionDelay(state));
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;

        TarnishState tarnishState = this.getAge();

        // Crystallized springs launch on contact
        if (tarnishState == TarnishState.CRYSTALLIZED) {
            launchEntity(livingEntity, state);
            return;
        }

        // Other tarnish states only launch when extended (powered by redstone)
        if (state.getValue(EXTENDED)) {
            launchEntity(livingEntity, state);
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

        // Play sound
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
            // Check if space above is clear before extending
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (aboveState.isAir() || aboveState.canBeReplaced()) {
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