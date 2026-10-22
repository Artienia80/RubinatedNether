package corundum.rubinated_nether.content.module;

import net.minecraft.world.item.ItemStack;

/**
 * Flywheel modules trade peak output against how fast a tool spools up.
 * Heavy: higher peak, slower acceleration. Light: lower peak, faster acceleration.
 * The level bonus is applied symmetrically (+x% one way, -x% the other).
 */
public final class Flywheel {
    /** Bonus per level; index = level (0 = no flywheel). */
    private static final float[] BONUS = {0f, 0.10f, 0.25f, 0.33f, 0.50f};

    /** Jackhammer progress per hit for the Heavy Flywheel; index = level. Level 0 uses the jackhammer's own default. */
    private static final float[] HEAVY_HIT_PROGRESS = {0f, 0.25f, 1f / 3f, 0.50f, 0.75f};

    private Flywheel() {}

    /** +level for Heavy, -level for Light, 0 for none. */
    private static int signedLevel(ItemStack stack) {
        int heavy = ToolModules.level(stack, ToolModules.HEAVY_FLYWHEEL);
        if (heavy > 0) return Math.min(heavy, BONUS.length - 1);
        int light = ToolModules.level(stack, ToolModules.LIGHT_FLYWHEEL);
        return -Math.min(light, BONUS.length - 1);
    }

    private static float bonus(ItemStack stack) {
        int level = signedLevel(stack);
        return level >= 0 ? BONUS[level] : -BONUS[-level];
    }

    /** Multiplier on a tool's peak speed (drill max boost, chainsaw windup curve). */
    public static float peakMult(ItemStack stack) {
        return 1f + bonus(stack);
    }

    /** Multiplier on how fast a tool gains speed (levels per tick). Time to ramp is divided by this. */
    public static float accelMult(ItemStack stack) {
        return 1f - bonus(stack);
    }

    /** Multiplier on the progress a jackhammer hit spills onto its neighbours. */
    public static float spillMult(ItemStack stack) {
        return 1f - bonus(stack);
    }

    /** Jackhammer progress per hit: fixed steps for Heavy, scaled down for Light. */
    public static float hitProgress(ItemStack stack, float base) {
        int level = signedLevel(stack);
        if (level > 0) return HEAVY_HIT_PROGRESS[level];
        if (level < 0) return base * (1f - BONUS[-level]);
        return base;
    }
}
