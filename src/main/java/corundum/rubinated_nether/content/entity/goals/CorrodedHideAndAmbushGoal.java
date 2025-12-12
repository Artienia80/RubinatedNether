package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CorrodedHideAndAmbushGoal extends Goal {
    private final BronzeEntity entity;
    private Player target;

    private int state = 0;
    private int stateTicks = 0;
    private Vec3 ambushTargetPos;

    private static final double MOVE_SPEED = 0.3;
    private static final float ATTACK_RANGE = 1.5f;
    private boolean hasAttacked = false;

    public CorrodedHideAndAmbushGoal(BronzeEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (entity.getAmbushCooldown() > 0) return false;
        if (entity.getTarget() instanceof Player player && entity.getTarnishLevel() == 2) {
            if (entity.distanceTo(player) < 10 && entity.hasLineOfSight(player)) {
                this.target = player;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return state != 0 && entity.getTarnishLevel() == 2 && target != null && !entity.isInWater() && target.isAlive();
    }

    @Override
    public void start() {
        state = 1;
        stateTicks = 0;
        entity.setBurrowed(true);
        entity.getNavigation().stop();
        entity.setNoCollision(true);

        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, (byte) 76);
        }
    }

    @Override
    public void tick() {
        if(entity.getTarnishLevel() != 2) stop();
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
                // Calculate horizontal direction only (ignore Y component to prevent flying)
                Vec3 entityPosFlat = new Vec3(entity.getX(), 0, entity.getZ());
                Vec3 targetPosFlat = new Vec3(ambushTargetPos.x, 0, ambushTargetPos.z);
                Vec3 horizontalDirection = targetPosFlat.subtract(entityPosFlat).normalize();

                // Keep entity at ground level while moving
                double currentY = entity.getY();
                entity.setDeltaMovement(horizontalDirection.x * MOVE_SPEED, 0, horizontalDirection.z * MOVE_SPEED);

                // Force entity to stay at the same Y level (underground)
                entity.setPos(entity.getX(), currentY, entity.getZ());

                if (entity.level() instanceof ServerLevel server) {
                    BlockPos below = entity.blockPosition().below();
                    BlockState blockstate = entity.level().getBlockState(below);
                    ParticleOptions dust = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);

                    server.sendParticles(dust, entity.getX(), entity.getY() + 0.1, entity.getZ(), 4, 0.2, 0.05, 0.2, 0.02);
                }

                // Check horizontal distance only (changed from 1.1 to 0.5)
                double horizontalDistSqr = entityPosFlat.distanceToSqr(targetPosFlat);
                if (horizontalDistSqr < 0.25) { // 0.5 * 0.5 = 0.25
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
                entity.setDeltaMovement(Vec3.ZERO);
                if (!entity.level().isClientSide) {
                    entity.level().broadcastEntityEvent(entity, (byte) 81);
                }
                if (stateTicks == 15 && !hasAttacked) {
                    hasAttacked = true;

                    boolean shouldDamage = entity.distanceToSqr(target) < ATTACK_RANGE;
                    if (shouldDamage) {
                        if (entity.level() instanceof ServerLevel server) {
                            server.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1, target.getZ(), 10, 0.3, 0.2, 0.3, 0.1);
                        }
                        target.hurt(entity.damageSources().mobAttack(entity), 10.0F); // Changed from 6.0F to 10.0F
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
        entity.setNoCollision(false);

        if (!entity.level().isClientSide) {
            entity.level().broadcastEntityEvent(entity, (byte) 87);
        }
    }
}