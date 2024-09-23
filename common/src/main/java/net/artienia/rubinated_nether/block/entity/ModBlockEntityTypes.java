package net.artienia.rubinated_nether.block.entity;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.block.ModBlocks;
import net.artienia.rubinated_nether.client.render.blockEntity.RubyLaserRenderer;
import uwu.serenity.critter.stdlib.blockEntities.BlockEntityEntry;
import uwu.serenity.critter.stdlib.blockEntities.BlockEntityRegistrar;

public class ModBlockEntityTypes {

    public static final BlockEntityRegistrar BLOCK_ENTITY_TYPES = BlockEntityRegistrar.create(RubinatedNether.REGISTRIES);

    public static final BlockEntityEntry<FreezerBlockEntity> FREEZER = BLOCK_ENTITY_TYPES.entry("freezer", FreezerBlockEntity::new)
        .validBlock(ModBlocks.FREEZER)
        .register();

    public static final BlockEntityEntry<RubyLaserBlockEntity> RUBY_LASER = BLOCK_ENTITY_TYPES.entry("ruby_laser", RubyLaserBlockEntity::new)
        .validBlock(ModBlocks.RUBY_LASER)
        .renderer(() -> RubyLaserRenderer::new)
        .register();

    public static void register() {
        BLOCK_ENTITY_TYPES.register();
    }

}
