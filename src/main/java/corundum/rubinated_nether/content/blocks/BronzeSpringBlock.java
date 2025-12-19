package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BronzeSpringBlock extends DirectionalBlock implements TarnishingBronze {
    public static final MapCodec<BronzeSpringBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(BronzeSpringBlock::getAge),
                    propertiesCodec()
            ).apply(instance, BronzeSpringBlock::new)
    );

    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    public static final BooleanProperty WAXED = TarnishingBronze.WAXED;

    private static final double BASE_LAUNCH_VELOCITY = 0.5;
    private static final double CRYSTALLIZED_DETECTION_RANGE = 0.3; // blocks in front of spring

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

    private final TarnishStage tarnishStage;

    public BronzeSpringBlock(TarnishStage tarnishStage, BlockBehaviour.Properties properties) {
        super(properties);
        this.tarnishStage = tarnishStage;
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

        if (!isWithinBounds(extendPos, level)) {
            return null;
        }

        BlockState extendState = level.getBlockState(extendPos);

        if (extendState.isAir() || extendState.canBeReplaced(context) || extendState.getBlock() instanceof BronzeSpringBlock) {
            boolean hasSignal = level.hasNeighborSignal(blockPos);
            boolean canExtend = extendState.isAir() || extendState.canBeReplaced();

            BlockState placementState = this.defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(EXTENDED, hasSignal && canExtend)
                    .setValue(WAXED, false);

            // Schedule ticking for crystallized springs
            if (tarnishStage == TarnishStage.CRYSTALLIZED && !level.isClientSide()) {
                level.scheduleTick(blockPos, this, 1);
            }

            return placementState;
        }

        // Allow placement even if blocked, but always compressed
        BlockState placementState = this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(EXTENDED, false)
                .setValue(WAXED, false);

        // Schedule ticking for crystallized springs
        if (tarnishStage == TarnishStage.CRYSTALLIZED && !level.isClientSide()) {
            level.scheduleTick(blockPos, this, 1);
        }

        return placementState;
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
        return switch (tarnishStage) {
            case UNAFFECTED -> 10;
            case DISCOLORED -> 15;
            case CORRODED -> 20;
            case TARNISHED -> 40;
            case CRYSTALLIZED -> 5;
        };
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (level.isClientSide) {
            super.fallOn(level, state, pos, entity, fallDistance);
            return;
        }

        // Only trigger on vertical springs (UP facing)
        if (state.getValue(FACING) != Direction.UP) {
            super.fallOn(level, state, pos, entity, fallDistance);
            return;
        }

        entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());

        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (belowState.getBlock() instanceof BronzeSpringBlock &&
                belowState.getValue(FACING) == Direction.UP &&
                belowState.getValue(EXTENDED)) {
            if (fallDistance > 0.1f) {
                launchEntity(entity, belowState);
            }
            return;
        }

        if (state.getValue(EXTENDED)) {
            launchEntity(entity, state);
            return;
        }

        if (fallDistance > 0.5f) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (aboveState.isAir() || aboveState.canBeReplaced()) {
                level.setBlock(pos, state.setValue(EXTENDED, true), 3);
                level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, 1.0F);

                launchEntity(entity, state);

                level.scheduleTick(pos, this, getContractionDelay(state));
            }
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level.isClientSide) return;

        // Only trigger step-on for upward-facing springs
        if (state.getValue(FACING) != Direction.UP) return;

        // Only crystallized springs trigger on step
        if (tarnishStage != TarnishStage.CRYSTALLIZED) return;

        if (isEntityOnSpring(entity, pos)) {
            // Extend the spring first
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (aboveState.isAir() || aboveState.canBeReplaced()) {
                level.setBlock(pos, state.setValue(EXTENDED, true), 3);
                level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, 1.2F);

                // Then launch the entity
                launchEntity(entity, state.setValue(EXTENDED, true));

                // Schedule contraction
                level.scheduleTick(pos, this, getContractionDelay(state));
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) return;

        Direction facing = state.getValue(FACING);

        // For crystallized springs, ignore entityInside - only use proximity detection
        if (tarnishStage == TarnishStage.CRYSTALLIZED) {
            return;
        }

        // Check if entity is within the spring's bounds based on facing direction
        if (!isEntityInSpringBounds(entity, pos, facing)) {
            return;
        }

        if (state.getValue(EXTENDED)) {
            if (entity.onGround() && entity.fallDistance > 0.0f && facing == Direction.UP) {
                launchEntity(entity, state);
                entity.fallDistance = 0;
                return;
            }
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Handle contraction from redstone
        if (state.getValue(EXTENDED) && !level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.setValue(EXTENDED, false), 3);
            level.playSound(null, pos, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5F, 1.0F);
        }

        // Crystallized springs check for nearby entities and reschedule
        if (tarnishStage == TarnishStage.CRYSTALLIZED) {
            checkAndLaunchNearbyEntities(state, level, pos);
            // Reschedule the next tick (every tick = 1/20 second)
            level.scheduleTick(pos, this, 1);
        }
    }

    private void checkAndLaunchNearbyEntities(BlockState state, Level level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos extendPos = pos.relative(facing);
        BlockState extendState = level.getBlockState(extendPos);

        // Only check if spring can extend into the space ahead
        if (!extendState.isAir() && !extendState.canBeReplaced()) {
            return;
        }

        AABB detectionBox = getDetectionBox(pos, facing);

        boolean foundEntity = false;
        for (Entity entity : level.getEntitiesOfClass(Entity.class, detectionBox)) {
            if (shouldLaunchEntity(entity, pos, facing)) {
                foundEntity = true;
            }
        }

        // Extend the spring FIRST if we found an entity and it's not already extended
        if (foundEntity && !state.getValue(EXTENDED)) {
            level.setBlock(pos, state.setValue(EXTENDED, true), 3);
            level.playSound(null, pos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, 1.2F);

            // Now launch all entities in the detection box
            for (Entity entity : level.getEntitiesOfClass(Entity.class, detectionBox)) {
                if (shouldLaunchEntity(entity, pos, facing)) {
                    launchEntity(entity, state.setValue(EXTENDED, true));
                }
            }

            // Schedule contraction
            level.scheduleTick(pos, this, getContractionDelay(state));
        }
    }

    private AABB getDetectionBox(BlockPos pos, Direction facing) {
        // Spring is 12x12 pixels (0.75 blocks) centered in the block
        // min = 2/16 = 0.125, max = 14/16 = 0.875
        double min = 0.125;
        double max = 0.875;
        double range = CRYSTALLIZED_DETECTION_RANGE;

        return switch (facing) {
            case UP -> new AABB(
                    pos.getX() + min, pos.getY() + 1, pos.getZ() + min,
                    pos.getX() + max, pos.getY() + 1 + range, pos.getZ() + max
            );
            case DOWN -> new AABB(
                    pos.getX() + min, pos.getY() - range, pos.getZ() + min,
                    pos.getX() + max, pos.getY(), pos.getZ() + max
            );
            case NORTH -> new AABB(
                    pos.getX() + min, pos.getY() + min, pos.getZ() - range,
                    pos.getX() + max, pos.getY() + max, pos.getZ()
            );
            case SOUTH -> new AABB(
                    pos.getX() + min, pos.getY() + min, pos.getZ() + 1,
                    pos.getX() + max, pos.getY() + max, pos.getZ() + 1 + range
            );
            case WEST -> new AABB(
                    pos.getX() - range, pos.getY() + min, pos.getZ() + min,
                    pos.getX(), pos.getY() + max, pos.getZ() + max
            );
            case EAST -> new AABB(
                    pos.getX() + 1, pos.getY() + min, pos.getZ() + min,
                    pos.getX() + 1 + range, pos.getY() + max, pos.getZ() + max
            );
        };
    }

    private boolean shouldLaunchEntity(Entity entity, BlockPos pos, Direction facing) {
        Vec3 entityPos = entity.position();
        Vec3 springCenter = Vec3.atCenterOf(pos);

        // Check if entity is in the "firing zone" in front of the spring
        return switch (facing) {
            case UP -> entityPos.y > springCenter.y;
            case DOWN -> entityPos.y < springCenter.y;
            case NORTH -> entityPos.z < springCenter.z;
            case SOUTH -> entityPos.z > springCenter.z;
            case WEST -> entityPos.x < springCenter.x;
            case EAST -> entityPos.x > springCenter.x;
        };
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
        double min = 2.0 / 16.0;  // 0.125
        double max = 14.0 / 16.0; // 0.875

        return switch (facing) {
            case UP, DOWN -> {
                // For vertical springs, check X and Z (12x12 area in horizontal plane)
                double relativeX = entity.getX() - pos.getX();
                double relativeZ = entity.getZ() - pos.getZ();
                boolean xOverlap = (relativeX + entityRadius > min) && (relativeX - entityRadius < max);
                boolean zOverlap = (relativeZ + entityRadius > min) && (relativeZ - entityRadius < max);
                yield xOverlap && zOverlap;
            }
            case NORTH, SOUTH -> {
                // For north/south springs, check X and Y (12x12 area in vertical X plane)
                double relativeX = entity.getX() - pos.getX();
                double relativeY = entity.getY() - pos.getY();
                boolean xOverlap = (relativeX + entityRadius > min) && (relativeX - entityRadius < max);
                boolean yOverlap = (relativeY + entity.getBbHeight() / 2.0 > min) && (relativeY - entity.getBbHeight() / 2.0 < max);
                yield xOverlap && yOverlap;
            }
            case WEST, EAST -> {
                // For west/east springs, check Z and Y (12x12 area in vertical Z plane)
                double relativeZ = entity.getZ() - pos.getZ();
                double relativeY = entity.getY() - pos.getY();
                boolean zOverlap = (relativeZ + entityRadius > min) && (relativeZ - entityRadius < max);
                boolean yOverlap = (relativeY + entity.getBbHeight() / 2.0 > min) && (relativeY - entity.getBbHeight() / 2.0 < max);
                yield zOverlap && yOverlap;
            }
        };
    }

    private void launchEntity(Entity entity, BlockState state) {
        double velocityMultiplier = switch (tarnishStage) {
            case UNAFFECTED -> 1.5;
            case DISCOLORED -> 2.0;
            case CORRODED -> 3.0;
            case TARNISHED -> 4.0;
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

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.resetFallDistance();
        }

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
                level.getEntities(null, new AABB(
                        center.x - 0.5, center.y - 0.5, center.z - 0.5,
                        center.x + 0.5, center.y + 0.5, center.z + 0.5
                )).forEach(entity -> {
                    launchEntity(entity, state);
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
    public boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(WAXED) && TarnishingBronze.canCrystallize(this);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(WAXED)) return;
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    public TarnishStage getAge() {
        return tarnishStage;
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        return waxing(
                stack,
                state,
                level,
                pos,
                player,
                hand,
                hitResult
        )
                ? ItemInteractionResult.SUCCESS
                : super.useItemOn(
                stack,
                state,
                level,
                pos,
                player,
                hand,
                hitResult
        );
    }

    @Override
    public ItemStack getCloneItemStack(
            BlockState state,
            HitResult target,
            LevelReader level,
            BlockPos pos,
            Player player
    ) {
        return new ItemStack(
                state.getValue(WAXED)
                        ? BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(this)))
                        : this
        );
    }

    @Override
    protected MapCodec<? extends BronzeSpringBlock> codec() {
        return CODEC;
    }
}