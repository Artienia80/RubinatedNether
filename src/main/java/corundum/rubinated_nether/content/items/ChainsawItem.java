package corundum.rubinated_nether.content.items;

import com.mojang.logging.LogUtils;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.module.Flywheel;
import corundum.rubinated_nether.content.module.ToolModules;
import corundum.rubinated_nether.utils.RNAdvancementAwards;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.slf4j.Logger;

import java.util.*;

public class ChainsawItem extends AxeItem implements MechanicalTool {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final boolean DEBUG = false;

    /** Entity tag on every falling block the chainsaw spawns (logs, leaves, fungus caps, shroomlight, hives). */
    public static final String HEAVY_TAG = "rn_heavy_log";

    /** Upper bound on trunk/branch blocks felled by one cut, to keep a single swing from spawning unbounded entities. */
    private static final int MAX_TREE_LOGS = 512;

    private static final int LOG_AWARD_THRESHOLD = 64;

    private static final int MAX_CANOPY_BLOCKS = 512;

    private static final int CANOPY_REACH = 5;

    public static final int MAX_WINDUP = 30;

    public static final int TICKS_PER_LEVEL = 4;

    public static final int GRACE_TICKS = 15;

    public static final float WINDUP_START_SPEED = 2.0F;

    public static final float INSTANT_SPEED = 10_000F;

    public static final float FALL_DAMAGE_PER_HARDNESS = 1.0F;

    public static final int FALL_DAMAGE_CAP_PER_HARDNESS = 10;

    public ChainsawItem(Properties properties) {
        super(RNTiers.BRONZE, properties);
    }

    public static final float MINING_SPEED = 8.0F;

    public static boolean isChainsawTarget(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_AXE)
                || state.is(BlockTags.MINEABLE_WITH_HOE)
                || state.is(BlockTags.SWORD_EFFICIENT)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.WOOL)
                || state.is(BlockTags.WOOL_CARPETS)
                || state.is(Blocks.COBWEB)
                || state.is(Blocks.VINE)
                || state.is(Blocks.GLOW_LICHEN);
    }

    public static boolean isTrunk(BlockState state) {
        return state.is(BlockTags.LOGS)
                || state.is(Blocks.MUSHROOM_STEM)
                || state.is(RNTags.Blocks.PETRIFIED_LOGS);
    }

    public static boolean isCanopy(BlockState state) {
        return state.is(BlockTags.LEAVES)
                || state.is(Blocks.NETHER_WART_BLOCK)
                || state.is(Blocks.WARPED_WART_BLOCK)
                || state.is(Blocks.RED_MUSHROOM_BLOCK)
                || state.is(Blocks.BROWN_MUSHROOM_BLOCK);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (!MechanicalFuel.hasFuel(stack)) return super.getDestroySpeed(stack, state);
        if (isCanopy(state)) return INSTANT_SPEED;
        if (isTrunk(state)) {
            int windup = getWindup(stack);
            if (windup >= MAX_WINDUP) return INSTANT_SPEED;
            return windupSpeed(windup) * Flywheel.peakMult(stack);
        }
        return isChainsawTarget(state) ? MINING_SPEED : super.getDestroySpeed(stack, state);
    }

    public static float windupSpeed(int level) {
        if (level >= MAX_WINDUP) return INSTANT_SPEED;
        return WINDUP_START_SPEED * (float) Math.pow(30.0, level / (double) MAX_WINDUP);
    }

    public static float ticksPerLevel(ItemStack stack) {
        return TICKS_PER_LEVEL / Flywheel.accelMult(stack);
    }

    public static int getWindup(ItemStack stack) {
        return stack.getOrDefault(RNDataComponents.CHAINSAW_WINDUP, 0);
    }

    public static void setWindup(ItemStack stack, int level) {
        stack.set(RNDataComponents.CHAINSAW_WINDUP, Mth.clamp(level, 0, MAX_WINDUP));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide()) MechanicalFuel.syncRarity(stack);
        if (!level.isClientSide()
                && entity instanceof ServerPlayer player
                && getWindup(stack) > 0
                && !ChainsawHandler.isMining(player, level.getGameTime())) {
            setWindup(stack, 0);
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public boolean isRunning(ServerPlayer player) {
        return ChainsawHandler.isMining(player, player.level().getGameTime());
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        return stack.getCount() == 1 && MechanicalFuel.onStackedOnOther(stack, slot, action, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        return stack.getCount() == 1 && MechanicalFuel.onOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ToolStatsTooltip.breaking(tooltip,
                windupSpeed(MAX_WINDUP - 1) * Flywheel.peakMult(stack),
                20.0 / ticksPerLevel(stack));
        MechanicalFuel.appendTooltip(stack, tooltip);
        ToolModules.appendTooltip(stack, tooltip);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return MechanicalFuel.tooltipImage(stack);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return MechanicalFuel.hasFuel(player.getMainHandItem()) && canWorkOn(state);
    }

    @Override
    public boolean canWorkOn(BlockState state) {
        return isChainsawTarget(state) || isTrunk(state) || isCanopy(state);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return MechanicalFuel.hasFuel(stack) || super.isFoil(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return MechanicalFuel.isBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return MechanicalFuel.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return MechanicalFuel.getBarColor(stack);
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility ability) {
        return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_HOE_ACTIONS.contains(ability)
                || ItemAbilities.DEFAULT_SHEARS_ACTIONS.contains(ability);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!MechanicalFuel.hasFuel(context.getItemInHand())) return InteractionResult.PASS;
        InteractionResult axeResult = super.useOn(context);
        if (axeResult != InteractionResult.PASS) return axeResult;

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState tilled = level.getBlockState(pos).getToolModifiedState(context, ItemAbilities.HOE_TILL, false);
        if (tilled == null) return InteractionResult.PASS;

        Player player = context.getPlayer();
        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide) {
            level.setBlock(pos, tilled, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, tilled));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (DEBUG) {
            LOGGER.info("[Chainsaw] mineBlock side={} block={} inLogsTag={}",
                    level.isClientSide() ? "client" : "server", state.getBlock(), isTrunk(state));
        }

        if (!level.isClientSide() && isTrunk(state) && MechanicalFuel.hasFuel(stack)) {
            int felled = fellTree(level, pos);
            if (felled > LOG_AWARD_THRESHOLD && miner instanceof ServerPlayer player) {
                RNAdvancementAwards.award(player, "chainsaw_timber", "chainsaw_timber");
            }
        }
        return super.mineBlock(stack, level, state, pos, miner);
    }

    private static int fellTree(Level level, BlockPos cut) {
        Set<BlockPos> connected = collectConnectedTrunks(level, cut);
        if (connected.isEmpty()) return 0;

        // A log falls if nothing solid holds it up. Going bottom-up lets a log rely on the log beneath it falling too.
        // NeoForge calls mineBlock() *before* it removes the block, so the cut position is still a log at this point;
        // isOpenBelow() treats it as already gone, otherwise the log directly above the cut would look supported.
        List<BlockPos> ordered = new ArrayList<>(connected);
        ordered.sort(Comparator.comparingInt(BlockPos::getY));
        Set<BlockPos> unsupported = new HashSet<>();
        for (BlockPos p : ordered) {
            BlockPos below = p.below();
            if (unsupported.contains(below) || isOpen(level, below, cut)) {
                unsupported.add(p);
            }
        }

        // Only logs reachable from the cut through other falling logs (26 neighbours, so diagonals count) come down.
        // A branch held on only by a log that stays standing is left alone.
        Set<BlockPos> reached = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        reached.add(cut);
        queue.add(cut);
        while (!queue.isEmpty()) {
            BlockPos p = queue.poll();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos n = p.offset(dx, dy, dz);
                        if (unsupported.contains(n) && reached.add(n)) queue.add(n);
                    }
                }
            }
        }
        reached.remove(cut);

        List<BlockPos> fallen = new ArrayList<>();
        for (BlockPos p : ordered) {
            if (reached.contains(p)) fallen.add(p);
        }
        if (fallen.isEmpty()) return 0;

        if (DEBUG) {
            LOGGER.info("[Chainsaw] cut at {}: connected={} falling={}", cut, connected.size(), fallen.size());
        }

        for (BlockPos p : fallen) {
            spawnHeavy(level, p);
        }

        List<BlockPos> fallenCanopy = dropCanopy(level, cut, fallen);

        List<BlockPos> everything = new ArrayList<>(fallen);
        everything.addAll(fallenCanopy);
        dropAttachments(level, cut, everything);
        return fallen.size();
    }

    /**
     * Flood-fills through the full 26-neighbourhood of every trunk block, starting at the block that was just cut,
     * so diagonal logs are picked up as well as straight columns. Only blocks above the cut are collected.
     */
    private static Set<BlockPos> collectConnectedTrunks(Level level, BlockPos cut) {
        Set<BlockPos> found = new LinkedHashSet<>();
        Set<BlockPos> seen = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        seen.add(cut);
        queue.add(cut);
        while (!queue.isEmpty() && found.size() < MAX_TREE_LOGS) {
            BlockPos p = queue.poll();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos n = p.offset(dx, dy, dz);
                        if (n.getY() <= cut.getY() || !seen.add(n)) continue;
                        if (!level.hasChunkAt(n) || !isTrunk(level.getBlockState(n))) continue;
                        found.add(n);
                        queue.add(n);
                    }
                }
            }
        }
        return found;
    }

    private static List<BlockPos> dropCanopy(Level level, BlockPos cut, List<BlockPos> sources) {
        Set<BlockPos> candidates = new LinkedHashSet<>();
        Set<BlockPos> seen = new HashSet<>(sources);
        Map<BlockPos, Integer> depth = new HashMap<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        for (BlockPos s : sources) {
            depth.put(s, 0);
            queue.add(s);
        }

        while (!queue.isEmpty() && candidates.size() < MAX_CANOPY_BLOCKS) {
            BlockPos p = queue.poll();
            int d = depth.get(p);
            if (d >= CANOPY_REACH) continue;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos n = p.offset(dx, dy, dz);
                        if (n.getY() <= cut.getY() || !seen.add(n)) continue;
                        BlockState s = level.getBlockState(n);
                        if (!isCanopy(s)) continue;
                        if (s.getOptionalValue(LeavesBlock.PERSISTENT).orElse(false)) continue;
                        candidates.add(n);
                        depth.put(n, d + 1);
                        queue.add(n);
                    }
                }
            }
        }

        if (candidates.isEmpty()) return new ArrayList<>();

        List<BlockPos> standing = findStandingTrunks(level, candidates, cut);

        Set<BlockPos> owned = new LinkedHashSet<>();
        Map<Object, Integer> tally = new LinkedHashMap<>();
        for (BlockPos leaf : candidates) {
            double fallingDist = nearestDistSq(leaf, sources);
            if (hasTrunkAtLeastAsClose(leaf, standing, fallingDist)) continue;
            owned.add(leaf);
            tally.merge(leafFamily(level.getBlockState(leaf)), 1, Integer::sum);
        }
        if (owned.isEmpty()) return new ArrayList<>();

        Object family = null;
        int best = 0;
        for (Map.Entry<Object, Integer> e : tally.entrySet()) {
            if (e.getValue() > best) {
                best = e.getValue();
                family = e.getKey();
            }
        }

        Set<BlockPos> canopy = new LinkedHashSet<>();
        for (BlockPos leaf : owned) {
            if (leafFamily(level.getBlockState(leaf)).equals(family)) canopy.add(leaf);
        }

        if (DEBUG) {
            LOGGER.info("[Chainsaw] canopy candidates={} owned={} falling={} standingTrunks={}",
                    candidates.size(), owned.size(), canopy.size(), standing.size());
        }

        List<BlockPos> fallenCanopy = new ArrayList<>();
        for (BlockPos p : canopy) {
            BlockPos below = p.below();
            if (canopy.contains(below) || isOpen(level, below, cut)) {
                spawnHeavy(level, p);
                fallenCanopy.add(p);
            }
        }
        return fallenCanopy;
    }

    private static void dropAttachments(Level level, BlockPos cut, List<BlockPos> fallen) {
        Set<BlockPos> attachments = new LinkedHashSet<>();
        for (BlockPos p : fallen) {
            for (Direction dir : Direction.values()) {
                BlockPos n = p.relative(dir);
                if (n.getY() <= cut.getY() || attachments.contains(n)) continue;
                if (!isAttachment(level.getBlockState(n))) continue;
                if (touchesStandingTrunk(level, n, cut)) continue;
                attachments.add(n);
            }
        }

        if (DEBUG) {
            LOGGER.info("[Chainsaw] attachments (shroomlight/hives) found={}", attachments.size());
        }

        for (BlockPos p : attachments) {
            BlockPos below = p.below();
            if (attachments.contains(below) || isOpen(level, below, cut)) {
                spawnHeavy(level, p);
            }
        }
    }

    private static boolean touchesStandingTrunk(Level level, BlockPos pos, BlockPos cut) {
        for (Direction dir : Direction.values()) {
            BlockPos n = pos.relative(dir);
            if (n.equals(cut)) continue; // being removed right now
            if (isTrunk(level.getBlockState(n))) return true;
        }
        return false;
    }

    /**
     * True if nothing solid occupies {@code pos}. The block being cut counts as open: mineBlock() runs before the
     * block is actually removed, so its state is still the log.
     */
    private static boolean isOpen(Level level, BlockPos pos, BlockPos cut) {
        if (pos.equals(cut)) return true;
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }

    public static boolean isAttachment(BlockState state) {
        return state.is(Blocks.SHROOMLIGHT) || state.is(Blocks.BEEHIVE) || state.is(Blocks.BEE_NEST);
    }

    private static Object leafFamily(BlockState state) {
        if (state.is(Blocks.FLOWERING_AZALEA_LEAVES)) return Blocks.AZALEA_LEAVES;
        return state.getBlock();
    }

    private static double nearestDistSq(BlockPos from, List<BlockPos> targets) {
        double best = Double.MAX_VALUE;
        for (BlockPos t : targets) {
            best = Math.min(best, from.distSqr(t));
        }
        return best;
    }

    private static boolean hasTrunkAtLeastAsClose(BlockPos leaf, List<BlockPos> trunks, double maxDistSq) {
        for (BlockPos t : trunks) {
            if (leaf.distSqr(t) <= maxDistSq) return true;
        }
        return false;
    }

    private static List<BlockPos> findStandingTrunks(Level level, Set<BlockPos> around, BlockPos cut) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos p : around) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            minZ = Math.min(minZ, p.getZ());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
            maxZ = Math.max(maxZ, p.getZ());
        }
        int margin = (int) Math.ceil(Math.sqrt(3.0) * CANOPY_REACH);

        List<BlockPos> result = new ArrayList<>();
        for (BlockPos p : BlockPos.betweenClosed(minX - margin, minY - margin, minZ - margin,
                maxX + margin, maxY + margin, maxZ + margin)) {
            if (!p.equals(cut) && level.hasChunkAt(p) && isTrunk(level.getBlockState(p))) {
                result.add(p.immutable());
            }
        }
        return result;
    }

    private static void spawnHeavy(Level level, BlockPos p) {
        BlockState s = level.getBlockState(p);
        if (!isTrunk(s) && !isCanopy(s) && !isAttachment(s)) return;
        float hardness = Math.max(s.getDestroySpeed(level, p), 0.0F);
        FallingBlockEntity block = FallingBlockEntity.fall(level, p, s);
        block.addTag(HEAVY_TAG);
        block.setHurtsEntities(hardness * FALL_DAMAGE_PER_HARDNESS,
                Math.max(1, Math.round(hardness * FALL_DAMAGE_CAP_PER_HARDNESS)));
    }

    public static void onEntityTick(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof FallingBlockEntity falling)) return;
        Level level = falling.level();
        if (level.isClientSide() || !falling.getTags().contains(HEAVY_TAG)) return;

        BlockPos feet = falling.blockPosition();
        crush(level, feet);
        crush(level, feet.below());
    }

    private static void crush(Level level, BlockPos p) {
        BlockState s = level.getBlockState(p);
        if (s.isAir() || !s.getFluidState().isEmpty()) return;
        if (s.getDestroySpeed(level, p) < 0) return;
        if (!s.getCollisionShape(level, p).isEmpty()) return;
        level.destroyBlock(p, true);
    }
}