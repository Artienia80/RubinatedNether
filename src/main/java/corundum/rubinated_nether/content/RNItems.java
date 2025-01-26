package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.BronzeShotItem;
import corundum.rubinated_nether.content.items.RubyLensItem;
import corundum.rubinated_nether.data.registries.RNJukeboxSongs;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RubinatedNether.MODID);

	public static final DeferredItem<Item> RUBY_ITEM = basicItem("ruby");
	public static final DeferredItem<Item> MOLTEN_RUBY_ITEM = basicItem("molten_ruby");
	public static final DeferredItem<Item> RUBY_SHARD_ITEM = basicItem("ruby_shard");
	public static final DeferredItem<Item> MOLTEN_RUBY_NUGGET_ITEM = basicItem("molten_ruby_nugget");

	public static final DeferredItem<Item> RUBY_LENS = ITEMS.register(
		"ruby_lens", 
		() -> new RubyLensItem(new Item.Properties())
	);

	public static final DeferredItem<Item> MUSIC_DISC_SHIMMER = ITEMS.registerSimpleItem(
		"music_disc_shimmer", 
		new Item.Properties()
			.stacksTo(1)
			.rarity(Rarity.RARE)
			.jukeboxPlayable(RNJukeboxSongs.SHIMMER)
	);

	public static final DeferredItem<BlockItem> FROSTED_ICE = ITEMS.registerSimpleBlockItem(
		"frosted_ice", 
		() -> Blocks.FROSTED_ICE
	);

	public static final DeferredItem<BlockItem> POWDER_SNOW = ITEMS.registerSimpleBlockItem(
		"powder_snow", 
		() -> Blocks.POWDER_SNOW
	);

	public static final DeferredItem<Item> BRONZE_ROD = basicItem("bronze_rod");
	public static final DeferredItem<Item> BRONZE_SCRAP = basicItem("bronze_scrap");

	public static final DeferredItem<Item> BRONZE_SHOT = ITEMS.register(
		"bronze_shot",
		() -> new BronzeShotItem(new Item.Properties())
	);

	public static DeferredItem<Item> basicItem(String name) {
		return ITEMS.registerSimpleItem(
			name, 
			new Item.Properties()
		);
	}
}
