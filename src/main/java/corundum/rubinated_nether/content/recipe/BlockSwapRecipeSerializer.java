package corundum.rubinated_nether.content.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

import java.util.function.BiFunction;

public class BlockSwapRecipeSerializer<T extends BlockSwapRecipe> implements RecipeSerializer<T> {

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public BlockSwapRecipeSerializer(BiFunction<Block, Block, T> factory) {
        this.codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("source").forGetter(BlockSwapRecipe::getSource),
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("result").forGetter(BlockSwapRecipe::getResultBlock)
        ).apply(instance, factory::apply));
        this.streamCodec = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.BLOCK),
                BlockSwapRecipe::getSource,
                ByteBufCodecs.registry(Registries.BLOCK),
                BlockSwapRecipe::getResultBlock,
                factory
        );
    }

    @Override
    public MapCodec<T> codec() { return codec; }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() { return streamCodec; }
}
