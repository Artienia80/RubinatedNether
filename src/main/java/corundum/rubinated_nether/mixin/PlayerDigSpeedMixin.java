package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Player.class)
public class PlayerDigSpeedMixin {

    @ModifyVariable(method = "getDigSpeed", at = @At(value = "STORE"), ordinal = 0)
    private float allowNegativeMiningEfficiency(float f, BlockState blockState, BlockPos pos) {
        Player player = (Player) (Object) this;

        ItemStack heldItem = player.getMainHandItem();
        int curseLevel = EnchantmentHelper.getItemEnchantmentLevel(player.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(RNEnchantments.DEFICIENCY_CURSE), heldItem);

        if (curseLevel > 0) {
            float reduction = curseLevel;
            f = Math.max(0.1f, f - reduction);
        }

        return f;
    }
}