package corundum.rubinated_nether.misc;

import corundum.rubinated_nether.client.render.RubinatedTextRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import snownee.jade.api.Accessor;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

public enum RunestoneStorageClientProvider implements IClientExtensionProvider<ItemStack, ItemView> {
    INSTANCE;

    @Override
    public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> groups) {
        return ClientViewGroup.map(groups, RunestoneStorageClientProvider::toView, null);
    }

    private static ItemView toView(ItemStack stack) {
        MutableComponent name = stack.getHoverName().copy();
        if (RubinatedTextRenderer.isRubinated(stack) && RubinatedTextRenderer.useRubinatedLang()
                && !stack.has(DataComponents.CUSTOM_NAME)) {
            name = RubinatedTextRenderer.restyleWithEnglishName(name, stack);
        }

        MutableComponent line = Component.literal(stack.getCount() + "\u00D7 ").append(name);
        Rarity rarity = stack.getOrDefault(DataComponents.RARITY, Rarity.COMMON);
        line.withStyle(rarity.getStyleModifier());

        List<IElement> description = List.of(IElementHelper.get().text(line).message(null));
        return new ItemView(stack).description(description);
    }

    @Override
    public ResourceLocation getUid() {
        return RunestoneStorageProvider.UID;
    }
}
