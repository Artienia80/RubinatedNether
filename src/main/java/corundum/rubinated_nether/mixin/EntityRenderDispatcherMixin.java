package corundum.rubinated_nether.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.utils.BrazierTracker;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "renderFlame", at = @At("HEAD"), cancellable = true)
    private void rubinated_nether$onRenderFlame(PoseStack poseStack, MultiBufferSource buffer, Entity entity, Quaternionf quaternion, CallbackInfo ci) {
        if (!RNConfig.brazierPowerDisableFireOverlay) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(RNEffects.BRAZIER_POWER)) {
            ci.cancel();
            return;
        }

        if (entity instanceof Player player
                && BrazierTracker.anyInRange(player.level(), player.getX(), player.getY(), player.getZ(), RNConfig.brazierEffectRange)) {
            ci.cancel();
            return;
        }

        if (RNConfig.brazierPowerLavaImmuneItems
                && entity instanceof ItemEntity itemEntity
                && BrazierTracker.anyInRange(itemEntity.level(), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), RNConfig.brazierEffectRange)) {
            ci.cancel();
        }
    }
}