package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.gearbox.GearboxBlockEntity;
import corundum.rubinated_nether.utils.BEBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;

public class ActiveGearboxBlock extends GearboxBlock implements BEBlock<GearboxBlockEntity> {

    public ActiveGearboxBlock(TarnishStage stage, Properties properties) {
        super(stage, properties);
    }

    @Override
    public BlockEntityType<? extends GearboxBlockEntity> getBlockEntityType() {
        return RNBlockEntities.GEARBOX.get();
    }

    @Override
    public Class<? extends GearboxBlockEntity> getBlockEntityClass() {
        return GearboxBlockEntity.class;
    }

    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, components, tooltipFlag);
        Spawner.appendHoverText(itemStack, components, "spawn_data");
    }
}
