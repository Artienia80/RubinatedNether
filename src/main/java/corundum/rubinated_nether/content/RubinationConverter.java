package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.blocks.CofferBlock;
import corundum.rubinated_nether.content.blocks.entities.CofferBlockEntity;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiPredicate;


public class RubinationConverter {

    private static final double MIN_REPLACEMENT_CHANCE = 0.0;
    private static final double MAX_REPLACEMENT_CHANCE = 1.0;

    private static final Map<Block, Block> RUBINATED_TO_NORMAL_MAP = Map.of(
            RNBlocks.RUBINATED_SHRINE_STONE_TILES.get(), RNBlocks.SHRINE_STONE_TILES.get(),
            RNBlocks.RUBINATED_SHRINE_STONE_PILLAR.get(), RNBlocks.SHRINE_STONE_PILLAR.get(),
            RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get(), RNBlocks.SHRINE_STONE_BRICKS.get(),
            RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get(), RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get(),
            RNBlocks.RUBINATED_SHRINE_STONE_COFFER.get(), RNBlocks.SHRINE_STONE_COFFER.get()
    );

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

        public Block getInputBlock() { return inputBlock; }
        public Block getOutputBlock() { return outputBlock; }
        public double getRadius() { return radius; }
        public int getAttempts() { return attempts; }
        public boolean isShrineStoneBased() { return isShrineStoneBased; }

        public boolean checkCondition(Level level, BlockPos pos) {
            return condition == null || condition.test(level, pos);
        }
    }

    public static class Conditions {

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
    }

    public static final int ALTAR_SCAN_RADIUS = 20;

    public static boolean isRubinatedBlock(BlockState state) {
        return RUBINATED_TO_NORMAL_MAP.containsKey(state.getBlock());
    }

    @FunctionalInterface
    public interface RubinatedVisitor {
        boolean visit(BlockPos.MutableBlockPos pos);
    }

    public static void forEachRubinatedBlock(Level level, BlockPos centerPos, int radius, RubinatedVisitor visitor) {
        int minX = centerPos.getX() - radius;
        int maxX = centerPos.getX() + radius;
        int minZ = centerPos.getZ() - radius;
        int maxZ = centerPos.getZ() + radius;
        int minY = Math.max(centerPos.getY() - radius, level.getMinBuildHeight());
        int maxY = Math.min(centerPos.getY() + radius, level.getMaxBuildHeight() - 1);
        if (minY > maxY) return;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) continue;

                int fromX = Math.max(minX, chunkX << 4);
                int toX = Math.min(maxX, (chunkX << 4) + 15);
                int fromZ = Math.max(minZ, chunkZ << 4);
                int toZ = Math.min(maxZ, (chunkZ << 4) + 15);

                for (int sectionY = minY >> 4; sectionY <= maxY >> 4; sectionY++) {
                    int index = chunk.getSectionIndexFromSectionY(sectionY);
                    if (index < 0 || index >= chunk.getSectionsCount()) continue;

                    LevelChunkSection section = chunk.getSection(index);
                    if (section.hasOnlyAir() || !section.maybeHas(RubinationConverter::isRubinatedBlock)) continue;

                    int fromY = Math.max(minY, sectionY << 4);
                    int toY = Math.min(maxY, (sectionY << 4) + 15);

                    for (int y = fromY; y <= toY; y++) {
                        for (int z = fromZ; z <= toZ; z++) {
                            for (int x = fromX; x <= toX; x++) {
                                if (isRubinatedBlock(section.getBlockState(x & 15, y & 15, z & 15))) {
                                    cursor.set(x, y, z);
                                    if (!visitor.visit(cursor)) return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static int countRubinatedBlocks(Level level, BlockPos centerPos, int radius) {
        int[] count = {0};
        forEachRubinatedBlock(level, centerPos, radius, pos -> {
            count[0]++;
            return true;
        });
        return count[0];
    }

    public static boolean hasEnoughRubinatedBlocks(Level level, BlockPos centerPos, int radius) {
        int required = Math.max(1, RNConfig.altarRubinationCost);
        int[] count = {0};
        forEachRubinatedBlock(level, centerPos, radius, pos -> ++count[0] < required);
        return count[0] >= RNConfig.altarRubinationCost;
    }

    public static boolean hasEnoughRubinatedBlocks(Level level, BlockPos centerPos) {
        return hasEnoughRubinatedBlocks(level, centerPos, ALTAR_SCAN_RADIUS);
    }

    @Nullable
    public static BlockState getDerubinatedVersion(BlockState state) {
        var derubinatedBlock = RUBINATED_TO_NORMAL_MAP.get(state.getBlock());
        if (derubinatedBlock != null) {
            return transferProperties(state, derubinatedBlock.defaultBlockState());
        }
        return null;
    }

    public static void derubinateBlocks(Level level, BlockPos centerPos, int radius, int amountToRemove) {
        if (level.isClientSide) return;

        List<BlockPos> rubinatedPositions = new ArrayList<>();
        forEachRubinatedBlock(level, centerPos, radius, pos -> {
            rubinatedPositions.add(pos.immutable());
            return true;
        });

        Collections.shuffle(rubinatedPositions, new Random(level.random.nextLong()));
        for (int i = 0; i < Math.min(amountToRemove, rubinatedPositions.size()); i++) {
            BlockPos pos = rubinatedPositions.get(i);
            BlockState currentState = level.getBlockState(pos);
            Block currentBlock = currentState.getBlock();
            Block normalBlock = RUBINATED_TO_NORMAL_MAP.get(currentBlock);

            if (normalBlock != null) {
                BlockState newState = transferProperties(currentState, normalBlock.defaultBlockState());
                setBlockKeepingContents(level, pos, newState, Block.UPDATE_ALL);
            }
        }
    }

    public static void setBlockKeepingContents(Level level, BlockPos pos, BlockState newState, int flags) {
        CompoundTag contents = null;
        if (newState.getBlock() instanceof CofferBlock && level.getBlockEntity(pos) instanceof CofferBlockEntity coffer) {
            contents = coffer.extractContents(level.registryAccess());
        }
        level.setBlock(pos, newState, flags);
        if (contents != null && level.getBlockEntity(pos) instanceof CofferBlockEntity target) {
            target.insertContents(contents, level.registryAccess());
        }
    }

    public static List<ConversionRule> buildResonanceRules(Level level, boolean isOffering) {
        List<ConversionRule> rules = new ArrayList<>();

        level.getRecipeManager()
                .getAllRecipesFor(RNRecipes.RESONANCE.get())
                .stream()
                .map(RecipeHolder::value)
                .forEach(recipe -> {
                    var triggerData = isOffering ? recipe.getOffering() : recipe.getKey();
                    if (triggerData.isEmpty()) return;

                    var data = triggerData.get();
                    Block resultBlock = Block.byItem(recipe.getResult().getItem());
                    if (resultBlock == Blocks.AIR) return;

                    for (ItemStack input : recipe.getIngredient().getItems()) {
                        Block inputBlock = Block.byItem(input.getItem());
                        if (inputBlock != Blocks.AIR) {
                            rules.add(new ConversionRule(
                                    inputBlock,
                                    resultBlock,
                                    data.radius(),
                                    data.attempts(),
                                    recipe.requiresShrineStoneNeighbor()
                            ));
                        }
                    }
                });

        return rules;
    }

    public static void applyConversions(Level level, BlockPos centerPos, List<ConversionRule> rules) {
        if (level.isClientSide) return;

        RandomSource random = level.getRandom();

        for (ConversionRule rule : rules) {
            applyConversion(level, centerPos, rule, random);
        }
    }

    private static void applyConversion(Level level, BlockPos centerPos, ConversionRule rule, RandomSource random) {
        for (int attempt = 0; attempt < rule.getAttempts(); attempt++) {
            BlockPos targetPos = generateRandomPositionInSphere(centerPos, rule.getRadius(), random);

            BlockState currentState = level.getBlockState(targetPos);
            if (!currentState.is(rule.getInputBlock())) continue;
            if (!rule.checkCondition(level, targetPos)) continue;

            double distance = centerPos.distSqr(targetPos);
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
                BlockState newState = transferProperties(currentState, rule.getOutputBlock().defaultBlockState());
                setBlockKeepingContents(level, targetPos, newState, Block.UPDATE_ALL);
                playEffects(level, centerPos, targetPos, random);
            }
        }
    }

    private static BlockState transferProperties(BlockState sourceState, BlockState targetState) {
        BlockState resultState = targetState;

        for (Property<?> property : sourceState.getProperties()) {
            if (targetState.hasProperty(property)) {
                try {
                    resultState = transferProperty(sourceState, resultState, property);
                } catch (Exception ignored) {}
            }
        }

        return resultState;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState transferProperty(BlockState sourceState, BlockState targetState, Property<T> property) {
        T value = sourceState.getValue(property);
        return targetState.setValue(property, value);
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
                    particleX, particleY, particleZ,
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

                    if (altarPos.distSqr(checkPos) > JUKEBOX_SEARCH_RADIUS * JUKEBOX_SEARCH_RADIUS) continue;

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

    private static void playJukeboxReplacementEffects(Level level, BlockPos altarPos, BlockPos jukeboxPos) {
        if (level.isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level;
        RandomSource random = level.getRandom();

        serverLevel.playSound(
                null,
                jukeboxPos,
                SoundEvents.AMETHYST_BLOCK_RESONATE,
                SoundSource.BLOCKS,
                1.0F,
                1.2F + random.nextFloat() * 0.2F
        );

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
                    particleX, particleY, particleZ,
                    1,
                    velocityX, velocityY, velocityZ,
                    0.15
            );
        }
    }

    public static void RubinateAreaOffering(Level level, BlockPos altarPos) {
        applyConversions(level, altarPos, buildResonanceRules(level, true));
    }

    public static void RubinateAreaKey(Level level, BlockPos altarPos) {
        replaceJukeboxDiscs(level, altarPos);
        applyConversions(level, altarPos, buildResonanceRules(level, false));
    }
}