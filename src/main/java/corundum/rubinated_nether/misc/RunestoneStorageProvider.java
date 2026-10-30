package corundum.rubinated_nether.misc;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.RunestoneBlock;
import corundum.rubinated_nether.content.blocks.entities.RunestoneBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

public enum RunestoneStorageProvider implements IServerExtensionProvider<ItemStack> {
    INSTANCE;

    public static final ResourceLocation UID = RubinatedNether.id("runestone_storage");

    @Nullable
    @Override
    public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        if (!(accessor instanceof BlockAccessor blockAccessor)) return null;

        var state = blockAccessor.getBlockState();
        if (!(state.getBlock() instanceof RunestoneBlock)) return null;

        var pos = blockAccessor.getPosition();
        var lowerPos = state.getValue(RunestoneBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        if (!(accessor.getLevel().getBlockEntity(lowerPos) instanceof RunestoneBlockEntity runestone)) return null;

        var rune = runestone.getTheItem();
        if (rune.isEmpty()) return List.of();

        return List.of(new ViewGroup<>(List.of(rune.copy())));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public int getDefaultPriority() {
        return -100;
    }
}
