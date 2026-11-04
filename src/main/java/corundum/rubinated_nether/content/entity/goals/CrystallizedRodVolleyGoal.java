package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class CrystallizedRodVolleyGoal extends Goal {
    private static final int MIN_DELAY = 30;
    private static final int MAX_DELAY = 90;
    private static final int TELEGRAPH_TICKS = 60;
    private static final int FIRE_TIMEOUT = 40;
    private static final double RANGE = 24.0;
    private static final float MIN_SPEED_SCALE = 0.5F;
    private static final double VELOCITY_SMOOTHING = 0.5;
    private static final float BRONZE_TARGET_CHANCE = 0.35F;

    private final BronzeEntity entity;
    private LivingEntity target;
    private int delay;
    private int telegraphTicks;
    private int holdTicks;
    private Vec3 lastTargetPosition;
    private Vec3 targetVelocity = Vec3.ZERO;

    public CrystallizedRodVolleyGoal(BronzeEntity entity) {
        this.entity = entity;
        this.delay = nextDelay();
    }

    private int nextDelay() {
        return MIN_DELAY + entity.getRandom().nextInt(MAX_DELAY - MIN_DELAY + 1);
    }

    @Override
    public boolean canUse() {
        return entity.isCrystallized() && !entity.isPlummeting() && entity.getRodCount() > 0;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        if (delay > 0) {
            delay--;
            return;
        }

        if (target != null && !isValid(target)) {
            cancelTelegraph();
        }

        if (target == null) {
            target = pickTarget();
            if (target == null) {
                return;
            }
        }

        trackTarget();
        Vec3 aim = entity.predictAim(target, targetVelocity);
        entity.getLookControl().setLookAt(aim.x, aim.y, aim.z, 90.0F, 90.0F);

        if (entity.getRodCount() > 1 && !entity.isRodAiming() && !entity.beginRodAim(target)) {
            return;
        }

        entity.setTelegraphing(true);

        if (telegraphTicks < TELEGRAPH_TICKS) {
            telegraphTicks++;
            float progress = (float) telegraphTicks / TELEGRAPH_TICKS;
            entity.setCrystalSpeedScale(1.0F - (1.0F - MIN_SPEED_SCALE) * progress);
            return;
        }

        entity.setCrystalSpeedScale(MIN_SPEED_SCALE);

        if (entity.fireRodAt(target, targetVelocity)) {
            cancelTelegraph();
            delay = nextDelay();
        } else if (++holdTicks > FIRE_TIMEOUT) {
            cancelTelegraph();
            delay = MIN_DELAY;
        }
    }

    @Override
    public void stop() {
        cancelTelegraph();
    }

    private void cancelTelegraph() {
        if (entity.isRodAiming()) {
            entity.cancelRodAim();
        }
        entity.setCrystalSpeedScale(1.0F);
        entity.setTelegraphing(false);
        target = null;
        telegraphTicks = 0;
        holdTicks = 0;
        lastTargetPosition = null;
        targetVelocity = Vec3.ZERO;
    }

    private void trackTarget() {
        Vec3 position = target.position();
        if (lastTargetPosition != null) {
            targetVelocity = targetVelocity.lerp(position.subtract(lastTargetPosition), VELOCITY_SMOOTHING);
        }
        lastTargetPosition = position;
    }

    private boolean isValid(LivingEntity candidate) {
        if (!candidate.isAlive() || entity.distanceTo(candidate) > RANGE || !entity.hasLineOfSight(candidate)) {
            return false;
        }
        if (candidate instanceof Player player) {
            return !player.isCreative() && !player.isSpectator() && !BronzeEntity.isSoothed(player);
        }
        if (candidate instanceof BronzeEntity bronze) {
            return bronze != entity && !bronze.isCrystallized() && entity.getRodCount() > 1;
        }
        return false;
    }

    private LivingEntity pickTarget() {
        Player player = entity.level().getNearestPlayer(
                entity.getX(), entity.getY(), entity.getZ(), RANGE,
                e -> !e.isSpectator() && !(e instanceof Player p && p.isCreative()) && !BronzeEntity.isSoothed(e) && entity.hasLineOfSight(e)
        );

        BronzeEntity bronze = null;
        if (entity.getRodCount() > 1) {
            List<BronzeEntity> bronzes = entity.level().getEntitiesOfClass(
                    BronzeEntity.class,
                    entity.getBoundingBox().inflate(RANGE),
                    other -> other != entity && !other.isCrystallized() && other.isAlive() && entity.hasLineOfSight(other)
            );
            bronze = bronzes.stream().min(Comparator.comparingDouble(entity::distanceToSqr)).orElse(null);
        }

        if (bronze == null) {
            return player;
        }
        if (player == null) {
            return bronze;
        }
        return entity.getRandom().nextFloat() < BRONZE_TARGET_CHANCE ? bronze : player;
    }
}
