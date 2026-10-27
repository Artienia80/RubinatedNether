package corundum.rubinated_nether.event;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class BrazierLavaSwimHandler {
    private static final double LAVA_DRAG = 0.5;
    private static final double WATER_VERTICAL_DRAG = 0.8;
    private static final double STEP_UP_SPEED = 0.3F;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        if (!RNConfig.brazierPowerLavaSwimming || !player.level().isClientSide || !player.isLocalPlayer()) {
            return;
        }
        if (!player.hasEffect(RNEffects.BRAZIER_POWER) || !player.isInLava()) {
            return;
        }
        if (player.isPassenger() || player.isSpectator() || player.isFallFlying() || player.getAbilities().flying) {
            return;
        }
        if (player.canStandOnFluid(player.level().getFluidState(player.blockPosition()))) {
            return;
        }

        Vec3 motion = player.getDeltaMovement();

        double gravity = player.getAttributeValue(Attributes.GRAVITY);
        if (motion.y <= 0.0 && player.hasEffect(MobEffects.SLOW_FALLING)) {
            gravity = Math.min(gravity, 0.01);
        }
        boolean hasGravity = !player.isNoGravity();
        double lavaGravity = hasGravity ? gravity / 4.0 : 0.0;
        double waterGravity = hasGravity && !player.isSprinting() ? gravity / 16.0 : 0.0;

        float horizontalDrag = player.isSprinting() ? 0.9F : 0.8F;
        float efficiency = (float) player.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);
        if (!player.onGround()) {
            efficiency *= 0.5F;
        }
        if (efficiency > 0.0F) {
            horizontalDrag += (0.54600006F - horizontalDrag) * efficiency;
        }
        if (player.hasEffect(MobEffects.DOLPHINS_GRACE)) {
            horizontalDrag = 0.96F;
        }
        double horizontalScale = horizontalDrag / LAVA_DRAG;

        double jumpThreshold = player.getEyeHeight() < 0.4F ? 0.0 : 0.4;
        boolean deep = player.getFluidTypeHeight(NeoForgeMod.LAVA_TYPE.value()) > jumpThreshold;

        double y;
        if (player.horizontalCollision && Math.abs(motion.y - STEP_UP_SPEED) < 1.0E-6) {
            y = motion.y;
        } else if (deep) {
            y = (motion.y + lavaGravity) / LAVA_DRAG * WATER_VERTICAL_DRAG - waterGravity;
        } else {
            y = motion.y + lavaGravity;
        }

        player.setDeltaMovement(motion.x * horizontalScale, y, motion.z * horizontalScale);
    }
}