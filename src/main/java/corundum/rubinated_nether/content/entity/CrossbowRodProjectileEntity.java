package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.CrossbowRodType;
import corundum.rubinated_nether.content.RNEntityCreator;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import java.util.Optional;
import java.util.function.Function;

public class CrossbowRodProjectileEntity extends AbstractArrow {
    private static final EntityDataAccessor<Integer> ROD_TYPE =
            SynchedEntityData.defineId(CrossbowRodProjectileEntity.class, EntityDataSerializers.INT);

    private static final double BRONZE_BASE_DAMAGE = 4.0;
    private static final float BREEZE_RADIUS = 3.0F;
    private static final float BLAZE_POWER = 1.0F;

    public CrossbowRodProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public CrossbowRodProjectileEntity(Level level, LivingEntity shooter, ItemStack weapon, ItemStack rod, CrossbowRodType type, boolean crit) {
        super(
                RNEntityCreator.CROSSBOW_ROD.get(),
                shooter.getX(),
                shooter.getEyeY() - 0.1F,
                shooter.getZ(),
                level,
                rod.copyWithCount(1),
                weapon
        );
        this.setOwner(shooter);
        this.setRodType(type);
        this.setCritArrow(crit);
        if (type == CrossbowRodType.BRONZE) {
            this.setBaseDamage(BRONZE_BASE_DAMAGE);
            this.pickup = rod.has(DataComponents.INTANGIBLE_PROJECTILE) ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
        } else {
            this.pickup = Pickup.DISALLOWED;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ROD_TYPE, 0);
    }

    public CrossbowRodType getRodType() {
        return CrossbowRodType.byId(this.entityData.get(ROD_TYPE));
    }

    public void setRodType(CrossbowRodType type) {
        this.entityData.set(ROD_TYPE, type.ordinal());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("RodType", this.getRodType().name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        CrossbowRodType type = CrossbowRodType.byName(tag.getString("RodType"));
        if (type != null) {
            this.setRodType(type);
        }
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return this.getRodType() == CrossbowRodType.BREEZE || super.ignoreExplosion(explosion);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(RNItems.BRONZE_ROD.get());
    }

    @Override
    protected void onHit(HitResult result) {
        CrossbowRodType type = this.getRodType();
        if (type == CrossbowRodType.BRONZE) {
            super.onHit(result);
            return;
        }
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 pos = result.getLocation();
        if (result instanceof BlockHitResult blockHit) {
            pos = pos.add(Vec3.atLowerCornerOf(blockHit.getDirection().getNormal()).scale(0.1));
        }

        if (type == CrossbowRodType.BREEZE) {
            this.windBurst(serverLevel, pos);
        } else {
            this.fireBlast(serverLevel, pos);
        }
        this.discard();
    }

    private void windBurst(ServerLevel level, Vec3 pos) {
        ExplosionDamageCalculator calculator = new SimpleExplosionDamageCalculator(
                true,
                false,
                Optional.of(1.22F),
                BuiltInRegistries.BLOCK.getTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
        );
        level.explode(
                this,
                null,
                calculator,
                pos.x,
                pos.y,
                pos.z,
                BREEZE_RADIUS,
                false,
                Level.ExplosionInteraction.TRIGGER,
                ParticleTypes.GUST_EMITTER_LARGE,
                ParticleTypes.GUST_EMITTER_LARGE,
                SoundEvents.WIND_CHARGE_BURST
        );
    }

    private void fireBlast(ServerLevel level, Vec3 pos) {
        boolean fire = EventHooks.canEntityGrief(level, this.getOwner());
        level.explode(this, pos.x, pos.y, pos.z, BLAZE_POWER, fire, Level.ExplosionInteraction.MOB);
    }
}
