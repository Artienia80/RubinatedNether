package net.artienia.rubinated_nether.content;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.client.render.blockEntity.RubyLaserRenderer;
import net.artienia.rubinated_nether.content.block.RubinationAltarBlockEntity;
import net.artienia.rubinated_nether.content.block.brazier.BrazierBlockEntity;
import net.artienia.rubinated_nether.content.block.freezer.FreezerBlockEntity;
import net.artienia.rubinated_nether.content.block.ruby_laser.RubyLaserBlockEntity;
import uwu.serenity.critter.stdlib.blockEntities.BlockEntityEntry;
import uwu.serenity.critter.stdlib.blockEntities.BlockEntityRegistrar;

public final class RNBlockEntities {

    public static final BlockEntityRegistrar BLOCK_ENTITY_TYPES = BlockEntityRegistrar.create(RubinatedNether.REGISTRIES);

    public static final BlockEntityEntry<FreezerBlockEntity> FREEZER = BLOCK_ENTITY_TYPES.entry("freezer", FreezerBlockEntity::new)
        .validBlock(RNBlocks.FREEZER)
        .register();

    public static final BlockEntityEntry<RubyLaserBlockEntity> RUBY_LASER = BLOCK_ENTITY_TYPES.entry("ruby_laser", RubyLaserBlockEntity::new)
        .validBlock(RNBlocks.RUBY_LASER)
        .renderer(() -> RubyLaserRenderer::new)
        .register();

    public static final BlockEntityEntry<BrazierBlockEntity> BRAZIER = BLOCK_ENTITY_TYPES.entry("brazier", BrazierBlockEntity::new)
        .validBlock(RNBlocks.RUBY_BRAZIER)
        .register();

    public static final BlockEntityEntry<RubinationAltarBlockEntity> RUBINATION_ALTAR = BLOCK_ENTITY_TYPES.entry("rubination_altar", RubinationAltarBlockEntity::new)
            .validBlock(RNBlocks.RUBINATION_ATLAR)
            .register();

    public static void register() {
        BLOCK_ENTITY_TYPES.register();
    }

}
