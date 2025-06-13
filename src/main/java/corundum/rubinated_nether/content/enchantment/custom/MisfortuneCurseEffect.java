package corundum.rubinated_nether.content.enchantment.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;

public class MisfortuneCurseEffect implements EnchantmentValueEffect {
    public static final MapCodec<MisfortuneCurseEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.stable(new MisfortuneCurseEffect())
    );

    @Override
    public float process(int level, RandomSource random, float value) {
        return random.nextFloat() < 0.5f ? 0.0f : value;
    }

    @Override
    public MapCodec<MisfortuneCurseEffect> codec() {
        return CODEC;
    }
}