package corundum.rubinated_nether.content.world.structures.shrine;

import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.world.structures.RNStructureTypes;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RubinationShrineStructure extends Structure {
	public static final MapCodec<RubinationShrineStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			JigsawStructure.CODEC.forGetter(structure -> structure.delegate),
			Codec.intRange(1, 6).optionalFieldOf("perimeter_width", 3).forGetter(structure -> structure.perimeterWidth),
			Codec.intRange(0, 127).optionalFieldOf("expected_lava_level", 31).forGetter(structure -> structure.expectedLavaLevel),
			Codec.intRange(0, 8).optionalFieldOf("lava_level_search_range", 2).forGetter(structure -> structure.levelSearchRange),
			Codec.intRange(1, 64).optionalFieldOf("clearance_height", 28).forGetter(structure -> structure.clearanceHeight)
	).apply(instance, RubinationShrineStructure::new));

	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int BIOME_MARGIN = 16;

	private final JigsawStructure delegate;
	private final int perimeterWidth;
	private final int expectedLavaLevel;
	private final int levelSearchRange;
	private final int clearanceHeight;

	public RubinationShrineStructure(JigsawStructure delegate, int perimeterWidth, int expectedLavaLevel, int levelSearchRange, int clearanceHeight) {
		super(delegate.modifiableStructureInfo().getOriginalStructureInfo().structureSettings());
		this.delegate = delegate;
		this.perimeterWidth = perimeterWidth;
		this.expectedLavaLevel = expectedLavaLevel;
		this.levelSearchRange = levelSearchRange;
		this.clearanceHeight = clearanceHeight;
	}

	@Override
	protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
		Optional<GenerationStub> stubOptional = delegate.findValidGenerationPoint(context);
		if (stubOptional.isEmpty()) {
			return Optional.empty();
		}

		GenerationStub stub = stubOptional.get();
		StructurePiecesBuilder builder = stub.getPiecesBuilder();
		List<StructurePiece> pieces = builder.build().pieces();
		if (pieces.isEmpty()) {
			return Optional.empty();
		}

		BoundingBox footprint = pieces.get(0).getBoundingBox();
		int[] level = new int[1];
		String failure = check(context, footprint, level);
		if (failure != null) {
			LOGGER.info("Rubination shrine rejected at {} box {}: {}", context.chunkPos(), footprint, failure);
			return Optional.empty();
		}

		LOGGER.info("Rubination shrine accepted at {} box {} lava level {}", context.chunkPos(), footprint, level[0]);
		return Optional.of(new GenerationStub(stub.position(), Either.right(builder)));
	}

	private String check(GenerationContext context, BoundingBox box, int[] chosenLevel) {
		if (nearBasaltDeltas(context, box)) {
			return "basalt deltas within " + BIOME_MARGIN + " blocks";
		}

		Columns columns = new Columns(context);
		int top = box.maxY();
		for (int x = box.minX(); x <= box.maxX(); x++) {
			for (int z = box.minZ(); z <= box.maxZ(); z++) {
				NoiseColumn column = columns.get(x, z);
				for (int y = top + 1; y <= top + clearanceHeight; y++) {
					if (!column.getBlock(y).isAir()) {
						return "obstructed above entrance at " + x + "," + y + "," + z;
					}
				}
			}
		}

		for (int offset = 0; offset <= levelSearchRange; offset++) {
			if (ringIsOpenLava(columns, box, expectedLavaLevel + offset)) {
				chosenLevel[0] = expectedLavaLevel + offset;
				return null;
			}
			if (offset > 0 && ringIsOpenLava(columns, box, expectedLavaLevel - offset)) {
				chosenLevel[0] = expectedLavaLevel - offset;
				return null;
			}
		}
		return "no open lava ring near level " + expectedLavaLevel;
	}

	private boolean nearBasaltDeltas(GenerationContext context, BoundingBox box) {
		int[] heights = {expectedLavaLevel, box.maxY()};
		for (int x = box.minX() - BIOME_MARGIN; x <= box.maxX() + BIOME_MARGIN; x += 4) {
			for (int z = box.minZ() - BIOME_MARGIN; z <= box.maxZ() + BIOME_MARGIN; z += 4) {
				for (int y : heights) {
					boolean deltas = context.biomeSource().getNoiseBiome(
							QuartPos.fromBlock(x),
							QuartPos.fromBlock(y),
							QuartPos.fromBlock(z),
							context.randomState().sampler()
					).is(Biomes.BASALT_DELTAS);
					if (deltas) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean ringIsOpenLava(Columns columns, BoundingBox box, int level) {
		int width = perimeterWidth;
		int top = box.maxY() + clearanceHeight;
		for (int x = box.minX() - width; x <= box.maxX() + width; x++) {
			for (int z = box.minZ() - width; z <= box.maxZ() + width; z++) {
				boolean insideFootprint = x >= box.minX() && x <= box.maxX() && z >= box.minZ() && z <= box.maxZ();
				if (insideFootprint) {
					continue;
				}
				NoiseColumn column = columns.get(x, z);
				if (!column.getBlock(level).is(Blocks.LAVA)) {
					return false;
				}
				for (int y = level + 1; y <= top; y++) {
					if (!column.getBlock(y).isAir()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public StructureType<?> type() {
		return RNStructureTypes.RUBINATION_SHRINE.get();
	}

	public static void verify(ServerStartingEvent event) {
		Registry<Structure> registry = event.getServer().registryAccess().registryOrThrow(Registries.STRUCTURE);
		boolean found = false;
		for (Map.Entry<ResourceKey<Structure>, Structure> entry : registry.entrySet()) {
			var id = entry.getKey().location();
			if (!id.getNamespace().equals(RubinatedNether.MODID) || !id.getPath().contains("shrine")) {
				continue;
			}
			found = true;
			if (entry.getValue() instanceof RubinationShrineStructure) {
				LOGGER.info("Structure {} uses lava validation", id);
			} else {
				LOGGER.error("Structure {} uses type {} and is NOT lava validated; set its type to {}:rubination_shrine",
						id, BuiltInRegistries.STRUCTURE_TYPE.getKey(entry.getValue().type()), RubinatedNether.MODID);
			}
		}
		if (!found) {
			LOGGER.warn("No {} structure with 'shrine' in its id was found", RubinatedNether.MODID);
		}
	}

	private static final class Columns {
		private final GenerationContext context;
		private final Long2ObjectOpenHashMap<NoiseColumn> cache = new Long2ObjectOpenHashMap<>();

		private Columns(GenerationContext context) {
			this.context = context;
		}

		private NoiseColumn get(int x, int z) {
			long key = ChunkPos.asLong(x, z);
			NoiseColumn column = cache.get(key);
			if (column == null) {
				column = context.chunkGenerator().getBaseColumn(x, z, context.heightAccessor(), context.randomState());
				cache.put(key, column);
			}
			return column;
		}
	}
}
