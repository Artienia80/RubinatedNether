package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.module.Flywheel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class JackhammerItem extends PickShovelItem {
    public static boolean allowServerSwing = false;
    private static final Map<Entity, Long> NEXT_CLIENT_SWING = new WeakHashMap<>();

    public JackhammerItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    protected void appendStats(ItemStack stack, List<Component> tooltip) {
        float hit = Flywheel.hitProgress(stack, JackhammerHandler.HIT_PROGRESS);
        float splash = JackhammerHandler.HIT_SPLASH * Flywheel.spillMult(stack);
        double strengthPct = hit * 100.0;
        double spreadPct = hit > 0 ? splash / hit * 100.0 : 0.0;
        ToolStatsTooltip.shattering(tooltip, strengthPct, spreadPct);
    }

    @Override
    public boolean isRunning(ServerPlayer player) {
        return JackhammerHandler.isHitting(player);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        if (!MechanicalFuel.hasFuel(stack)) return false;
        if (entity.level().isClientSide) {
            long now = entity.level().getGameTime();
            if (now >= NEXT_CLIENT_SWING.getOrDefault(entity, 0L)) {
                NEXT_CLIENT_SWING.put(entity, now + JackhammerHandler.TICKS_PER_HIT);
                return false;
            }
            return true;
        }
        return !allowServerSwing;
    }
}