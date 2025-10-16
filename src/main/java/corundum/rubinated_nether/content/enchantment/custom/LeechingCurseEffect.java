package corundum.rubinated_nether.content.enchantment.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record LeechingCurseEffect() implements EnchantmentEntityEffect {

    public static final MapCodec<LeechingCurseEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.stable(new LeechingCurseEffect())
    );

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 origin) {


        if (entity instanceof LivingEntity livingEntity) {
            if (level.random.nextFloat() < 0.5f) {
                float healAmount = 1.0f + level.random.nextFloat() * 4.0f;

                livingEntity.heal(healAmount);

                level.sendParticles(ParticleTypes.HEART,
                        livingEntity.getX(), livingEntity.getY() + 1.0, livingEntity.getZ(),
                        1, 0.5, 0.5, 0.5, 0.1);

            }
        }
    }

    @Override
    public @NotNull MapCodec<LeechingCurseEffect> codec() {
        return CODEC;
    }
}