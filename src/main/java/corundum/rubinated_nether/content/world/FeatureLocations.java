package corundum.rubinated_nether.content.world;

import corundum.rubinated_nether.RubinatedNether;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public final class FeatureLocations {
	private static final String STORAGE = RubinatedNether.MODID + "_feature_locations";
	private static final Queue<Pending> PENDING = new ConcurrentLinkedQueue<>();

	private record Pending(ResourceKey<Level> dimension, String id, long pos) {
	}

	private FeatureLocations() {
	}

	public static void record(WorldGenLevel level, String id, BlockPos pos) {
		PENDING.add(new Pending(level.getLevel().dimension(), id, pos.asLong()));
	}

	public static List<BlockPos> positions(ServerLevel level, String id) {
		return data(level).positions(id);
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		Pending pending;
		while ((pending = PENDING.poll()) != null) {
			ServerLevel level = event.getServer().getLevel(pending.dimension());
			if (level != null) {
				data(level).add(pending.id(), pending.pos());
			}
		}
	}

	@SubscribeEvent
	public static void onServerStopped(ServerStoppedEvent event) {
		PENDING.clear();
	}

	private static Data data(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(Data.FACTORY, STORAGE);
	}

	private static final class Data extends SavedData {
		private static final SavedData.Factory<Data> FACTORY = new SavedData.Factory<>(Data::new, Data::load, null);

		private final Map<String, LongOpenHashSet> entries = new HashMap<>();

		private static Data load(CompoundTag tag, HolderLookup.Provider registries) {
			Data data = new Data();
			for (String id : tag.getAllKeys()) {
				data.entries.put(id, new LongOpenHashSet(tag.getLongArray(id)));
			}
			return data;
		}

		@Override
		public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
			for (Map.Entry<String, LongOpenHashSet> entry : entries.entrySet()) {
				tag.putLongArray(entry.getKey(), entry.getValue().toLongArray());
			}
			return tag;
		}

		private void add(String id, long pos) {
			if (entries.computeIfAbsent(id, key -> new LongOpenHashSet()).add(pos)) {
				setDirty();
			}
		}

		private List<BlockPos> positions(String id) {
			List<BlockPos> result = new ArrayList<>();
			LongOpenHashSet set = entries.get(id);
			if (set != null) {
				set.forEach(pos -> result.add(BlockPos.of(pos)));
			}
			return result;
		}
	}
}
