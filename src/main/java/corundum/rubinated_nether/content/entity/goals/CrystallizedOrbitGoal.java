package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class CrystallizedOrbitGoal extends Goal {
    private final BronzeEntity entity;
    private Player target;

    private Vec3 moveDirection = Vec3.ZERO;
    private int blocksToMove = 0;
    private int blocksMoved = 0;

    private static final double MIN_DISTANCE = 1.5;
    private static final double MAX_DISTANCE = 6.0;
    private static final double MOVE_SPEED = 0.32;

    public CrystallizedOrbitGoal(BronzeEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (entity.getTarnishLevel() != 4) return false;

        Player nearestPlayer = entity.level().getNearestPlayer(entity, MAX_DISTANCE + 5);
        if (nearestPlayer != null && !nearestPlayer.isCreative() && !nearestPlayer.isSpectator()) {
            this.target = nearestPlayer;
            return true;
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return entity.getTarnishLevel() == 4 && target != null && target.isAlive();
    }

    @Override
    public void start() {
        entity.getNavigation().stop();
        pickNewDirection();
    }

    @Override
    public void tick() {
        if (entity.getTarnishLevel() != 4 || target == null) {
            stop();
            return;
        }

        double distanceToPlayer = entity.distanceTo(target);

        boolean inDonut = distanceToPlayer >= MIN_DISTANCE && distanceToPlayer <= MAX_DISTANCE;

        if (!inDonut) {
            satisfyRequirements(distanceToPlayer);
        } else {
            if (blocksMoved >= blocksToMove) {
                pickNewDirection();
            } else {
                moveInDirection();
                blocksMoved++;
            }
        }
    }

    private void satisfyRequirements(double currentDistance) {
        Vec3 toPlayer = target.position().subtract(entity.position()).normalize();

        if (currentDistance < MIN_DISTANCE) {
            entity.setDeltaMovement(toPlayer.scale(-MOVE_SPEED));
        } else if (currentDistance > MAX_DISTANCE) {
            entity.setDeltaMovement(toPlayer.scale(MOVE_SPEED));
        }

        entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
    }

    private void pickNewDirection() {
        double angle = entity.getRandom().nextDouble() * Math.PI * 2;
        moveDirection = new Vec3(
                Math.cos(angle),
                0,
                Math.sin(angle)
        ).normalize();

        blocksToMove = 2 + entity.getRandom().nextInt(9);
        blocksMoved = 0;
    }

    private void moveInDirection() {
        entity.setDeltaMovement(moveDirection.scale(MOVE_SPEED));

        entity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
    }

    @Override
    public void stop() {
        entity.setDeltaMovement(Vec3.ZERO);
        target = null;
        moveDirection = Vec3.ZERO;
        blocksToMove = 0;
        blocksMoved = 0;
    }
}