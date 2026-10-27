package corundum.rubinated_nether.content.enchantment.custom;

import com.mojang.logging.LogUtils;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import org.slf4j.Logger;

import java.lang.reflect.Method;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class BlessedLootingEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Method DROP_ALL_DEATH_LOOT = findDropAllDeathLoot();

    private static boolean doublingLoot = false;

    private static Method findDropAllDeathLoot() {
        try {
            return ObfuscationReflectionHelper.findMethod(LivingEntity.class, "dropAllDeathLoot", ServerLevel.class, DamageSource.class);
        } catch (RuntimeException e) {
            LOGGER.error("Blessed looting is disabled: could not find LivingEntity#dropAllDeathLoot", e);
            return null;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void doubleBlessedLoot(LivingDropsEvent event) {
        if (doublingLoot || DROP_ALL_DEATH_LOOT == null) return;

        LivingEntity entity = event.getEntity();
        if (entity.getType() == EntityType.WITHER) return;
        if (!(entity.level() instanceof ServerLevel level)) return;
        if (!(event.getSource().getEntity() instanceof LivingEntity killer)) return;

        MobEffectInstance blessed = killer.getEffect(RNEffects.BLESSED);
        if (blessed == null) return;

        float chance = Math.min(1.0f, blessed.getDuration() / 384000.0f);
        if (level.getRandom().nextFloat() >= chance) return;

        doublingLoot = true;
        try {
            DROP_ALL_DEATH_LOOT.invoke(entity, level, event.getSource());
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Blessed looting failed to re-run death loot for {}", entity, e);
        } finally {
            doublingLoot = false;
        }
    }
}