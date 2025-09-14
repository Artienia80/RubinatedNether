package corundum.rubinated_nether.utils;

import eu.midnightdust.lib.config.MidnightConfig;

public class RNConfig extends MidnightConfig {
	public static final String CHANDELIER = "chandelier";
	public static final String BRAZIER = "brazier";
	public static final String CLIENT = "client";

	/* -- Chandelier -- */

	@Entry(
		category = CHANDELIER,
		name = "Chandelier Default Damage Value"
	)
	public static int chandelierDefaultDamage = 400;

	@Entry(
		category = CHANDELIER, 
		name = "Chandelier Damage Multiplier Increase",
		isSlider = true, 
		min = 0.25,
		max = 0.5
	)
	public static float chandelierStateMultiplierIncrease = .25F;

	/* -- Brazier -- */

	@Entry(
		category = BRAZIER,
		name = "Brazier Effect Range",
		isSlider = true, 
		min = 0, 
		max = 32
	) 
	public static int brazierEffectRange = 16;

	@Entry(
		category = BRAZIER,
		name = "Brazier Effect Duration"
	)
	public static float brazierEffectDuration = 15.F;

	@Entry(
		category = BRAZIER,
		name = "Brazier Effect Particles"
	)
	public static boolean brazierEffectParticles = false;

	/* -- Client -- */

	@Entry(
		category = CLIENT,
		name = "Ruby Lens Opacity",
		isSlider = true, 
		min = 0f, 
		max = 1f
	)
	public static float rubyLensOpacity = 1f;

	@Entry(
		category = CLIENT,
		name = "Brazier Particle Count",
		isSlider = true, 
		min = 1, 
		max = 6
	)
	public static int brazierParticleCount = 2;
}
