package corundum.rubinated_nether.client.render;

import net.irisshaders.iris.api.v0.IrisApi;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;

import java.lang.reflect.Field;
import java.util.function.BooleanSupplier;

@OnlyIn(Dist.CLIENT)
public class ShaderHelper {
	private static final BooleanSupplier shaderPackInUse;

	static {
		if (ModList.get().isLoaded("iris") || ModList.get().isLoaded("oculus")) {
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
