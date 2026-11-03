package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;

public class CrystallizedRodVolleyGoal extends Goal {
    private static final int MIN_DELAY = 30;
    private static final int MAX_DELAY = 90;
    private static final int AIM_TICKS = 25;
    private static final int AIM_TIMEOUT = 60;
    private static final double RANGE = 24.0;
    private static final float BRONZE_TARGET_CHANCE = 0.35F;

    private final BronzeEntity entity;
    private LivingEntity target;
    private int delay;
    private int aimTicks;

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

        if (target == null || !isValid(target)) {
            abortAim();
            target = pickTarget();
            if (target == null) {
                return;
            }
        }

        entity.getLookControl().setLookAt(target, 40.0F, 40.0F);

        if (entity.getRodCount() > 1) {
            if (!entity.isRodAiming()) {
                if (!entity.beginRodAim(target)) {
                    return;
                }
                aimTicks = 0;
            }
            aimTicks++;
            if (aimTicks < AIM_TICKS) {
                return;
            }
        }

        if (entity.fireRodAt(target)) {
            target = null;
            aimTicks = 0;
            delay = nextDelay();
        } else if (entity.isRodAiming() && aimTicks > AIM_TIMEOUT) {
            abortAim();
            delay = MIN_DELAY;
        }
    }

    @Override
    public void stop() {
        abortAim();
        target = null;
    }

    private void abortAim() {
        if (entity.isRodAiming()) {
            entity.cancelRodAim();
        }
        aimTicks = 0;
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