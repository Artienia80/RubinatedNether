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

    public ToolModule(ResourceLocation id, TagKey<Item> catalystTag) {
        this.id = id;
        this.catalystTag = catalystTag;
    }

    public ToolModule group(ResourceLocation group) {
        this.group = group;
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
