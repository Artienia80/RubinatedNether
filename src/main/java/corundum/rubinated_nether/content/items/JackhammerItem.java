package corundum.rubinated_nether.content.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

import java.util.Map;
import java.util.WeakHashMap;

public class JackhammerItem extends PickShovelItem {
    /** Set by the handler while it fires its own swing on the server. */
    public static boolean allowServerSwing = false;
    private static final Map<Entity, Long> NEXT_CLIENT_SWING = new WeakHashMap<>();

    public JackhammerItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    // Return true = cancel the swing
    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        if (entity.level().isClientSide) {
            long now = entity.level().getGameTime();
            if (now >= NEXT_CLIENT_SWING.getOrDefault(entity, 0L)) {
                NEXT_CLIENT_SWING.put(entity, now + JackhammerHandler.TICKS_PER_HIT);
                return false; // allow this one
            }
            return true;
        }
        return !allowServerSwing;
    }
}