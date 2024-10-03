package net.artienia.rubinated_nether.integrations.aether;

import net.artienia.rubinated_nether.content.block.freezer.FreezerBlockEntity;
import net.artienia.rubinated_nether.integrations.CompatHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class AetherCompat implements CompatHandler {
    @Override
    public void init() {}

    @Override
    public void setup() {
        Block icestone = BuiltInRegistries.BLOCK.get(new ResourceLocation("aether", "icestone"));
        FreezerBlockEntity.addItemFreezingTime(icestone, 1200);
    }
}
