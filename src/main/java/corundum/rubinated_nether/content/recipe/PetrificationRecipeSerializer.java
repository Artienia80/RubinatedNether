package corundum.rubinated_nether.content.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PetrificationRecipeSerializer implements RecipeSerializer<PetrificationRecipe> {
	public static final MapCodec<PetrificationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			TagKey.codec(Registries.BLOCK).fieldOf("source").forGetter(PetrificationRecipe::getSource),
			BuiltInRegistries.BLOCK.byNameCodec().fieldOf("result").forGetter(PetrificationRecipe::getResultBlock)
	).apply(instance, PetrificationRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PetrificationRecipe> STREAM_CODEC =
			StreamCodec.composite(
					ResourceLocation.STREAM_CODEC.map(id -> TagKey.create(Registries.BLOCK, id), TagKey::location),
					PetrificationRecipe::getSource,
					ByteBufCodecs.registry(Registries.BLOCK),
					PetrificationRecipe::getResultBlock,
					PetrificationRecipe::new
			);

	@Override public MapCodec<PetrificationRecipe> codec() { return CODEC; }
	@Override public StreamCodec<RegistryFriendlyByteBuf, PetrificationRecipe> streamCodec() { return STREAM_CODEC; }
}
