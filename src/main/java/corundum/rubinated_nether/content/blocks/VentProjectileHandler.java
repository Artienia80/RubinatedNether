package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.BronzeShotProjectileEntity;
import corundum.rubinated_nether.content.entity.CrystallizedBronzeShotProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * A dispenser pointing into the BACK of a powered vent (both facing the same way) shoots its
 * projectile straight through the vent. The vent speeds the projectile up, and the faster it
 * is the harder it hits. Bronze shots fired this way can also break blocks.
 *
 * <p>Tuning (see the constants below), with a dispenser shot starting at ~1.1 blocks/tick and a
 * fully powered tarnished vent adding ~2.4: energy = SHOT_HARDNESS * speed^2, cost = 5 * hardness.
 * Glass (0.3) needs speed 1.22, dirt (0.5) 1.58, stone (1.5) 2.74, iron block (5) 5.0,
 * obsidian (50) 15.8 - basically out of reach.
 */
@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class VentProjectileHandler {

    private static final String FLAG = "rubinated_vent_shot";
    private static final String PENDING = "rubinated_vent_pending";
    private static final String PENDING_X = "rubinated_vent_vx";
    private static final String PENDING_Y = "rubinated_vent_vy";
    private static final String PENDING_Z = "rubinated_vent_vz";

    /** Damage dealt per block/tick of projectile speed. */
    private static final double DAMAGE_PER_SPEED = 1.5;

    /**
     * Innate hardness of a bronze shot. Impact energy is {@code SHOT_HARDNESS * speed^2}.
     * Raise it to let shots punch through tougher blocks.
     */
    private static final double SHOT_HARDNESS = 1.0;

    /**
     * Energy needed to break a block = {@code BREAK_COST_PER_HARDNESS * block hardness}.
     * Glass (0.3) and dirt (0.5) are cheap, stone (1.5) takes a fast shot, obsidian (50) is out of reach.
     */
    private static final double BREAK_COST_PER_HARDNESS = 5.0;

    /** Speed a shot keeps at least after smashing through a block. */
    private static final double MIN_EXIT_SPEED = 0.15;

    private VentProjectileHandler() {}

    /**
     * True if the dispenser at {@code dispenserPos} points straight into the back of a powered vent
     * that faces the same way, i.e. it is part of the vent railgun contraption.
     */
    public static boolean isVentContraption(Level level, BlockPos dispenserPos, BlockState dispenserState) {
        if (!dispenserState.is(Blocks.DISPENSER)) return false;
        Direction facing = dispenserState.getValue(DispenserBlock.FACING);
        BlockState front = level.getBlockState(dispenserPos.relative(facing));
        return front.getBlock() instanceof TarnishingBronzeVentBlock vent
                && front.getValue(TarnishingBronzeVentBlock.FACING) == facing
                && vent.isPushingActive(front);
    }

    @SubscribeEvent
    public static void onProjectileJoin(EntityJoinLevelEvent event) {
        Level level = event.getLevel();
        if (level.isClientSide() || event.loadedFromDisk()) return;
        if (!(event.getEntity() instanceof Projectile projectile)) return;

        // A dispenser spawns its projectile inside the block in front of it, i.e. inside the vent.
        BlockPos ventPos = projectile.blockPosition();
        BlockState ventState = level.getBlockState(ventPos);
        if (!(ventState.getBlock() instanceof TarnishingBronzeVentBlock vent)) return;
        if (!vent.isPushingActive(ventState)) return;

        Direction facing = ventState.getValue(TarnishingBronzeVentBlock.FACING);
        BlockState dispenser = level.getBlockState(ventPos.relative(facing.getOpposite()));
        if (!dispenser.is(Blocks.DISPENSER) || dispenser.getValue(DispenserBlock.FACING) != facing) return;

        Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());
        Vec3 velocity = projectile.getDeltaMovement();
        if (velocity.lengthSqr() < 1.0E-6 || velocity.dot(normal) <= 0) return;

        // Move the projectile to the vent's mouth, keeping its sideways offset.
        Vec3 centre = Vec3.atCenterOf(ventPos);
        Vec3 lateral = projectile.position().subtract(centre);
        lateral = lateral.subtract(normal.scale(lateral.dot(normal)));
        Vec3 mouth = centre.add(lateral).add(normal.scale(0.501));
        projectile.setPos(mouth.x, mouth.y, mouth.z);
        projectile.xo = mouth.x;
        projectile.yo = mouth.y;
        projectile.zo = mouth.z;

        // Speed it up by the vent's push.
        double boost = vent.getProjectileBoost(level, ventPos, ventState);
        double speed = velocity.length() + boost;
        projectile.setDeltaMovement(velocity.normalize().scale(speed));

        projectile.getPersistentData().putBoolean(FLAG, true);
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        Level level = projectile.level();
        if (level.isClientSide() || !projectile.getPersistentData().getBoolean(FLAG)) return;

        double speed = projectile.getDeltaMovement().length();
        HitResult hit = event.getRayTraceResult();

        if (hit instanceof EntityHitResult entityHit) {
            applySpeedDamage(projectile, entityHit, speed);
        } else if (hit instanceof BlockHitResult blockHit && projectile instanceof BronzeShotProjectileEntity) {
            if (tryBreakBlock(level, projectile, blockHit, speed)) {
                event.setCanceled(true);
            }
        }
    }

    /** Restores the velocity a shot keeps after breaking a block (see {@link #tryBreakBlock}). */
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide() || !(entity instanceof Projectile)) return;

        CompoundTag data = entity.getPersistentData();
        if (!data.getBoolean(PENDING)) return;

        data.remove(PENDING);
        entity.setDeltaMovement(data.getDouble(PENDING_X), data.getDouble(PENDING_Y), data.getDouble(PENDING_Z));
        entity.hurtMarked = true;
    }

    private static void applySpeedDamage(Projectile projectile, EntityHitResult hit, double speed) {
        if (projectile instanceof BronzeShotProjectileEntity || projectile instanceof CrystallizedBronzeShotProjectileEntity) {
            // Their hit code uses the arrow base damage as-is, so make it follow the speed.
            AbstractArrow arrow = (AbstractArrow) projectile;
            arrow.setBaseDamage(Math.max(arrow.getBaseDamage(), speed * DAMAGE_PER_SPEED));
        } else if (projectile instanceof ThrowableProjectile
                && !(projectile instanceof ThrownPotion)
                && !(projectile instanceof ThrownExperienceBottle)
                && !(projectile instanceof ThrownEnderpearl)
                && hit.getEntity() instanceof LivingEntity target) {
            // Vanilla arrows already scale with speed; thrown items (snowballs, eggs...) normally do nothing.
            target.hurt(projectile.damageSources().thrown(projectile, projectile.getOwner()), (float) (speed * DAMAGE_PER_SPEED));
        }
    }

    /**
     * Bronze shot vs block: the shot has {@code SHOT_HARDNESS * speed^2} energy and breaking a block costs
     * {@code BREAK_COST_PER_HARDNESS * hardness}. If it can pay, the block breaks, the leftover energy
     * becomes the new speed and the shot flies on. Otherwise it impacts like normal.
     */
    private static boolean tryBreakBlock(Level level, Projectile projectile, BlockHitResult hit, double speed) {
        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;

        float hardness = state.getDestroySpeed(level, pos);
        if (hardness < 0) return false; // bedrock and friends

        double energy = SHOT_HARDNESS * speed * speed;
        double cost = BREAK_COST_PER_HARDNESS * hardness;
        if (energy < cost) return false;

        double exitSpeed = Math.max(MIN_EXIT_SPEED, Math.sqrt((energy - cost) / SHOT_HARDNESS));
        Vec3 direction = projectile.getDeltaMovement().normalize();

        level.destroyBlock(pos, true, projectile);

        // Park the shot at the broken block for the rest of this tick so it can't skip over the next
        // block, and hand its remaining velocity back at the start of the next tick.
        Vec3 resume = direction.scale(exitSpeed);
        Vec3 at = hit.getLocation().add(direction.scale(0.05));
        projectile.setPos(at.x, at.y, at.z);
        projectile.setDeltaMovement(direction.scale(0.05));
        projectile.hurtMarked = true;

        CompoundTag data = projectile.getPersistentData();
        data.putBoolean(PENDING, true);
        data.putDouble(PENDING_X, resume.x);
        data.putDouble(PENDING_Y, resume.y);
        data.putDouble(PENDING_Z, resume.z);
        return true;
    }
}
