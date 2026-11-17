package corundum.rubinated_nether.data.tags;

import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class RNEntityTags extends EntityTypeTagsProvider {
	public RNEntityTags(
		PackOutput output, 
		CompletableFuture<HolderLookup.Provider> lookupProvider, 
		ExistingFileHelper existingFileHelper
	) {
		super(output, lookupProvider, RubinatedNether.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {
//		tag(EntityTypeTags.REDIRECTABLE_PROJECTILE).add(
//			RNEntityCreator.BRONZE_SHOT.get()
//		);
		tag(RNTags.EntityTypes.SOUL_STARVED_SAND_IMMUNE).add(
			EntityType.WITHER_SKELETON,
			EntityType.WITHER
		);
	}
}
