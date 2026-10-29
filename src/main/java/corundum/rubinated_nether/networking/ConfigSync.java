package corundum.rubinated_nether.networking;

import com.mojang.logging.LogUtils;
import corundum.rubinated_nether.utils.RNConfig;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/** Syncs {@link RNConfig.Synced} fields from a dedicated server to its clients (in memory only, never written to disk on the client). */
public final class ConfigSync {
    private static final Logger LOGGER = LogUtils.getLogger();
    /** Client's own values, saved before the first server value overwrote them. */
    private static final Map<Field, Object> LOCAL = new HashMap<>();

    private ConfigSync() {}

    public static ConfigSyncPayload snapshot() {
        HashMap<String, String> map = new HashMap<>();
        for (Field f : RNConfig.class.getFields()) {
            if (!isSynced(f)) continue;
            try {
                map.put(f.getName(), String.valueOf(f.get(null)));
            } catch (IllegalAccessException e) {
                LOGGER.warn("Cannot read config field {}", f.getName(), e);
            }
        }
        return new ConfigSyncPayload(map);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void applyOnClient(ConfigSyncPayload payload) {
        for (Field f : RNConfig.class.getFields()) {
            if (!isSynced(f)) continue;
            String text = payload.values().get(f.getName());
            if (text == null) continue;
            try {
                LOCAL.putIfAbsent(f, f.get(null));
                Class<?> t = f.getType();
                Object v;
                if (t == int.class) v = Integer.parseInt(text);
                else if (t == float.class) v = Float.parseFloat(text);
                else if (t == double.class) v = Double.parseDouble(text);
                else if (t == boolean.class) v = Boolean.parseBoolean(text);
                else if (t.isEnum()) v = Enum.valueOf((Class<Enum>) t, text);
                else if (t == String.class) v = text;
                else continue;
                f.set(null, v);
            } catch (Exception e) {
                LOGGER.warn("Cannot apply synced config field {}", f.getName(), e);
            }
        }
    }

    public static void restoreLocal() {
        for (Map.Entry<Field, Object> e : LOCAL.entrySet()) {
            try {
                e.getKey().set(null, e.getValue());
            } catch (IllegalAccessException ex) {
                LOGGER.warn("Cannot restore config field {}", e.getKey().getName(), ex);
            }
        }
        LOCAL.clear();
    }

    public static void broadcastIfServer() {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            PacketDistributor.sendToAllPlayers(snapshot());
        }
    }

    private static boolean isSynced(Field f) {
        return Modifier.isStatic(f.getModifiers()) && f.isAnnotationPresent(RNConfig.Synced.class);
    }
}
