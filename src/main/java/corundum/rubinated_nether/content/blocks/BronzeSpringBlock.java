package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BronzeSpringBlock extends DirectionalBlock implements TarnishingBronze {
    public static final MapCodec<BronzeSpringBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TarnishState.CODEC.fieldOf("tarnishing_state").forGetter(BronzeSpringBlock::getAge),
                    propertiesCodec()
            ).apply(instance, BronzeSpringBlock::new)
    );

    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    public static final BooleanProperty WAXED = TarnishingBronze.WAXED;

    private static final double BASE_LAUNCH_VELOCITY = 0.5;
    private static final double ACTIVATION_VELOCITY_THRESHOLD = 0.25;

    // Shapes for each direction
    private static final VoxelShape SQUISHED_UP = Block.box(2, 0, 2, 14, 16, 14);
    private static final VoxelShape EXTENDED_UP = Block.box(2, 0, 2, 14, 24, 14);

    private static final VoxelShape SQUISHED_DOWN = Block.box(2, 0, 2, 14, 16, 14);
    private static final VoxelShape EXTENDED_DOWN = Block.box(2, -8, 2, 14, 16, 14);

    private static final VoxelShape SQUISHED_NORTH = Block.box(2, 2, 0, 14, 14, 16);
    private static final VoxelShape EXTENDED_NORTH = Block.box(2, 2, -8, 14, 14, 16);

    private static final VoxelShape SQUISHED_SOUTH = Block.box(2, 2, 0, 14, 14, 16);
    private static final VoxelShape EXTENDED_SOUTH = Block.box(2, 2, 0, 14, 14, 24);

    private static final VoxelShape SQUISHED_WEST = Block.box(0, 2, 2, 16, 14, 14);
    private static final VoxelShape EXTENDED_WEST = Block.box(-8, 2, 2, 16, 14, 14);

    private static final VoxelShape SQUISHED_EAST = Block.box(0, 2, 2, 16, 14, 14);
    private static final VoxelShape EXTENDED_EAST = Block.box(0, 2, 2, 24, 14, 14);

    private final TarnishState tarnishState;

    public BronzeSpringBlock(TarnishState tarnishState, BlockBehaviour.Properties properties) {
        super(properties);
        this.tarnishState = tarnishState;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(EXTENDED, false)
                .setValue(WAXED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, EXTENDED, WAXED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean extended = state.getValue(EXTENDED);
        return switch (state.getValue(FACING)) {
            case UP -> extended ? EXTENDED_UP : SQUISHED_UP;
            case DOWN -> extended ? EXTENDED_DOWN : SQUISHED_DOWN;
            case NORTH -> extended ? EXTENDED_NORTH : SQUISHED_NORTH;
            case SOUTH -> extended ? EXTENDED_SOUTH : SQUISHED_SOUTH;
            case WEST -> extended ? EXTENDED_WEST : SQUISHED_WEST;
            case EAST -> extended ? EXTENDED_EAST : SQUISHED_EAST;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        BlockPos blockPos = context.getClickedPos();
        Level level = context.getLevel();
        BlockPos extendPos = blockPos.relative(facing);

        if (isWithinBounds(extendPos, level)) {
            BlockState extendState = level.getBlockState(extendPos);
            if (extendState.canBeReplaced(context) || extendState.getBlock() instanceof BronzeSpringBlock) {
                boolean hasSignal = level.hasNeighborSignal(blockPos);
                boolean canExtend = extendState.isAir() || extendState.canBeReplaced();

                return this.defaultBlockState()
                        .setValue(FACING, facing)
                        .setValue(EXTENDED, hasSignal && canExtend)
                        .setValue(WAXED, false);
            }
        }

        return null;
    }

    private boolean isWithinBounds(BlockPos pos, Level level) {
        return pos.getY() >= level.getMinBuildHeight() && pos.getY() < level.getMaxBuildHeight();
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                     LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        Direction springFacing = state.getValue(FACING);

        if (facing == springFacing) {
            if (state.getValue(EXTENDED)) {
                if (!facingState.isAir() && !facingState.canBeReplaced() && !(facingState.getBlock() instanceof BronzeSpringBlock)) {
                    return state.setValue(EXTENDED, false);
                }
            }

            if (facingState.getBlock() instanceof BronzeSpringBlock && state.getValue(EXTENDED)) {
                return Blocks.AIR.defaultBlockState();
            }
        }

        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        BlockState supportState = level.getBlockState(supportPos);

        boolean hasSupport = supportState.isFaceSturdy(level, supportPos, facing) ||
                supportState.getBlock() instanceof BronzeSpringBlock;

        if (state.getValue(EXTENDED)) {
            BlockPos extendPos = pos.relative(facing);
            BlockState extendState = level.getBlockState(extendPos);
            return hasSupport && (extendState.isAir() || extendState.canBeReplaced() || extendState.getBlock() instanceof BronzeSpringBlock);
        }

        return hasSupport;
    }

    private int getContractionDelay(BlockState state) {
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

        if (!(entity instanceof LivingEntity livingEntity)) {
            super.fallOn(level, state, pos, entity, fallDistance);
            return;
        }

        Direction facing = state.getValue(FACING);
        Vec3 velocity = entity.getDeltaMovement();

        // Get velocity component in the direction opposite to spring facing
        double approachVelocity = switch (facing) {
            case UP -> -velocity.y;    // Falling down onto upward spring
            case DOWN -> velocity.y;   // Moving up into downward spring
            case NORTH -> velocity.z;  // Moving south into north spring
            case SOUTH -> -velocity.z; // Moving north into south spring
            case WEST -> velocity.x;   // Moving east into west spring
            case EAST -> -velocity.x;  // Moving west into east spring
        };

        // Check if entity is moving toward the spring with sufficient velocity
        if (approachVelocity >= ACTIVATION_VELOCITY_THRESHOLD) {
            // Apply fall damage only for upward-facing springs
            if (facing == Direction.UP) {
                entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
            }

            // Check if landing on an extended spring below
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.getBlock() instanceof BronzeSpringBlock &&
                    belowState.getValue(FACING) == Direction.UP &&
                    belowState.getValue(EXTENDED)) {
                launchEntity(livingEntity, belowState);
                return;
            }

            // If spring is already extended, launch immediately
            if (state.getValue(EXTENDED)) {
                launchEntity(livingEntity, state);
                return;
            }

            // Try to extend and launch
            BlockPos extendPos = pos.relative(facing);
            BlockState extendState = level.getBlockState(extendPos);

            if (extendState.isAir() || extendState.canBeReplaced()) {
                level.setBlock(pos, state.setValue(EXTENDED, true), 3);
                level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, 1.0F);

                launchEntity(livingEntity, state);

                level.scheduleTick(pos, this, getContractionDelay(state));
            }
        } else {
            // Not enough velocity, apply normal fall behavior
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;

        // Only trigger step-on for upward-facing springs
        if (state.getValue(FACING) != Direction.UP) return;

        if (tarnishState == TarnishState.CRYSTALLIZED) {
            if (isEntityOnSpring(entity, pos)) {
                launchEntity(livingEntity, state);
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;

        Direction facing = state.getValue(FACING);

        // Check if entity is within the spring's bounds based on facing direction
        if (!isEntityInSpringBounds(entity, pos, facing)) {
            return;
        }

        if (tarnishState == TarnishState.CRYSTALLIZED) {
            launchEntity(livingEntity, state);
            return;
        }

        if (state.getValue(EXTENDED)) {
            if (entity.onGround() && entity.fallDistance > 0.0f && facing == Direction.UP) {
                launchEntity(livingEntity, state);
                entity.fallDistance = 0;
                return;
            }
        }
    }

    private boolean isEntityOnSpring(Entity entity, BlockPos pos) {
        double relativeX = entity.getX() - pos.getX();
        double relativeZ = entity.getZ() - pos.getZ();

        double entityRadius = entity.getBbWidth() / 2.0;
        double minX = 2.0 / 16.0;
        double maxX = 14.0 / 16.0;
        double minZ = 2.0 / 16.0;
        double maxZ = 14.0 / 16.0;

        boolean xOverlap = (relativeX + entityRadius > minX) && (relativeX - entityRadius < maxX);
        boolean zOverlap = (relativeZ + entityRadius > minZ) && (relativeZ - entityRadius < maxZ);

        return xOverlap && zOverlap;
    }

    private boolean isEntityInSpringBounds(Entity entity, BlockPos pos, Direction facing) {
        double entityRadius = entity.getBbWidth() / 2.0;
        double min = 2.0 / 16.0;
        double max = 14.0 / 16.0;

        return switch (facing) {
            case UP, DOWN -> {
                double relativeX = entity.getX() - pos.getX();
                double relativeZ = entity.getZ() - pos.getZ();
                boolean xOverlap = (relativeX + entityRadius > min) && (relativeX - entityRadius < max);
                boolean zOverlap = (relativeZ + entityRadius > min) && (relativeZ - entityRadius < max);
                yield xOverlap && zOverlap;
            }
            case NORTH, SOUTH -> {
                double relativeX = entity.getX() - pos.getX();
                double relativeY = entity.getY() - pos.getY();
                boolean xOverlap = (relativeX + entityRadius > min) && (relativeX - entityRadius < max);
                boolean yOverlap = (relativeY + entityRadius > min) && (relativeY - entityRadius < max);
                yield xOverlap && yOverlap;
            }
            case WEST, EAST -> {
                double relativeZ = entity.getZ() - pos.getZ();
                double relativeY = entity.getY() - pos.getY();
                boolean zOverlap = (relativeZ + entityRadius > min) && (relativeZ - entityRadius < max);
                boolean yOverlap = (relativeY + entityRadius > min) && (relativeY - entityRadius < max);
                yield zOverlap && yOverlap;
            }
        };
    }

    private void launchEntity(LivingEntity entity, BlockState state) {
        double velocityMultiplier = switch (tarnishState) {
            case UNAFFECTED -> 1.0;
            case DISCOLORED -> 2.0;
            case CORRODED -> 4.0;
            case TARNISHED -> 8.0;
            case CRYSTALLIZED -> 1.5;
        };

        double launchVelocity = BASE_LAUNCH_VELOCITY * velocityMultiplier;
        Direction facing = state.getValue(FACING);

        Vec3 velocity = entity.getDeltaMovement();
        Vec3 newVelocity = switch (facing) {
            case UP -> new Vec3(velocity.x, launchVelocity, velocity.z);
            case DOWN -> new Vec3(velocity.x, -launchVelocity, velocity.z);
            case NORTH -> new Vec3(velocity.x, velocity.y, -launchVelocity);
            case SOUTH -> new Vec3(velocity.x, velocity.y, launchVelocity);
            case WEST -> new Vec3(-launchVelocity, velocity.y, velocity.z);
            case EAST -> new Vec3(launchVelocity, velocity.y, velocity.z);
        };

        entity.setDeltaMovement(newVelocity);
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
        Direction facing = state.getValue(FACING);

        if (hasSignal && !isExtended) {
            BlockPos extendPos = pos.relative(facing);
            BlockState extendState = level.getBlockState(extendPos);

            if (extendState.isAir() || extendState.canBeReplaced()) {
                // Launch entities in the extension space
                Vec3 center = Vec3.atCenterOf(extendPos);
                level.getEntities(null, new net.minecraft.world.phys.AABB(
                        center.x - 0.5, center.y - 0.5, center.z - 0.5,
                        center.x + 0.5, center.y + 0.5, center.z + 0.5
                )).forEach(entity -> {
                    if (entity instanceof LivingEntity livingEntity) {
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
        return !state.getValue(WAXED) && TarnishingBronze.canCrystallize(this);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(WAXED)) return;
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    public TarnishState getAge() {
        return tarnishState;
    }

    @Override
    protected MapCodec<? extends BronzeSpringBlock> codec() {
        return CODEC;
    }
}