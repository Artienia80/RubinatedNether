package corundum.rubinated_nether.client;

import corundum.rubinated_nether.content.dowsing.DowsingClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IArmPoseTransformer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public final class DowsingArmPose {
    public static final IArmPoseTransformer TRANSFORMER = DowsingArmPose::apply;

    public static final IClientItemExtensions EXTENSIONS = new IClientItemExtensions() {
        @Override
        public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
            return entity instanceof Player player && DowsingClient.isActive(player)
                    ? RNClientEnumExtensions.dowsingArmPose()
                    : null;
        }
    };

    private DowsingArmPose() {
    }

    private static void apply(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        if (!(entity instanceof Player player)) {
            return;
        }

        float partialTick = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        DowsingClient.setRenderAngle(player, DowsingClient.getAngle(player, partialTick));

        ModelPart part = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        part.xRot = -Mth.HALF_PI + model.head.xRot;
        part.yRot = model.head.yRot + model.body.yRot;
        part.zRot = 0.0F;
    }
}
