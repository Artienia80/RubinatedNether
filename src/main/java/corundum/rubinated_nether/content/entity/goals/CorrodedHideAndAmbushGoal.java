package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class CorrodedHideAndAmbushGoal extends Goal {
    private final BronzeEntity entity;
    private Player target;

    private int state = 0;
    private int stateTicks = 0;
    private Vec3 ambushTargetPos;

    private static final double MOVE_SPEED = 0.4;
    private static final double AMBUSH_MOVE_SPEED = 0.15;
    private static final float ATTACK_RANGE = 2.0f;
    private static final float EMERGE_ATTACK_RANGE = 2.5f;
    private boolean hasAttacked = false;
    private boolean hasEmergeAttacked = false;

    public CorrodedHideAndAmbushGoal(BronzeEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (entity.getAmbushCooldown() > 0 || entity.isBurrowed()) return false;
        if (entity.getTarget() instanceof Player player && entity.getTarnishLevel().equals(TarnishStage.CORRODED)) {
            if (entity.distanceTo(player) < 10 && entity.hasLineOfSight(player)) {
                this.target = player;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return state != 0 && entity.getTarnishLevel().equals(TarnishStage.CORRODED) && target != null && !entity.isInWater() && target.isAlive();
    }

    @Override
    public void start() {
        state = 1;
        stateTicks = 0;
        hasEmergeAttacked = false;
        entity.setBurrowed(true);
        entity.getNavigation().stop();

        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, (byte) 76);
        }
    }

    @Override
    public void tick() {
        if (!entity.getTarnishLevel().equals(TarnishStage.CORRODED)) stop();
        ambushTargetPos = target.position();
        switch (state) {
            case 1 -> {
                stateTicks++;
                if (stateTicks >= 20) {
                    state = 2;
                    stateTicks = 0;

                    if (entity.level() instanceof ServerLevel server) {
                        BlockPos below = entity.blockPosition().below();
                        BlockState blockstate = entity.level().getBlockState(below);
                        ParticleOptions dust = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);
                        server.sendParticles(dust, entity.getX(), entity.getY() + 0.1, entity.getZ(), 4, 0.2, 0.05, 0.2, 0.02);
                    }

                    if (!entity.level().isClientSide) {
                        entity.level().broadcastEntityEvent(entity, (byte) 79);
                    }
                }
            }

            case 2 -> {
                Vec3 entityPosFlat = new Vec3(entity.getX(), 0, entity.getZ());
                Vec3 targetPosFlat = new Vec3(ambushTargetPos.x, 0, ambushTargetPos.z);
                Vec3 horizontalDirection = targetPosFlat.subtract(entityPosFlat).normalize();

                double currentY = entity.getY();
                entity.setDeltaMovement(horizontalDirection.x * MOVE_SPEED, 0, horizontalDirection.z * MOVE_SPEED);
                entity.setPos(entity.getX(), currentY, entity.getZ());

                if (entity.level() instanceof ServerLevel server) {
                    BlockPos below = entity.blockPosition().below();
                    BlockState blockstate = entity.level().getBlockState(below);
                    ParticleOptions dust = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);
                    server.sendParticles(dust, entity.getX(), entity.getY() + 0.1, entity.getZ(), 4, 0.2, 0.05, 0.2, 0.02);
                }

                double horizontalDistSqr = entityPosFlat.distanceToSqr(targetPosFlat);
                if (horizontalDistSqr < 0.25) {
                    entity.setDeltaMovement(Vec3.ZERO);
                    state = 3;
                    stateTicks = 0;

                    if (!entity.level().isClientSide) {
                        entity.level().broadcastEntityEvent(entity, (byte) 81);
                    }
                    if (entity.level() instanceof ServerLevel server) {
                        BlockPos below = entity.blockPosition().below();
                        BlockState blockstate = entity.level().getBlockState(below);
                        ParticleOptions dust = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);
                        server.sendParticles(dust, entity.getX(), entity.getY() + 0.1, entity.getZ(), 4, 0.2, 0.05, 0.2, 0.02);
                    }
                }
            }

            case 3 -> {
                stateTicks++;

                Vec3 entityPosFlat = new Vec3(entity.getX(), 0, entity.getZ());
                Vec3 targetPosFlat = new Vec3(ambushTargetPos.x, 0, ambushTargetPos.z);
                Vec3 horizontalDirection = targetPosFlat.subtract(entityPosFlat).normalize();

                double currentY = entity.getY();
                entity.setDeltaMovement(horizontalDirection.x * AMBUSH_MOVE_SPEED, 0, horizontalDirection.z * AMBUSH_MOVE_SPEED);
                entity.setPos(entity.getX(), currentY, entity.getZ());

                if (!entity.level().isClientSide) {
                    entity.level().broadcastEntityEvent(entity, (byte) 81);
                }

                if (stateTicks == 1 && !hasEmergeAttacked) {
                    hasEmergeAttacked = true;

                    if (!entity.level().isClientSide) {
                        List<LivingEntity> nearbyEntities = entity.level().getEntitiesOfClass(
                                LivingEntity.class,
                                entity.getBoundingBox().inflate(EMERGE_ATTACK_RANGE),
                                e -> e != entity && e.isAlive() && !(e instanceof Player)
                        );

                        for (LivingEntity hit : nearbyEntities) {
                            if (entity.distanceTo(hit) > EMERGE_ATTACK_RANGE) continue;

                            hit.hurt(entity.damageSources().mobAttack(entity), 6.0F);

                            if (hit instanceof BronzeEntity && !hit.isAlive()) {
                                ItemStack disc = new ItemStack(RNItems.MUSIC_DISC_SINNER.get());
                                ItemEntity itemEntity = new ItemEntity(
                                        entity.level(),
                                        hit.getX(), hit.getY(), hit.getZ(),
                                        disc
                                );
                                entity.level().addFreshEntity(itemEntity);
                            }
                        }
                    }
                }

                if (stateTicks == 15 && !hasAttacked) {
                    hasAttacked = true;

                    boolean shouldDamage = entity.distanceToSqr(target) < ATTACK_RANGE;
                    if (shouldDamage) {
                        if (entity.level() instanceof ServerLevel server) {
                            server.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1, target.getZ(), 10, 0.3, 0.2, 0.3, 0.1);
                        }
                        target.hurt(entity.damageSources().mobAttack(entity), 10.0F);
                    }
                }

                if (stateTicks >= 25) {
                    entity.setBurrowed(false);
                    stop();
                }
            }

            case 4 -> {
            }
        }
    }

    @Override
    public void stop() {
        state = 0;
        stateTicks = 0;
        target = null;
        entity.setBurrowed(false);
        entity.setAmbushCooldown(30);
        hasAttacked = false;
        hasEmergeAttacked = false;

        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, (byte) 87);
        }
    }
}