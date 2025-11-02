package corundum.rubinated_nether.content.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class SoakStoneBlock extends Block{
	public static final int MAX_DEPTH = 6;
	public static final int MAX_COUNT = 64;
	private static final Direction[] ALL_DIRECTIONS = Direction.values();
	private static final int MAX_CHAIN_PROPAGATION = 256;
	private static final int CHAIN_DELAY = 5;
	private static final int CRUMBLE_TICKS = 40;

	private final Map<UUID, Integer> standTime = new HashMap<>();
	private static final Set<BlockPos> preventChainBreaks = new HashSet<>();

	public SoakStoneBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!oldState.is(state.getBlock())) {
			this.tryAbsorbLava(level, pos);
		}
	}

	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
		this.tryAbsorbLava(level, pos);
		super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
	}

	protected void tryAbsorbLava(Level level, BlockPos pos) {
		if (this.removeLavaBreadthFirstSearch(level, pos)) {
			level.setBlock(pos, Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
			level.levelEvent(2001, pos, Block.getId(Blocks.LAVA.defaultBlockState()));
		}
	}

	private boolean removeLavaBreadthFirstSearch(Level level, BlockPos pos) {
		return BlockPos.breadthFirstTraversal(pos, 6, 65, (blockPos, consumer) -> {
			Direction[] var2 = ALL_DIRECTIONS;
			int var3 = var2.length;
			for(int var4 = 0; var4 < var3; ++var4) {
				Direction direction = var2[var4];
				consumer.accept(blockPos.relative(direction));
			}}, (blockPos2) -> {
			if (blockPos2.equals(pos)) {
				return true;
			} else {
				BlockState blockState = level.getBlockState(blockPos2);
				FluidState fluidState = level.getFluidState(blockPos2);
				if (!fluidState.is(FluidTags.LAVA)) {
					return false;
				} else {
					Block block = blockState.getBlock();
					if (block instanceof BucketPickup) {
						BucketPickup bucketPickup = (BucketPickup)block;
						if (!bucketPickup.pickupBlock(null, level, blockPos2, blockState).isEmpty()) {
							return true;
						}
					}
					if (blockState.getBlock() instanceof LiquidBlock) {
						level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
					} else {
						if (!blockState.is(Blocks.KELP) && !blockState.is(Blocks.KELP_PLANT) && !blockState.is(Blocks.SEAGRASS) && !blockState.is(Blocks.TALL_SEAGRASS)) {
							return false;
						}
						BlockEntity blockEntity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos2) : null;
						dropResources(blockState, level, blockPos2, blockEntity);
						level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 3);
					}
					return true;
				}
			}
		}) > 1;
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide()) {
			ItemStack tool = player.getMainHandItem();
			int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(
					level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH),
					tool
			);

			System.out.println("[SoakStone] playerWillDestroy at " + pos + " | tool=" + tool.getItem() + " | silkTouch=" + silkTouchLevel);

			if (silkTouchLevel > 0) {
				preventChainBreaks.add(pos.immutable());
				System.out.println("[SoakStone] Added " + pos + " to prevent set (silk touch). Set size: " + preventChainBreaks.size());
			}
		}

		standTime.remove(player.getUUID());
		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!level.isClientSide && !state.is(newState.getBlock())) {
			if (level instanceof ServerLevel serverLevel) {
				boolean prevented = preventChainBreaks.remove(pos);
				System.out.println("[SoakStone] onRemove at " + pos + " | prevented=" + prevented + " | preventSet size=" + preventChainBreaks.size());

				if (!prevented) {
					System.out.println("[SoakStone] Triggering chain destruction from " + pos);
					triggerChainDestruction(serverLevel, pos);
				} else {
					System.out.println("[SoakStone] Chain destruction prevented for " + pos);
				}
			}
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	private void triggerChainDestruction(ServerLevel level, BlockPos origin) {
		Set<BlockPos> visited = new HashSet<>();
		visited.add(origin);

		for (Direction dir : Direction.values()) {
			BlockPos neighborPos = origin.relative(dir);
			BlockState neighborState = level.getBlockState(neighborPos);

			if (neighborState.is(this) && !visited.contains(neighborPos)) {
				scheduleChainBreak(level, neighborPos, visited, 1);
			}
		}
	}

	private void scheduleChainBreak(ServerLevel level, BlockPos pos, Set<BlockPos> globalVisited, int depth) {
		if (depth > MAX_CHAIN_PROPAGATION) return;
		if (globalVisited.contains(pos)) return;

		globalVisited.add(pos);

		if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
			level.scheduleTick(pos, this, CHAIN_DELAY);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		System.out.println("[SoakStone] tick at " + pos + " - breaking block");
		Block.dropResources(state, level, pos);
		level.destroyBlock(pos, false);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (level.isClientSide) return;

		if (entity instanceof Player player) {
			double fallDistance = entity.fallDistance;
			ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

			System.out.println("[SoakStone] stepOn at " + pos + " | player=" + player.getName().getString() + " | fallDistance=" + fallDistance + " | boots=" + boots.getItem());

			if (fallDistance > 0.5) {
				if (boots.is(Items.LEATHER_BOOTS)) {
					System.out.println("[SoakStone] Player has leather boots - NO BREAK AT ALL");
					return;
				}

				if (level instanceof ServerLevel serverLevel) {
					System.out.println("[SoakStone] Triggering fall impact");
					handleFallImpact(serverLevel, pos);
				}
			}
		}

		super.stepOn(level, pos, state, entity);
	}

	private void handleFallImpact(ServerLevel level, BlockPos center) {
		Set<BlockPos> immediateBreaks = new HashSet<>();

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				BlockPos checkPos = center.offset(x, 0, z);
				BlockState checkState = level.getBlockState(checkPos);

				if (checkState.is(this)) {
					BlockPos immutablePos = checkPos.immutable();
					immediateBreaks.add(immutablePos);
				}
			}
		}

		System.out.println("[SoakStone] Fall impact - adding " + immediateBreaks.size() + " blocks to prevent set BEFORE breaking");

		for (BlockPos breakPos : immediateBreaks) {
			preventChainBreaks.add(breakPos);
		}

		System.out.println("[SoakStone] Prevent set size after adding all: " + preventChainBreaks.size());

		for (BlockPos breakPos : immediateBreaks) {
			BlockState breakState = level.getBlockState(breakPos);
			Block.dropResources(breakState, level, breakPos);
			level.destroyBlock(breakPos, false);
		}

		System.out.println("[SoakStone] All 3x3 blocks broken. Prevent set size: " + preventChainBreaks.size());
	}
}