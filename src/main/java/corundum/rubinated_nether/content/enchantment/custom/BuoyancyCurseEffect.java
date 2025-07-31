package corundum.rubinated_nether.content.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;

public record BuoyancyCurseEffect() implements EnchantmentValueEffect {
    public static final MapCodec<BuoyancyCurseEffect> CODEC = MapCodec.unit(BuoyancyCurseEffect::new);

    @Override
    public float process(int level, RandomSource randomSource, float originalValue) {
        return originalValue * 0.5F; // Reduce all mace damage by 50%
    }

    @Override
    public MapCodec<BuoyancyCurseEffect> codec() {
        return CODEC;
    }
}