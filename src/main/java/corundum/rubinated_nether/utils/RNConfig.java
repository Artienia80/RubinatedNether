package corundum.rubinated_nether.utils;

import eu.midnightdust.lib.config.MidnightConfig;

public class RNConfig extends MidnightConfig {
	public static final String CHANDELIER = "chandelier";
	public static final String BRAZIER = "brazier";
	public static final String CLIENT = "client";

	/* -- Chandelier -- */

	@Entry(category = CHANDELIER)
	public static int chandelierDefaultDamage = 400;

	@Entry(
			category = CHANDELIER,
			isSlider = true,
			min = 0.25,
			max = 0.5
	)
	public static float chandelierStateMultiplierIncrease = .25F;

	/* -- Brazier -- */

	@Entry(
			category = BRAZIER,
			isSlider = true,
			min = 0,
			max = 32
	)
	public static int brazierEffectRange = 16;

	@Entry(category = BRAZIER)
	public static float brazierEffectDuration = 15.F;

	@Entry(category = BRAZIER)
	public static boolean brazierEffectParticles = false;

	@Entry(category = BRAZIER)
	public static boolean brazierPowerLavaVision = true;

	@Entry(
			category = BRAZIER,
			isSlider = true,
			min = 0,
			max = 64
	)
	public static float brazierPowerViewRange = 16.0F;

	@Entry(category = BRAZIER)
	public static boolean brazierPowerLavaHealing = true;

	@Entry(category = BRAZIER)
	public static boolean brazierPowerLavaSwim = true;

	@Entry(category = BRAZIER)
	public static boolean brazierPowerDisableFireOverlay = true;

	@Entry(category = BRAZIER)
	public static boolean brazierPowerLavaImmuneItems = true;

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
			min = 1,
			max = 6
	)
	public static int brazierParticleCount = 2;
}