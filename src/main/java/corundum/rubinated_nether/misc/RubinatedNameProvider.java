package corundum.rubinated_nether.misc;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.client.render.RubinatedTextRenderer;
import corundum.rubinated_nether.content.RNRarity;
import corundum.rubinated_nether.content.blocks.RunestoneBlock;
import corundum.rubinated_nether.content.items.Rubination;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;

public enum RubinatedNameProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = RubinatedNether.id("rubinated_name");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        ItemStack stack = accessor.getPickedResult();
        if (stack == null || stack.isEmpty()) {
            stack = accessor.getBlock().asItem().getDefaultInstance();
        }
        var state = accessor.getBlockState();
        boolean charged = state.getBlock() instanceof RunestoneBlock
                && state.getValue(RunestoneBlock.HAS_RUNE) != Rubination.EMPTY;
        if (!charged && !RubinatedTextRenderer.isRubinated(stack)) {
            return;
        }

        String message = tooltip.getMessage(JadeIds.CORE_OBJECT_NAME);
        if (message.isBlank()) {
            return;
        }

        MutableComponent name = Component.literal(message);
        if (RubinatedTextRenderer.useRubinatedLang() && !stack.has(DataComponents.CUSTOM_NAME)) {
            name = RubinatedTextRenderer.restyleWithEnglishName(name, stack);
        }

        Rarity rarity = charged ? RNRarity.RUBINATED_NETHER_RUBY.get() : stack.getOrDefault(DataComponents.RARITY, Rarity.COMMON);
        name.withStyle(rarity.getStyleModifier());
        tooltip.replace(JadeIds.CORE_OBJECT_NAME, name);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public int getDefaultPriority() {
        return TooltipPosition.HEAD - 90;
    }

    @Override
    public boolean isRequired() {
        return true;
    }
}
