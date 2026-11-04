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
    private static final float DAMAGE_PER_DISEASE_LEVEL = 1.0F;
    private static final int MAX_AGE = 120;
    private static final int DISEASE_TICKS = 600;
    private static final int MAX_DISEASE_AMPLIFIER = 4;

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
            MobEffectInstance current = target instanceof LivingEntity livingTarget ? livingTarget.getEffect(RNEffects.BRONZE_DISEASED) : null;
            float damage = DAMAGE + (current == null ? 0 : current.getAmplifier() + 1) * DAMAGE_PER_DISEASE_LEVEL;
            target.hurt(this.damageSources().arrow(this, this.getOwner()), damage);
            if (target instanceof LivingEntity living) {
                int amplifier = current == null ? 0 : Math.min(current.getAmplifier() + 1, MAX_DISEASE_AMPLIFIER);
                int duration = current == null ? DISEASE_TICKS : current.getDuration() + DISEASE_TICKS;
                living.addEffect(new MobEffectInstance(RNEffects.BRONZE_DISEASED, duration, amplifier, false, true));
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
