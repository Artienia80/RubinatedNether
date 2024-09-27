package net.artienia.rubinated_nether.datagen.blockstates;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.block.ModBlocks;
import net.artienia.rubinated_nether.block.RubyLaserBlock;
import net.artienia.rubinated_nether.block.custom.FreezerBlock;
import net.artienia.rubinated_nether.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ModModels extends FabricModelProvider {

    private static final ResourceLocation RUBY_LASER = RubinatedNether.id("block/ruby_laser");
    private static final ResourceLocation RUBY_LASER_ON = RubinatedNether.id("block/ruby_laser_on");
    private static final ResourceLocation RUBY_LASER_TINTED = RubinatedNether.id("block/ruby_laser_tinted");
    private static final ResourceLocation RUBY_LASER_TINTED_ON = RubinatedNether.id("block/ruby_laser_tinted_on");

    private static final ResourceLocation FREEZER = RubinatedNether.id("block/freezer");
    private static final ResourceLocation FREEZER_ON = RubinatedNether.id("block/freezer_on");

    private static final ResourceLocation RUBY_CHANDELIER = RubinatedNether.id("block/ruby_chandelier");

    private static final ResourceLocation RUBY_LAVA_LAMP = RubinatedNether.id("block/ruby_lava_lamp");

    public ModModels(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {

        // Block state only stuff
        this.generateBlockStates(generators.blockStateOutput);

        // Glass Blocks
        generators.createGlassBlocks(ModBlocks.RUBY_GLASS.get(), ModBlocks.RUBY_GLASS_PANE.get());
        generators.createGlassBlocks(ModBlocks.MOLTEN_RUBY_GLASS.get(), ModBlocks.MOLTEN_RUBY_GLASS_PANE.get());

        // Regular Blocks
        generators.createTrivialCube(ModBlocks.RUBY_BLOCK.get());
        generators.createTrivialCube(ModBlocks.NETHER_RUBY_ORE.get());
        generators.createTrivialCube(ModBlocks.MOLTEN_RUBY_ORE.get());
        generators.createTrivialCube(ModBlocks.RUBINATED_BLACKSTONE.get());
        generators.createTrivialCube(ModBlocks.BLEEDING_OBSIDIAN.get());
        generators.createLantern(ModBlocks.RUBY_LANTERN.get());
        generators.createRotatedPillarWithHorizontalVariant(ModBlocks.MOLTEN_RUBY_BLOCK.get(), TexturedModel.COLUMN,  TexturedModel.COLUMN_HORIZONTAL);

    }

    private void generateBlockStates(Consumer<BlockStateGenerator> output) {
        // Ruby Laser
        output.accept(MultiVariantGenerator.multiVariant(ModBlocks.RUBY_LASER.get())
            .with(PropertyDispatch.properties(RubyLaserBlock.FACING, RubyLaserBlock.POWER, RubyLaserBlock.TINTED)
                .generate((facing, power, tinted) -> Variant.variant()
                    .with(VariantProperties.X_ROT, xRotFromDirection(facing))
                    .with(VariantProperties.Y_ROT, yRotFromDirection(facing))
                    .with(VariantProperties.MODEL, tinted ? power > 0 ? RUBY_LASER_TINTED_ON : RUBY_LASER_TINTED : power > 0 ? RUBY_LASER_ON : RUBY_LASER)
                )
            ));

        // Freezer
        output.accept(MultiVariantGenerator.multiVariant(ModBlocks.FREEZER.get())
            .with(PropertyDispatch.properties(FreezerBlock.FACING, FreezerBlock.LIT)
                .generate((facing, lit) -> Variant.variant()
                    .with(VariantProperties.Y_ROT, yRotFromDirection(facing))
                    .with(VariantProperties.MODEL, lit ? FREEZER_ON : FREEZER))
            ));

        output.accept(BlockModelGenerators.createSimpleBlock(ModBlocks.RUBY_CHANDELIER.get(), RUBY_CHANDELIER));
        output.accept(BlockModelGenerators.createAxisAlignedPillarBlock(ModBlocks.RUBY_LAVA_LAMP.get(), RUBY_LAVA_LAMP));
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        generators.generateFlatItem(ModItems.RUBY.get(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.MOLTEN_RUBY.get(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.RUBY_SHARD.get(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.MOLTEN_RUBY_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModItems.RUBY_LENS.get(), ModelTemplates.FLAT_ITEM);
        generators.generateFlatItem(ModBlocks.RUBY_CHANDELIER.asItem(), ModelTemplates.FLAT_ITEM);
    }

    private static VariantProperties.Rotation xRotFromDirection(Direction facing) {
        return facing == Direction.DOWN ? VariantProperties.Rotation.R180 : facing.getAxis().isHorizontal() ? VariantProperties.Rotation.R90 : VariantProperties.Rotation.R0;
    }

    private static VariantProperties.Rotation yRotFromDirection(Direction facing) {
        return switch (facing) {
            case UP, DOWN, NORTH -> VariantProperties.Rotation.R0;
            case SOUTH -> VariantProperties.Rotation.R180;
            case WEST -> VariantProperties.Rotation.R270;
            case EAST -> VariantProperties.Rotation.R90;
        };
    }

}
