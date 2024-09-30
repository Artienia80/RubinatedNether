package net.artienia.rubinated_nether.content;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.block.Chandelier;
import net.artienia.rubinated_nether.content.block.LavaLamp;
import net.artienia.rubinated_nether.content.block.MagmaXP;
import net.artienia.rubinated_nether.content.block.ruby_laser.RubyLaserBlock;
import net.artienia.rubinated_nether.content.block.freezer.FreezerBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import uwu.serenity.critter.creative.TabPlacement;
import uwu.serenity.critter.stdlib.blocks.BlockBuilder;
import uwu.serenity.critter.stdlib.blocks.BlockEntry;
import uwu.serenity.critter.stdlib.blocks.BlockRegistrar;
import uwu.serenity.critter.utils.StatePredicates;

import java.util.function.UnaryOperator;

public final class RNBlocks {

    public static final BlockRegistrar BLOCKS = BlockRegistrar.create(RubinatedNether.REGISTRIES);

    public static final BlockEntry<Block> RUBY_BLOCK = BLOCKS.entry("ruby_block", Block::new)
        .properties(p -> p.mapColor(MapColor.FIRE)
            .sound(SoundType.METAL)
            .strength(5.0f, 1200.0f)
            .requiresCorrectToolForDrops())
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.BUILDING_BLOCKS, TabPlacement.after(Blocks.DIAMOND_BLOCK))
        .build()
        .register();

    public static final BlockEntry<Block> BLEEDING_OBSIDIAN = BLOCKS.entry("bleeding_obsidian", Block::new)
        .copyProperties(() -> Blocks.CRYING_OBSIDIAN)
        .properties(p -> p.mapColor(MapColor.FIRE)
            .pushReaction(PushReaction.BLOCK))
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, TabPlacement.after(Blocks.CRYING_OBSIDIAN))
        .build()
        .register();

    public static final BlockEntry<StainedGlassBlock> RUBY_GLASS = BLOCKS.entry("ruby_glass", p -> new StainedGlassBlock(DyeColor.RED, p))
        .transform(rubyGlassBlock(false))
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.COLORED_BLOCKS, TabPlacement.after(Blocks.GLASS))
        .build()
        .register();

    public static final BlockEntry<StainedGlassPaneBlock> RUBY_GLASS_PANE = BLOCKS.entry("ruby_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.RED, p))
        .transform(rubyGlassBlock(true))
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.COLORED_BLOCKS, TabPlacement.after(Blocks.GLASS_PANE))
        .build()
        .register();

    public static final BlockEntry<StainedGlassBlock> MOLTEN_RUBY_GLASS = BLOCKS.entry("molten_ruby_glass", p -> new StainedGlassBlock(DyeColor.ORANGE, p))
        .transform(rubyGlassBlock(false))
        .properties(p -> p.lightLevel($ -> 10))
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.COLORED_BLOCKS, TabPlacement.after(RUBY_GLASS))
        .build()
        .register();

    public static final BlockEntry<StainedGlassPaneBlock> MOLTEN_RUBY_GLASS_PANE = BLOCKS.entry("molten_ruby_glass_pane", p -> new StainedGlassPaneBlock(DyeColor.ORANGE, p))
        .transform(rubyGlassBlock(true))
        .properties(p -> p.lightLevel($ -> 10))
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.COLORED_BLOCKS, TabPlacement.after(RUBY_GLASS_PANE))
        .build()
        .register();

    public static final BlockEntry<LanternBlock> RUBY_LANTERN = BLOCKS.entry("ruby_lantern", LanternBlock::new)
        .copyProperties(() -> Blocks.LANTERN)
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, TabPlacement.after(Blocks.SOUL_LANTERN))
        .build()
        .register();

    public static final BlockEntry<Chandelier> RUBY_CHANDELIER = BLOCKS.entry("ruby_chandelier", Chandelier::new)
        .copyProperties(() -> Blocks.COPPER_BLOCK)
        .properties(p -> p.mapColor(MapColor.FIRE)
            .noOcclusion()
            .isViewBlocking(StatePredicates::never)
            .lightLevel($ -> 15))
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, TabPlacement.after(RUBY_LANTERN))
        .build()
        .renderType(() -> RenderType::cutout)
        .register();

    public static final BlockEntry<LavaLamp> RUBY_LAVA_LAMP = BLOCKS.entry("ruby_lava_lamp", LavaLamp::new)
        .copyProperties(() -> Blocks.COPPER_BLOCK)
        .properties(p -> p.mapColor(MapColor.FIRE)
            .noOcclusion()
            .lightLevel($ -> 15))
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, TabPlacement.after(Blocks.SOUL_CAMPFIRE))
        .build()
        .renderType(() -> RenderType::cutout)
        .register();

    public static final BlockEntry<DropExperienceBlock> NETHER_RUBY_ORE = BLOCKS.entry("nether_ruby_ore", p -> new DropExperienceBlock(p, UniformInt.of(3, 6)))
        .copyProperties(() -> Blocks.NETHERRACK)
        .properties(p -> p.strength(2f)
            .requiresCorrectToolForDrops())
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.NATURAL_BLOCKS, TabPlacement.after(Blocks.NETHER_QUARTZ_ORE))
        .build()
        .register();

    public static final BlockEntry<MagmaXP> MOLTEN_RUBY_ORE = BLOCKS.entry("molten_ruby_ore", p -> new MagmaXP(p, UniformInt.of(4, 8)))
        .copyProperties(() -> Blocks.MAGMA_BLOCK)
        .properties(p -> p.strength(2f)
            .requiresCorrectToolForDrops())
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.NATURAL_BLOCKS, TabPlacement.after(NETHER_RUBY_ORE))
        .build()
        .register();

    public static final BlockEntry<RotatedPillarBlock> MOLTEN_RUBY_BLOCK = BLOCKS.entry("molten_ruby_block", RotatedPillarBlock::new)
        .properties(p -> p.mapColor(MapColor.FIRE)
            .sound(SoundType.ANCIENT_DEBRIS)
            .strength(5.0f, 1200.0f)
            .requiresCorrectToolForDrops()
            .lightLevel($ -> 10))
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.NATURAL_BLOCKS, TabPlacement.after(MOLTEN_RUBY_ORE))
        .build()
        .register();

    public static final BlockEntry<DropExperienceBlock> RUBINATED_BLACKSTONE = BLOCKS.entry("rubinated_blackstone", DropExperienceBlock::new)
        .copyProperties(() -> Blocks.GILDED_BLACKSTONE)
        .properties(p -> p.strength(2f)
            .requiresCorrectToolForDrops())
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.BUILDING_BLOCKS, TabPlacement.after(Blocks.GILDED_BLACKSTONE))
        .build()
        .register();

    public static final BlockEntry<FreezerBlock> FREEZER = BLOCKS.entry("freezer", FreezerBlock::new)
        .copyProperties(() -> Blocks.COPPER_BLOCK)
        .properties(BlockBehaviour.Properties::noOcclusion)
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, TabPlacement.after(Blocks.BLAST_FURNACE))
        .build()
        .register();

    public static final BlockEntry<RubyLaserBlock> RUBY_LASER = BLOCKS.entry("ruby_laser", RubyLaserBlock::new)
        .copyProperties(() -> Blocks.COPPER_BLOCK)
        .properties(BlockBehaviour.Properties::noOcclusion)
        .item(BlockItem::new)
        .creativeTab(CreativeModeTabs.REDSTONE_BLOCKS, TabPlacement.after(Blocks.OBSERVER))
        .build()
        .renderType(() -> RenderType::cutout)
        .register();

    private static <T extends Block, P> UnaryOperator<BlockBuilder<T, P>> rubyGlassBlock(boolean pane) {
        return b -> b.copyProperties(() -> pane ? Blocks.GLASS_PANE : Blocks.GLASS)
            .properties(p -> {
                p.mapColor(MapColor.FIRE)
                    .explosionResistance(100F)
                    .noOcclusion()
                    .isRedstoneConductor(StatePredicates::never);
                if(pane) p.isViewBlocking(StatePredicates::never);
            })
            .renderType(() -> RenderType::translucent);
    }

    public static void register() {
        BLOCKS.register();
    }

}