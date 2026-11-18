package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.datamaps.RubinationBundle;
import corundum.rubinated_nether.content.datamaps.Tarnishable;
import corundum.rubinated_nether.content.datamaps.VaseEngravingMaterialData;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class RNDataMaps {
	public static final DataMapType<Block, Tarnishable> TARNISHABLES = DataMapType.builder(
		ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "tarnishables"),
		Registries.BLOCK, Tarnishable.CODEC
	)
	.synced(Tarnishable.CODEC, false)
	.build();

	public static final DataMapType<Item, VaseEngravingMaterialData> VASE_ENGRAVING_MATERIALS = DataMapType.builder(
		ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "vase_engraving_materials"),
		Registries.ITEM, VaseEngravingMaterialData.CODEC
	)
	.synced(VaseEngravingMaterialData.CODEC, false)
	.build();

	public static final DataMapType<Item, RubinationBundle> RUBINATION_BUNDLES = DataMapType.builder(
		ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "rubination_bundles"),
		Registries.ITEM, RubinationBundle.CODEC
	)
	.synced(RubinationBundle.CODEC, false)
	.build();
}
