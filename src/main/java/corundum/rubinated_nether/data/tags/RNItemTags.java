package corundum.rubinated_nether.data.tags;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class RNItemTags extends ItemTagsProvider {
	public RNItemTags(
		PackOutput output, 
		CompletableFuture<HolderLookup.Provider> registries, 
		CompletableFuture<TagLookup<Block>> blockTags, 
		ExistingFileHelper helper
	) {
		super(output, registries, blockTags, RubinatedNether.MODID, helper);
	}

	@Override
	public void addTags(Provider provider) {
		this.tag(RNTags.Items.RUNES).add(
				RNItems.RUNE.asItem(),
				RNItems.GREED_RUNE.asItem(),
				RNItems.WRATH_RUNE.asItem(),
				RNItems.SLOTH_RUNE.asItem(),
				RNItems.GLUTTONY_RUNE.asItem(),
				RNItems.ENVY_RUNE.asItem(),
				RNItems.VAINGLORY_RUNE.asItem(),
				RNItems.PRIDE_RUNE.asItem(),
				RNItems.ACEDIA_RUNE.asItem(),
				RNItems.LUXURIA_RUNE.asItem(),
				RNItems.INSIDIAE_RUNE.asItem(),
				RNItems.SUPERBIA_RUNE.asItem(),
				RNItems.TRISTIA_RUNE.asItem(),
				RNItems.STUDIOSE_RUNE.asItem(),
				RNItems.ARDENTER_RUNE.asItem(),
				RNItems.NIMIS_RUNE.asItem(),
				RNItems.IRA_RUNE.asItem(),
				RNItems.INVIDIA_RUNE.asItem(),
				RNItems.GULA_RUNE.asItem(),
				RNItems.IGNAVIA_RUNE.asItem(),
				RNItems.KENODOXIA_RUNE.asItem(),
				RNItems.PHILARGYRIA_RUNE.asItem()
		);

		this.tag(RNTags.Items.RUBINATION_TOOL).addTag(Tags.Items.MINING_TOOL_TOOLS); //Included bc tag only has pickaxes

		// this.tag(RNTags.Items.RUBINATION_WEAPON).addTag(Tags.Items.MELEE_WEAPON_TOOLS);
		// Intentionally OMMITTED due to tag including Tridents and Mace.

		this.tag(RNTags.Items.RUBINATION_ARMOR).addTag(Tags.Items.ARMORS); //Included for backup
		this.tag(RNTags.Items.RUBINATION_BOW).addTag(Tags.Items.TOOLS_BOW); //Included for backup
		this.tag(RNTags.Items.RUBINATION_CROSSBOW).addTag(Tags.Items.TOOLS_CROSSBOW); //Included for backup
		this.tag(RNTags.Items.RUBINATION_TRIDENT).add(Items.TRIDENT); //Added for trident
		this.tag(RNTags.Items.RUBINATION_MACE).add(Items.MACE); //Added for mace

		this.tag(RNTags.Items.RUBINATION_TOOL).addTags(net.minecraft.tags.ItemTags.AXES);
		this.tag(RNTags.Items.RUBINATION_TOOL).add(
				Items.WOODEN_SHOVEL,
				Items.WOODEN_HOE,

				Items.STONE_SHOVEL,
				Items.STONE_HOE,

				Items.IRON_SHOVEL,
				Items.IRON_HOE,

				Items.GOLDEN_SHOVEL,
				Items.GOLDEN_HOE,

				Items.DIAMOND_SHOVEL,
				Items.DIAMOND_HOE,

				Items.NETHERITE_SHOVEL,
				Items.NETHERITE_HOE
		);

		this.tag(RNTags.Items.RUBINATION_WEAPON).addTags(net.minecraft.tags.ItemTags.AXES);
		this.tag(RNTags.Items.RUBINATION_WEAPON).add(
				Items.WOODEN_SWORD,
				Items.STONE_SWORD,
				Items.GOLDEN_SWORD,
				Items.IRON_SWORD,
				Items.DIAMOND_SWORD,
				Items.NETHERITE_SWORD
		);

		this.tag(RNTags.Items.RUBINATABLE)
				.addTag(RNTags.Items.RUBINATION_TOOL)
				.addTag(RNTags.Items.RUBINATION_ARMOR)
				.addTag(RNTags.Items.RUBINATION_WEAPON)
				.addTag(RNTags.Items.RUBINATION_BOW)
				.addTag(RNTags.Items.RUBINATION_CROSSBOW)
				.addTag(RNTags.Items.RUBINATION_TRIDENT)
				.addTag(RNTags.Items.RUBINATION_MACE);

		// Brazier fuel tags
		this.tag(RNTags.Items.SMALL_BRAZIER_FUEL).add(
				RNItems.MOLTEN_RUBY_NUGGET.asItem()
		);

		this.tag(RNTags.Items.STANDARD_BRAZIER_FUEL).add(
				RNItems.MOLTEN_RUBY.asItem()
		);

		this.tag(RNTags.Items.GREAT_BRAZIER_FUEL).add(
				RNBlocks.MOLTEN_RUBY_BLOCK.asItem()
		);
	}
}
