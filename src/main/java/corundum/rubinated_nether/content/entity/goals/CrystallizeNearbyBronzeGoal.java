package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.bases.TarnishingBronze;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CrystallizeNearbyBronzeGoal extends Goal {
    private final BronzeEntity entity;
    private int cooldown;

    public CrystallizeNearbyBronzeGoal(BronzeEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return entity.getTarnishLevel().equals(TarnishStage.CRYSTALLIZED);
    }

    @Override
    public void tick() {
        if(!entity.getTarnishLevel().equals(TarnishStage.CRYSTALLIZED)) stop();
        if (--cooldown > 0) return;
        cooldown = 20 + entity.getRandom().nextInt(200);

        BlockPos origin = entity.blockPosition();
        Level level = entity.level();

        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-3, -3, -3), origin.offset(3, 3, 3))) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (block instanceof TarnishingBronze tarnishing) {
                if (state.hasProperty(TarnishingBronze.WAXED) && state.getValue(TarnishingBronze.WAXED)) {
                    BlockState unwaxed = state.setValue(TarnishingBronze.WAXED, false);
                    level.setBlock(pos, unwaxed, 3);
                    return;
                }

                if (!state.getValue(TarnishingBronze.WAXED) && TarnishingBronze.canCrystallize(block)) {
                    tarnishing.getCrystallized(state).ifPresent(newState -> level.setBlockAndUpdate(pos, newState));
                    return;
                }
            }
        }
    }
}
