package corundum.rubinated_nether.content.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;

public record HookingCurseEffect() implements EnchantmentEntityEffect {
    public static final MapCodec<HookingCurseEffect> CODEC = MapCodec.unit(HookingCurseEffect::new);

    @Override
    public void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse enchantedItem, Entity target, Vec3 vec3) {
        if (!(enchantedItem.owner() instanceof LivingEntity livingAttacker)) return;
        if (!(target instanceof LivingEntity victim)) return;

        Vec3 attackerPos = livingAttacker.position();
        Vec3 victimPos = victim.position();

        Vec3 horizontalPull = new Vec3(attackerPos.x - victimPos.x, 0, attackerPos.z - victimPos.z);
        double distance = horizontalPull.length();

        if (distance < 0.01) return;

        Vec3 pullVector = horizontalPull.normalize().scale(0.5 + 0.1 * enchantmentLevel);

        Vec3 newMotion = victim.getDeltaMovement().add(pullVector);
        double maxSpeed = 0.7;
        if (newMotion.length() > maxSpeed) {
            newMotion = newMotion.normalize().scale(maxSpeed);
        }

        victim.setDeltaMovement(new Vec3(newMotion.x, 0, newMotion.z));
        victim.hurtMarked = true;
    }


    @Override
    public MapCodec<? extends EnchantmentEntityEffect> codec() {
        return CODEC;
    }
}