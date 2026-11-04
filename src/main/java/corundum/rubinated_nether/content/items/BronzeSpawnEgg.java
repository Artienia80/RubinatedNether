package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.TarnishStage;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import java.util.function.Supplier;

public class BronzeSpawnEgg extends DeferredSpawnEggItem {
    public BronzeSpawnEgg(Supplier<? extends EntityType<? extends Mob>> entityType,
                          TarnishStage tarnishStage,
                          boolean waxed,
                          int backgroundColor,
                          int highlightColor,
                          Item.Properties properties) {
        super(entityType, backgroundColor, highlightColor,
                properties.component(DataComponents.ENTITY_DATA, entityData(tarnishStage, waxed)));
    }

    private static CustomData entityData(TarnishStage tarnishStage, boolean waxed) {
        CompoundTag tag = new CompoundTag();
        tag.putByte("TarnishStage", (byte) tarnishStage.getId());
        tag.putBoolean("Waxed", waxed);
        return CustomData.of(tag);
    }
}
