package corundum.rubinated_nether.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class RNRubinateEverywhere {


    private static final double MIN_REPLACEMENT_CHANCE = 0.0;
    private static final double MAX_REPLACEMENT_CHANCE = 1.0;

    public static class ConversionRule {
        private final Block inputBlock;
        private final Block outputBlock;
        private final double radius;
        private final int attempts;
        private final BiPredicate<Level, BlockPos> condition;

        public ConversionRule(Block inputBlock, Block outputBlock, double radius, int attempts,
                              BiPredicate<Level, BlockPos> condition) {
            this.inputBlock = inputBlock;
            this.outputBlock = outputBlock;
            this.radius = radius;
            this.attempts = attempts;
            this.condition = condition;
        }

        public ConversionRule(Block inputBlock, Block outputBlock, double radius, int attempts) {
            this(inputBlock, outputBlock, radius, attempts, null);
        }

        public Block getInputBlock() {
            return inputBlock;
        }

        public Block getOutputBlock() {
            return outputBlock;
        }

        public double getRadius() {
            return radius;
        }

        public int getAttempts() {
            return attempts;
        }

        public boolean checkCondition(Level level, BlockPos pos) {
            return condition == null || condition.test(level, pos);
        }
    }

    public static class Conditions {

        public static final BiPredicate<Level, BlockPos> HAS_SOLID_NEIGHBOR_NOT_RUNESTONE_SIDE = (level, pos) -> {
            for (Direction direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                BlockPos neighborPos = pos.relative(direction);
                BlockState neighborState = level.getBlockState(neighborPos);
                if (!neighborState.isAir() && neighborState.isSolidRender(level, neighborPos)) {
                    if (!neighborState.is(RNBlocks.RUNESTONE.get())) {
                        return true;
                    }
                }
            }
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            return !aboveState.isAir() && aboveState.isSolidRender(level, abovePos);
        };

        public static BiPredicate<Level, BlockPos> withinYRange(int minY, int maxY) {
            return (level, pos) -> pos.getY() >= minY && pos.getY() <= maxY;
        }

        public static BiPredicate<Level, BlockPos> and(BiPredicate<Level, BlockPos>... conditions) {
            return (level, pos) -> {
                for (BiPredicate<Level, BlockPos> condition : conditions) {
                    if (!condition.test(level, pos)) {
                        return false;
                    }
                }
                return true;
            };
        }

        public static BiPredicate<Level, BlockPos> or(BiPredicate<Level, BlockPos>... conditions) {
            return (level, pos) -> {
                for (BiPredicate<Level, BlockPos> condition : conditions) {
                    if (condition.test(level, pos)) {
                        return true;
                    }
                }
                return false;
            };
        }
    }

    public static void applyConversions(Level level, BlockPos centerPos, List<ConversionRule> rules) {
        if (level.isClientSide) return;

        RandomSource random = level.getRandom();

        for (ConversionRule rule : rules) {
            applyConversion(level, centerPos, rule, random);
        }
    }

    private static void applyConversion(Level level, BlockPos centerPos, ConversionRule rule, RandomSource random) {
        int successfulConversions = 0;

        for (int attempt = 0; attempt < rule.getAttempts(); attempt++) {
            BlockPos targetPos = generateRandomPositionInSphere(centerPos, rule.getRadius(), random);

            BlockState currentState = level.getBlockState(targetPos);
            if (!currentState.is(rule.getInputBlock())) {
                continue;
            }

            if (!rule.checkCondition(level, targetPos)) {
                continue;
            }

            double distance = centerPos.distSqr(targetPos);
            double maxDistanceSquared = rule.getRadius() * rule.getRadius();

            double normalizedDistance = Math.sqrt(distance) / rule.getRadius();
            double replacementChance = MAX_REPLACEMENT_CHANCE - (normalizedDistance * (MAX_REPLACEMENT_CHANCE - MIN_REPLACEMENT_CHANCE));

            replacementChance = Math.max(0.0, Math.min(1.0, replacementChance));

            if (random.nextDouble() < replacementChance) {
                level.setBlockAndUpdate(targetPos, rule.getOutputBlock().defaultBlockState());
                playEffects(level, centerPos, targetPos, random);
                successfulConversions++;
            }
        }
    }

    private static BlockPos generateRandomPositionInSphere(BlockPos center, double radius, RandomSource random) {
        double x, y, z;
        do {
            x = (random.nextDouble() * 2.0 - 1.0) * radius;
            y = (random.nextDouble() * 2.0 - 1.0) * radius;
            z = (random.nextDouble() * 2.0 - 1.0) * radius;
        } while (x * x + y * y + z * z > radius * radius);

        return center.offset((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }

    private static void playEffects(Level level, BlockPos altarPos, BlockPos targetPos, RandomSource random) {
        if (level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level;

        serverLevel.playSound(
                null,
                targetPos,
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.BLOCKS,
                0.5F + random.nextFloat() * 0.3F,
                0.8F + random.nextFloat() * 0.4F
        );

        double deltaX = targetPos.getX() - altarPos.getX();
        double deltaY = targetPos.getY() - altarPos.getY();
        double deltaZ = targetPos.getZ() - altarPos.getZ();

        double startX = altarPos.getX() + 0.5;
        double startY = altarPos.getY() + 2.0;
        double startZ = altarPos.getZ() + 0.5;

        for (int i = 0; i < 5 + random.nextInt(3); i++) {
            double particleStartX = startX + (random.nextDouble() - 0.5) * 0.3;
            double particleStartY = startY + (random.nextDouble() - 0.5) * 0.3;
            double particleStartZ = startZ + (random.nextDouble() - 0.5) * 0.3;

            double velocityX = deltaX * 0.1 + (random.nextDouble() - 0.5) * 0.02;
            double velocityY = Math.max(0.05, deltaY * 0.1 + 0.15 + random.nextDouble() * 0.1); // Always upward with arc
            double velocityZ = deltaZ * 0.1 + (random.nextDouble() - 0.5) * 0.02;

            serverLevel.sendParticles(
                    RNParticleTypes.RUBINATE.get(),
                    particleStartX,
                    particleStartY,
                    particleStartZ,
                    1,
                    velocityX, velocityY, velocityZ,
                    0.1
            );
        }
    }

    public static void RubinateArea(Level level, BlockPos altarPos) {
        List<ConversionRule> rules = new ArrayList<>();

        rules.add(new ConversionRule(
                Blocks.NETHERRACK,
                RNBlocks.NETHER_RUBY_ORE.get(),
                8.0,
                25));

        rules.add(new ConversionRule(
                Blocks.BLACKSTONE,
                RNBlocks.RUBINATED_BLACKSTONE.get(),
                10.0,
                50));

        rules.add(new ConversionRule(
                RNBlocks.SHRINE_STONE_BRICKS.get(),
                RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get(),
                10.0,
                200
        ));

        rules.add(new ConversionRule(
                RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get(),
                RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get(),
                10.0,
                200
        ));

        rules.add(new ConversionRule(
                Blocks.AIR,
                Blocks.SMALL_AMETHYST_BUD,
                15.0,
                300,
                Conditions.HAS_SOLID_NEIGHBOR_NOT_RUNESTONE_SIDE
        ));

        applyConversions(level, altarPos, rules);
    }

}