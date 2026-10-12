package corundum.rubinated_nether.data.tags;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
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

		this.tag(RNTags.Items.RUBINATION_RUNES).addTag(RNTags.Items.RUNES);


		this.tag(RNTags.Items.RUBINATION_TOOL).addTag(Tags.Items.MINING_TOOL_TOOLS);

		this.tag(RNTags.Items.RUBINATION_ARMOR).addTag(Tags.Items.ARMORS);
		this.tag(RNTags.Items.RUBINATION_BOW).addTag(Tags.Items.TOOLS_BOW);
		this.tag(RNTags.Items.RUBINATION_CROSSBOW).addTag(Tags.Items.TOOLS_CROSSBOW);
		this.tag(RNTags.Items.RUBINATION_TRIDENT).add(Items.TRIDENT);
		this.tag(RNTags.Items.RUBINATION_MACE).add(Items.MACE);

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

		this.tag(RNTags.Items.SMALL_BRAZIER_FUEL).add(
				RNItems.MOLTEN_RUBY_NUGGET.asItem()
		);

		this.tag(RNTags.Items.STANDARD_BRAZIER_FUEL).add(
				RNItems.MOLTEN_RUBY.asItem()
		);

		this.tag(RNTags.Items.GREAT_BRAZIER_FUEL).add(
				RNBlocks.MOLTEN_RUBY_BLOCK.asItem(),
				RNItems.MOLTEN_RUBY_BUCKET.asItem()
		);

		this.tag(RNTags.Items.OFFERING_BRAZIER_ITEM).add(
				RNItems.BRONZE_ROD.asItem()
		);

		this.tag(RNTags.Items.ALTAR_OFFERING_ITEM).add(
				RNItems.RITUAL_OFFERING.asItem()
		);

		this.tag(RNTags.Items.ALTAR_INSCRIPTION_ITEM).add(
				RNItems.WINDING_KEY.asItem()
		);

		this.tag(RNTags.Items.ALTAR_RUBINATION_ITEM).add(
				RNItems.COGWHEEL.asItem()
		);

		this.tag(ItemTags.TRIM_MATERIALS)
				.add(
						RNItems.BRONZE_SCRAP.asItem(),
						RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL.asItem());

		this.tag(ItemTags.TRIM_TEMPLATES)
				.add(
						RNItems.GREED_RUNE.asItem(),
						RNItems.GLUTTONY_RUNE.asItem(),
						RNItems.SLOTH_RUNE.asItem(),
						RNItems.WRATH_RUNE.asItem(),
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

		this.tag(RNTags.Items.SHRINE_STONE_CANDIDATE).add(
				Items.STONE,
				Items.DEEPSLATE,
				Items.BLACKSTONE,
				Items.ANDESITE,
				Items.DIORITE,
				Items.GRANITE,
				Items.TUFF
		);

		this.tag(RNTags.Items.SHRINE_STONE_STAIRS_CANDIDATE).add(
				Items.STONE_STAIRS,
				Items.BLACKSTONE_STAIRS,
				Items.ANDESITE_STAIRS,
				Items.DIORITE_STAIRS,
				Items.GRANITE_STAIRS,
				Items.TUFF_STAIRS
		);

		this.tag(RNTags.Items.SHRINE_STONE_SLAB_CANDIDATE).add(
				Items.STONE_SLAB,
				Items.BLACKSTONE_SLAB,
				Items.ANDESITE_SLAB,
				Items.DIORITE_SLAB,
				Items.GRANITE_SLAB,
				Items.TUFF_SLAB
		);

		this.tag(RNTags.Items.SHRINE_STONE_WALL_CANDIDATE).add(
				Items.BLACKSTONE_WALL,
				Items.ANDESITE_WALL,
				Items.DIORITE_WALL,
				Items.GRANITE_WALL,
				Items.TUFF_WALL
		);

		this.tag(RNTags.Items.COBBLED_SHRINE_STONE_CANDIDATE).add(
				Items.COBBLESTONE,
				Items.COBBLED_DEEPSLATE
		);

		this.tag(RNTags.Items.COBBLED_SHRINE_STONE_STAIRS_CANDIDATE).add(
				Items.COBBLESTONE_STAIRS,
				Items.COBBLED_DEEPSLATE_STAIRS
		);

		this.tag(RNTags.Items.COBBLED_SHRINE_STONE_SLAB_CANDIDATE).add(
				Items.COBBLESTONE_SLAB,
				Items.COBBLED_DEEPSLATE_SLAB
		);

		this.tag(RNTags.Items.COBBLED_SHRINE_STONE_WALL_CANDIDATE).add(
				Items.COBBLESTONE_WALL,
				Items.COBBLED_DEEPSLATE_WALL
		);

		this.tag(RNTags.Items.POLISHED_SHRINE_STONE_CANDIDATE).add(
				Items.SMOOTH_STONE,
				Items.POLISHED_DEEPSLATE,
				Items.POLISHED_BLACKSTONE,
				Items.POLISHED_ANDESITE,
				Items.POLISHED_DIORITE,
				Items.POLISHED_GRANITE,
				Items.POLISHED_TUFF
		);

		this.tag(RNTags.Items.POLISHED_SHRINE_STONE_STAIRS_CANDIDATE).add(
				Items.POLISHED_DEEPSLATE_STAIRS,
				Items.POLISHED_BLACKSTONE_STAIRS,
				Items.POLISHED_ANDESITE_STAIRS,
				Items.POLISHED_DIORITE_STAIRS,
				Items.POLISHED_GRANITE_STAIRS,
				Items.POLISHED_TUFF_STAIRS
		);

		this.tag(RNTags.Items.POLISHED_SHRINE_STONE_SLAB_CANDIDATE).add(
				Items.SMOOTH_STONE_SLAB,
				Items.POLISHED_DEEPSLATE_SLAB,
				Items.POLISHED_BLACKSTONE_SLAB,
				Items.POLISHED_ANDESITE_SLAB,
				Items.POLISHED_DIORITE_SLAB,
				Items.POLISHED_GRANITE_SLAB,
				Items.POLISHED_TUFF_SLAB
		);

		this.tag(RNTags.Items.POLISHED_SHRINE_STONE_WALL_CANDIDATE).add(
				Items.POLISHED_DEEPSLATE_WALL,
				Items.POLISHED_BLACKSTONE_WALL,
				Items.POLISHED_TUFF_WALL
		);

		// No vanilla blocks, this is for modcompat
		this.tag(RNTags.Items.SHRINE_STONE_PILLAR_CANDIDATE);

		this.tag(RNTags.Items.CHISELED_SHRINE_STONE_BRICKS_CANDIDATE).add(
				Items.CHISELED_STONE_BRICKS,
				Items.CHISELED_DEEPSLATE,
				Items.CHISELED_POLISHED_BLACKSTONE,
				Items.CHISELED_TUFF,
				Items.CHISELED_TUFF_BRICKS
		);

		this.tag(RNTags.Items.SHRINE_STONE_BRICKS_CANDIDATE).add(
				Items.STONE_BRICKS,
				Items.MOSSY_STONE_BRICKS,
				Items.CRACKED_STONE_BRICKS,
				Items.DEEPSLATE_BRICKS,
				Items.CRACKED_DEEPSLATE_BRICKS,
				Items.POLISHED_BLACKSTONE_BRICKS,
				Items.CRACKED_POLISHED_BLACKSTONE_BRICKS,
				Items.TUFF_BRICKS
		);

		this.tag(RNTags.Items.SHRINE_STONE_BRICKS_STAIRS_CANDIDATE).add(
				Items.STONE_BRICK_STAIRS,
				Items.MOSSY_STONE_BRICK_STAIRS,
				Items.DEEPSLATE_BRICK_STAIRS,
				Items.POLISHED_BLACKSTONE_BRICK_STAIRS,
				Items.TUFF_BRICK_STAIRS
		);

		this.tag(RNTags.Items.SHRINE_STONE_BRICKS_SLAB_CANDIDATE).add(
				Items.STONE_BRICK_SLAB,
				Items.MOSSY_STONE_BRICK_SLAB,
				Items.DEEPSLATE_BRICK_SLAB,
				Items.POLISHED_BLACKSTONE_BRICK_SLAB,
				Items.TUFF_BRICK_SLAB
		);

		this.tag(RNTags.Items.SHRINE_STONE_BRICKS_WALL_CANDIDATE).add(
				Items.STONE_BRICK_WALL,
				Items.MOSSY_STONE_BRICK_WALL,
				Items.DEEPSLATE_BRICK_WALL,
				Items.POLISHED_BLACKSTONE_BRICK_WALL,
				Items.TUFF_BRICK_WALL
		);

		this.tag(RNTags.Items.SHRINE_STONE_TILES_CANDIDATE).add(
				Items.DEEPSLATE_TILES,
				Items.CRACKED_DEEPSLATE_TILES
		);

		this.tag(RNTags.Items.SHRINE_STONE_TILES_STAIRS_CANDIDATE).add(
				Items.DEEPSLATE_TILE_STAIRS
		);

		this.tag(RNTags.Items.SHRINE_STONE_TILES_SLAB_CANDIDATE).add(
				Items.DEEPSLATE_TILE_SLAB
		);

		this.tag(RNTags.Items.SHRINE_STONE_TILES_WALL_CANDIDATE).add(
				Items.DEEPSLATE_TILE_WALL
		);

		this.tag(RNTags.Items.VASES).add(
				RNBlocks.BRONZE_VASE.asItem(),
				RNBlocks.DISCOLORED_BRONZE_VASE.asItem(),
				RNBlocks.CORRODED_BRONZE_VASE.asItem(),
				RNBlocks.TARNISHED_BRONZE_VASE.asItem(),
				RNBlocks.CRYSTALLIZED_BRONZE_VASE.asItem()
		);
		this.tag(RNTags.Items.VASE_ENGRAVING_MATERIALS)
				.addTag(ItemTags.TRIM_MATERIALS)
				.add(
						RNItems.BRONZE_SCRAP.asItem(),
						RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL.asItem(),
						RNItems.RUBY.asItem(),
						RNItems.MOLTEN_RUBY.asItem()
				);
	}
}