package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.module.Flywheel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class JackhammerHandler {
    // Tuning
    public static final float HIT_PROGRESS = 0.20f;        // default: 5 hits per block
    public static final int TICKS_PER_HIT = 10;        // 0.5 s per hit -> 5 hits = 2.5 s
    public static final float HIT_SPLASH = 0.0625f;    // progress each neighbour gets per hit
    public static final float BREAK_BLAST = 0.25f;
    public static final float CORNER_FACTOR = 0.5f;
    public static final float SPILL_CAP = 0.9f;
    public static final long HOLD_TICKS = 600;         // progress stays for 30 s
    public static final long FADE_INTERVAL_TICKS = 200; // then -12.5% every 10 s
    public static final float FADE_STEP = 0.125f;

    private static final class Spill {
        BlockState state;
        float progress;
        long nextFade; // game time of the next fade step
    }

    private record Session(BlockPos pos, Direction face) {}

    private static final Map<UUID, Map<BlockPos, Spill>> SPILL = new HashMap<>();
    private static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private static final Map<UUID, Long> NEXT_HIT = new HashMap<>(); // per player, not per block

    private JackhammerHandler() {}

    /** True while a hit session is active (button held on a block). Drives fuel burn. */
    public static boolean isHitting(ServerPlayer player) {
        return SESSIONS.containsKey(player.getUUID());
    }

    /** Holding a jackhammer that still has fuel. Without fuel it mines like a plain pickaxe. */
    private static boolean isPoweredJackhammer(Player player) {
        var held = player.getMainHandItem();
        return held.getItem() instanceof JackhammerItem && MechanicalFuel.hasFuel(held);
    }

    // ---------- Disable vanilla progress so mining only happens in our hits ----------
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!isPoweredJackhammer(player)) return; // no fuel: vanilla mining at normal speed
        Optional<BlockPos> pos = event.getPosition();
        if (pos.isEmpty()) return;
        if (isTarget(player.level(), pos.get(), event.getState())) {
            event.setNewSpeed(0f);
        }
    }

    // ---------- Holding the button = a session; releasing ends it ----------
    // LOWEST so claim/protection mods that cancel the click have already run
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        UUID id = player.getUUID();
        switch (event.getAction()) {
            case START -> {
                Level level = player.level();
                BlockPos pos = event.getPos();
                if (isPoweredJackhammer(player)
                        && isTarget(level, pos, level.getBlockState(pos))
                        && canMine(player, pos)) {
                    Direction face = event.getFace() != null ? event.getFace() : Direction.UP;
                    SESSIONS.put(id, new Session(pos.immutable(), face));
                } else {
                    SESSIONS.remove(id);
                }
            }
            case ABORT, STOP -> SESSIONS.remove(id);
            default -> {}
        }
    }

    // ---------- Fire a hit whenever the player's timer is ready ----------
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        UUID id = player.getUUID();
        Session s = SESSIONS.get(id);
        if (s != null) {
            Level level = player.level();
            long now = level.getGameTime();
            if (!isPoweredJackhammer(player)
                    || !isTarget(level, s.pos(), level.getBlockState(s.pos()))
                    || !canMine(player, s.pos())) {
                SESSIONS.remove(id);
            } else if (now >= NEXT_HIT.getOrDefault(id, 0L)) {
                NEXT_HIT.put(id, now + TICKS_PER_HIT);
                hit(player, s);
            }
        }
        if (player.tickCount % 20 == 0) fade(player);
    }

    // ---------- Cleanup ----------
    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { forget(event.getEntity()); }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) { forget(event.getEntity()); }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { forget(event.getEntity()); }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        SPILL.clear();
        SESSIONS.clear();
        NEXT_HIT.clear();
    }

    private static void forget(Player p) {
        SPILL.remove(p.getUUID());
        SESSIONS.remove(p.getUUID());
        NEXT_HIT.remove(p.getUUID());
    }

    // ---------- Safety ----------
    private static boolean canMine(ServerPlayer player, BlockPos pos) {
        ServerLevel level = player.serverLevel();
        return player.isAlive()
                && !player.isSpectator()
                && level.isLoaded(pos)
                && player.canInteractWithBlock(pos, 1.0)  // reach check for the whole session
                && level.mayInteract(player, pos)         // spawn protection
                && !player.blockActionRestricted(level, pos, player.gameMode.getGameModeForPlayer());
    }

    private static boolean isTarget(Level level, BlockPos pos, BlockState state) {
        return !state.isAir()
                && state.is(RNTags.Blocks.MINEABLE_WITH_DRILL)
                && state.getDestroySpeed(level, pos) > 0f; // skips bedrock and instant-break blocks
    }

    // ---------- Progress storage ----------
    /** Gets (or creates) the entry for a block and restarts its 30 s hold. Discards stale entries. */
    private static Spill touch(Map<BlockPos, Spill> map, BlockPos pos, BlockState state, long now) {
        Spill sp = map.get(pos);
        if (sp == null || !sp.state.equals(state)) {
            sp = new Spill();
            sp.state = state;
            map.put(pos.immutable(), sp);
        }
        sp.nextFade = now + HOLD_TICKS;
        return sp;
    }

    /** One jackhammer hit. */
    private static void hit(ServerPlayer player, Session s) {
        ServerLevel level = player.serverLevel();
        BlockPos pos = s.pos();
        BlockState state = level.getBlockState(pos);
        long now = level.getGameTime();

        Map<BlockPos, Spill> map = SPILL.computeIfAbsent(player.getUUID(), k -> new HashMap<>());
        ItemStack held = player.getMainHandItem();
        float hitProgress = Flywheel.hitProgress(held, HIT_PROGRESS);
        float spillMult = Flywheel.spillMult(held);

        Spill sp = touch(map, pos, state, now);
        sp.progress += hitProgress; // starts from stored progress, so pre-damaged blocks need fewer hits

        level.playSound(null, pos, state.getSoundType().getHitSound(),
                SoundSource.BLOCKS, 1.0f, 0.75f);
        try {
            JackhammerItem.allowServerSwing = true;
            player.swing(InteractionHand.MAIN_HAND, false); // others see it; own client gates itself
        } finally {
            JackhammerItem.allowServerSwing = false;
        }

        if (sp.progress >= 1f - 1e-4f) {
            map.remove(pos);
            level.destroyBlockProgress(crackId(pos), pos, -1);
            SESSIONS.remove(player.getUUID()); // the client sends a new START for the next block
            spill(player, pos, s.face(), (HIT_SPLASH + BREAK_BLAST) * spillMult);
            player.gameMode.destroyBlock(pos); // drops + durability
            return;
        }

        level.destroyBlockProgress(crackId(pos), pos, (int) (sp.progress * 10));
        spill(player, pos, s.face(), HIT_SPLASH * spillMult);
    }

    // ---------- Spill: 8 neighbors in the face plane, corners get half ----------
    private static void spill(ServerPlayer player, BlockPos origin, Direction face, float amount) {
        ServerLevel level = player.serverLevel();
        long now = level.getGameTime();
        Map<BlockPos, Spill> map = SPILL.computeIfAbsent(player.getUUID(), k -> new HashMap<>());

        for (int u = -1; u <= 1; u++) {
            for (int v = -1; v <= 1; v++) {
                if (u == 0 && v == 0) continue;
                BlockPos n = switch (face.getAxis()) {
                    case X -> origin.offset(0, u, v);
                    case Y -> origin.offset(u, 0, v);
                    case Z -> origin.offset(u, v, 0);
                };
                BlockState ns = level.getBlockState(n);
                if (!isTarget(level, n, ns) || !level.mayInteract(player, n)) continue;

                float factor = (u != 0 && v != 0) ? CORNER_FACTOR : 1f;
                Spill sp = touch(map, n, ns, now);
                // max() so a block already past the cap from direct hits is never reduced
                sp.progress = Math.max(sp.progress, Math.min(sp.progress + amount * factor, SPILL_CAP));
                level.destroyBlockProgress(crackId(n), n, (int) (sp.progress * 10));
            }
        }
    }

    // ---------- Fade: after the 30 s hold, -12.5% every 10 s until intact ----------
    private static void fade(ServerPlayer player) {
        Map<BlockPos, Spill> map = SPILL.get(player.getUUID());
        if (map == null || map.isEmpty()) return;
        ServerLevel level = player.serverLevel();
        long now = level.getGameTime();

        Iterator<Map.Entry<BlockPos, Spill>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Spill> e = it.next();
            Spill sp = e.getValue();
            if (now < sp.nextFade) continue;

            while (now >= sp.nextFade && sp.progress > 1e-4f) {
                sp.progress -= FADE_STEP;
                sp.nextFade += FADE_INTERVAL_TICKS;
            }

            if (sp.progress <= 1e-4f) {
                level.destroyBlockProgress(crackId(e.getKey()), e.getKey(), -1);
                it.remove();
            } else {
                level.destroyBlockProgress(crackId(e.getKey()), e.getKey(), (int) (sp.progress * 10));
            }
        }
    }

    private static int crackId(BlockPos pos) {
        return pos.hashCode() | Integer.MIN_VALUE; // never collides with entity ids
    }
}