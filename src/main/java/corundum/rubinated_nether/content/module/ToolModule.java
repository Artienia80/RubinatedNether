package corundum.rubinated_nether.content.module;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ToolModule {
    public static final TextColor BRONZE = TextColor.fromRgb(0xCD7F32);

    private final ResourceLocation id;
    private final TagKey<Item> catalystTag;
    private final Set<ResourceLocation> incompatible = new HashSet<>();
    private ResourceLocation group;
    private int maxLevel = 1;
    private boolean swappable;
    private int fuelMultiplier = 1;
    private int fuelPerLevel;

    public ToolModule(ResourceLocation id, TagKey<Item> catalystTag) {
        this.id = id;
        this.catalystTag = catalystTag;
    }

    public ToolModule group(ResourceLocation group) {
        this.group = group;
        return this;
    }

    /** Applying the same module again raises its level, up to this cap. */
    public ToolModule maxLevel(int maxLevel) {
        this.maxLevel = Math.max(1, maxLevel);
        return this;
    }

    /** Applying this module replaces a swappable module of the same group (and resets to level 1). */
    public ToolModule swappable() {
        this.swappable = true;
        return this;
    }

    /** Multiplies the tool's fuel burn rate while this module is installed. */
    public ToolModule fuelMultiplier(int fuelMultiplier) {
        this.fuelMultiplier = Math.max(1, fuelMultiplier);
        return this;
    }

    /** Extra droplets burned per fuel tick for each level of this module (level 3 = +3 per tick). */
    public ToolModule fuelPerLevel(int fuelPerLevel) {
        this.fuelPerLevel = Math.max(0, fuelPerLevel);
        return this;
    }

    public ToolModule incompatibleWith(ResourceLocation... otherIds) {
        incompatible.addAll(List.of(otherIds));
        return this;
    }

    public boolean conflictsWith(ToolModule other) {
        if (group != null && group.equals(other.group)) return true;
        return incompatible.contains(other.id) || other.incompatible.contains(id);
    }

    /** True when applying this module may replace {@code other} instead of being blocked by it. */
    public boolean replaces(ToolModule other) {
        return swappable && other.swappable && group != null && group.equals(other.group);
    }

    public int maxLevel() {
        return maxLevel;
    }

    public int fuelPerLevel() {
        return fuelPerLevel;
    }

    public int fuelMultiplier() {
        return fuelMultiplier;
    }

    public ResourceLocation id() {
        return id;
    }

    public boolean isCatalyst(ItemStack stack) {
        return !stack.isEmpty() && stack.is(catalystTag);
    }

    public MutableComponent displayName() {
        return Component.translatable("module." + id.getNamespace() + "." + id.getPath())
                .withStyle(style -> style.withColor(BRONZE));
    }
}
