package net.artienia.rubinated_nether.world;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public abstract class RNLivingEntity extends LivingEntity {

    protected RNLivingEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    // Vanilla knockback override
    @Override
    public void knockback(double strength, double x, double z) {
        strength *= 1.0 - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
            this.hasImpulse = true;

            Vec3 vec3;
            for(vec3 = this.getDeltaMovement(); x * x + z * z < 9.999999747378752E-6; z = (Math.random() - Math.random()) * 0.01) {
                x = (Math.random() - Math.random()) * 0.01;
            }

            Vec3 vec31 = (new Vec3(x, 0.0, z)).normalize().scale(strength);
            this.setDeltaMovement(vec3.x / 2.0 - vec31.x,
                    this.onGround() ? Math.min(0.4, vec3.y / 2.0 + strength) : vec3.y,
                    vec3.z / 2.0 - vec31.z);
    }



    // Required implementations
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return List.of();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        // Implementation if needed
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
}