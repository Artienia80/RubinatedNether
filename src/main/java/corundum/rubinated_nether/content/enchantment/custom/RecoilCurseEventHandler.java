package corundum.rubinated_nether.content.enchantment.custom;

import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber
public class RecoilCurseEventHandler {

    @SubscribeEvent
    public static void onTridentThrown(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ThrownTrident trident && !event.getLevel().isClientSide()) {
            if (!(trident.getOwner() instanceof LivingEntity thrower)) {
                return;
            }

            ItemStack tridentStack = trident.getPickupItemStackOrigin();

            int recoilLevel = EnchantmentHelper.getItemEnchantmentLevel(
                    event.getLevel().holderOrThrow(RNEnchantments.RECOIL_CURSE),
                    tridentStack
            );

            if (recoilLevel > 0) {
                Vec3 tridentMotion = trident.getDeltaMovement().normalize();
                double recoilStrength = 2;
                applyRecoil(thrower, recoilStrength, -tridentMotion.x, -tridentMotion.z, -tridentMotion.y);

                if (thrower instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket(serverPlayer));
                }
            }
        }
    }

    private static void applyRecoil(LivingEntity entity, double strength, double x, double z, double y) {
        strength *= 1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (strength <= 0) return;

        entity.hasImpulse = true;
        Vec3 currentMotion = entity.getDeltaMovement();

        double horizontalLength = Math.sqrt(x * x + z * z);
        if (horizontalLength < 9.999999747378752E-6) {
            x = (Math.random() - Math.random()) * 0.01;
            z = (Math.random() - Math.random()) * 0.01;
            horizontalLength = Math.sqrt(x * x + z * z);
        }

        double scale = strength / horizontalLength;
        Vec3 recoilVector = new Vec3(x * scale, 0.0, z * scale);

        entity.setDeltaMovement(
                currentMotion.x + recoilVector.x,
                Math.max(currentMotion.y, 0.4 + Math.abs(y) * strength * 0.3),
                currentMotion.z + recoilVector.z
        );
    }
}