package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNItems {
	// Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RubinatedNether.MODID);

	public static final DeferredItem<Item> RUBY_ITEM = ITEMS.registerSimpleItem("ruby", new Item.Properties());
	public static final DeferredItem<Item> MOLTEN_RUBY_ITEM = ITEMS.registerSimpleItem("molten_ruby", new Item.Properties());
	public static final DeferredItem<Item> RUBY_SHARD_ITEM = ITEMS.registerSimpleItem("ruby_shard", new Item.Properties());
	public static final DeferredItem<Item> MOLTEN_RUBY_NUGGET_ITEM = ITEMS.registerSimpleItem("molten_ruby_nugget", new Item.Properties());

	public static final DeferredItem<Item> MUSIC_DISC_SHIMMER = ITEMS.registerSimpleItem("music_disc_shimmer", new Item.Properties().stacksTo(1)
			.rarity(Rarity.RARE)
			.jukeboxPlayable(RNJukeboxSongs.SHIMMER));

	public static final DeferredItem<BlockItem> FROSTED_ICE = ITEMS.registerSimpleBlockItem("frosted_ice", () -> Blocks.FROSTED_ICE);

	public static final DeferredItem<BlockItem> POWDER_SNOW = ITEMS.registerSimpleBlockItem("powder_snow", () -> Blocks.POWDER_SNOW);


}
