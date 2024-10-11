package net.artienia.rubinated_nether.datagen.models;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.content.RNBlocks;
import net.artienia.rubinated_nether.content.block.ruby_laser.RubyLaserBlock;
import net.artienia.rubinated_nether.content.block.freezer.FreezerBlock;
import net.artienia.rubinated_nether.content.RNItems;
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

import static net.artienia.rubinated_nether.RubinatedNether.REGISTRIES;

public class RNModels extends FabricModelProvider {

    private static final ResourceLocation RUBY_LASER = RubinatedNether.id("block/ruby_laser");
    private static final ResourceLocation RUBY_LASER_ON = RubinatedNether.id("block/ruby_laser_on");
    private static final ResourceLocation RUBY_LASER_TINTED = RubinatedNether.id("block/ruby_laser_tinted");
    private static final ResourceLocation RUBY_LASER_TINTED_ON = RubinatedNether.id("block/ruby_laser_tinted_on");

    private static final ResourceLocation FREEZER = RubinatedNether.id("block/freezer");
    private static final ResourceLocation FREEZER_ON = RubinatedNether.id("block/freezer_on");

    private static final ResourceLocation RUBY_CHANDELIER = RubinatedNether.id("block/ruby_chandelier");

    private static final ResourceLocation RUBY_LAVA_LAMP = RubinatedNether.id("block/ruby_lava_lamp");

    public RNModels(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generators) {

        // Block state only stuff
        this.generateBlockStates(generators.blockStateOutput);

        // Glass Blocks
        generators.createGlassBlocks(RNBlocks.RUBY_GLASS.get(), RNBlocks.RUBY_GLASS_PANE.get());
        generators.createGlassBlocks(RNBlocks.MOLTEN_RUBY_GLASS.get(), RNBlocks.MOLTEN_RUBY_GLASS_PANE.get());

        generators.createLantern(RNBlocks.RUBY_LANTERN.get());
        generators.createRotatedPillarWithHorizontalVariant(RNBlocks.MOLTEN_RUBY_BLOCK.get(), TexturedModel.COLUMN,  TexturedModel.COLUMN_HORIZONTAL);

        // Regular Blocks (Automated)
        REGISTRIES.getAllEntries(Registries.BLOCK, RNBlocks.CUBE)
            .forEach(entry -> generators.createTrivialCube(entry.get()));
    }

    private void generateBlockStates(Consumer<BlockStateGenerator> output) {
        // Ruby Laser
        output.accept(MultiVariantGenerator.multiVariant(RNBlocks.RUBY_LASER.get())
            .with(PropertyDispatch.properties(RubyLaserBlock.FACING, RubyLaserBlock.POWER, RubyLaserBlock.TINTED)
                .generate((facing, power, tinted) -> Variant.variant()
                    .with(VariantProperties.X_ROT, xRotFromDirection(facing))
                    .with(VariantProperties.Y_ROT, yRotFromDirection(facing))
                    .with(VariantProperties.MODEL, tinted ? power > 0 ? RUBY_LASER_TINTED_ON : RUBY_LASER_TINTED : power > 0 ? RUBY_LASER_ON : RUBY_LASER)
                )
            ));

        // Freezer
        output.accept(MultiVariantGenerator.multiVariant(RNBlocks.FREEZER.get())
            .with(PropertyDispatch.properties(FreezerBlock.FACING, FreezerBlock.LIT)
                .generate((facing, lit) -> Variant.variant()
                    .with(VariantProperties.Y_ROT, yRotFromDirection(facing))
                    .with(VariantProperties.MODEL, lit ? FREEZER_ON : FREEZER))
            ));

        output.accept(BlockModelGenerators.createAxisAlignedPillarBlock(RNBlocks.RUBY_LAVA_LAMP.get(), RUBY_LAVA_LAMP));

        // Basic blockstates (automated)
        REGISTRIES.getAllEntries(Registries.BLOCK, RNBlocks.SINGLE_BLOCKSTATE)
            .forEach(entry -> output.accept(BlockModelGenerators.createSimpleBlock(
                entry.get(),
                new ResourceLocation(entry.getId().getNamespace(), "block/" + entry.getId().getPath())
            )));
    }

    @Override
    public void generateItemModels(ItemModelGenerators generators) {
        REGISTRIES.getAllEntries(Registries.ITEM, RNItems.GENERATE_FLAT_MODEL)
            .forEach(entry -> generators.generateFlatItem(entry.get(), ModelTemplates.FLAT_ITEM));
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
