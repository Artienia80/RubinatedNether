package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.world.surface.NetherGravelReplacer;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SurfaceSystem.class)
public abstract class SurfaceSystemMixin {

    @ModifyVariable(method = "buildSurface", at = @At("HEAD"), argsOnly = true)
    private SurfaceRules.RuleSource rubinated_nether$replaceNetherGravel(SurfaceRules.RuleSource original) {
        if (!RNConfig.replaceNetherGravelWithCinder) {
            return original;
        }
        return NetherGravelReplacer.apply(original);
    }
}
