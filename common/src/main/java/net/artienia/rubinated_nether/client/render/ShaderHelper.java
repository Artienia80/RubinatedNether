package net.artienia.rubinated_nether.client.render;

import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.irisshaders.iris.api.v0.IrisApi;

import java.lang.reflect.Field;
import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public class ShaderHelper {

    private static BooleanSupplier shaderPackInUse;

    public static boolean isShaderPackInUse() {
        if(shaderPackInUse == null) init();
        return shaderPackInUse.getAsBoolean();
    }

    @SuppressWarnings("deprecation")
    private static void init() {
        if (Platform.isModLoaded("iris") || Platform.isModLoaded("oculus")) {
            shaderPackInUse = () -> IrisApi.getInstance().isShaderPackInUse();
        } else if (Package.getPackage("net.optifine") != null) {
            shaderPackInUse = optifineShadersInUse();
        } else {
            shaderPackInUse = () -> false;
        }
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
