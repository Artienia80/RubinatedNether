package net.artienia.rubinated_nether.forge.datagen;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.block.ModBlocks;
import net.artienia.rubinated_nether.block.RubyLaserBlock;
import net.minecraft.data.PackOutput;


import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, RubinatedNether.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // blockWithItem(ModBlocks.RUBY_BLOCK);
        // blockWithItem(ModBlocks.BLEEDING_OBSIDIAN);
        // blockWithItem(ModBlocks.RUBY_GLASS);
        paneBlockWithRenderType(ModBlocks.RUBY_GLASS_PANE.get(), modLoc("block/ruby_glass"), modLoc("block/ruby_glass_pane_top"), "minecraft:translucent");
        paneBlockWithRenderType(ModBlocks.MOLTEN_RUBY_GLASS_PANE.get(), modLoc("block/molten_ruby_glass"), modLoc("block/molten_ruby_glass_pane_top"), "minecraft:translucent");

        ModelFile laserOn = new ModelFile.ExistingModelFile(RubinatedNether.id("block/ruby_laser_on"), this.models().existingFileHelper);
        ModelFile laserOff = new ModelFile.ExistingModelFile(RubinatedNether.id("block/ruby_laser"), this.models().existingFileHelper);
        directionalBlock(ModBlocks.RUBY_LASER.get(), state -> state.getValue(RubyLaserBlock.POWER) > 0 ? laserOn : laserOff);
        // blockWithItem(ModBlocks.MOLTEN_RUBY_ORE);
        // blockWithItem(ModBlocks.NETHER_RUBY_ORE);
        // blockWithItem(ModBlocks.RUBINATED_BLACKSTONE);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}