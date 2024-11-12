package net.artienia.rubinated_nether.integrations.netherexp;

import net.artienia.rubinated_nether.content.block.freezer.FreezerBlockEntity;
import net.artienia.rubinated_nether.integrations.CompatHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class JNECompat implements CompatHandler {
    @Override
    public void init() {}

    @Override
    public void setup() {
        Block black_ice = BuiltInRegistries.BLOCK.get(new ResourceLocation("netherexp", "black_ice"));
        FreezerBlockEntity.addItemFreezingTime(black_ice, 3200);

        Item ectoplasm_bucket = BuiltInRegistries.ITEM.get(new ResourceLocation("netherexp", "ectoplasm_bucket"));
        FreezerBlockEntity.addItemFreezingTime(ectoplasm_bucket, 20000);
    }
}
