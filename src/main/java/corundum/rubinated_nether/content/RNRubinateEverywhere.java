package corundum.rubinated_nether.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

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
        private final boolean isShrineStoneBased;

        public ConversionRule(Block inputBlock, Block outputBlock, double radius, int attempts,
                              BiPredicate<Level, BlockPos> condition, boolean isShrineStoneBased) {
            this.inputBlock = inputBlock;
            this.outputBlock = outputBlock;
            this.radius = radius;
            this.attempts = attempts;
            this.condition = condition;
            this.isShrineStoneBased = isShrineStoneBased;
        }

        public ConversionRule(Block inputBlock, Block outputBlock, double radius, int attempts,
                              BiPredicate<Level, BlockPos> condition) {
            this(inputBlock, outputBlock, radius, attempts, condition, false);
        }

        public ConversionRule(Block inputBlock, Block outputBlock, double radius, int attempts) {
            this(inputBlock, outputBlock, radius, attempts, null, false);
        }

        public ConversionRule(Block inputBlock, Block outputBlock, double radius, int attempts, boolean isShrineStoneBased) {
            this(inputBlock, outputBlock, radius, attempts, null, isShrineStoneBased);
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

        public boolean isShrineStoneBased() {
            return isShrineStoneBased;
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

        public static final BiPredicate<Level, BlockPos> HAS_GROWABLE_BLOCK_NEIGHBOR_FOR_CRYSTAL = (level, pos) -> {
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

        public static final BiPredicate<Level, BlockPos> HAS_SHRINE_STONE_NEIGHBOR = (level, pos) -> {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.relative(direction);
                BlockState neighborState = level.getBlockState(neighborPos);
                if (neighborState.is(RNTags.Blocks.SHRINE_STONE_BLOCKS)) {
                    return true;
                }
            }
            return false;
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

            if (rule.isShrineStoneBased()) {
                boolean hasShrineStoneBased = Conditions.HAS_SHRINE_STONE_NEIGHBOR.test(level, targetPos);
                if (hasShrineStoneBased) {
                    replacementChance = 1.0;
                }
            }

            if (random.nextDouble() < replacementChance) {
                BlockState newState;
                if (rule.getOutputBlock() == Blocks.SMALL_AMETHYST_BUD) {
                    newState = createCrystalWithCorrectFacing(level, targetPos, rule.getOutputBlock());
                } else {
                    newState = transferProperties(currentState, rule.getOutputBlock().defaultBlockState());
                }
                level.setBlockAndUpdate(targetPos, newState);
                playEffects(level, centerPos, targetPos, random);
                successfulConversions++;
            }
        }
    }

    private static BlockState createCrystalWithCorrectFacing(Level level, BlockPos pos, Block crystalBlock) {
        BlockState crystalState = crystalBlock.defaultBlockState();

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.is(RNTags.Blocks.GROWABLE_SURFACE)) {
                if (crystalState.hasProperty(AmethystClusterBlock.FACING)) {
                    return crystalState.setValue(AmethystClusterBlock.FACING, direction.getOpposite());
                }
                break;
            }
        }

        return crystalState;
    }

    private static BlockState transferProperties(BlockState sourceState, BlockState targetState) {
        BlockState resultState = targetState;

        for (Property<?> property : sourceState.getProperties()) {
            if (targetState.hasProperty(property)) {
                try {
                    resultState = transferProperty(sourceState, resultState, property);
                } catch (Exception e) {
                }
            }
        }

        return resultState;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState transferProperty(BlockState sourceState, BlockState targetState, Property<T> property) {
        T value = sourceState.getValue(property);
        return targetState.setValue(property, value);
    }

    public static void addTagConversionRule(List<ConversionRule> rules, TagKey<Block> inputTag, Block outputBlock, double radius, int attempts) {
        Registry<Block> blockRegistry = BuiltInRegistries.BLOCK;
        blockRegistry.getTagOrEmpty(inputTag).forEach(blockHolder -> {
            rules.add(new ConversionRule(blockHolder.value(), outputBlock, radius, attempts, true));
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

        serverLevel.playSound(
                null,
                targetPos,
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.BLOCKS,
                0.5F + random.nextFloat() * 0.3F,
                0.8F + random.nextFloat() * 0.4F
        );

        double spawnX = targetPos.getX() + 0.5;
        double spawnY = targetPos.getY() + 1.0;
        double spawnZ = targetPos.getZ() + 0.5;

        for (int i = 0; i < 8 + random.nextInt(5); i++) {
            double particleX = spawnX + (random.nextFloat() - 0.5) * 0.4;
            double particleY = spawnY + random.nextFloat() * 0.2;
            double particleZ = spawnZ + (random.nextFloat() - 0.5) * 0.4;

            double velocityX = (random.nextFloat() - 0.5) * 0.05;
            double velocityY = 0.4 + random.nextFloat() * 0.3;
            double velocityZ = (random.nextFloat() - 0.5) * 0.05;

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

    private static void replaceJukeboxDiscs(Level level, BlockPos altarPos) {
        if (level.isClientSide) return;

        final double JUKEBOX_SEARCH_RADIUS = 10.0;
        boolean discReplaced = false;

        int radius = (int) Math.ceil(JUKEBOX_SEARCH_RADIUS);

        for (int x = -radius; x <= radius && !discReplaced; x++) {
            for (int y = -radius; y <= radius && !discReplaced; y++) {
                for (int z = -radius; z <= radius && !discReplaced; z++) {
                    BlockPos checkPos = altarPos.offset(x, y, z);

                    if (altarPos.distSqr(checkPos) > JUKEBOX_SEARCH_RADIUS * JUKEBOX_SEARCH_RADIUS) {
                        continue;
                    }

                    BlockState blockState = level.getBlockState(checkPos);
                    if (blockState.is(Blocks.JUKEBOX)) {
                        if (blockState.getValue(JukeboxBlock.HAS_RECORD)) {
                            if (level.getBlockEntity(checkPos) instanceof JukeboxBlockEntity jukeboxEntity) {
                                ItemStack currentRecord = jukeboxEntity.getTheItem();
                                if (!currentRecord.isEmpty()) {
                                    jukeboxEntity.setTheItem(new ItemStack(RNItems.MUSIC_DISC_SHIMMER.get()));


                                    playJukeboxReplacementEffects(level, altarPos, checkPos);

                                    discReplaced = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Plays special effects when a jukebox disc is replaced
     */
    private static void playJukeboxReplacementEffects(Level level, BlockPos altarPos, BlockPos jukeboxPos) {
        if (level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level;
        RandomSource random = level.getRandom();

        // Play a special sound for disc replacement
        serverLevel.playSound(
                null,
                jukeboxPos,
                SoundEvents.AMETHYST_BLOCK_RESONATE,
                SoundSource.BLOCKS,
                1.0F,
                1.2F + random.nextFloat() * 0.2F
        );

        // Create shimmer particles around the jukebox
        double spawnX = jukeboxPos.getX() + 0.5;
        double spawnY = jukeboxPos.getY() + 1.2;
        double spawnZ = jukeboxPos.getZ() + 0.5;

        for (int i = 0; i < 15 + random.nextInt(10); i++) {
            double particleX = spawnX + (random.nextFloat() - 0.5) * 1.0;
            double particleY = spawnY + random.nextFloat() * 0.5;
            double particleZ = spawnZ + (random.nextFloat() - 0.5) * 1.0;

            double velocityX = (random.nextFloat() - 0.5) * 0.08;
            double velocityY = 0.6 + random.nextFloat() * 0.4;
            double velocityZ = (random.nextFloat() - 0.5) * 0.08;

            serverLevel.sendParticles(
                    RNParticleTypes.RUBINATE.get(),
                    particleX,
                    particleY,
                    particleZ,
                    1,
                    velocityX, velocityY, velocityZ,
                    0.15
            );
        }
    }

    public static void RubinateArea(Level level, BlockPos altarPos) {
        List<ConversionRule> rules = new ArrayList<>();

        // First, handle jukebox disc replacement
        replaceJukeboxDiscs(level, altarPos);

        // Then proceed with normal block conversions
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

        rules.add(new ConversionRule(
                RNBlocks.SHRINE_STONE_BRICKS.get(),
                RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get(),
                20.0,
                200,
                true
        ));

        rules.add(new ConversionRule(
                RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get(),
                RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get(),
                20.0,
                200,
                true
        ));

        rules.add(new ConversionRule(
                Blocks.CRYING_OBSIDIAN,
                RNBlocks.BLEEDING_OBSIDIAN.get(),
                20.0,
                200));

        rules.add(new ConversionRule(
                Blocks.AIR,
                Blocks.SMALL_AMETHYST_BUD,
                15.0,
                300,
                Conditions.HAS_GROWABLE_BLOCK_NEIGHBOR_FOR_CRYSTAL
        ));

        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_CANDIDATE, RNBlocks.SHRINE_STONE.get(), 20.0, 5000);

        addTagConversionRule(rules, RNTags.Blocks.POLISHED_SHRINE_STONE_CANDIDATE, RNBlocks.POLISHED_SHRINE_STONE.get(), 20.0, 5000);
        addTagConversionRule(rules, RNTags.Blocks.POLISHED_SHRINE_STONE_STAIRS_CANDIDATE, RNBlocks.POLISHED_SHRINE_STONE_STAIRS.get(), 20.0, 5000);
        addTagConversionRule(rules, RNTags.Blocks.POLISHED_SHRINE_STONE_SLAB_CANDIDATE, RNBlocks.POLISHED_SHRINE_STONE_SLAB.get(), 20.0, 5000);
        addTagConversionRule(rules, RNTags.Blocks.POLISHED_SHRINE_STONE_WALL_CANDIDATE, RNBlocks.POLISHED_SHRINE_STONE_WALL.get(), 20.0, 5000);

        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_PILLAR_CANDIDATE, RNBlocks.SHRINE_STONE_PILLAR.get(), 20.0, 5000);

        addTagConversionRule(rules, RNTags.Blocks.CHISELED_SHRINE_STONE_BRICKS_CANDIDATE, RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get(), 20.0, 5000);

        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_BRICKS_CANDIDATE, RNBlocks.SHRINE_STONE_BRICKS.get(), 20.0, 5000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_BRICKS_STAIRS_CANDIDATE, RNBlocks.SHRINE_STONE_BRICKS_STAIRS.get(), 20.0, 5000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_BRICKS_SLAB_CANDIDATE, RNBlocks.SHRINE_STONE_BRICKS_SLAB.get(), 20.0, 5000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_BRICKS_WALL_CANDIDATE, RNBlocks.SHRINE_STONE_BRICKS_WALL.get(), 20.0, 5000);

        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_TILES_CANDIDATE, RNBlocks.SHRINE_STONE_TILES.get(), 20.0, 55000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_TILES_STAIRS_CANDIDATE, RNBlocks.SHRINE_STONE_TILES_STAIRS.get(), 20.0, 5000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_TILES_SLAB_CANDIDATE, RNBlocks.SHRINE_STONE_TILES_SLAB.get(), 20.0, 5000);
        addTagConversionRule(rules, RNTags.Blocks.SHRINE_STONE_TILES_WALL_CANDIDATE, RNBlocks.SHRINE_STONE_TILES_WALL.get(), 20.0, 5000);

        applyConversions(level, altarPos, rules);
    }

}