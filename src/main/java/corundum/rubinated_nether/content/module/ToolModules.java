package corundum.rubinated_nether.content.module;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.items.MechanicalTool;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ToolModules {
    /** Extra fuel droplets burned per fuel tick (see MechanicalFuel.TICKS_PER_DROPLET) for each installed module. */
    public static final int EXTRA_DROPLETS_PER_MODULE = 1;

    private static final Map<ResourceLocation, ToolModule> REGISTRY = new LinkedHashMap<>();

    public static final ResourceLocation DROPS_GROUP = RubinatedNether.id("drops");

    public static final ToolModule DAMPENING = register(new ToolModule(
            RubinatedNether.id("dampening"),
            TagKey.create(Registries.ITEM, RubinatedNether.id("module_catalyst/dampening")))
            .group(DROPS_GROUP));

    public static final ToolModule SMELTING = register(new ToolModule(
            RubinatedNether.id("smelting"),
            TagKey.create(Registries.ITEM, RubinatedNether.id("module_catalyst/smelting")))
            .group(DROPS_GROUP));

    public static final ToolModule PROCESSING = register(new ToolModule(
            RubinatedNether.id("processing"),
            TagKey.create(Registries.ITEM, RubinatedNether.id("module_catalyst/processing")))
            .group(DROPS_GROUP));

    static {
        DropProcessors.register(SMELTING, BuiltinDropProcessors::smelt);
        DropProcessors.register(PROCESSING, BuiltinDropProcessors::process);
    }

    private ToolModules() {}

    public static ToolModule register(ToolModule module) {
        REGISTRY.put(module.id(), module);
        return module;
    }

    public static ToolModule byId(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static Collection<ToolModule> all() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static boolean isModdable(ItemStack stack) {
        return stack.getItem() instanceof MechanicalTool;
    }

    public static List<ResourceLocation> moduleIds(ItemStack stack) {
        return stack.getOrDefault(RNDataComponents.TOOL_MODULES, List.of());
    }

    public static int moduleCount(ItemStack stack) {
        return moduleIds(stack).size();
    }

    public static boolean hasAnyModule(ItemStack stack) {
        return isModdable(stack) && !moduleIds(stack).isEmpty();
    }

    public static boolean has(ItemStack stack, ToolModule module) {
        return isModdable(stack) && moduleIds(stack).contains(module.id());
    }

    /** Droplets burned per fuel tick: 1 base + EXTRA_DROPLETS_PER_MODULE for every installed module. */
    public static int dropletsPerBurn(ItemStack stack) {
        return 1 + moduleCount(stack) * EXTRA_DROPLETS_PER_MODULE;
    }

    public static boolean canApply(ItemStack tool, ToolModule module) {
        if (!isModdable(tool) || moduleIds(tool).contains(module.id())) return false;
        for (ResourceLocation existingId : moduleIds(tool)) {
            ToolModule existing = byId(existingId);
            if (existing != null && existing.conflictsWith(module)) return false;
        }
        return true;
    }

    public static ToolModule moduleForCatalyst(ItemStack tool, ItemStack catalyst) {
        for (ToolModule module : REGISTRY.values()) {
            if (module.isCatalyst(catalyst) && canApply(tool, module)) return module;
        }
        return null;
    }

    public static boolean isAnyCatalyst(ItemStack stack) {
        for (ToolModule module : REGISTRY.values()) {
            if (module.isCatalyst(stack)) return true;
        }
        return false;
    }

    public static ItemStack withModule(ItemStack tool, ToolModule module) {
        ItemStack result = tool.copy();
        List<ResourceLocation> ids = new ArrayList<>(moduleIds(result));
        ids.add(module.id());
        result.set(RNDataComponents.TOOL_MODULES, List.copyOf(ids));
        return result;
    }

    public static void appendTooltip(ItemStack stack, List<Component> tooltip) {
        if (!hasAnyModule(stack)) return;
        for (ResourceLocation id : moduleIds(stack)) {
            ToolModule module = byId(id);
            if (module != null) tooltip.add(module.displayName());
        }
    }
}
