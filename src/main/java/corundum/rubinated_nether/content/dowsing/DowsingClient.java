package corundum.rubinated_nether.content.dowsing;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class DowsingClient {
    public static final float MAX_CROSS = 45.0F;
    public static final float POINT_DEADZONE = 10.0F;
    public static final float FULL_CROSS_ANGLE = 60.0F;
    private static final float EASING = 0.3F;

    private static final Map<Integer, State> STATES = new HashMap<>();

    private DowsingClient() {
    }

    private static final class State {
        private BlockPos target;
        private boolean active;
        private boolean arrived;
        private float angle;
        private float prevAngle;
        private float renderAngle;
    }

    public static void setTarget(int entityId, boolean found, BlockPos pos) {
        STATES.computeIfAbsent(entityId, id -> new State()).target = found ? pos : null;
    }

    public static boolean isActive(Player player) {
        State state = STATES.get(player.getId());
        return state != null && state.active && state.target != null;
    }

    public static boolean isArrived(Player player) {
        State state = STATES.get(player.getId());
        return state != null && state.active && state.arrived;
    }

    public static float getAngle(Player player, float partialTick) {
        State state = STATES.get(player.getId());
        return state == null ? 0.0F : Mth.lerp(partialTick, state.prevAngle, state.angle);
    }

    public static void setRenderAngle(Player player, float angle) {
        State state = STATES.get(player.getId());
        if (state != null) {
            state.renderAngle = angle;
        }
    }

    public static float getRenderAngle(Player player) {
        State state = STATES.get(player.getId());
        return state == null ? 0.0F : state.renderAngle;
    }

    public static void tickAll(Collection<? extends Player> players) {
        Set<Integer> seen = new HashSet<>();
        for (Player player : players) {
            seen.add(player.getId());
            tick(player);
        }
        STATES.keySet().retainAll(seen);
    }

    public static void reset() {
        STATES.clear();
    }

    private static void tick(Player player) {
        boolean holding = DowsingRods.isHolding(player);
        State state = holding ? STATES.computeIfAbsent(player.getId(), id -> new State()) : STATES.get(player.getId());

        if (state == null) {
            return;
        }

        if (!holding) {
            state.active = false;
            state.arrived = false;
            state.angle = 0.0F;
            state.prevAngle = 0.0F;
            return;
        }

        state.active = true;
        state.arrived = isWithinArrival(player, state.target);
        state.prevAngle = state.angle;
        state.angle += (desiredAngle(player, state.target, state.arrived) - state.angle) * EASING;
    }

    private static boolean isWithinArrival(Player player, BlockPos target) {
        if (target == null) {
            return false;
        }

        double dx = target.getX() + 0.5D - player.getX();
        double dz = target.getZ() + 0.5D - player.getZ();

        return dx * dx + dz * dz <= DowsingRods.ARRIVAL_RADIUS * DowsingRods.ARRIVAL_RADIUS;
    }

    private static float desiredAngle(Player player, BlockPos target, boolean arrived) {
        if (arrived) {
            return MAX_CROSS;
        }

        if (target == null) {
            return 0.0F;
        }

        double dx = target.getX() + 0.5D - player.getX();
        double dz = target.getZ() + 0.5D - player.getZ();

        float bearing = (float) (Mth.atan2(-dx, dz) * Mth.RAD_TO_DEG);
        float relative = Mth.wrapDegrees(bearing - player.getYRot());

        float off = Mth.clamp((Math.abs(relative) - POINT_DEADZONE) / (FULL_CROSS_ANGLE - POINT_DEADZONE), 0.0F, 1.0F);

        return MAX_CROSS * off;
    }
}
