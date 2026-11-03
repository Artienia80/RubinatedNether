package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class TarnishedStalkGoal extends Goal {
    private static final double SPEED_MODIFIER = 1.0;

    private final BronzeEntity entity;
    private int repathDelay;

    public TarnishedStalkGoal(BronzeEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private boolean hasValidTarget() {
        if (!entity.getTarnishLevel().equals(TarnishStage.TARNISHED)) return false;
        LivingEntity target = entity.getTarget();
        if (target == null || !target.isAlive() || BronzeEntity.isSoothed(target)) return false;
        return !(target instanceof Player player) || (!player.isCreative() && !player.isSpectator());
    }

    @Override
    public boolean canUse() {
        return hasValidTarget();
    }

    @Override
    public boolean canContinueToUse() {
        return hasValidTarget();
    }

    @Override
    public void start() {
        repathDelay = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = entity.getTarget();
        if (target == null) return;

        entity.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (--repathDelay <= 0) {
            repathDelay = 4 + entity.getRandom().nextInt(7);
            entity.getNavigation().moveTo(target, SPEED_MODIFIER);
        }
    }

    @Override
    public void stop() {
        entity.getNavigation().stop();
    }
}
