package corundum.rubinated_nether.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
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

        public static final BiPredicate<Level, BlockPos> HAS_GROWABLE_BLOCK_NEIGHBOR = (level, pos) -> {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.relative(direction);
                BlockState neighborState = level.getBlockState(neighborPos);
                if (neighborState.is(RNTags.Blocks.GROWABLE_SURFACE)) {
                    return true;
                }
            }
            return false;
        };

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

    public static void addTagConversionRule(List<ConversionRule> rules, TagKey<Block> inputTag, Block outputBlock, double chance, int priority) {
        Registry<Block> blockRegistry = BuiltInRegistries.BLOCK;
        blockRegistry.getTagOrEmpty(inputTag).forEach(blockHolder -> {
            rules.add(new ConversionRule(blockHolder.value(), outputBlock, chance, priority));
        });
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

        // Play sound at the target position
        serverLevel.playSound(
                null,
                targetPos,
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.BLOCKS,
                0.5F + random.nextFloat() * 0.3F,
                0.8F + random.nextFloat() * 0.4F
        );

        // Spawn particles at the target block and send them upwards like a beam of light
        double spawnX = targetPos.getX() + 0.5;
        double spawnY = targetPos.getY() + 1.0; // Slightly above the block
        double spawnZ = targetPos.getZ() + 0.5;

        // Create upward beam particles
        for (int i = 0; i < 8 + random.nextInt(5); i++) {
            // Tighter spawn position around the target block
            double particleX = spawnX + (random.nextFloat() - 0.5) * 0.4;
            double particleY = spawnY + random.nextFloat() * 0.2;
            double particleZ = spawnZ + (random.nextFloat() - 0.5) * 0.4;

            // Upward velocity with minimal horizontal drift (tighter beam)
            double velocityX = (random.nextFloat() - 0.5) * 0.05; // Less horizontal drift
            double velocityY = 0.4 + random.nextFloat() * 0.3; // Strong upward motion
            double velocityZ = (random.nextFloat() - 0.5) * 0.05; // Less horizontal drift

            serverLevel.sendParticles(
                    RNParticleTypes.RUBINATE.get(),
                    particleX,
                    particleY,
                    particleZ,
                    1,
                    velocityX, velocityY, velocityZ,
                    0.1
            );
        }
    }



    public static void RubinateArea(Level level, BlockPos altarPos) {
        List<ConversionRule> rules = new ArrayList<>();


        // RUBY FARM - ORE
        rules.add(new ConversionRule(
                Blocks.MAGMA_BLOCK,
                RNBlocks.MOLTEN_RUBY_ORE.get(),
                6.0,
                15));

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

        // RUBY FARM - SHRINE STONE
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

        // RUBY FARM - CRYSTALS
        rules.add(new ConversionRule(
                Blocks.AIR,
                Blocks.SMALL_AMETHYST_BUD,
                15.0,
                300,
                Conditions.HAS_GROWABLE_BLOCK_NEIGHBOR
        ));

        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_CANDIDATE, RNBlocks.SHRINE_STONE.get(), 20.0, 1000);

        addTagConversionRule(rules, RNTags.Blocks.POLISHED_SHRINE_STONE_CANDIDATE, RNBlocks.POLISHED_SHRINE_STONE.get(), 20.0, 1000);
        addTagConversionRule(rules, RNTags.Blocks.POLISHED_SHRINE_STONE_STAIRS_CANDIDATE, RNBlocks.POLISHED_SHRINE_STONE_STAIRS.get(), 20.0, 1000);
        addTagConversionRule(rules, RNTags.Blocks.POLISHED_SHRINE_STONE_SLAB_CANDIDATE, RNBlocks.POLISHED_SHRINE_STONE_SLAB.get(), 20.0, 1000);
        addTagConversionRule(rules, RNTags.Blocks.POLISHED_SHRINE_STONE_WALL_CANDIDATE, RNBlocks.POLISHED_SHRINE_STONE_WALL.get(), 20.0, 1000);

        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_PILLAR_CANDIDATE, RNBlocks.SHRINE_STONE_PILLAR.get(), 20.0, 1000);

        addTagConversionRule(rules, RNTags.Blocks.CHISELED_SHRINE_STONE_BRICKS_CANDIDATE, RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get(), 20.0, 1000);

        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_BRICKS_CANDIDATE, RNBlocks.SHRINE_STONE_BRICKS.get(), 20.0, 1000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_BRICKS_STAIRS_CANDIDATE, RNBlocks.SHRINE_STONE_BRICKS_STAIRS.get(), 20.0, 1000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_BRICKS_SLAB_CANDIDATE, RNBlocks.SHRINE_STONE_BRICKS_SLAB.get(), 20.0, 1000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_BRICKS_WALL_CANDIDATE, RNBlocks.SHRINE_STONE_BRICKS_WALL.get(), 20.0, 1000);

        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_TILES_CANDIDATE, RNBlocks.SHRINE_STONE_TILES.get(), 20.0, 1000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_TILES_STAIRS_CANDIDATE, RNBlocks.SHRINE_STONE_TILES_STAIRS.get(), 20.0, 1000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_TILES_SLAB_CANDIDATE, RNBlocks.SHRINE_STONE_TILES_SLAB.get(), 20.0, 1000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_TILES_WALL_CANDIDATE, RNBlocks.SHRINE_STONE_TILES_WALL.get(), 20.0, 1000);





        applyConversions(level, altarPos, rules);
    }

}