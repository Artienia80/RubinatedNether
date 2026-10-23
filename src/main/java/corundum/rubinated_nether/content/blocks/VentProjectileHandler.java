package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.BronzeShotProjectileEntity;
import corundum.rubinated_nether.content.entity.CrystallizedBronzeShotProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.ArrayList;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class VentProjectileHandler {

    private static final String FLAG = "rubinated_vent_shot";
    private static final String PENDING = "rubinated_vent_pending";
    private static final String PENDING_X = "rubinated_vent_vx";
    private static final String PENDING_Y = "rubinated_vent_vy";
    private static final String PENDING_Z = "rubinated_vent_vz";

    private static final double DAMAGE_PER_SPEED = 1.5;

    private static final double SHOT_HARDNESS = 1.0;

    private static final double BREAK_COST_PER_HARDNESS = 5.0;

    private static final double MIN_EXIT_SPEED = 0.15;

    private static final double TNT_BASE_SPEED = 1.1;

    private VentProjectileHandler() {}

    public static void registerDispenserPulse() {
        for (var entry : new ArrayList<>(DispenserBlock.DISPENSER_REGISTRY.entrySet())) {
            Item item = entry.getKey();
            DispenseItemBehavior original = entry.getValue();
            DispenserBlock.DISPENSER_REGISTRY.put(item, wrapWithPulse(original));
        }

        if (DispenserBlock.DISPENSER_REGISTRY instanceof Object2ObjectMap<Item, DispenseItemBehavior> fast) {
            fast.defaultReturnValue(wrapWithPulse(fast.defaultReturnValue()));
        }
    }

    private static DispenseItemBehavior wrapWithPulse(DispenseItemBehavior original) {
        return (source, stack) -> {
            pulseVentBehind(source);
            return original.dispense(source, stack);
        };
    }

    private static void pulseVentBehind(BlockSource source) {
        BlockState dispenser = source.state();
        if (!dispenser.is(Blocks.DISPENSER)) return;
        Direction facing = dispenser.getValue(DispenserBlock.FACING);
        Level level = source.level();
        BlockPos ventPos = source.pos().relative(facing);
        BlockState ventState = level.getBlockState(ventPos);
        if (ventState.getBlock() instanceof TarnishingBronzeVentBlock vent
                && ventState.getValue(TarnishingBronzeVentBlock.FACING) == facing) {
            vent.pulseFromDispenser(level, ventPos, ventState);
        }
    }

    public static void registerTntBehavior() {
        DispenseItemBehavior vanilla = DispenserBlock.DISPENSER_REGISTRY.get(Items.TNT);
        DispenseItemBehavior vent = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                return dispenseTntThroughVent(source, stack);
            }
        };
        DispenserBlock.registerBehavior(Items.TNT, (source, stack) ->
                isVentContraption(source.level(), source.pos(), source.state())
                        ? vent.dispense(source, stack)
                        : vanilla.dispense(source, stack));
    }

    private static ItemStack dispenseTntThroughVent(BlockSource source, ItemStack stack) {
        Level level = source.level();
        BlockPos dispenserPos = source.pos();
        Direction facing = source.state().getValue(DispenserBlock.FACING);
        BlockPos ventPos = dispenserPos.relative(facing);
        BlockState ventState = level.getBlockState(ventPos);
        if (!(ventState.getBlock() instanceof TarnishingBronzeVentBlock vent)) return stack;

        PrimedTnt tnt = new PrimedTnt(level, ventPos.getX() + 0.5, ventPos.getY(), ventPos.getZ() + 0.5, null);

        BlockPos frontPos = ventPos.relative(facing);
        Vec3 front = Vec3.atCenterOf(frontPos);
        tnt.setPos(front.x, front.y - tnt.getBbHeight() / 2.0, front.z);
        if (!level.noCollision(tnt, tnt.getBoundingBox())) {
            tnt.setPos(ventPos.getX() + 0.5, ventPos.getY(), ventPos.getZ() + 0.5);
        }

        double speed = TNT_BASE_SPEED + vent.getProjectileBoost(level, ventPos, ventState);
        tnt.setDeltaMovement(Vec3.atLowerCornerOf(facing.getNormal()).scale(speed));

        level.addFreshEntity(tnt);
        level.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(null, GameEvent.ENTITY_PLACE, ventPos);
        stack.shrink(1);
        return stack;
    }

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

        Vec3 centre = Vec3.atCenterOf(ventPos);
        Vec3 lateral = projectile.position().subtract(centre);
        lateral = lateral.subtract(normal.scale(lateral.dot(normal)));
        Vec3 mouth = centre.add(lateral).add(normal.scale(0.501));
        projectile.setPos(mouth.x, mouth.y, mouth.z);
        projectile.xo = mouth.x;
        projectile.yo = mouth.y;
        projectile.zo = mouth.z;

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

            AbstractArrow arrow = (AbstractArrow) projectile;
            arrow.setBaseDamage(Math.max(arrow.getBaseDamage(), speed * DAMAGE_PER_SPEED));
        } else if (projectile instanceof ThrowableProjectile
                && !(projectile instanceof ThrownPotion)
                && !(projectile instanceof ThrownExperienceBottle)
                && !(projectile instanceof ThrownEnderpearl)
                && hit.getEntity() instanceof LivingEntity target) {

            target.hurt(projectile.damageSources().thrown(projectile, projectile.getOwner()), (float) (speed * DAMAGE_PER_SPEED));
        }
    }

    private static boolean tryBreakBlock(Level level, Projectile projectile, BlockHitResult hit, double speed) {
        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) return false;

        float hardness = state.getDestroySpeed(level, pos);
        if (hardness < 0) return false;

        double energy = SHOT_HARDNESS * speed * speed;
        double cost = BREAK_COST_PER_HARDNESS * hardness;
        if (energy < cost) return false;

        double exitSpeed = Math.max(MIN_EXIT_SPEED, Math.sqrt((energy - cost) / SHOT_HARDNESS));
        Vec3 direction = projectile.getDeltaMovement().normalize();

        level.destroyBlock(pos, true, projectile);

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