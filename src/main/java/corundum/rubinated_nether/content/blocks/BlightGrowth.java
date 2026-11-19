package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class BlightGrowth {
    private static final int MIN_DELAY_TICKS = 15;
    private static final int MAX_DELAY_TICKS = 30;
    private static final int MAX_PENDING_PER_LEVEL = 256;
    private static final Map<ServerLevel, List<Pending>> QUEUES = new IdentityHashMap<>();

    private BlightGrowth() {}

    public static int growthsPerDamage(Difficulty difficulty) {
        return switch (difficulty) {
            case PEACEFUL -> 0;
            case EASY -> 1;
            case NORMAL -> 2;
            case HARD -> 4;
        };
    }

    public static boolean isInBlight(LivingEntity entity) {
        return findBlight(entity) != null;
    }

    private static BlockPos findBlight(LivingEntity entity) {
        Level level = entity.level();
        CrystallizedBronzeBlightBlock blight = RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT.get();
        AABB box = entity.getBoundingBox().inflate(0.05);
        var boxShape = Shapes.create(box);
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);

        for (BlockPos cell : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(cell);
            if (!state.is(blight)) continue;

            var shape = state.getShape(level, cell).move(cell.getX(), cell.getY(), cell.getZ());
            if (Shapes.joinIsNotEmpty(shape, boxShape, BooleanOp.AND)) {
                return cell.immutable();
            }
        }
        return null;
    }

    private static boolean canHost(BlockState state) {
        return state.isAir() || (state.canBeReplaced() && state.getFluidState().isEmpty());
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level)) return;
        if (event.getNewDamage() <= 0.0F || !entity.hasEffect(RNEffects.BRONZE_DISEASED)) return;

        int growths = growthsPerDamage(level.getDifficulty());
        if (growths <= 0) return;

        BlockPos pos = findBlight(entity);
        if (pos == null) return;

        List<Pending> queue = QUEUES.computeIfAbsent(level, key -> new ArrayList<>());
        for (int i = 0; i < growths; i++) {
            if (queue.size() >= MAX_PENDING_PER_LEVEL) break;
            long due = level.getGameTime() + MIN_DELAY_TICKS + level.random.nextInt(MAX_DELAY_TICKS - MIN_DELAY_TICKS + 1);
            queue.add(new Pending(pos.asLong(), due));
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (QUEUES.isEmpty()) return;

        for (ServerLevel level : new ArrayList<>(QUEUES.keySet())) {
            List<Pending> pending = QUEUES.get(level);
            if (pending == null) continue;

            long time = level.getGameTime();
            List<Pending> due = new ArrayList<>();
            for (Iterator<Pending> iterator = pending.iterator(); iterator.hasNext(); ) {
                Pending next = iterator.next();
                if (next.due() <= time) {
                    due.add(next);
                    iterator.remove();
                }
            }
            if (pending.isEmpty()) {
                QUEUES.remove(level);
            }

            for (Pending growth : due) {
                grow(level, BlockPos.of(growth.pos()));
            }
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        QUEUES.clear();
    }

    public static void scatter(ServerLevel level, BlockPos center, double radius, int attempts) {
        CrystallizedBronzeBlightBlock blight = RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT.get();
        RandomSource random = level.random;

        for (int attempt = 0; attempt < attempts; attempt++) {
            BlockPos target = randomInSphere(center, radius, random);
            if (!canHost(level.getBlockState(target))) continue;

            Direction attachment = findAttachment(level, target);
            if (attachment == null) continue;

            double chance = 1.0 - Math.sqrt(center.distSqr(target)) / radius;
            if (random.nextDouble() < chance) {
                level.setBlockAndUpdate(target, blight.stateFacing(attachment.getOpposite()));
                particles(level, target);
            }
        }
    }

    private static void grow(ServerLevel level, BlockPos origin) {
        if (level.getDifficulty() == Difficulty.PEACEFUL || !level.isLoaded(origin)) return;

        CrystallizedBronzeBlightBlock blight = RNBlocks.CRYSTALLIZED_BRONZE_BLIGHT.get();
        if (!level.getBlockState(origin).is(blight)) return;

        List<Target> targets = new ArrayList<>();
        BlockPos.MutableBlockPos cell = new BlockPos.MutableBlockPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    cell.setWithOffset(origin, dx, dy, dz);
                    if (!level.isLoaded(cell)) continue;

                    BlockState state = level.getBlockState(cell);
                    boolean hasBlight = state.is(blight);
                    if (!hasBlight && !canHost(state)) continue;

                    for (Direction face : Direction.values()) {
                        if (hasBlight && state.getValue(CrystallizedBronzeBlightBlock.faceProperty(face))) continue;

                        BlockPos support = cell.relative(face.getOpposite());
                        if (!level.isLoaded(support)) continue;
                        if (level.getBlockState(support).isFaceSturdy(level, support, face)) {
                            targets.add(new Target(cell.immutable(), face));
                        }
                    }
                }
            }
        }

        if (targets.isEmpty()) return;

        Target target = targets.get(level.random.nextInt(targets.size()));
        BlockState existing = level.getBlockState(target.pos());
        BlockState next = existing.is(blight)
                ? existing.setValue(CrystallizedBronzeBlightBlock.faceProperty(target.face()), true)
                : blight.stateFacing(target.face());

        level.setBlockAndUpdate(target.pos(), next);
        particles(level, target.pos());
        level.playSound(
                null,
                target.pos(),
                SoundEvents.AMETHYST_BLOCK_PLACE,
                SoundSource.BLOCKS,
                0.6F,
                0.8F + level.random.nextFloat() * 0.4F
        );
    }

    private static void particles(ServerLevel level, BlockPos pos) {
        level.sendParticles(
                ParticleTypes.GLOW,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                5,
                0.3, 0.3, 0.3,
                0.05
        );
    }

    private static Direction findAttachment(ServerLevel level, BlockPos airPos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = airPos.relative(direction);
            if (level.getBlockState(neighborPos).isFaceSturdy(level, neighborPos, direction.getOpposite())) {
                return direction;
            }
        }
        return null;
    }

    private static BlockPos randomInSphere(BlockPos center, double radius, RandomSource random) {
        double x, y, z;
        do {
            x = (random.nextDouble() * 2.0 - 1.0) * radius;
            y = (random.nextDouble() * 2.0 - 1.0) * radius;
            z = (random.nextDouble() * 2.0 - 1.0) * radius;
        } while (x * x + y * y + z * z > radius * radius);

        return center.offset((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }

    private record Pending(long pos, long due) {}

    private record Target(BlockPos pos, Direction face) {}
}
