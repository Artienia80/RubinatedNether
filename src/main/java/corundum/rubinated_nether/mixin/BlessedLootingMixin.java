package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class BlessedLootingMixin {

    protected abstract void dropAllDeathLoot(DamageSource damageSource, ServerLevel p_level);

    private boolean doubleLoot = false;
    private boolean isDoublingLoot = false;

    @Inject(method = "die", at = @At("HEAD"))
    private void checkBlessedEffect(DamageSource damageSource, CallbackInfo ci) {
        LivingEntity thisEntity = (LivingEntity) (Object) this;

        doubleLoot = false;
        isDoublingLoot = false;

        if (thisEntity.getType() == net.minecraft.world.entity.EntityType.WITHER) {
            return;
        }

        if (damageSource.getEntity() instanceof LivingEntity killer) {
            if (killer.hasEffect(RNEffects.BLESSED)) {
                int remainingDuration = killer.getEffect(RNEffects.BLESSED).getDuration();
                float chance = Math.min(1.0f, remainingDuration / 384000.0f);

                if (thisEntity.level().getRandom().nextFloat() < chance) {
                    doubleLoot = true;
                }
            }
        }
    }

    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    private void doubleLootIfBlessed(ServerLevel p_level, DamageSource damageSource, CallbackInfo ci) {
        if (isDoublingLoot) {
            return;
        }

        if (doubleLoot) {
            isDoublingLoot = true;

            LivingEntity thisEntity = (LivingEntity) (Object) this;
            try {
                java.lang.reflect.Method method = LivingEntity.class.getDeclaredMethod("dropAllDeathLoot", ServerLevel.class, DamageSource.class);
                method.setAccessible(true);
                method.invoke(thisEntity, p_level, damageSource);
            } catch (Exception e) {
            }

            isDoublingLoot = false;
        }
    }
}