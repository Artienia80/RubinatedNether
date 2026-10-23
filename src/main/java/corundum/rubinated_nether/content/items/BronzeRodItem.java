package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.blocks.VentProjectileHandler;
import corundum.rubinated_nether.content.entity.BronzeRodProjectileEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

/**
 * The bronze rod. A dispenser only fires it as a projectile when it points into the back of a powered
 * vent (the railgun contraption); anywhere else it just drops the rod like any other item.
 */
public class BronzeRodItem extends Item implements ProjectileItem {

	public BronzeRodItem(Properties properties) {
		super(properties);

		DispenseItemBehavior drop = new DefaultDispenseItemBehavior();
		DispenseItemBehavior shoot = new ProjectileDispenseBehavior(this);
		DispenserBlock.registerBehavior(this, (source, stack) ->
				VentProjectileHandler.isVentContraption(source.level(), source.pos(), source.state())
						? shoot.dispense(source, stack)
						: drop.dispense(source, stack));
	}

	@Override
	public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
		BronzeRodProjectileEntity rod = new BronzeRodProjectileEntity(level, pos);
		rod.pickup = AbstractArrow.Pickup.ALLOWED;
		return rod;
	}

	@Override
	public DispenseConfig createDispenseConfig() {
		// Streamlined and machined: no spread at all.
		return DispenseConfig.builder().uncertainty(0.0F).power(1.1F).build();
	}
}
