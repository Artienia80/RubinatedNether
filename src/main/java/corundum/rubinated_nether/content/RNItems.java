package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RNItems {
	// Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RubinatedNether.MODID);

	public static final DeferredItem<Item> RUBY_ITEM = ITEMS.registerSimpleItem("ruby", new Item.Properties());
	public static final DeferredItem<Item> MOLTEN_RUBY_ITEM = ITEMS.registerSimpleItem("molten_ruby", new Item.Properties());
}
