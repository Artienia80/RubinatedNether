package corundum.rubinated_nether.content.world.structures.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.world.structures.RNStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;

public class PetrifiedTreeStructure extends Structure {
	public static final MapCodec<PetrifiedTreeStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			settingsCodec(instance),
			PetrifiedTreeVariant.CODEC.fieldOf("variant").forGetter(structure -> structure.variant)
	).apply(instance, PetrifiedTreeStructure::new));

	private static final int CHECK_OFFSET = 6;
	private static final int MAX_SLOPE = 8;
	private static final int MAX_SCAN_Y = 100;
	private static final int[][] CHECK_OFFSETS = {
			{CHECK_OFFSET, 0},
			{-CHECK_OFFSET, 0},
			{0, CHECK_OFFSET},
			{0, -CHECK_OFFSET}
	};

	private final PetrifiedTreeVariant variant;

	public PetrifiedTreeStructure(StructureSettings settings, PetrifiedTreeVariant variant) {
		super(settings);
		this.variant = variant;
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		ChunkPos chunkPos = context.chunkPos();
		int x = chunkPos.getMiddleBlockX();
		int z = chunkPos.getMiddleBlockZ();
		int y = floorY(context, x, z);
		if (y < 0 || !isValidBiome(context, x, y, z)) {
			return Optional.empty();
		}

		for (int[] offset : CHECK_OFFSETS) {
			int ox = x + offset[0];
			int oz = z + offset[1];
			int oy = floorY(context, ox, oz);
			if (oy < 0 || Math.abs(oy - y) > MAX_SLOPE || !isValidBiome(context, ox, oy, oz)) {
				return Optional.empty();
			}
		}

		BlockPos center = new BlockPos(x, y, z);
		int radius = 5 + context.random().nextInt(2);
		float phase = context.random().nextFloat() * Mth.TWO_PI;
		long seed = context.random().nextLong();

		return Optional.of(new GenerationStub(
				center,
				builder -> builder.addPiece(new PetrifiedTreePiece(center, radius, phase, seed, variant))
		));
	}

	private static int floorY(GenerationContext context, int x, int z) {
		ChunkGenerator generator = context.chunkGenerator();
		NoiseColumn column = generator.getBaseColumn(x, z, context.heightAccessor(), context.randomState());
		for (int y = generator.getSeaLevel(); y < MAX_SCAN_Y; y++) {
			BlockState below = column.getBlock(y - 1);
			if (column.getBlock(y).isAir() && !below.isAir() && below.getFluidState().isEmpty()) {
				return y;
			}
		}
		return -1;
	}

	private static boolean isValidBiome(GenerationContext context, int x, int y, int z) {
		return context.validBiome().test(
				context.biomeSource().getNoiseBiome(
						QuartPos.fromBlock(x),
						QuartPos.fromBlock(y),
						QuartPos.fromBlock(z),
						context.randomState().sampler()
				)
		);
	}

	@Override
	public StructureType<?> type() {
		return RNStructureTypes.PETRIFIED_TREE.get();
	}
}
