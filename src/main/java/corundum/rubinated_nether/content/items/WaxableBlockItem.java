package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.blocks.bases.TarnishingBronze;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Map;

public class WaxableBlockItem extends BlockItem {
	public WaxableBlockItem(DeferredBlock<?> block, Item.Properties properties) {
		super(block.get(), properties);
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return "item." + RubinatedNether.MODID + "." + stack.toString().split(":")[1]; 
	}

	@Override
	public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
		// >:3c
	}

	public static String getWaxableItem(ItemLike og) {
		return "waxed_" + og.asItem().toString().split(":")[1];
	}
}
