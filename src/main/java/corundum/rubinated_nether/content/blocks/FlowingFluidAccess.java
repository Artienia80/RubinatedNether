package corundum.rubinated_nether.content.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class FlowingFluidAccess {
    private static final Method GET_NEW_LIQUID = find("getNewLiquid", 3, false);
    private static final Method SPREAD = find("spread", 3, true);
    private static final Method SPREAD_DELAY = find("getSpreadDelay", 4, false);

    private FlowingFluidAccess() {}

    static FluidState getNewLiquid(FlowingFluid fluid, Level level, BlockPos pos, BlockState state) {
        return (FluidState) invoke(GET_NEW_LIQUID, fluid, level, pos, state);
    }

    static int getSpreadDelay(FlowingFluid fluid, Level level, BlockPos pos, FluidState current, FluidState next) {
        if (SPREAD_DELAY == null) return fluid.getTickDelay(level);
        return (int) invoke(SPREAD_DELAY, fluid, level, pos, current, next);
    }

    static void spread(FlowingFluid fluid, Level level, BlockPos pos, FluidState state) {
        if (SPREAD.getParameterCount() == 3) {
            invoke(SPREAD, fluid, level, pos, state);
        } else {
            invoke(SPREAD, fluid, level, pos, level.getBlockState(pos), state);
        }
    }

    private static Method find(String name, int parameters, boolean allowOneMore) {
        Method found = null;
        for (Method method : FlowingFluid.class.getDeclaredMethods()) {
            if (!method.getName().equals(name)) continue;
            int count = method.getParameterCount();
            if (count == parameters || (allowOneMore && count == parameters + 1 && found == null)) {
                found = method;
                if (count == parameters) break;
            }
        }
        if (found == null) {
            if (name.equals("getSpreadDelay")) return null;
            throw new IllegalStateException("FlowingFluid." + name + " not found");
        }
        found.setAccessible(true);
        return found;
    }

    private static Object invoke(Method method, FlowingFluid fluid, Object... args) {
        try {
            return method.invoke(fluid, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException runtime) throw runtime;
            throw new IllegalStateException(e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
