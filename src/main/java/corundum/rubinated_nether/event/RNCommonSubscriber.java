package corundum.rubinated_nether.event;

import com.mojang.logging.LogUtils;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNDamageTypes;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.ChandelierBlock;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import corundum.rubinated_nether.content.items.DrillItem;
import corundum.rubinated_nether.misc.DatapackRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class RNCommonSubscriber {
	public static final Logger LOGGER = LogUtils.getLogger();

    private static final Vec3 SINKING_MOTION = new Vec3(0, -1.5, 0);
    private static final int BATCH_SIZE = 8;
    private static final int BATCH_DELAY_MS = 50;
    private static final float DROP_CHANCE = 0.5f;
    private static final float SKIP_CHANCE = 0.05f;

	@SubscribeEvent
	public static void onLivingHurt(LivingDamageEvent.Post event) {
		var entity = event.getEntity();
		var source = event.getSource();

		if (!source.is(RNDamageTypes.CHANDELIER)) return;
        if (!(source.getDirectEntity() instanceof FallingBlockEntity fallingBlock)) return;

        BlockState blockState = fallingBlock.getBlockState();
        if (!(blockState.getBlock() instanceof ChandelierBlock chandelier)) return;

        TarnishStage tarnishStage = chandelier.getAge();
        if (tarnishStage != TarnishStage.CRYSTALLIZED) return;

        entity.addEffect(new MobEffectInstance(RNEffects.BRONZE_DISEASED, 72000, 0));
	}

    @SubscribeEvent
    public static void onLivingRavagingCurseDeath(LivingDropsEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity killer) {
            ItemStack mainHandItem = killer.getMainHandItem();

            if (!mainHandItem.isEmpty()) {
                Holder<Enchantment> antiLootingCurse = event.getEntity().level().registryAccess()
                        .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                        .getHolderOrThrow(RNEnchantments.RAVAGING_CURSE);

                int enchantmentLevel = mainHandItem.getEnchantmentLevel(antiLootingCurse);

                if (enchantmentLevel > 0) {
                    if (event.getEntity().level().getRandom().nextFloat() < 0.5f) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPreSinkingCurseHurt(LivingDamageEvent.Pre event) {
        var player = event.getEntity();
        var stack = event.getSource().getWeaponItem();
        if(stack == null || !stack.is(Items.MACE)) return;

        Holder<Enchantment> sinkingCurse = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(RNEnchantments.SINKING_CURSE);

        int enchantLevel = stack.getEnchantmentLevel(sinkingCurse);
        var motion = player.getDeltaMovement().with(Direction.Axis.Y, 0.01F);
        if (enchantLevel > 0 && MaceItem.canSmashAttack(player)) {
            player.setDeltaMovement(new Vec3(motion.x, SINKING_MOTION.y, motion.z));
        } else {
            player.setDeltaMovement(motion);
        }
    }

    @SubscribeEvent
    public static void onPostSinkingCurseHurt(LivingDamageEvent.Pre event) {
        var player = event.getEntity();
        var stack = event.getSource().getWeaponItem();
        if(stack == null || !stack.is(Items.MACE)) return;

        Holder<Enchantment> sinkingCurse = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(RNEnchantments.SINKING_CURSE);

        int enchantLevel = stack.getEnchantmentLevel(sinkingCurse);

        if (enchantLevel > 0 && MaceItem.canSmashAttack(player)) {
            scheduleProgressiveSinkhole((ServerLevel) player.level(), player.blockPosition(), player.fallDistance);
        }
    }

    @SubscribeEvent
    public static void changeBronzeSize(EntityEvent.Size event) {
        if (!(event.getEntity() instanceof BronzeEntity bronzeEntity)) return;

        event.setNewSize(bronzeEntity.isBurrowed() ?
                EntityDimensions.fixed(0.35F, 0.375F) :
                EntityDimensions.scalable(0.7F, 1.4F));
    }

	@SubscribeEvent
	public static void modifyBreakSpeed(PlayerEvent.BreakSpeed event) {
		var player = event.getEntity();
		var itemStack = player.getMainHandItem();

		if (!(itemStack.getItem() instanceof DrillItem drillItem)) return;

        var modified = drillItem.calcModifier(itemStack, event.getOriginalSpeed());
        if (modified > event.getOriginalSpeed())
            event.setNewSpeed(modified);
	}

    @SubscribeEvent
    public static void modifyCurseBreakSpeed(PlayerEvent.BreakSpeed event) {
        var player = event.getEntity();
        var itemStack = player.getMainHandItem();
        int curseLevel = itemStack.getEnchantmentLevel(player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(RNEnchantments.DEFICIENCY_CURSE));

        if (curseLevel > 0) {
            event.setNewSpeed(Math.max(0.1f, event.getOriginalSpeed() - (float) curseLevel));
        }
    }

	@SubscribeEvent
	public static void freezerFuel(ServerAboutToStartEvent event) {
		FreezerBlockEntity.cleanFreezingTimes();

		var entries = event.getServer().registryAccess().registryOrThrow(DatapackRegistry.FREEZER_FUELS).entrySet();
		LOGGER.info("Registered Freezer Fuels: {}", entries.size());

		for (var entry : entries) {
			var x = entry.getValue();
			var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(x.item()));

			LOGGER.info(x.toString());
			FreezerBlockEntity.addItemFreezingTime(item, x.freezeTime());
		}
	}



    private static void scheduleProgressiveSinkhole(ServerLevel level, BlockPos centerPos, float fallDistance) {
        BlockPos groundPos = findGroundLevel(level, centerPos);
        if (groundPos == null) return;

        float strength = Math.max(1.0f, fallDistance * 0.25f);
        float maxRadiusFloat = 1.5f + (float)Math.sqrt(fallDistance) * 0.15f;
        int maxRadius = (int) Math.ceil(maxRadiusFloat);
        float maxRadiusSquared = maxRadiusFloat * maxRadiusFloat;

        // Use two parallel lists instead of a helper class
        List<BlockPos> positions = new ObjectArrayList<>();
        List<Float> distances = new ObjectArrayList<>();

        int halfRadius = maxRadius / 2;
        for (int x = -maxRadius; x <= maxRadius; x++) {
            for (int z = -maxRadius; z <= maxRadius; z++) {
                // Quick 2D distance check before doing 3D checks
                int distXZ = x*x + z*z;
                if (distXZ > maxRadiusSquared) continue;

                for (int y = -halfRadius; y <= 1; y++) {
                    float distanceSquared = distXZ + y*y;
                    if (distanceSquared > maxRadiusSquared) continue;

                    BlockPos targetPos = groundPos.offset(x, y, z);
                    BlockState currentState = level.getBlockState(targetPos);

                    if (shouldSkipBlock(level, targetPos, currentState, maxRadiusSquared, x, y, z)) {
                        continue;
                    }

                    float blockHardness = currentState.getDestroySpeed(level, targetPos);
                    double scaledHardness = Math.pow(blockHardness, 0.7);
                    float distance = (float)Math.sqrt(distanceSquared);
                    double breakThreshold = distance * scaledHardness * 7.0;

                    if (strength > breakThreshold) {
                        positions.add(targetPos);
                        distances.add(distance);
                    }
                }
            }
        }

        if (positions.isEmpty()) return;

        // Sort both lists by distance
        sortByDistance(positions, distances);

        // Break blocks progressively
        breakBlocksProgressively(level, positions, 0, new Random());
    }

    private static void sortByDistance(List<BlockPos> positions, List<Float> distances) {
        // Create indices array and sort by distance
        Integer[] indices = new Integer[positions.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        java.util.Arrays.sort(indices, Comparator.comparingDouble(distances::get));

        // Reorder both lists
        List<BlockPos> sortedPos = new ObjectArrayList<>(positions.size());
        List<Float> sortedDist = new ObjectArrayList<>(distances.size());

        for (int idx : indices) {
            sortedPos.add(positions.get(idx));
            sortedDist.add(distances.get(idx));
        }

        positions.clear();
        positions.addAll(sortedPos);
        distances.clear();
        distances.addAll(sortedDist);
    }

    private static void breakBlocksProgressively(ServerLevel level, List<BlockPos> blocksToBreak, int index, Random random) {
        if (index >= blocksToBreak.size()) return;

        int batchSize = Math.min(BATCH_SIZE, blocksToBreak.size() - index);

        for (int i = 0; i < batchSize; i++) {
            int currentIndex = index + i;
            if (currentIndex >= blocksToBreak.size()) break;

            if (random.nextFloat() < SKIP_CHANCE) continue;

            BlockPos pos = blocksToBreak.get(currentIndex);
            BlockState state = level.getBlockState(pos);

            if (!state.isAir()) {
                level.destroyBlock(pos, random.nextFloat() < DROP_CHANCE);
            }
        }

        // Schedule next batch
        int nextIndex = index + batchSize;
        if (nextIndex < blocksToBreak.size()) {
            level.getServer().tell(new net.minecraft.server.TickTask(
                    level.getServer().getTickCount() + 1,
                    () -> breakBlocksProgressively(level, blocksToBreak, nextIndex, random)
            ));
        }
    }

    private static boolean shouldSkipBlock(ServerLevel level, BlockPos targetPos, BlockState currentState,
                                           float maxRadiusSquared, int x, int y, int z) {
        // Skip unbreakable blocks
        if (currentState.isAir() || currentState.liquid() || currentState.getDestroySpeed(level, targetPos) < 0) {
            return true;
        }

        // Check if block above is solid and outside radius
        BlockPos abovePos = targetPos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (!aboveState.isAir() && aboveState.isSolid()) {
            int aboveDistSquared = x*x + (y+1)*(y+1) + z*z;
            return aboveDistSquared > maxRadiusSquared;
        }

        return false;
    }

    private static BlockPos findGroundLevel(ServerLevel level, BlockPos startPos) {
        BlockState currentState = level.getBlockState(startPos);
        if (!currentState.isAir() && currentState.isSolid()) {
            return startPos;
        }

        // Search downward first (more likely)
        for (int y = startPos.getY() - 1; y >= level.getMinBuildHeight(); y--) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            BlockState state = level.getBlockState(checkPos);

            if (!state.isAir() && state.isSolid()) {
                return checkPos;
            }
        }

        // Search upward if needed
        for (int y = startPos.getY() + 1; y <= level.getMaxBuildHeight(); y++) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            BlockState state = level.getBlockState(checkPos);

            if (!state.isAir() && state.isSolid()) {
                return checkPos;
            }
        }

        return null;
    }
}