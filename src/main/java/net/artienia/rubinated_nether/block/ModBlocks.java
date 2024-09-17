package net.artienia.rubinated_nether.block;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.block.custom.FreezerBlock;
import net.artienia.rubinated_nether.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RubinatedNether.MOD_ID);

    public static final RegistryObject<Block> RUBY_BLOCK = registerBlock("ruby_block", () -> new Block(BlockBehaviour
    .Properties.copy(Blocks.NETHERITE_BLOCK)
    .mapColor(MapColor.FIRE)
    ));
    public static final RegistryObject<Block> MOLTEN_RUBY_BLOCK = registerBlock("molten_ruby_block", () -> new RotatedPillarBlock(BlockBehaviour
    .Properties.copy(Blocks.ANCIENT_DEBRIS)
    .mapColor(MapColor.FIRE)
    .lightLevel((p_220871_) -> {return 15;})
    ));

    public static final RegistryObject<Block> BLEEDING_OBSIDIAN = registerBlock("bleeding_obsidian", () -> new Block(BlockBehaviour
    .Properties.copy(Blocks.CRYING_OBSIDIAN)
    .mapColor(MapColor.FIRE)
    .pushReaction(PushReaction.BLOCK)
    ));

    public static final RegistryObject<StainedGlassBlock> RUBY_GLASS = registerBlock("ruby_glass", () -> new StainedGlassBlock(DyeColor.RED, BlockBehaviour
    .Properties.copy(Blocks.GLASS)
    .mapColor(MapColor.FIRE)
    .explosionResistance(100F)
    .noOcclusion()
    .isRedstoneConductor(ModBlocks::never)
    ));
    public static final RegistryObject<StainedGlassPaneBlock> RUBY_GLASS_PANE = registerBlock("ruby_glass_pane", () -> new StainedGlassPaneBlock(DyeColor.RED, Block
    .Properties.copy(Blocks.GLASS)
    .mapColor(MapColor.FIRE)
    .explosionResistance(100F)
    .noOcclusion()
    .isRedstoneConductor(ModBlocks::never)
    .isViewBlocking(ModBlocks::never)
    ));

    public static final RegistryObject<StainedGlassBlock> MOLTEN_RUBY_GLASS = registerBlock("molten_ruby_glass", () -> new StainedGlassBlock(DyeColor.ORANGE, BlockBehaviour
            .Properties.copy(Blocks.GLASS)
            .mapColor(MapColor.FIRE)
            .explosionResistance(100F)
            .noOcclusion()
            .isRedstoneConductor(ModBlocks::never)
            .lightLevel((p_220871_) -> {return 10;})
    ));
    public static final RegistryObject<StainedGlassPaneBlock> MOLTEN_RUBY_GLASS_PANE = registerBlock("molten_ruby_glass_pane", () -> new StainedGlassPaneBlock(DyeColor.ORANGE, Block
            .Properties.copy(Blocks.GLASS)
            .mapColor(MapColor.FIRE)
            .explosionResistance(100F)
            .noOcclusion()
            .isRedstoneConductor(ModBlocks::never)
            .isViewBlocking(ModBlocks::never)
            .lightLevel((p_220871_) -> {return 10;})
    ));


    public static final RegistryObject<Block> RUBY_CHANDELIER = registerBlock("ruby_chandelier", () -> new Chandelier(BlockBehaviour
    .Properties.copy(Blocks.COPPER_BLOCK)
    .mapColor(MapColor.FIRE)
    .noOcclusion()
    .isViewBlocking(ModBlocks::never)
    .lightLevel((p_220871_) -> {return 15;})
    ));
    public static final RegistryObject<Block> RUBY_LAVA_LAMP = registerBlock("ruby_lava_lamp", () -> new LavaLamp(BlockBehaviour
    .Properties.copy(Blocks.COPPER_BLOCK)
    .mapColor(MapColor.FIRE)
    .noOcclusion()
    .lightLevel((p_220871_) -> {return 15;})
    ));

    public static final RegistryObject<Block> MOLTEN_RUBY_ORE = registerBlock("molten_ruby_ore", () -> new MagmaXP(BlockBehaviour
    .Properties.copy(Blocks.MAGMA_BLOCK)
    .strength(2f)
    .requiresCorrectToolForDrops(), UniformInt.of(4, 8)
    ));
    public static final RegistryObject<Block> NETHER_RUBY_ORE = registerBlock("nether_ruby_ore", () -> new DropExperienceBlock(BlockBehaviour
    .Properties.copy(Blocks.NETHERRACK)
    .strength(2f).requiresCorrectToolForDrops(), UniformInt.of(3, 6)
    ));
    public static final RegistryObject<Block> RUBINATED_BLACKSTONE = registerBlock("rubinated_blackstone", () -> new DropExperienceBlock(BlockBehaviour
    .Properties.copy(Blocks.GILDED_BLACKSTONE)
    .strength(2f)
    .requiresCorrectToolForDrops()
    ));

    public static final RegistryObject<Block> FREEZER = registerBlock("freezer", () -> new FreezerBlock(BlockBehaviour
    .Properties.copy(Blocks.COPPER_BLOCK)
    .noOcclusion()
    ));
    public static final RegistryObject<Block> RUBY_LASER = registerBlock("ruby_laser", () -> new ObserverBlock(BlockBehaviour
    .Properties.copy(Blocks.COPPER_BLOCK)
    .noOcclusion()
    ));
    public static final RegistryObject<LanternBlock> RUBY_LANTERN = registerBlock("ruby_lantern", () -> new LanternBlock(BlockBehaviour.Properties.copy(Blocks.LANTERN)));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    public static boolean never(BlockState state, BlockGetter getter, BlockPos pos){
        return false;
    }
}
