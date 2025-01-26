package corundum.rubinated_nether.content.entity;

import com.google.common.collect.ImmutableList;

import corundum.rubinated_nether.content.RNEntities;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class BronzeShotProjectileEntity extends AbstractArrow {
	private float rotation;
	private ItemStack firedFromWeapon;

	private float weight = 0.05F;
	private boolean hasBounced = false;
	private boolean hasBeenDeflected = false;

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
		rotation += 2.5f;
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
		if (hasBounced && !hasBeenDeflected) 
			return;

		Entity entity = result.getEntity();
		entity.hurt(this.damageSources().thrown(this, this.getOwner()), 4);

		if (!hasBeenDeflected)
			this.setDeltaMovement(this.getDeltaMovement().multiply(-0.001, -0.3, -0.001));

		this.playSound(SoundEvents.ANVIL_PLACE, 1.0F, 1.0F);
		hasBounced = true;
		weight = 0;

		if (entity instanceof LivingEntity livingEntity) {
			// Apply potion effects

			var effects = ImmutableList.of(
				MobEffects.MOVEMENT_SLOWDOWN,
				MobEffects.BLINDNESS,
				MobEffects.BLINDNESS
			);

			for (var effect : effects) {
				livingEntity.addEffect(
					new MobEffectInstance(effect, 60, 5, true, false)
				);
			}
		}
	}

	@Override
	protected void onHitBlock(BlockHitResult result) {
		// Match AbstractArrow functionality
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

		if (hasBeenDeflected)
			this.discard();
	}

	@Override
	public void tick() {
		if (!hasBeenDeflected && weight < 0.2)
			weight += weight / 4;

		
		if (!this.inGround){
			this.setBaseDamage(this.getBaseDamage() + weight);

			setDeltaMovement(
				getDeltaMovement().x, 
				getDeltaMovement().y - (weight / 2), 
				getDeltaMovement().z
			);
		}

		super.tick();
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		double d0 = this.getBoundingBox().getSize() * 4.0;
		if (Double.isNaN(d0)) {
			d0 = 4.0;
		}

		d0 *= 64.0;
		return distance < d0 * d0;
	}

	@Override
	public boolean deflect(ProjectileDeflection deflection, Entity entity, Entity owner, boolean deflectedByPlayer) {
		deflection.deflect(this, entity, random);

		setOwner(owner);
		hasBeenDeflected = true;
		weight = 0;

		return true;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		compound.putFloat("weight", weight);
		compound.putBoolean("has_bounced", hasBounced);
		compound.putBoolean("has_been_deflected", hasBeenDeflected);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		if (compound.contains("weight"))
			this.weight = compound.getFloat("weight");
		
		if (compound.contains("has_bounced"))
			this.hasBounced = compound.getBoolean("has_bounced");

		if (compound.contains("has_been_deflected"))
			this.hasBeenDeflected = compound.getBoolean("has_been_deflected");
	}
}
