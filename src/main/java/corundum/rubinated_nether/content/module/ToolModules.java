package corundum.rubinated_nether.content.module;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.items.MechanicalTool;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ToolModules {
    /** Drop Modules (the ones that change how drops work) multiply the tool's fuel burn rate by this. */
    public static final int DROP_MODULE_FUEL_MULTIPLIER = 2;

    /** Each Flywheel level burns this many extra droplets per fuel tick (level 3 = +3). */
    public static final int FLYWHEEL_FUEL_PER_LEVEL = 1;

    public static final int FLYWHEEL_MAX_LEVEL = 4;

    private static final Map<ResourceLocation, ToolModule> REGISTRY = new LinkedHashMap<>();

    public static final ResourceLocation DROPS_GROUP = RubinatedNether.id("drops");

    public static final ToolModule DAMPENING = register(new ToolModule(
            RubinatedNether.id("dampening"),
            TagKey.create(Registries.ITEM, RubinatedNether.id("module_catalyst/dampening")))
            .group(DROPS_GROUP)
            .fuelMultiplier(DROP_MODULE_FUEL_MULTIPLIER));

    public static final ToolModule SMELTING = register(new ToolModule(
            RubinatedNether.id("smelting"),
            TagKey.create(Registries.ITEM, RubinatedNether.id("module_catalyst/smelting")))
            .group(DROPS_GROUP)
            .fuelMultiplier(DROP_MODULE_FUEL_MULTIPLIER));

    public static final ToolModule PROCESSING = register(new ToolModule(
            RubinatedNether.id("processing"),
            TagKey.create(Registries.ITEM, RubinatedNether.id("module_catalyst/processing")))
            .group(DROPS_GROUP)
            .fuelMultiplier(DROP_MODULE_FUEL_MULTIPLIER));

    // Flywheel: trades peak speed against acceleration (see Flywheel for the numbers)
    public static final ResourceLocation FLYWHEEL_GROUP = RubinatedNether.id("flywheel");

    public static final ToolModule HEAVY_FLYWHEEL = register(new ToolModule(
            RubinatedNether.id("heavy_flywheel"),
            TagKey.create(Registries.ITEM, RubinatedNether.id("module_catalyst/heavy_flywheel")))
            .group(FLYWHEEL_GROUP)
            .maxLevel(FLYWHEEL_MAX_LEVEL)
            .fuelPerLevel(FLYWHEEL_FUEL_PER_LEVEL)
            .swappable());

    public static final ToolModule LIGHT_FLYWHEEL = register(new ToolModule(
            RubinatedNether.id("light_flywheel"),
            TagKey.create(Registries.ITEM, RubinatedNether.id("module_catalyst/light_flywheel")))
            .group(FLYWHEEL_GROUP)
            .maxLevel(FLYWHEEL_MAX_LEVEL)
            .fuelPerLevel(FLYWHEEL_FUEL_PER_LEVEL)
            .swappable());

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

    /**
     * Droplets burned per fuel tick: (1 base + each module's fuel-per-level x its level) x each module's multiplier.
     * Drop Modules multiply by 2; a level 3 Flywheel adds +3. Both together: (1 + 3) x 2 = 8.
     */
    public static int dropletsPerBurn(ItemStack stack) {
        int extra = 0;
        int multiplier = 1;
        for (ResourceLocation id : moduleIds(stack)) {
            ToolModule module = byId(id);
            if (module == null) continue;
            extra += module.fuelPerLevel() * level(stack, module);
            multiplier *= module.fuelMultiplier();
        }
        return (1 + extra) * multiplier;
    }

    public static Map<ResourceLocation, Integer> levels(ItemStack stack) {
        return stack.getOrDefault(RNDataComponents.MODULE_LEVELS, Map.of());
    }

    /** 0 if the module is not installed, otherwise its level (1 for modules without levels). */
    public static int level(ItemStack stack, ToolModule module) {
        if (!has(stack, module)) return 0;
        return Mth.clamp(levels(stack).getOrDefault(module.id(), 1), 1, module.maxLevel());
    }

    /** Installed modules can be levelled up until maxLevel; swappable modules replace their group mates. */
    public static boolean canApply(ItemStack tool, ToolModule module) {
        if (!isModdable(tool)) return false;
        if (moduleIds(tool).contains(module.id())) return level(tool, module) < module.maxLevel();
        for (ResourceLocation existingId : moduleIds(tool)) {
            ToolModule existing = byId(existingId);
            if (existing != null && existing.conflictsWith(module) && !module.replaces(existing)) return false;
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

    /** Installs the module (level 1, replacing a swappable group mate), or raises its level by one. */
    public static ItemStack withModule(ItemStack tool, ToolModule module) {
        ItemStack result = tool.copy();
        List<ResourceLocation> ids = new ArrayList<>(moduleIds(result));
        Map<ResourceLocation, Integer> levels = new HashMap<>(levels(result));

        if (ids.contains(module.id())) {
            levels.put(module.id(), Math.min(level(tool, module) + 1, module.maxLevel()));
        } else {
            ids.removeIf(existingId -> {
                ToolModule existing = byId(existingId);
                if (existing == null || !module.replaces(existing)) return false;
                levels.remove(existingId);
                return true;
            });
            ids.add(module.id());
            if (module.maxLevel() > 1) levels.put(module.id(), 1);
        }

        result.set(RNDataComponents.TOOL_MODULES, List.copyOf(ids));
        if (levels.isEmpty()) {
            result.remove(RNDataComponents.MODULE_LEVELS.get());
        } else {
            result.set(RNDataComponents.MODULE_LEVELS, Map.copyOf(levels));
        }
        return result;
    }

    public static void appendTooltip(ItemStack stack, List<Component> tooltip) {
        if (!hasAnyModule(stack)) return;
        for (ResourceLocation id : moduleIds(stack)) {
            ToolModule module = byId(id);
            if (module == null) continue;
            MutableComponent line = module.displayName();
            if (module.maxLevel() > 1) {
                line.append(CommonComponents.SPACE)
                        .append(Component.translatable("enchantment.level." + level(stack, module)));
            }
            tooltip.add(line);
        }
    }
}
