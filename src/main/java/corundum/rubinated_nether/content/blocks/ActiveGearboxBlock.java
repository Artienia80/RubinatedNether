package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.gearbox.GearboxBlockEntity;
import corundum.rubinated_nether.utils.BEBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ActiveGearboxBlock extends GearboxBlock implements BEBlock<GearboxBlockEntity> {

    public ActiveGearboxBlock(TarnishStage stage, Properties properties) {
        super(stage, properties);
    }

    @Override
    public BlockEntityType<? extends GearboxBlockEntity> getBlockEntityType() {
        return RNBlockEntities.BRONZE_GEARBOX.get();
    }

    @Override
    public Class<? extends GearboxBlockEntity> getBlockEntityClass() {
        return GearboxBlockEntity.class;
    }
}
