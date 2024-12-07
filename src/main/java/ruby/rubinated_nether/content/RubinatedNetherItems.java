package ruby.rubinated_nether.content;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ruby.rubinated_nether.RubinatedNether;

public class RubinatedNetherItems {
	// Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RubinatedNether.MODID);

	// Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
	public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
	.alwaysEdible().nutrition(1).saturationModifier(2f).build()));
}
