package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CrystallizedOrbitGoal extends Goal {
    private static final double MIN_DISTANCE = 4.0;
    private static final double MAX_DISTANCE = 7.0;
    private static final double EDGE_BUFFER = 0.5;
    private static final double ACQUIRE_RANGE = 25.0;
    private static final double MOVE_SPEED = 0.3;
    private static final double SMOOTHING = 0.25;
    private static final double LAST_ROD_HEIGHT = 5.0;
    private static final double LAST_ROD_CLIMB_WEIGHT = 0.5;
    private static final double WANDER_WEIGHT = 0.6;
    private static final int MIN_WANDER_TICKS = 20;
    private static final int MAX_WANDER_TICKS = 60;
    private static final int ESCAPE_TICKS = 20;

    private final BronzeEntity entity;
    private Player target;
    private Vec3 wanderDirection = Vec3.ZERO;
    private int wanderTicks;
    private int escapeTicks;
    private double escapeVertical;

    public CrystallizedOrbitGoal(BronzeEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private static boolean isValidTarget(Entity candidate) {
        if (candidate.isSpectator() || BronzeEntity.isSoothed(candidate)) {
            return false;
        }
        return !(candidate instanceof Player player && player.isCreative());
    }

    @Override
    public boolean canUse() {
        if (!entity.isCrystallized() || entity.isPlummeting()) {
            return false;
        }
        Player nearest = entity.level().getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), ACQUIRE_RANGE, CrystallizedOrbitGoal::isValidTarget);
        if (nearest == null) {
            return false;
        }
        this.target = nearest;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return entity.isCrystallized()
                && !entity.isPlummeting()
                && target != null
                && target.isAlive()
                && isValidTarget(target)
                && entity.distanceTo(target) <= ACQUIRE_RANGE + 8.0;
    }

    @Override
    public void start() {
        entity.getNavigation().stop();
        escapeTicks = 0;
        pickWander();
    }

    @Override
    public void tick() {
        if (target == null) {
            return;
        }

        Vec3 offset = entity.getBoundingBox().getCenter().subtract(target.getBoundingBox().getCenter());
        double distance = offset.length();
        Vec3 radial = distance > 1.0E-4 ? offset.scale(1.0 / distance) : new Vec3(1.0, 0.0, 0.0);

        double radialPush = 0.0;
        if (distance < MIN_DISTANCE + EDGE_BUFFER) {
            radialPush = MIN_DISTANCE + EDGE_BUFFER - distance;
        } else if (distance > MAX_DISTANCE - EDGE_BUFFER) {
            radialPush = MAX_DISTANCE - EDGE_BUFFER - distance;
        }
        radialPush = Mth.clamp(radialPush * 1.5, -1.0, 1.0);

        boolean blocked = entity.horizontalCollision || entity.verticalCollision;
        if (blocked && escapeTicks <= 0) {
            escapeTicks = ESCAPE_TICKS;
            escapeVertical = entity.getRandom().nextBoolean() ? 1.0 : -1.0;
            pickWander();
        } else if (--wanderTicks <= 0) {
            pickWander();
        }

        Vec3 wander = wanderDirection.subtract(radial.scale(wanderDirection.dot(radial)));
        if (wander.lengthSqr() > 1.0E-4) {
            wander = wander.normalize();
        }
        double wanderWeight = WANDER_WEIGHT * (1.0 - Math.abs(radialPush));

        double vertical = 0.0;
        if (escapeTicks > 0) {
            escapeTicks--;
            vertical = escapeVertical * 0.5;
        } else if (entity.getRodCount() <= 1) {
            double heightDifference = entity.getY() - target.getY();
            vertical = Mth.clamp((LAST_ROD_HEIGHT - heightDifference) * 0.5, -LAST_ROD_CLIMB_WEIGHT, LAST_ROD_CLIMB_WEIGHT);
        }

        Vec3 desired = radial.scale(radialPush).add(wander.scale(wanderWeight)).add(0.0, vertical, 0.0);
        if (desired.lengthSqr() > 1.0) {
            desired = desired.normalize();
        }

        float speedScale = entity.getCrystalSpeedScale();
        Vec3 goalVelocity = desired.scale(MOVE_SPEED * speedScale);
        entity.setDeltaMovement(entity.getDeltaMovement().lerp(goalVelocity, SMOOTHING));

        if (speedScale >= 1.0F) {
            entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }
    }

    private void pickWander() {
        double angle = entity.getRandom().nextDouble() * Math.PI * 2.0;
        double y = entity.getRandom().nextDouble() * 2.0 - 1.0;
        double ring = Math.sqrt(1.0 - y * y);
        wanderDirection = new Vec3(Math.cos(angle) * ring, y, Math.sin(angle) * ring);
        wanderTicks = MIN_WANDER_TICKS + entity.getRandom().nextInt(MAX_WANDER_TICKS - MIN_WANDER_TICKS + 1);
    }

    @Override
    public void stop() {
        entity.setDeltaMovement(Vec3.ZERO);
        target = null;
        wanderDirection = Vec3.ZERO;
        wanderTicks = 0;
        escapeTicks = 0;
    }
}
