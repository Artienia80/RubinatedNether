package net.artienia.rubinated_nether.integrations.netherexp;

import net.artienia.rubinated_nether.content.block.freezer.FreezerBlockEntity;
import net.artienia.rubinated_nether.integrations.CompatHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class JNECompat implements CompatHandler {
    @Override
    public void init() {}

    @Override
    public void setup() {
        Block black_ice = BuiltInRegistries.BLOCK.get(new ResourceLocation("netherexp", "black_ice"));
        FreezerBlockEntity.addItemFreezingTime(black_ice, 3200);
    }
}
