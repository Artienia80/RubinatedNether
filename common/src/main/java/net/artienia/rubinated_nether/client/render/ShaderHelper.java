package net.artienia.rubinated_nether.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.irisshaders.iris.api.v0.IrisApi;
import uwu.serenity.critter.platform.PlatformUtils;

import java.lang.reflect.Field;
import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public class ShaderHelper {

	private static final BooleanSupplier shaderPackInUse;

	static {
		if (PlatformUtils.modLoaded("iris") || PlatformUtils.modLoaded("oculus")) {
			shaderPackInUse = () -> IrisApi.getInstance().isShaderPackInUse();
		} else if (ClassLoader.getPlatformClassLoader().getDefinedPackage("net.optifine") != null) {
			shaderPackInUse = optifineShadersInUse();
		} else {
			shaderPackInUse = () -> false;
		}
	}

	public static boolean isShaderPackInUse() {
		return shaderPackInUse.getAsBoolean();
	}

	/**
	 * Code taken from Flywheel by Jozufozu, licensed under MIT.
	 * Thank you for your scary reflection magic.
	 */
	private static BooleanSupplier optifineShadersInUse() {
		try {
			Class<?> ofShaders = Class.forName("net.optifine.shaders.Shaders");
			Field field = ofShaders.getDeclaredField("shaderPackLoaded");
			field.setAccessible(true);
			return () -> {
				try {
					return field.getBoolean(null);
				} catch (IllegalAccessException ignored) {
					return false;
				}
			};
		} catch (Exception ignored) {
			return () -> false;
		}
	}

}
