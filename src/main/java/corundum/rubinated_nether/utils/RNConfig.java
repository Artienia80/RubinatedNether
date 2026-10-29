package corundum.rubinated_nether.utils;

import eu.midnightdust.lib.config.MidnightConfig;

public class RNConfig extends MidnightConfig {
	/** Marks server-authoritative gameplay fields that are synced to connected clients. */
	@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
	@java.lang.annotation.Target(java.lang.annotation.ElementType.FIELD)
	public @interface Synced {}

	public static final String BRAZIER = "brazier";
	public static final String CHANDELIER = "chandelier";
	public static final String COFFER = "coffer";
	public static final String ALTAR = "altar";
	public static final String CLIENT = "client";
	public static final String CAULDRON = "cauldron";
	public static final String VENT = "vent";
	public static final String POCKETWATCH = "pocketwatch";

	/* -- Brazier -- */

	@Entry(
			category = BRAZIER,
			isSlider = true,
			min = 0,
			max = 128
	)
	@Synced
	public static int brazierEffectRange = 16;

	@Entry(
			category = BRAZIER,
			isSlider = true,
			min = 1,
			max = 120
	)
	@Synced
	public static int brazierMinutesPerLevel = 20;

	@Entry(category = BRAZIER)
	@Synced
	public static boolean brazierPowerFireRes = true;

	@Entry(category = BRAZIER)
	public static boolean brazierPowerLavaVision = true;

	@Entry(
			category = BRAZIER,
			isSlider = true,
			min = 0,
			max = 64
	)
	public static int brazierPowerViewRange = 16;

	@Entry(category = BRAZIER)
	@Synced
	public static boolean brazierPowerLavaHealing = true;

	@Entry(category = BRAZIER)
	@Synced
	public static boolean brazierPowerLavaSwimming = true;

	@Entry(category = BRAZIER)
	@Synced
	public static boolean brazierPowerBoatLava = true;

	@Entry(category = BRAZIER)
	@Synced
	public static boolean brazierPowerLavaImmuneItems = true;

	@Entry(category = BRAZIER)
	public static boolean brazierPowerDisableFireOverlay = true;

	@Entry(category = BRAZIER)
	public static boolean brazierEffectParticles = true;

	@Entry(category = BRAZIER)
	public static boolean brazierBoundaryParticles = true;

	/* -- Altar -- */

	@Entry(
			category = ALTAR,
			isSlider = true,
			min = 0,
			max = 1024
	)
	@Synced
	public static int altarRubinationCost = 100;

	@Entry(
			category = ALTAR,
			isSlider = true,
			min = 0,
			max = 20
	)
	@Synced
	public static int altarLesserBlessingTime = 5;

	@Entry(
			category = ALTAR,
			isSlider = true,
			min = 0,
			max = 60
	)
	@Synced
	public static int altarGreaterBlessingTime = 20;

	@Entry(
			category = ALTAR,
			isSlider = true,
			min = 60,
			max = 1440
	)
	@Synced
	public static int altarFullBlessingThreshold = 320;

	@Entry(category = ALTAR)
	@Synced
	public static boolean blessedEffectGlowing = true;

	@Entry(category = ALTAR)
	@Synced
	public static boolean bronzeDiseasedWeakness = true;

	@Entry(
			category = ALTAR,
			isSlider = true,
			min = 0,
			max = 1
	)
	@Synced
	public static float bronzeDiseasedWeaknessStrength = 0.4f;

	/* -- Chandelier -- */

	@Entry(category = CHANDELIER)
	@Synced
	public static int chandelierDefaultDamage = 400;

	@Entry(
			category = CHANDELIER,
			isSlider = true,
			min = 0.25,
			max = 0.5
	)
	@Synced
	public static float chandelierStateMultiplierIncrease = .25F;

	/* -- Coffer -- */

	@Entry(
			category = COFFER,
			isSlider = true,
			min = 2,
			max = 15
	)
	@Synced
	public static int cofferStackMultiplier = 4;

	/* -- Cauldron -- */

	@Entry(category = CAULDRON)
	@Synced
	public static int moltenRubyCauldronAverageMinutes = 30;

	@Entry(category = CAULDRON)
	@Synced
	public static boolean moltenRubyCauldronNetherOnly = true;

	/* -- Vent -- */

	@Entry(
			category = VENT,
			isSlider = true,
			min = 0f,
			max = 5f
	)
	public static float ventSmokeParticleMultiplier = 1.0f;

	@Entry(
			category = VENT,
			isSlider = true,
			min = 1,
			max = 15
	)
	@Synced
	public static int crystallizedVentRange = 3;



	/* -- Client -- */

	@Entry(
			category = CLIENT,
			isSlider = true,
			min = 0f,
			max = 1f
	)
	public static float rubyLensOpacity = 1f;

	@Entry(
			category = CLIENT,
			isSlider = true,
			min = 0f,
			max = 1f
	)
	public static float bronzeDiseasedOverlayOpacity = 1f;

	public enum TabDisplay {
		BOTH,
		VANILLA_ONLY,
		MODDED_ONLY
	}

	@Entry(category = CLIENT)
	public static TabDisplay tabDisplay = TabDisplay.BOTH;

	@Entry(category = CLIENT)
	public static boolean rubinatedLanguage = true;

	@Entry(
			category = POCKETWATCH,
			isSlider = true,
			min = 0,
			max = 60
	)
	@Synced
	public static int pocketwatchStopLockMinutes = 15;

	/* -- Calculated Values -- */

	/**
	 * Calculates how many seconds one brazier level provides.
	 * @return seconds per level (default: 1200 = 20 minutes)
	 */
	public static int getBrazierSecondsPerLevel() {
		return brazierMinutesPerLevel * 60;
	}

	/**
	 * Calculates the probability per tick for cauldron filling.
	 * Checks happen every 20 ticks, so this returns the chance per check.
	 * @return probability (0.0 to 1.0)
	 */
	public static double getMoltenRubyCauldronFillProbability() {
		// Convert minutes to ticks: minutes * 60 seconds * 20 ticks/second
		int totalTicks = moltenRubyCauldronAverageMinutes * 60 * 20;
		// We check every 20 ticks, so divide by 20
		int checksNeeded = totalTicks / 20;
		// Probability per check is 1/checksNeeded
		return 1.0 / checksNeeded;
	}
}