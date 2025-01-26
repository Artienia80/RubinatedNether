package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNArmorMaterials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorMaterial.Layer;
import net.minecraft.world.level.Level;

public class RubyLensItem extends ArmorItem {
	public RubyLensItem(Properties properties) {
		super(RNArmorMaterials.RUBY_LENS_MATERIAL, Type.HELMET, properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		return swapWithEquipmentSlot(this, level, player, usedHand);
	}

	@Override
	public EquipmentSlot getEquipmentSlot() {
		return EquipmentSlot.HEAD;
	}

	@Override
	public ResourceLocation getArmorTexture(
		ItemStack stack, 
		Entity entity, 
		EquipmentSlot slot, 
		Layer layer,
		boolean innerModel
	) {
		return null;
	}
}
