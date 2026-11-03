package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.Comparator;
import java.util.List;

public class CrystallizedRodVolleyGoal extends Goal {
    private static final int MIN_DELAY = 80;
    private static final int MAX_DELAY = 220;
    private static final double RANGE = 24.0;
    private static final float BRONZE_TARGET_CHANCE = 0.35F;

    private final BronzeEntity entity;
    private LivingEntity target;
    private int delay;

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
            target = pickTarget();
            if (target == null) {
                return;
            }
        }

        if (entity.fireRodAt(target)) {
            target = null;
            delay = nextDelay();
        }
    }

    @Override
    public void stop() {
        target = null;
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
                e -> !e.isSpectator() && !e.isCreative() && !BronzeEntity.isSoothed(e) && entity.hasLineOfSight(e)
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
