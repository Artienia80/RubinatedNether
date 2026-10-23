package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import corundum.rubinated_nether.utils.BEBlock;
import corundum.rubinated_nether.utils.RNConfig;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TarnishingBronzeVentBlock extends DirectionalBlock implements BEBlock<TarnishingBronzeVentBlock.VentBlockEntity>, TarnishingBronze {

    public static final MapCodec<TarnishingBronzeVentBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(TarnishingBronzeVentBlock::getAge),
                    propertiesCodec()
            ).apply(instance, TarnishingBronzeVentBlock::new)
    );

    private static final VoxelShape SMOKE_SEGMENT_BASE = Shapes.box(0.3, 0, 0.3, 0.7, 1, 0.7);

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WAXED = TarnishingBronze.WAXED;
    public static final IntegerProperty SIGNAL_STRENGTH = IntegerProperty.create("signal_strength", 0, 15);

    public static final int DISPENSER_PULSE_TICKS = 40;

    private static final int DISPENSER_PULSE_SIGNAL = 15;
    private static final String FORCED_UNTIL_TAG = "forced_power_until";

    private static final double GEYSER_IMPULSE = 0.2;
    private static final double GEYSER_BASE_MAX_SPEED = 0.3;
    private static final double GEYSER_MAX_SPEED_PER_BLOCK = 0.1;
    private static final double ANTI_GRAVITY_LIFT = 0.10;

    private static final double PROJECTILE_BOOST_AT_FULL = 2.4;

    private static final float HIGH_DAMAGE_MULTIPLIER = 2.0f;
    private static final Predicate<Entity> LAUNCH_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isAlive);

    private final TarnishStage tarnishStage;

    public TarnishingBronzeVentBlock(TarnishStage tarnishStage, Properties properties) {
        super(properties);
        this.tarnishStage = tarnishStage;
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(POWERED, false)
                        .setValue(SIGNAL_STRENGTH, 0)
                        .setValue(WAXED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, SIGNAL_STRENGTH, WAXED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState placement = this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(WAXED, false);

        if (tarnishStage == TarnishStage.CRYSTALLIZED && !context.getLevel().isClientSide()) {
            context.getLevel().scheduleTick(context.getClickedPos(), this, 1);
        }

        return placement;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide()) return;
        if (tarnishStage == TarnishStage.CRYSTALLIZED) return;

        int signal = effectiveSignal(level, pos);
        boolean powered = signal > 0;
        boolean signalChanged = state.getValue(POWERED) != powered || state.getValue(SIGNAL_STRENGTH) != signal;

        if (signalChanged) {
            state = state.setValue(POWERED, powered).setValue(SIGNAL_STRENGTH, signal);
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }

        if (powered) {
            if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
                level.scheduleTick(pos, this, 1);
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.isClientSide()) return;

        if (tarnishStage == TarnishStage.CRYSTALLIZED) {
            if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
                level.scheduleTick(pos, this, 1);
            }
            return;
        }

        int signal = effectiveSignal(level, pos);
        if (signal > 0) {
            BlockState newState = state.setValue(POWERED, true).setValue(SIGNAL_STRENGTH, signal);
            level.setBlock(pos, newState, Block.UPDATE_ALL);
            level.scheduleTick(pos, this, 1);
        }
    }

    public static boolean isFaceBlocked(Level level, BlockPos pos, Direction dir) {
        BlockPos adj = pos.relative(dir);
        BlockState adjState = level.getBlockState(adj);
        if (adjState.is(RNTags.Blocks.SMOKE_PASSTHROUGH)) return false;
        VoxelShape col = adjState.getCollisionShape(level, adj);
        if (col.isEmpty()) return false;
        return !Shapes.join(col, rotateSmokeShape(dir), BooleanOp.AND).isEmpty();
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (tarnishStage.equals(TarnishStage.CRYSTALLIZED)) {
            checkAndFireCrystallized(state, level, pos);
            level.scheduleTick(pos, this, 1);
            return;
        }

        VentBlockEntity pulseEntity = ventEntity(level, pos);
        if (pulseEntity != null && pulseEntity.forcedUntil != 0) {
            if (pulseEntity.forcedUntil <= level.getGameTime()) {
                pulseEntity.forcedUntil = 0;
                pulseEntity.setChanged();
            }
            state = syncPower(level, pos, state);
        }

        if (!state.getValue(POWERED)) return;
        if (getLaunchImpulse() <= 0) return;

        int signalRange = state.getValue(SIGNAL_STRENGTH);
        Direction facing = state.getValue(FACING);

        var result = getRanges(level, pos, state);
        int front = result.getLeft();
        int back = result.getRight();

        HeatSource source = front > 0 ? getHeatSource(level, pos, facing, back) : HeatSource.NONE;
        if (source != HeatSource.NONE && isDamageTick(level, pos)) {
            if (source.isHot()) {
                dealSteamDamage(level, pos, facing, signalRange, front, source.damageMultiplier());
            } else {
                dealIceDamage(level, pos, facing, signalRange, front, source.damageMultiplier());
            }
        }

        level.scheduleTick(pos, this, 1);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(state.getBlock() instanceof TarnishingBronzeVentBlock vent)) return;

        Direction facing = state.getValue(FACING);

        if (vent.getAge() == TarnishStage.CRYSTALLIZED) {
            if (isFaceBlocked(level, pos, facing)) return;
            int range = RNConfig.crystallizedVentRange;
            if (level.getEntitiesOfClass(LivingEntity.class, buildDetectionAABB(pos, facing, range)).isEmpty()) return;
            int smokeRange = calculateSmokeRange(level, pos, facing, range);
            if (smokeRange <= 0) return;
            spawnSteamColumn(level, pos, facing, smokeRange, false, 1.5, HeatSource.HOT_LOW, random);
            return;
        }

        if (!state.getValue(POWERED)) return;
        int signal = state.getValue(SIGNAL_STRENGTH);
        if (signal <= 0) return;

        var ranges = vent.getRanges(level, pos, state);
        int front = ranges.getLeft();
        int back = ranges.getRight();
        double density = vent.getSteamDensity(signal);

        if (front > 0)
            spawnSteamColumn(level, pos, facing, front, false, density, vent.getHeatSource(level, pos, facing, back), random);

        if (back > 0)
            spawnSteamColumn(level, pos, facing.getOpposite(), back, true, density * 0.6, HeatSource.NONE, random);
    }

    public enum HeatSource {
        NONE, HOT_LOW, HOT_HIGH, COLD_LOW, COLD_HIGH;

        public boolean isHot()  { return this == HOT_LOW || this == HOT_HIGH; }
        public boolean isCold() { return this == COLD_LOW || this == COLD_HIGH; }
        public boolean isHigh() { return this == HOT_HIGH || this == COLD_HIGH; }
        public float damageMultiplier() { return isHigh() ? HIGH_DAMAGE_MULTIPLIER : 1.0f; }
    }

    private static HeatSource classify(BlockState state) {
        if (state.is(RNTags.Blocks.VENT_HOT_HIGH))  return HeatSource.HOT_HIGH;
        if (state.is(RNTags.Blocks.VENT_COLD_HIGH)) return HeatSource.COLD_HIGH;
        if (state.is(RNTags.Blocks.VENT_HOT_LOW))   return HeatSource.HOT_LOW;
        if (state.is(RNTags.Blocks.VENT_COLD_LOW))  return HeatSource.COLD_LOW;
        return HeatSource.NONE;
    }

    public HeatSource getHeatSource(Level level, BlockPos pos, Direction facing, int back) {
        if (getLaunchImpulse() <= 0) return HeatSource.NONE;

        Direction behind = facing.getOpposite();
        HeatSource adjacent = classify(level.getBlockState(pos.relative(behind)));

        BlockState endState = level.getBlockState(pos.relative(behind, back + 1));
        HeatSource end = endState.is(RNTags.Blocks.SMOKE_PASSTHROUGH) ? HeatSource.NONE : classify(endState);

        if (end != HeatSource.NONE && (adjacent == HeatSource.NONE || (end.isHigh() && !adjacent.isHigh()))) {
            return end;
        }
        return adjacent;
    }

    private double getSteamDensity(int signal) {
        double tarnish = switch (tarnishStage) {
            case UNAFFECTED   -> 0.5;
            case DISCOLORED   -> 0.75;
            case CORRODED     -> 1.0;
            case TARNISHED    -> 1.25;
            case CRYSTALLIZED -> 1.0;
        };
        return tarnish * (0.6 + 0.8 * signal / 15.0);
    }

    private Pair<Integer, Integer> getRanges(Level level, BlockPos pos, BlockState state) {
        int signalRange = state.getValue(SIGNAL_STRENGTH);
        Direction facing = state.getValue(FACING);

        int front = isFaceBlocked(level, pos, facing) ? 0
                : calculateSmokeRange(level, pos, facing, signalRange);

        int back = isFaceBlocked(level, pos, facing.getOpposite()) ? 0
                : calculateSmokeRange(level, pos, facing.getOpposite(), signalRange);

        return Pair.of(front, back);
    }

    private static final double STEAM_DRIFT_PER_SPEED = 40.0;
    private static final double MOUTH_RADIUS = 0.33;

    private static void spawnSteamColumn(Level level, BlockPos pos, Direction column, int range,
                                         boolean inward, double density, HeatSource source, RandomSource random) {
        int count = randomRound(density * RNConfig.ventSmokeParticleMultiplier, random);
        if (count <= 0) return;

        Vec3 centre = Vec3.atCenterOf(pos);
        Vec3 axis = Vec3.atLowerCornerOf(column.getNormal());
        Vec3[] cross = perpendiculars(column.getAxis());

        var type = switch (source) {
            case HOT_LOW -> RNParticleTypes.STEAM_HOT.get();
            case HOT_HIGH -> RNParticleTypes.STEAM_HOT_HIGH.get();
            case COLD_LOW -> RNParticleTypes.STEAM_COLD.get();
            case COLD_HIGH -> RNParticleTypes.STEAM_COLD_HIGH.get();
            case NONE -> RNParticleTypes.STEAM.get();
        };

        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double radius = MOUTH_RADIUS * Math.sqrt(random.nextDouble());
            Vec3 offset = cross[0].scale(Math.cos(angle) * radius).add(cross[1].scale(Math.sin(angle) * radius));

            double stagger = random.nextDouble() * 0.4;
            double start = inward ? 0.5 + range - stagger : 0.5 + stagger;
            double travel = Math.max(0.25, range - stagger);
            double speed = travel / STEAM_DRIFT_PER_SPEED * (0.85 + random.nextDouble() * 0.3);

            Vec3 spawn = centre.add(axis.scale(start)).add(offset);
            Vec3 velocity = axis.scale(inward ? -speed : speed)
                    .add((random.nextDouble() - 0.5) * 0.01, (random.nextDouble() - 0.5) * 0.01, (random.nextDouble() - 0.5) * 0.01);

            level.addParticle(type, true, spawn.x, spawn.y, spawn.z, velocity.x, velocity.y, velocity.z);
        }

        if (inward || source == HeatSource.NONE) return;

        if (!source.isHigh()) {

            if (random.nextDouble() < 0.12 * density) {
                Vec3 mouth = centre.add(axis.scale(0.55));
                Vec3 drift = axis.scale(0.04 + random.nextDouble() * 0.04);
                level.addParticle(source.isHot() ? ParticleTypes.SMALL_FLAME : ParticleTypes.SNOWFLAKE,
                        mouth.x + (random.nextDouble() - 0.5) * 0.4,
                        mouth.y + (random.nextDouble() - 0.5) * 0.4,
                        mouth.z + (random.nextDouble() - 0.5) * 0.4,
                        drift.x, drift.y, drift.z);
            }
            return;
        }

        int accents = randomRound(density * 1.6, random);
        for (int i = 0; i < accents; i++) {
            double along = 0.55 + random.nextDouble() * Math.max(0.3, range * 0.8);
            Vec3 at = centre.add(axis.scale(along))
                    .add(cross[0].scale((random.nextDouble() - 0.5) * 0.5))
                    .add(cross[1].scale((random.nextDouble() - 0.5) * 0.5));
            Vec3 drift = axis.scale(0.05 + random.nextDouble() * 0.07);

            var accent = source.isHot()
                    ? (random.nextDouble() < 0.2 ? ParticleTypes.LAVA : random.nextDouble() < 0.5 ? ParticleTypes.FLAME : ParticleTypes.SMALL_FLAME)
                    : (random.nextDouble() < 0.2 ? ParticleTypes.CLOUD : ParticleTypes.SNOWFLAKE);
            level.addParticle(accent, at.x, at.y, at.z, drift.x, drift.y, drift.z);
        }
    }

    private static Vec3[] perpendiculars(Direction.Axis axis) {
        return switch (axis) {
            case X -> new Vec3[]{ new Vec3(0, 1, 0), new Vec3(0, 0, 1) };
            case Y -> new Vec3[]{ new Vec3(1, 0, 0), new Vec3(0, 0, 1) };
            case Z -> new Vec3[]{ new Vec3(1, 0, 0), new Vec3(0, 1, 0) };
        };
    }

    private static int randomRound(double value, RandomSource random) {
        int whole = (int) value;
        return random.nextDouble() < value - whole ? whole + 1 : whole;
    }

    private void checkAndFireCrystallized(BlockState state, ServerLevel level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        int range = RNConfig.crystallizedVentRange;

        if (isFaceBlocked(level, pos, facing)) return;

        AABB detectionBox = buildDetectionAABB(pos, facing, range);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, detectionBox);
        if (entities.isEmpty()) return;

        int smokeRange = calculateSmokeRange(level, pos, facing, range);
        dealSteamDamage(level, pos, facing, range, smokeRange);
    }

    public static AABB buildDetectionAABB(BlockPos pos, Direction facing, int range) {
        double cx = pos.getX() + 0.5, cy = pos.getY() + 0.5, cz = pos.getZ() + 0.5;
        double hw = 0.375;
        double ex = facing.getStepX() * range;
        double ey = facing.getStepY() * range;
        double ez = facing.getStepZ() * range;

        double minX = cx + Math.min(0, ex) - (facing.getAxis() != Direction.Axis.X ? hw : 0);
        double maxX = cx + Math.max(0, ex) + (facing.getAxis() != Direction.Axis.X ? hw : 0);
        double minY = cy + Math.min(0, ey) - (facing.getAxis() != Direction.Axis.Y ? hw : 0);
        double maxY = cy + Math.max(0, ey) + (facing.getAxis() != Direction.Axis.Y ? hw : 0);
        double minZ = cz + Math.min(0, ez) - (facing.getAxis() != Direction.Axis.Z ? hw : 0);
        double maxZ = cz + Math.max(0, ez) + (facing.getAxis() != Direction.Axis.Z ? hw : 0);

        if (facing.getStepX() > 0) minX += 1.0; else if (facing.getStepX() < 0) maxX -= 1.0;
        if (facing.getStepY() > 0) minY += 1.0; else if (facing.getStepY() < 0) maxY -= 1.0;
        if (facing.getStepZ() > 0) minZ += 1.0; else if (facing.getStepZ() < 0) maxZ -= 1.0;

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static int calculateSmokeRange(Level level, BlockPos pos, Direction facing, int maxRange) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        VoxelShape smokeColumn = rotateSmokeShape(facing);

        for (int i = 0; i < maxRange; i++) {
            cursor.move(facing);
            BlockState state = level.getBlockState(cursor);
            if (state.is(RNTags.Blocks.SMOKE_PASSTHROUGH)) continue;
            VoxelShape collision = Shapes.join(state.getCollisionShape(level, cursor), smokeColumn, BooleanOp.AND);
            if (!collision.isEmpty()) return i;
        }

        return maxRange;
    }

    private static VoxelShape rotateSmokeShape(Direction facing) {
        return switch (facing.getAxis()) {
            case Z -> Shapes.box(0.125, 0.125, 0, 0.875, 0.875, 1);
            case X -> Shapes.box(0, 0.125, 0.125, 1, 0.875, 0.875);
            case Y -> Shapes.box(0.125, 0, 0.125, 0.875, 1, 0.875);
        };
    }

    private void dealSteamDamage(ServerLevel level, BlockPos pos, Direction facing, int signal, int smokeRange) {
        dealSteamDamage(level, pos, facing, signal, smokeRange, 1.0f);
    }

    private void dealSteamDamage(ServerLevel level, BlockPos pos, Direction facing, int signal, int smokeRange, float multiplier) {
        if (signal <= 0 || smokeRange <= 0) return;
        AABB steamBox = buildDetectionAABB(pos, facing, smokeRange);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, steamBox);

        for (LivingEntity entity : entities) {
            double dist = distanceAlongFacing(pos, BlockPos.containing(entity.position()), facing);
            if (dist < 0 || dist >= smokeRange) continue;
            float damage = (float) (signal - dist) / 3.0f * multiplier;
            if (damage > 0) entity.hurt(level.damageSources().inFire(), damage);
        }
    }

    private void dealIceDamage(ServerLevel level, BlockPos pos, Direction facing, int signal, int smokeRange, float multiplier) {
        if (signal <= 0 || smokeRange <= 0) return;
        AABB iceBox = buildDetectionAABB(pos, facing, smokeRange);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, iceBox);

        for (LivingEntity entity : entities) {
            double dist = distanceAlongFacing(pos, BlockPos.containing(entity.position()), facing);
            if (dist < 0 || dist >= smokeRange) continue;
            float damage = (float) (signal - dist) / 3.0f * multiplier;
            if (damage > 0) entity.hurt(level.damageSources().freeze(), damage);
        }
    }

    static void launchTick(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof TarnishingBronzeVentBlock vent)) return;
        if (!state.getValue(POWERED)) return;

        double impulse = vent.getLaunchImpulse();
        if (impulse <= 0) return;

        Direction facing = state.getValue(FACING);
        var ranges = vent.getRanges(level, pos, state);

        if (ranges.getLeft() > 0)
            launchColumn(level, pos, facing, facing, ranges.getLeft(), impulse);
        if (ranges.getRight() > 0)
            launchColumn(level, pos, facing.getOpposite(), facing, ranges.getRight(), impulse);
    }

    private static void launchColumn(Level level, BlockPos pos, Direction column, Direction flow, int range, double impulse) {
        Vec3 columnDir = Vec3.atLowerCornerOf(column.getNormal());
        AABB box = new AABB(pos.relative(column)).expandTowards(columnDir.scale(range - 1));
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, box, LAUNCH_PREDICATE);
        if (entities.isEmpty()) return;

        Vec3 flowDir = Vec3.atLowerCornerOf(flow.getNormal());
        double strength = impulse / GEYSER_IMPULSE;
        double maxSpeed = (GEYSER_BASE_MAX_SPEED + range * GEYSER_MAX_SPEED_PER_BLOCK) * strength;

        Vec3 push = flowDir.scale(impulse);
        if (flow == Direction.UP) push = push.add(0, ANTI_GRAVITY_LIFT * strength, 0);

        for (Entity entity : entities) {
            if (flow == Direction.UP) cushionFall(entity);

            if (!isSimulatedHere(level, entity)) continue;
            if (entity instanceof Player player && player.getAbilities().flying) continue;
            if (entity.isPassenger() || entity.getType().is(RNTags.EntityTypes.NOT_AFFECTED_BY_VENTS)) continue;
            if (entity.getDeltaMovement().dot(flowDir) >= maxSpeed) continue;

            entity.addDeltaMovement(push);

            if (!level.isClientSide) entity.hurtMarked = true;
        }
    }

    private static boolean isSimulatedHere(Level level, Entity entity) {
        if (entity instanceof Player player)
            return level.isClientSide && player.isLocalPlayer();
        if (entity.getControllingPassenger() instanceof Player rider)
            return level.isClientSide && rider.isLocalPlayer();
        return !level.isClientSide;
    }

    private static void cushionFall(Entity entity) {
        if (entity.getDeltaMovement().y > -0.5 && entity.fallDistance > 1.0F) {
            entity.fallDistance = 1.0F;
        }
    }

    private static boolean isDamageTick(Level level, BlockPos pos) {
        return Math.floorMod(level.getGameTime() + pos.hashCode(), 20L) == 0;
    }

    private static VentBlockEntity ventEntity(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof VentBlockEntity be ? be : null;
    }

    private static int effectiveSignal(Level level, BlockPos pos) {
        int signal = level.getBestNeighborSignal(pos);
        VentBlockEntity be = ventEntity(level, pos);
        boolean pulsed = be != null && be.forcedUntil > level.getGameTime();
        return pulsed ? Math.max(signal, DISPENSER_PULSE_SIGNAL) : signal;
    }

    private static BlockState syncPower(Level level, BlockPos pos, BlockState state) {
        int signal = effectiveSignal(level, pos);
        boolean powered = signal > 0;
        if (state.getValue(POWERED) != powered || state.getValue(SIGNAL_STRENGTH) != signal) {
            state = state.setValue(POWERED, powered).setValue(SIGNAL_STRENGTH, signal);
            level.setBlock(pos, state, Block.UPDATE_ALL);
        }
        return state;
    }

    public void pulseFromDispenser(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide() || tarnishStage == TarnishStage.CRYSTALLIZED || getLaunchImpulse() <= 0) return;
        VentBlockEntity be = ventEntity(level, pos);
        if (be == null) return;

        be.forcedUntil = level.getGameTime() + DISPENSER_PULSE_TICKS;
        be.setChanged();

        syncPower(level, pos, state);
        if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    public boolean isPushingActive(BlockState state) {
        return state.getValue(POWERED) && getLaunchImpulse() > 0;
    }

    public double getProjectileBoost(Level level, BlockPos pos, BlockState state) {
        double strength = getLaunchImpulse() / GEYSER_IMPULSE;
        double signal = state.getValue(SIGNAL_STRENGTH) / 15.0;
        return PROJECTILE_BOOST_AT_FULL * strength * (0.5 + 0.5 * signal);
    }

    private double getLaunchImpulse() {
        return switch (tarnishStage) {
            case UNAFFECTED,
                 CRYSTALLIZED -> 0.0;
            case DISCOLORED   -> GEYSER_IMPULSE * 0.25;
            case CORRODED     -> GEYSER_IMPULSE * 0.5;
            case TARNISHED    -> GEYSER_IMPULSE;
        };
    }

    private int distanceAlongFacing(BlockPos ventPos, BlockPos entityPos, Direction facing) {
        int ex = entityPos.getX() - ventPos.getX();
        int ey = entityPos.getY() - ventPos.getY();
        int ez = entityPos.getZ() - ventPos.getZ();
        return ex * facing.getStepX() + ey * facing.getStepY() + ez * facing.getStepZ() - 1;
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
    public TarnishStage getAge() { return tarnishStage; }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
                                              BlockPos pos, Player player, InteractionHand hand,
                                              BlockHitResult hitResult) {
        return waxing(stack, state, level, pos, player, hand, hitResult)
                ? ItemInteractionResult.SUCCESS
                : super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level,
                                       BlockPos pos, Player player) {
        return new ItemStack(
                state.getValue(WAXED)
                        ? BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(this)))
                        : this
        );
    }

    @Override
    protected MapCodec<? extends TarnishingBronzeVentBlock> codec() { return CODEC; }

    @Override
    public BlockEntityType<? extends VentBlockEntity> getBlockEntityType() {
        return RNBlockEntities.BRONZE_VENT.get();
    }

    @Override
    public Class<? extends VentBlockEntity> getBlockEntityClass() {
        return VentBlockEntity.class;
    }

    public static class VentBlockEntity extends BlockEntity implements TickableBlockEntity {

        private long forcedUntil;

        public VentBlockEntity(BlockPos pos, BlockState state) {
            super(RNBlockEntities.BRONZE_VENT.get(), pos, state);
        }

        @Override
        protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.saveAdditional(tag, registries);
            if (forcedUntil != 0) tag.putLong(FORCED_UNTIL_TAG, forcedUntil);
        }

        @Override
        protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
            super.loadAdditional(tag, registries);
            forcedUntil = tag.getLong(FORCED_UNTIL_TAG);
        }

        @Override
        public void tick(boolean clientSide) {
            if (this.level != null) launchTick(this.level, this.worldPosition, this.getBlockState());
        }

        @Override
        public void tick() {}
    }
}