package net.artienia.rubinated_nether.integrations;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import net.artienia.rubinated_nether.integrations.aether.AetherCompat;
import net.artienia.rubinated_nether.integrations.spelunkery.SpelunkeryCompat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import uwu.serenity.critter.platform.PlatformUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class RNModCompat {

    // Put all the compat handlers for compatible mods in here
    public static final Map<String, Supplier<Supplier<CompatHandler>>> COMPAT = ImmutableMap.<String, Supplier<Supplier<CompatHandler>>>builder()
        .put("spelunkery", () -> SpelunkeryCompat::new)
        .put("aether", () -> AetherCompat::new)
        .build();

    private static final Supplier<List<CompatHandler>> ACTIVE_HANDLERS = Suppliers.memoize(() -> COMPAT.entrySet()
        .stream()
        .filter(entry -> PlatformUtils.modLoaded(entry.getKey()))
        .map(entry -> entry.getValue().get().get())
        .toList()
    );

    public static void init() {
        ACTIVE_HANDLERS.get().forEach(CompatHandler::init);
    }

    public static void setup() {
        ACTIVE_HANDLERS.get().forEach(CompatHandler::setup);
    }

    @Environment(EnvType.CLIENT)
    public static void clientSetup() {
        ACTIVE_HANDLERS.get().forEach(CompatHandler::clientSetup);
    }

}
