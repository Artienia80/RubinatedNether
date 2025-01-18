package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.RNEntities;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class BronzeShotProjectileEntity extends AbstractArrow {
    private float rotation;
    private BlockState lastState;
    private ItemStack firedFromWeapon;

    public BronzeShotProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public BronzeShotProjectileEntity(LivingEntity shooter, Level level) {
        super(RNEntities.BRONZE_SHOT.get(), shooter, level, new ItemStack(RNItems.BRONZE_SHOT.get()), null);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(RNItems.BRONZE_SHOT.get());
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if (rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    public boolean isGrounded() {
        return inGround;
    }

    protected void doKnockback(LivingEntity entity, DamageSource damageSource) {
        double d0 = (double)(
                this.firedFromWeapon != null && this.level() instanceof ServerLevel serverlevel
                        ? EnchantmentHelper.modifyKnockback(serverlevel, this.firedFromWeapon, entity, damageSource, 0.0F)
                        : 0.0F
        );
        if (d0 > 0.0) {
            double d1 = Math.max(0.0, 1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(d0 * 0.6 * d1);
            if (vec3.lengthSqr() > 0.0) {
                entity.push(vec3.x, 0.1, vec3.z);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), 4);

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
        //This doesnt play idk why
        this.playSound(SoundEvents.ANVIL_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));


        if (entity instanceof LivingEntity livingEntity) {
            // Apply potion effects
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 5));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 5));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 1));
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        // Match AbstractArrow functionality
        this.lastState = this.level().getBlockState(result.getBlockPos());
        super.onHitBlock(result);
        Vec3 vec3 = result.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        ItemStack itemstack = this.getWeaponItem();
        if (this.level() instanceof ServerLevel serverlevel && itemstack != null) {
            this.hitBlockEnchantmentEffects(serverlevel, result, itemstack);
        }

        Vec3 vec31 = vec3.normalize().scale(0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        this.playSound(SoundEvents.ANVIL_LAND, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));

        this.inGround = true;
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setSoundEvent(SoundEvents.ANVIL_LAND);
    }
}
