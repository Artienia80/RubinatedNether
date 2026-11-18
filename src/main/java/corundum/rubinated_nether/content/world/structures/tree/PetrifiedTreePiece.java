package corundum.rubinated_nether.content.world.structures.tree;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.world.structures.RNStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

import java.util.HashMap;
import java.util.Map;

public class PetrifiedTreePiece extends StructurePiece {
	private static final int HALF_EXTENT = 18;
	private static final int SCAN_UP = 6;
	private static final int SCAN_DOWN = 6;
	private static final int SAND_DEPTH = 8;

	private final BlockPos center;
	private final int radius;
	private final float phase;
	private final long seed;
	private final PetrifiedTreeVariant variant;

	public PetrifiedTreePiece(BlockPos center, int radius, float phase, long seed, PetrifiedTreeVariant variant) {
		super(RNStructurePieceTypes.PETRIFIED_TREE.get(), 0, createBox(center));
		this.center = center;
		this.radius = radius;
		this.phase = phase;
		this.seed = seed;
		this.variant = variant;
	}

	public PetrifiedTreePiece(CompoundTag tag) {
		super(RNStructurePieceTypes.PETRIFIED_TREE.get(), tag);
		this.center = new BlockPos(tag.getInt("CenterX"), tag.getInt("CenterY"), tag.getInt("CenterZ"));
		this.radius = tag.getInt("Radius");
		this.phase = tag.getFloat("Phase");
		this.seed = tag.getLong("Seed");
		this.variant = PetrifiedTreeVariant.byName(tag.getString("Variant"));
	}

	private static BoundingBox createBox(BlockPos center) {
		return new BoundingBox(
				center.getX() - HALF_EXTENT,
				center.getY() - 8,
				center.getZ() - HALF_EXTENT,
				center.getX() + HALF_EXTENT,
				center.getY() + 32,
				center.getZ() + HALF_EXTENT
		);
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
		tag.putInt("CenterX", center.getX());
		tag.putInt("CenterY", center.getY());
		tag.putInt("CenterZ", center.getZ());
		tag.putInt("Radius", radius);
		tag.putFloat("Phase", phase);
		tag.putLong("Seed", seed);
		tag.putString("Variant", variant.getSerializedName());
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
		placeSand(level, box);
		placeTree(level, box);
		spawnMobs(level, box);
	}

	private void placeSand(WorldGenLevel level, BoundingBox box) {
		BlockState starved = RNBlocks.SOUL_STARVED_SAND.get().defaultBlockState();
		int reach = radius + 2;
		int minX = Math.max(center.getX() - reach, box.minX());
		int maxX = Math.min(center.getX() + reach, box.maxX());
		int minZ = Math.max(center.getZ() - reach, box.minZ());
		int maxZ = Math.min(center.getZ() + reach, box.maxZ());
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				double dx = x - (center.getX() + 0.5);
				double dz = z - (center.getZ() + 0.5);
				double angle = Math.atan2(dz, dx);
				double edge = radius + 0.6 * Math.sin(angle * 3.0 + phase) + 0.4 * Math.sin(angle * 5.0 - phase * 1.7);
				if (Math.sqrt(dx * dx + dz * dz) > edge) {
					continue;
				}

				int top = Integer.MIN_VALUE;
				for (int y = center.getY() + SCAN_UP; y >= center.getY() - SCAN_DOWN; y--) {
					cursor.set(x, y, z);
					if (isSoul(level.getBlockState(cursor))) {
						top = y;
						break;
					}
				}
				if (top == Integer.MIN_VALUE) {
					continue;
				}

				for (int depth = 0; depth < SAND_DEPTH; depth++) {
					cursor.set(x, top - depth, z);
					if (!isSoul(level.getBlockState(cursor))) {
						break;
					}
					level.setBlock(cursor, starved, 2);
				}
			}
		}
	}

	private static boolean isSoul(BlockState state) {
		return state.is(Blocks.SOUL_SAND) || state.is(Blocks.SOUL_SOIL);
	}

	private void spawnMobs(WorldGenLevel level, BoundingBox box) {
		RandomSource random = new XoroshiroRandomSource(seed ^ 0x5DEECE66DL);

		int witherCount = 2 + random.nextInt(2);
		for (int i = 0; i < witherCount; i++) {
			spawnAround(level, box, random, EntityType.WITHER_SKELETON, new ItemStack(Items.IRON_SWORD));
		}

		int skeletonCount = 2 + random.nextInt(2);
		for (int i = 0; i < skeletonCount; i++) {
			Item tool = random.nextBoolean() ? Items.STONE_AXE : Items.STONE_PICKAXE;
			spawnAround(level, box, random, EntityType.SKELETON, new ItemStack(tool));
		}
	}

	private <T extends Mob> void spawnAround(WorldGenLevel level, BoundingBox box, RandomSource random, EntityType<T> type, ItemStack weapon) {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		float angle = random.nextFloat() * Mth.TWO_PI;
		double distance = radius + 2 + random.nextInt(4);
		float yaw = random.nextFloat() * 360.0F;
		int x = Mth.floor(center.getX() + 0.5 + Mth.cos(angle) * distance);
		int z = Mth.floor(center.getZ() + 0.5 + Mth.sin(angle) * distance);
		if (x < box.minX() || x > box.maxX() || z < box.minZ() || z > box.maxZ()) {
			return;
		}

		for (int y = center.getY() + SCAN_UP; y >= center.getY() - SCAN_DOWN; y--) {
			cursor.set(x, y - 1, z);
			BlockState ground = level.getBlockState(cursor);
			if (ground.isAir() || !ground.getFluidState().isEmpty() || ground.is(RNBlocks.SOUL_STARVED_SAND.get())) {
				continue;
			}
			cursor.set(x, y, z);
			if (!level.getBlockState(cursor).isAir()) {
				continue;
			}
			cursor.set(x, y + 1, z);
			if (!level.getBlockState(cursor).isAir()) {
				continue;
			}

			T mob = type.create(level.getLevel());
			if (mob == null) {
				return;
			}
			BlockPos spawnPos = new BlockPos(x, y, z);
			mob.moveTo(x + 0.5, y, z + 0.5, yaw, 0.0F);
			mob.setPersistenceRequired();
			mob.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.STRUCTURE, null);
			// Replaces the default sword/bow, so skeletons here only ever fight in melee.
			mob.setItemSlot(EquipmentSlot.MAINHAND, weapon);
			mob.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
			level.addFreshEntityWithPassengers(mob);
			return;
		}
	}

	private void placeTree(WorldGenLevel level, BoundingBox box) {
		Map<Long, Integer> columnShift = new HashMap<>();
		for (Map.Entry<BlockPos, Direction.Axis> entry : buildTree().entrySet()) {
			BlockPos logPos = entry.getKey();
			if (!box.isInside(logPos)) {
				continue;
			}
			int floor = center.getY();
			if (variant == PetrifiedTreeVariant.FALLEN) {
				int columnX = logPos.getX();
				int columnZ = logPos.getZ();
				int shift = columnShift.computeIfAbsent(
						BlockPos.asLong(columnX, 0, columnZ),
						key -> groundShift(level, columnX, columnZ)
				);
				logPos = logPos.above(shift);
				floor -= 1;
			}
			if (logPos.getY() >= floor) {
				BlockState existing = level.getBlockState(logPos);
				if (!existing.isAir() && !existing.canBeReplaced()) {
					continue;
				}
			}
			level.setBlock(logPos, RNBlocks.PETRIFIED_SNAG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, entry.getValue()), 2);
		}
	}

	// Lets the fallen tree rest on uneven ground instead of floating or clipping into it.
	private int groundShift(WorldGenLevel level, int x, int z) {
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		for (int y = center.getY() + 1; y >= center.getY() - 2; y--) {
			cursor.set(x, y - 1, z);
			BlockState ground = level.getBlockState(cursor);
			if (ground.isAir() || ground.canBeReplaced() || !ground.getFluidState().isEmpty()) {
				continue;
			}
			cursor.set(x, y, z);
			BlockState above = level.getBlockState(cursor);
			if (above.isAir() || above.canBeReplaced()) {
				return y - center.getY();
			}
		}
		return 0;
	}

	private Map<BlockPos, Direction.Axis> buildTree() {
		RandomSource random = new XoroshiroRandomSource(seed);
		if (variant == PetrifiedTreeVariant.CHOPPED) {
			return buildStump(random);
		}
		if (variant == PetrifiedTreeVariant.FALLEN) {
			return buildFallen(random);
		}
		Map<BlockPos, Direction.Axis> logs = new HashMap<>();
		int x = center.getX();
		int y = center.getY();
		int z = center.getZ();
		int height = 6 + random.nextInt(2);
		int crownRadius = 3 + random.nextInt(2);
		int lowest = height * 2 / 5;

		for (int dy = -2; dy < height; dy++) {
			logs.put(new BlockPos(x, y + dy, z), Direction.Axis.Y);
		}

		buildRoots(logs, random, x, y, z);

		for (int i = 0; i < 2; i++) {
			int stubDy = 1 + random.nextInt(Math.max(1, lowest));
			float angle = random.nextFloat() * Mth.TWO_PI;
			int length = 1 + random.nextInt(2);
			BlockPos start = new BlockPos(x, y + stubDy, z);
			line(logs, start, start.offset(Math.round(Mth.cos(angle) * length), random.nextInt(2), Math.round(Mth.sin(angle) * length)));
		}

		int nodeCount = 5 + random.nextInt(2);
		float spin = random.nextFloat() * Mth.TWO_PI;
		for (int i = 0; i < nodeCount; i++) {
			float t = 0.55F + 0.4F * i / (nodeCount - 1) + (random.nextFloat() - 0.5F) * 0.08F;
			float shape = Math.max(0.25F, 1.0F - (t - 0.68F) * (t - 0.68F) / 0.16F);
			float reach = Math.max(2.0F, crownRadius * Mth.sqrt(shape) * (0.65F + 0.35F * random.nextFloat()));
			float angle = spin + i * 2.39996F + (random.nextFloat() - 0.5F) * 0.5F;
			int nodeDy = Math.min(height - 1, Math.round(t * height));
			int startDy = Math.max(lowest, nodeDy - Math.round(reach * 0.6F));
			BlockPos start = new BlockPos(x, y + startDy, z);
			BlockPos node = new BlockPos(x + Math.round(Mth.cos(angle) * reach), y + nodeDy, z + Math.round(Mth.sin(angle) * reach));
			line(logs, start, node);

			float mid = 0.5F + random.nextFloat() * 0.2F;
			BlockPos forkStart = start.offset(
					Math.round((node.getX() - start.getX()) * mid),
					Math.round((node.getY() - start.getY()) * mid),
					Math.round((node.getZ() - start.getZ()) * mid)
			);
			float forkAngle = angle + (random.nextBoolean() ? 1.0F : -1.0F) * (0.5F + random.nextFloat() * 0.4F);
			int forkLength = 1 + random.nextInt(2);
			line(logs, forkStart, forkStart.offset(Math.round(Mth.cos(forkAngle) * forkLength), 1, Math.round(Mth.sin(forkAngle) * forkLength)));

			int twigs = 1;
			for (int j = 0; j < twigs; j++) {
				float twigAngle = angle + (j == 0 ? 1.0F : -1.0F) * (0.5F + random.nextFloat() * 0.5F);
				int twigLength = 1 + random.nextInt(2);
				line(logs, node, node.offset(Math.round(Mth.cos(twigAngle) * twigLength), 1, Math.round(Mth.sin(twigAngle) * twigLength)));
			}
		}

		BlockPos crown = new BlockPos(x, y + height - 1, z);
		for (int i = 0; i < 2; i++) {
			float angle = random.nextFloat() * Mth.TWO_PI;
			int length = 1;
			int rise = 1 + random.nextInt(2);
			line(logs, crown, crown.offset(Math.round(Mth.cos(angle) * length), rise, Math.round(Mth.sin(angle) * length)));
		}

		logs.keySet().removeIf(pos -> pos.getY() > y + 6 || Math.abs(pos.getX() - x) > 4 || Math.abs(pos.getZ() - z) > 4);

		return logs;
	}

	private Map<BlockPos, Direction.Axis> buildStump(RandomSource random) {
		Map<BlockPos, Direction.Axis> logs = new HashMap<>();
		int x = center.getX();
		int y = center.getY();
		int z = center.getZ();

		// 2x2 trunk, sunk into the ground, sawn off at a slightly ragged height.
		int baseTop = 1 + random.nextInt(2);
		int splinterColumn = random.nextInt(4);
		for (int i = 0; i < 4; i++) {
			int cx = x + (i & 1);
			int cz = z + (i >> 1);
			int top = baseTop + (i == splinterColumn ? 1 : 0) - (random.nextInt(4) == 0 ? 1 : 0);
			for (int dy = -3; dy <= top; dy++) {
				logs.put(new BlockPos(cx, y + dy, cz), Direction.Axis.Y);
			}
		}

		// Grand roots: buttress flares along the surface plus deeper roots that dive into the ground.
		int count = 7 + random.nextInt(2);
		float spin = random.nextFloat() * Mth.TWO_PI;
		for (int i = 0; i < count; i++) {
			float angle = spin + i * Mth.TWO_PI / count + (random.nextFloat() - 0.5F) * 0.5F;
			float cos = Mth.cos(angle);
			float sin = Mth.sin(angle);
			int length = 4 + random.nextInt(3);
			boolean surface = i % 2 == 0;
			BlockPos from = new BlockPos(x + (cos > 0 ? 1 : 0), y + (surface ? 0 : -1), z + (sin > 0 ? 1 : 0));
			BlockPos tip = from.offset(Math.round(cos * length), surface ? -2 : -3, Math.round(sin * length));
			line(logs, from, tip);

			float mid = 0.4F + random.nextFloat() * 0.2F;
			BlockPos forkStart = from.offset(
					Math.round((tip.getX() - from.getX()) * mid),
					Math.round((tip.getY() - from.getY()) * mid),
					Math.round((tip.getZ() - from.getZ()) * mid)
			);
			float forkAngle = angle + (random.nextBoolean() ? 1.0F : -1.0F) * (0.5F + random.nextFloat() * 0.4F);
			int forkLength = 2 + random.nextInt(2);
			line(logs, forkStart, forkStart.offset(Math.round(Mth.cos(forkAngle) * forkLength), -1 - random.nextInt(2), Math.round(Mth.sin(forkAngle) * forkLength)));
		}

		logs.keySet().removeIf(pos -> pos.getY() > y + 3 || Math.abs(pos.getX() - x) > 9 || Math.abs(pos.getZ() - z) > 9);
		return logs;
	}

	private Map<BlockPos, Direction.Axis> buildFallen(RandomSource random) {
		Map<BlockPos, Direction.Axis> logs = new HashMap<>();
		boolean alongX = random.nextBoolean();
		int dir = random.nextBoolean() ? 1 : -1;
		Direction.Axis trunkAxis = alongX ? Direction.Axis.X : Direction.Axis.Z;
		int length = 8 + random.nextInt(3);
		int start = -(length / 2);
		int end = start + length - 1;

		// 1x1 trunk lying on the ground, with a single sideways kink partway along.
		int kinkAt = start + 3 + random.nextInt(length - 6);
		int kink = random.nextInt(3) == 0 ? 0 : (random.nextBoolean() ? 1 : -1);
		for (int i = start; i <= end; i++) {
			int across = i >= kinkAt ? kink : 0;
			logs.put(fallenPos(alongX, dir, i, across, 0), trunkAxis);
			if (i == kinkAt && kink != 0) {
				logs.put(fallenPos(alongX, dir, i, 0, 0), trunkAxis);
			}
		}

		// Torn-up root plate fanning out from the base end.
		BlockPos base = fallenPos(alongX, dir, start, 0, 0);
		int roots = 5 + random.nextInt(2);
		float spin = random.nextFloat() * Mth.TWO_PI;
		for (int i = 0; i < roots; i++) {
			float angle = spin + i * Mth.TWO_PI / roots + (random.nextFloat() - 0.5F) * 0.5F;
			int reach = 2 + random.nextInt(2);
			int across = Math.round(Mth.cos(angle) * reach);
			int up = Math.max(-1, Math.round(Mth.sin(angle) * reach));
			BlockPos tip = fallenPos(alongX, dir, start - 1 - random.nextInt(2), across, up);
			line(logs, base, tip);
		}

		// Dead branch stubs jutting up and out of the trunk.
		int branches = 3 + random.nextInt(2);
		for (int i = 0; i < branches; i++) {
			int at = start + 2 + random.nextInt(length - 3);
			int across = at >= kinkAt ? kink : 0;
			int side = random.nextBoolean() ? 1 : -1;
			BlockPos from = fallenPos(alongX, dir, at, across, 0);
			BlockPos tip = fallenPos(alongX, dir, at + random.nextInt(3) - 1, across + side * (1 + random.nextInt(2)), 2 + random.nextInt(2));
			line(logs, from, tip);
			if (random.nextBoolean()) {
				line(logs, tip, tip.above().relative(Direction.fromAxisAndDirection(alongX ? Direction.Axis.Z : Direction.Axis.X, side > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE)));
			}
		}

		// Splintered break at the far end.
		if (random.nextBoolean()) {
			logs.put(fallenPos(alongX, dir, end, end >= kinkAt ? kink : 0, 1), Direction.Axis.Y);
		}

		logs.keySet().removeIf(pos -> pos.getY() > center.getY() + 5 || pos.getY() < center.getY() - 2 || Math.abs(pos.getX() - center.getX()) > 10 || Math.abs(pos.getZ() - center.getZ()) > 10);
		return logs;
	}

	private BlockPos fallenPos(boolean alongX, int dir, int along, int across, int up) {
		int a = along * dir;
		return new BlockPos(center.getX() + (alongX ? a : across), center.getY() + up, center.getZ() + (alongX ? across : a));
	}

	private static void buildRoots(Map<BlockPos, Direction.Axis> logs, RandomSource random, int x, int y, int z) {
		int count = 3;
		float spin = random.nextFloat() * Mth.TWO_PI;
		BlockPos from = new BlockPos(x, y - 1, z);
		for (int i = 0; i < count; i++) {
			float angle = spin + i * Mth.TWO_PI / count + (random.nextFloat() - 0.5F) * 0.6F;
			int length = 2 + random.nextInt(2);
			line(logs, from, from.offset(Math.round(Mth.cos(angle) * length), -2, Math.round(Mth.sin(angle) * length)));
		}
	}

	private static void line(Map<BlockPos, Direction.Axis> logs, BlockPos from, BlockPos to) {
		int dx = to.getX() - from.getX();
		int dy = to.getY() - from.getY();
		int dz = to.getZ() - from.getZ();
		int ax = Math.abs(dx);
		int ay = Math.abs(dy);
		int az = Math.abs(dz);
		int steps = Math.max(ax, Math.max(ay, az));
		Direction.Axis axis = ay >= ax && ay >= az ? Direction.Axis.Y : ax >= az ? Direction.Axis.X : Direction.Axis.Z;

		for (int step = 0; step <= steps; step++) {
			float t = steps == 0 ? 0.0F : step / (float) steps;
			logs.putIfAbsent(
					new BlockPos(
							from.getX() + Math.round(dx * t),
							from.getY() + Math.round(dy * t),
							from.getZ() + Math.round(dz * t)
					),
					axis
			);
		}
	}
}
