package corundum.rubinated_nether.content.blocks.entities.gearbox;

import com.mojang.datafixers.util.Either;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNEntityCreator;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.GearboxBlock;
import corundum.rubinated_nether.content.blocks.TarnishingBronze;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GearboxBlockEntity extends BlockEntity implements Spawner, TickableBlockEntity {

    // Spawner state
    private int totalBronzes = 0;
    private int bronzesSpawned = 0;
    private int waveSize = 0;
    private int waveDelayTicks = 0;
    private int waveTimer = 0;
    private boolean spawning = false;
    private final List<UUID> aliveWaveBronzes = new ArrayList<>();

    // All bronzes ever spawned this run — tracked for cooldown condition
    private final List<UUID> allSpawnedBronzes = new ArrayList<>();

    // Cooldown state — waits for all bronzes to die rather than a timer
    private boolean coolingDown = false;

    private final BaseSpawner spawner = new BaseSpawner() {
        public void broadcastEvent(Level level, BlockPos blockPos, int id) {
            level.blockEvent(blockPos, level.getBlockState(blockPos).getBlock(), id, 0);
        }

        public void setNextSpawnData(Level level, BlockPos blockPos, SpawnData spawnData) {
            if (level != null) {
                if (level.getBlockState(blockPos).getBlock() instanceof TarnishingBronze block) {
                    spawnData.getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(RNEntityCreator.BRONZE.get()).toString());
                    spawnData.getEntityToSpawn().putInt("TarnishStage", block.getAge().getId());
                }
            }
            super.setNextSpawnData(level, blockPos, spawnData);
            if (level != null) {
                BlockState blockstate = level.getBlockState(blockPos);
                level.sendBlockUpdated(blockPos, blockstate, blockstate, 4);
            }
        }

        public Either<BlockEntity, Entity> getOwner() {
            return Either.left(GearboxBlockEntity.this);
        }
    };

    public GearboxBlockEntity(BlockPos pos, BlockState blockState) {
        super(RNBlockEntities.GEARBOX.get(), pos, blockState);
    }

    public void startSpawning(int total, int waveSize, int waveDelayTicks) {
        this.totalBronzes = total;
        this.bronzesSpawned = 0;
        this.waveSize = waveSize;
        this.waveDelayTicks = waveDelayTicks;
        this.waveTimer = 0;
        this.spawning = true;
        this.aliveWaveBronzes.clear();
        this.allSpawnedBronzes.clear();
        this.setChanged();
    }

    public void forceReset() {
        this.spawning = false;
        this.coolingDown = false;
        this.totalBronzes = 0;
        this.bronzesSpawned = 0;
        this.waveTimer = 0;
        this.aliveWaveBronzes.clear();
        this.allSpawnedBronzes.clear();
        this.setChanged();
    }

    @Override
    public void tick() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        // Safety net: if peaceful sneaks through, abort everything
        if (serverLevel.getDifficulty() == Difficulty.PEACEFUL && (spawning || coolingDown)) {
            forceReset();
            return;
        }

        // Cooldown: wait for all spawned bronzes to die or despawn
        if (coolingDown) {
            allSpawnedBronzes.removeIf(uuid -> {
                Entity e = serverLevel.getEntity(uuid);
                return e == null || e.isRemoved();
            });

            if (allSpawnedBronzes.isEmpty()) {
                coolingDown = false;
                BlockState state = serverLevel.getBlockState(this.worldPosition);
                if (state.getBlock() instanceof GearboxBlock gearboxBlock) {
                    gearboxBlock.finishCooldown(serverLevel, state, this.worldPosition);
                }
            }
            return;
        }

        if (!spawning) return;

        BlockState state = serverLevel.getBlockState(this.worldPosition);
        if (!state.getValue(GearboxBlock.LIT)) return;

        // Prune dead bronzes from wave tracking
        aliveWaveBronzes.removeIf(uuid -> {
            Entity e = serverLevel.getEntity(uuid);
            return e == null || e.isRemoved();
        });

        // If all bronzes from this wave are dead, skip remaining wait
        if (aliveWaveBronzes.isEmpty() && waveTimer > 0) {
            waveTimer = 0;
        }

        // Count down between waves
        if (waveTimer > 0) {
            waveTimer--;
            return;
        }

        // All waves done — enter cooldown
        if (bronzesSpawned >= totalBronzes) {
            finishSpawning(serverLevel, state);
            return;
        }

        // Spawn next wave
        int toSpawn = Math.min(waveSize, totalBronzes - bronzesSpawned);
        spawnWave(serverLevel, toSpawn);
        bronzesSpawned += toSpawn;
        waveTimer = waveDelayTicks;
        this.setChanged();
    }

    private void spawnWave(ServerLevel level, int count) {
        BlockPos pos = this.worldPosition;
        int tarnishId = 0;
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof TarnishingBronze tb) {
            tarnishId = tb.getAge().getId();
        }

        for (int i = 0; i < count; i++) {
            BronzeEntity bronze = RNEntityCreator.BRONZE.get().create(level);
            if (bronze == null) continue;

            double ox = (level.getRandom().nextDouble() - 0.5) * 4.0;
            double oz = (level.getRandom().nextDouble() - 0.5) * 4.0;
            double spawnX = pos.getX() + 0.5 + ox;
            double spawnY = pos.getY() + 1.0;
            double spawnZ = pos.getZ() + 0.5 + oz;

            bronze.moveTo(spawnX, spawnY, spawnZ, 0f, 0f);
            bronze.setTarnishLevel(TarnishStage.byId(tarnishId));
            level.addFreshEntity(bronze);

            aliveWaveBronzes.add(bronze.getUUID());
            allSpawnedBronzes.add(bronze.getUUID());

            // Spawner-style flame and smoke particles
            for (int p = 0; p < 20; p++) {
                double px = spawnX + (level.getRandom().nextDouble() - 0.5) * 0.8;
                double py = spawnY + level.getRandom().nextDouble() * 1.4;
                double pz = spawnZ + (level.getRandom().nextDouble() - 0.5) * 0.8;
                double vx = (level.getRandom().nextDouble() - 0.5) * 0.2;
                double vy = level.getRandom().nextDouble() * 0.1;
                double vz = (level.getRandom().nextDouble() - 0.5) * 0.2;
                level.sendParticles(ParticleTypes.FLAME, px, py, pz, 1, vx, vy, vz, 0.0);
                level.sendParticles(ParticleTypes.SMOKE, px, py, pz, 1, vx, vy, vz, 0.0);
            }
        }
    }

    private void finishSpawning(ServerLevel level, BlockState state) {
        spawning = false;
        aliveWaveBronzes.clear();
        // Don't clear allSpawnedBronzes — cooldown watches those
        state = state.setValue(GearboxBlock.LIT, false)
                .setValue(GearboxBlock.COOLING_DOWN, true);
        level.setBlock(this.worldPosition, state, 3);
        coolingDown = true;
        this.setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.spawner.load(this.level, this.worldPosition, tag);
        this.totalBronzes = tag.getInt("TotalBronzes");
        this.bronzesSpawned = tag.getInt("BronzesSpawned");
        this.waveSize = tag.getInt("WaveSize");
        this.waveDelayTicks = tag.getInt("WaveDelayTicks");
        this.waveTimer = tag.getInt("WaveTimer");
        this.spawning = tag.getBoolean("Spawning");
        this.coolingDown = tag.getBoolean("CoolingDown");

        this.aliveWaveBronzes.clear();
        if (tag.contains("AliveWaveBronzes")) {
            int[] uuidPairs = tag.getIntArray("AliveWaveBronzes");
            for (int i = 0; i + 3 < uuidPairs.length; i += 4) {
                long msb = ((long) uuidPairs[i] << 32) | (uuidPairs[i + 1] & 0xFFFFFFFFL);
                long lsb = ((long) uuidPairs[i + 2] << 32) | (uuidPairs[i + 3] & 0xFFFFFFFFL);
                this.aliveWaveBronzes.add(new UUID(msb, lsb));
            }
        }

        this.allSpawnedBronzes.clear();
        if (tag.contains("AllSpawnedBronzes")) {
            int[] uuidPairs = tag.getIntArray("AllSpawnedBronzes");
            for (int i = 0; i + 3 < uuidPairs.length; i += 4) {
                long msb = ((long) uuidPairs[i] << 32) | (uuidPairs[i + 1] & 0xFFFFFFFFL);
                long lsb = ((long) uuidPairs[i + 2] << 32) | (uuidPairs[i + 3] & 0xFFFFFFFFL);
                this.allSpawnedBronzes.add(new UUID(msb, lsb));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.spawner.save(tag);
        tag.putInt("TotalBronzes", this.totalBronzes);
        tag.putInt("BronzesSpawned", this.bronzesSpawned);
        tag.putInt("WaveSize", this.waveSize);
        tag.putInt("WaveDelayTicks", this.waveDelayTicks);
        tag.putInt("WaveTimer", this.waveTimer);
        tag.putBoolean("Spawning", this.spawning);
        tag.putBoolean("CoolingDown", this.coolingDown);

        tag.putIntArray("AliveWaveBronzes", toIntArray(this.aliveWaveBronzes));
        tag.putIntArray("AllSpawnedBronzes", toIntArray(this.allSpawnedBronzes));
    }

    private static int[] toIntArray(List<UUID> uuids) {
        int[] arr = new int[uuids.size() * 4];
        for (int i = 0; i < uuids.size(); i++) {
            UUID uuid = uuids.get(i);
            arr[i * 4]     = (int) (uuid.getMostSignificantBits() >> 32);
            arr[i * 4 + 1] = (int) (uuid.getMostSignificantBits());
            arr[i * 4 + 2] = (int) (uuid.getLeastSignificantBits() >> 32);
            arr[i * 4 + 3] = (int) (uuid.getLeastSignificantBits());
        }
        return arr;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean onlyOpCanSetNbt() {
        return true;
    }

    public boolean triggerEvent(int id, int type) {
        return this.spawner.onEventTriggered(this.level, id) || super.triggerEvent(id, type);
    }

    public void setEntityId(EntityType<?> type, RandomSource random) {
        this.spawner.setEntityId(type, this.level, random, this.worldPosition);
        this.setChanged();
    }

    public BaseSpawner getSpawner() {
        return this.spawner;
    }
}