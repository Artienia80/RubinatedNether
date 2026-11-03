package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNEntityCreator;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.CrystalConversion;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class CrystallizedBronzeRodProjectileEntity extends AbstractArrow {
    private static final float DAMAGE = 3.0F;
    private static final int MAX_AGE = 120;
    private static final int DISEASE_TICKS = 300;

    public CrystallizedBronzeRodProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        this.init();
    }

    public CrystallizedBronzeRodProjectileEntity(Level level, double x, double y, double z) {
        super(
                RNEntityCreator.CRYSTALLIZED_BRONZE_ROD.get(),
                x,
                y,
                z,
                level,
                new ItemStack(RNItems.CRYSTALLIZED_BRONZE_SHOT.get()),
                null
        );
        this.init();
    }

    private void init() {
        this.pickup = AbstractArrow.Pickup.DISALLOWED;
        this.setNoGravity(true);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(RNItems.CRYSTALLIZED_BRONZE_SHOT.get());
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        Entity resolved = entity instanceof BronzePart part ? part.parentMob : entity;
        if (resolved instanceof BronzeEntity bronze && bronze.isCrystallized()) {
            return false;
        }
        return super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        Entity target = entity instanceof BronzePart part ? part.parentMob : entity;
        BlockPos impact = BlockPos.containing(result.getLocation());

        if (target instanceof BronzeEntity bronze) {
            bronze.setTarnishLevel(TarnishStage.CRYSTALLIZED);
        } else {
            target.hurt(this.damageSources().arrow(this, this.getOwner()), DAMAGE);
            if (target instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(RNEffects.BRONZE_DISEASED, DISEASE_TICKS, 0, false, true));
            }
        }

        this.detonate(impact);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        this.detonate(result.getBlockPos());
    }

    private void detonate(BlockPos center) {
        if (this.level() instanceof ServerLevel serverLevel) {
            CrystalConversion.detonate(serverLevel, center, false);
        }
        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.tickCount > MAX_AGE) {
            this.discard();
        }
    }
}
