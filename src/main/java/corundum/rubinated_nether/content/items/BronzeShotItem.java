package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.entity.BronzeShotProjectileEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;

public class BronzeShotItem extends Item implements ProjectileItem {
	public BronzeShotItem(Properties properties) {
		super(properties);

		DispenserBlock.registerProjectileBehavior(this);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		var itemstack = player.getItemInHand(hand);

		level.playSound(
			null, 
			player.getX(), 
			player.getY(), 
			player.getZ(),
			SoundEvents.METAL_FALL, 
			SoundSource.NEUTRAL, 
			0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
		);
		
		if (!level.isClientSide) {
			var bronzeShotProjectile = new BronzeShotProjectileEntity(player, level);
			bronzeShotProjectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 0F);
			level.addFreshEntity(bronzeShotProjectile);
		}

		player.awardStat(Stats.ITEM_USED.get(this));
		if (!player.isCreative())
			itemstack.shrink(1);

		return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
	}

	@Override
	public Projectile asProjectile(
		Level level, 
		Position pos, 
		ItemStack stack, 
		Direction direction
	) {
		var proj = new BronzeShotProjectileEntity(level, pos);
		proj.addDeltaMovement(new Vec3(direction.getStepX(), direction.getStepY(), direction.getStepZ()));
		proj.pickup = AbstractArrow.Pickup.ALLOWED;

		return proj;
	}
}
