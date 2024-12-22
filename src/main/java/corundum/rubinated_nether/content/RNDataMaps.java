package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.datamaps.Tarnishable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class RNDataMaps {
	public static final DataMapType<Block, Tarnishable> TARNISHABLES = DataMapType.builder(
		ResourceLocation.fromNamespaceAndPath(RubinatedNether.MODID, "tarnishables"),
		Registries.BLOCK, Tarnishable.CODEC
	)
	.synced(Tarnishable.TARNISHABLE_CODEC, false)
	.build();
}
