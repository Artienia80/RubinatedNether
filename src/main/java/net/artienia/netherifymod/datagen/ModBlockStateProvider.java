package net.artienia.netherifymod.datagen;

import net.artienia.netherifymod.NetherifyMod;
import net.artienia.netherifymod.block.ModBlocks;
import net.minecraft.client.model.Model;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NetherifyMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.RUBY_BLOCK);
        blockWithItem(ModBlocks.MOLTEN_RUBY_BLOCK);
        blockWithItem(ModBlocks.BLEEDING_OBSIDIAN);
        blockWithItem(ModBlocks.RUBY_GLASS);


        blockWithItem(ModBlocks.MOLTEN_RUBY_ORE);
        blockWithItem(ModBlocks.NETHER_RUBY_ORE);
        blockWithItem(ModBlocks.RUBINATED_BLACKSTONE);

        simpleBlockWithItem(ModBlocks.FREEZER.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/freezer")));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}