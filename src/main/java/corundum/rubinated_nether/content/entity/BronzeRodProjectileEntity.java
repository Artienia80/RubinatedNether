package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.RNEntityCreator;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * A bronze rod fired out of a dispenser-vent contraption. Behaves like a very heavy, very streamlined arrow:
 * it hits hard (damage scales with speed like an arrow), passes straight through entities, barely drops
 * or slows down in the air, and sticks into the first block it meets instead of breaking it.
 */
public class BronzeRodProjectileEntity extends AbstractArrow {

    /** Arrow base damage is 2. Final damage is roughly {@code speed * BASE_DAMAGE}. */
    private static final double BASE_DAMAGE = 4.0;

    /** The rod goes through this many entities + 1 before it is used up. */
    private static final byte PIERCE_LEVEL = 5;

    /** Vanilla arrows fall 0.05/tick; this cancels most of it. */
    private static final double GRAVITY_COMPENSATION = 0.045;

    /** Vanilla air drag is 0.99/tick; this brings it down to 0.9985/tick. */
    private static final double DRAG_COMPENSATION = (1.0 / 0.99) * 0.9985;

    /** Above this speed the rod flies as a crit (trail particles and bonus damage). */
    private static final double CRIT_SPEED = 2.5;

    public BronzeRodProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        this.init();
    }

    public BronzeRodProjectileEntity(Level level, Position pos) {
        super(
                RNEntityCreator.BRONZE_ROD.get(),
                pos.x(),
                pos.y(),
                pos.z(),
                level,
                new ItemStack(RNItems.BRONZE_ROD.get()),
                null
        );
        this.init();
    }

    private void init() {
        this.setBaseDamage(BASE_DAMAGE);
        this.setSoundEvent(SoundEvents.TRIDENT_HIT_GROUND);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(RNItems.BRONZE_ROD.get());
    }

    @Override
    public byte getPierceLevel() {
        return PIERCE_LEVEL;
    }

    @Override
    public void tick() {
        if (!this.inGround && !this.isInWater()) {
            Vec3 motion = this.getDeltaMovement().scale(DRAG_COMPENSATION);
            this.setDeltaMovement(motion.x, motion.y + GRAVITY_COMPENSATION, motion.z);
        }

        if (!this.level().isClientSide()) {
            this.setCritArrow(!this.inGround && this.getDeltaMovement().length() >= CRIT_SPEED);
        }

        super.tick();
    }
}
