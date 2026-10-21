package corundum.rubinated_nether.content.module;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public final class DropProcessors {
    private static final Map<ResourceLocation, DropProcessor> PROCESSORS = new HashMap<>();

    private DropProcessors() {}

    public static void register(ToolModule module, DropProcessor processor) {
        PROCESSORS.put(module.id(), processor);
    }

    public static DropProcessor get(ResourceLocation moduleId) {
        return PROCESSORS.get(moduleId);
    }
}
