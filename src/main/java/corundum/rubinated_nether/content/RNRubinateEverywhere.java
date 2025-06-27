package corundum.rubinated_nether.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RNRubinateEverywhere {

    private static final double DEFAULT_RADIUS = 8.0;
    private static final int DEFAULT_ATTEMPTS = 50; // How many random positions to try
    private static final double MIN_REPLACEMENT_CHANCE = 0.0; // 0% at max radius
    private static final double MAX_REPLACEMENT_CHANCE = 1.0; // 100% at center
    
    public static class ConversionRule {
        private final Block inputBlock;
        private final Block outputBlock;
        private final double radius;
        private final int attempts;

        public ConversionRule(Block inputBlock, Block outputBlock, double radius, int attempts) {
            this.inputBlock = inputBlock;
            this.outputBlock = outputBlock;
            this.radius = radius;
            this.attempts = attempts;
        }

        public ConversionRule(Block inputBlock, Block outputBlock) {
            this(inputBlock, outputBlock, DEFAULT_RADIUS, DEFAULT_ATTEMPTS);
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
    }

    /**
     * Applies block conversions based on the provided rules
     */
    public static void applyConversions(Level level, BlockPos centerPos, List<ConversionRule> rules) {
        if (level.isClientSide) return; // Only run on server side

        RandomSource random = level.getRandom();

        for (ConversionRule rule : rules) {
            applyConversion(level, centerPos, rule, random);
        }
    }

    /**
     * Applies a single conversion rule
     */
    private static void applyConversion(Level level, BlockPos centerPos, ConversionRule rule, RandomSource random) {
        int successfulConversions = 0;

        for (int attempt = 0; attempt < rule.getAttempts(); attempt++) {
            // Generate random position within sphere
            BlockPos targetPos = generateRandomPositionInSphere(centerPos, rule.getRadius(), random);

            // Check if the block at this position matches our input block
            BlockState currentState = level.getBlockState(targetPos);
            if (!currentState.is(rule.getInputBlock())) {
                continue; // Skip if not the target block
            }

            // Calculate distance-based replacement chance
            double distance = centerPos.distSqr(targetPos);
            double maxDistanceSquared = rule.getRadius() * rule.getRadius();

            // Calculate replacement chance (100% at center, 0% at max radius)
            double normalizedDistance = Math.sqrt(distance) / rule.getRadius();
            double replacementChance = MAX_REPLACEMENT_CHANCE - (normalizedDistance * (MAX_REPLACEMENT_CHANCE - MIN_REPLACEMENT_CHANCE));

            // Clamp the chance between 0 and 1
            replacementChance = Math.max(0.0, Math.min(1.0, replacementChance));

            // Roll for replacement
            if (random.nextDouble() < replacementChance) {
                // Perform the replacement
                level.setBlockAndUpdate(targetPos, rule.getOutputBlock().defaultBlockState());

                // Play effects
                playEffects(level, targetPos, random);
                successfulConversions++;
            }
        }

        // Optional: Log the number of successful conversions for debugging
        // System.out.println("Successfully converted " + successfulConversions + " blocks from " +
        //                   rule.getInputBlock().getName().getString() + " to " +
        //                   rule.getOutputBlock().getName().getString());
    }

    /**
     * Generates a random position within a sphere
     */
    private static BlockPos generateRandomPositionInSphere(BlockPos center, double radius, RandomSource random) {
        // Generate random point in sphere using rejection sampling
        double x, y, z;
        do {
            x = (random.nextDouble() * 2.0 - 1.0) * radius;
            y = (random.nextDouble() * 2.0 - 1.0) * radius;
            z = (random.nextDouble() * 2.0 - 1.0) * radius;
        } while (x * x + y * y + z * z > radius * radius);

        return center.offset((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }

    /**
     * Plays sound and particle effects at the conversion location
     */
    private static void playEffects(Level level, BlockPos pos, RandomSource random) {
        // Play amethyst-like sound
        level.playSound(
                null,
                pos,
                SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.BLOCKS,
                0.5F + random.nextFloat() * 0.3F, // Volume: 0.5-0.8
                0.8F + random.nextFloat() * 0.4F  // Pitch: 0.8-1.2
        );

        // Spawn particles
        double particleX = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
        double particleY = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
        double particleZ = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;

        // Spawn multiple particles for better effect
        for (int i = 0; i < 3 + random.nextInt(3); i++) {
            level.addParticle(
                    ParticleTypes.END_ROD, // Nice glowing particle
                    particleX + (random.nextDouble() - 0.5) * 0.5,
                    particleY + (random.nextDouble() - 0.5) * 0.5,
                    particleZ + (random.nextDouble() - 0.5) * 0.5,
                    (random.nextDouble() - 0.5) * 0.1, // Velocity X
                    random.nextDouble() * 0.1,         // Velocity Y (upward)
                    (random.nextDouble() - 0.5) * 0.1  // Velocity Z
            );
        }

        // Add some amethyst-colored particles if available
        level.addParticle(
                ParticleTypes.PORTAL,
                particleX,
                particleY,
                particleZ,
                (random.nextDouble() - 0.5) * 0.2,
                random.nextDouble() * 0.2,
                (random.nextDouble() - 0.5) * 0.2
        );
    }

    /**
     * Convenience method for the default netherrack to ruby ore conversion
     * You can call this from your RubinationMenu
     */
    public static void convertNetherrackToRubyOre(Level level, BlockPos altarPos, Block rubyOreBlock) {
        List<ConversionRule> rules = new ArrayList<>();
        rules.add(new ConversionRule(Blocks.NETHERRACK, rubyOreBlock));
        applyConversions(level, altarPos, rules);
    }

    /**
     * More configurable version for custom conversions
     */
    public static void convertNetherrackToRubyOre(Level level, BlockPos altarPos, Block rubyOreBlock,
                                                  double radius, int attempts) {
        List<ConversionRule> rules = new ArrayList<>();
        rules.add(new ConversionRule(Blocks.NETHERRACK, rubyOreBlock, radius, attempts));
        applyConversions(level, altarPos, rules);
    }

    /**
     * Example of how you could set up multiple conversion rules
     */
    public static void applyAllRubinationConversions(Level level, BlockPos altarPos, Block rubyOreBlock) {
        List<ConversionRule> rules = new ArrayList<>();

        // Primary conversion: Netherrack to Ruby Ore
        rules.add(new ConversionRule(Blocks.NETHERRACK, rubyOreBlock, 8.0, 50));

        // You could add more rules here, for example:
        // rules.add(new ConversionRule(Blocks.STONE, Blocks.IRON_ORE, 5.0, 20));
        // rules.add(new ConversionRule(Blocks.DEEPSLATE, Blocks.DEEPSLATE_DIAMOND_ORE, 3.0, 10));

        applyConversions(level, altarPos, rules);
    }
}