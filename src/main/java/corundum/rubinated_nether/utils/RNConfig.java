package corundum.rubinated_nether.utils;

import eu.midnightdust.lib.config.MidnightConfig;

public class RNConfig extends MidnightConfig {
	public static final String CHANDELIER = "chandelier";
	public static final String BRAZIER = "brazier";
	public static final String LENS = "lens";

	/* -- Chandelier -- */

	@Entry(
		category = CHANDELIER,
		name = "Chandelier Max Damage"
	)
	public static int chandelierMaxDamage = 500;

	@Entry(
		category = CHANDELIER, 
		name = "Chandelier Damage Multiplier",
		isSlider = true, 
		min = 0, 
		max = 1
	)
	public static float chandelierMultiplier = .22F;

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

	@Entry(
			category = LENS,
			name = "Ruby Lens Opacity"
	)
	public static float rubyLensOpacity = 1f;
}
