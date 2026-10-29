package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.utils.BrazierTracker;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
public abstract class BoatMixin extends VehicleEntity {
    @Unique
    private static final double rubinated_nether$LAVA_FLOAT_LIFT = 0.2;

    @Shadow
    private double waterLevel;

    @Shadow
    private Boat.Status status;

    @Shadow
    private Boat.Status oldStatus;

    protected BoatMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Unique
    private boolean rubinated_nether$hasPoweredRider() {
        for (Entity passenger : this.getPassengers()) {
            if (passenger instanceof LivingEntity living && living.hasEffect(RNEffects.BRAZIER_POWER)) return true;
        }
        return false;
    }

    @Unique
    private boolean rubinated_nether$canFloatOnLava() {
        if (!RNConfig.brazierPowerBoatLava) return false;
        if (rubinated_nether$hasPoweredRider()) return true;
        return BrazierTracker.anyInRange(this.level(), this.getX(), this.getY(), this.getZ(), RNConfig.brazierEffectRange);
    }

    public boolean canBoatInFluid(FluidState state) {
        if (state.is(FluidTags.LAVA) && rubinated_nether$canFloatOnLava()) return true;
        return state.supportsBoating((Boat) (Object) this);
    }

    @Override
    public boolean fireImmune() {
        if (super.fireImmune()) return true;
        if (rubinated_nether$hasPoweredRider()) return true;
        return RNConfig.brazierPowerLavaImmuneItems
                && BrazierTracker.anyInRange(this.level(), this.getX(), this.getY(), this.getZ(), RNConfig.brazierEffectRange);
    }

    @Inject(method = "floatBoat", at = @At("HEAD"))
    private void rubinated_nether$liftOnLava(CallbackInfo ci) {
        boolean transition = this.oldStatus == Boat.Status.IN_AIR;
        if (!transition && this.status == Boat.Status.IN_WATER && this.waterLevel > -Double.MAX_VALUE / 2 && this.isInLava()) {
            this.waterLevel += rubinated_nether$LAVA_FLOAT_LIFT;
        }
    }
}