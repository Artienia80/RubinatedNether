package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean canSwimInFluidType(FluidType type) {
        if (type == NeoForgeMod.LAVA_TYPE.value()
                && RNConfig.brazierPowerLavaSwimming
                && this.hasEffect(RNEffects.BRAZIER_POWER)) {
            return true;
        }
        return super.canSwimInFluidType(type);
    }
}