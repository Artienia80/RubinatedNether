package corundum.rubinated_nether.content.blocks.entities.gearbox;

import com.mojang.datafixers.util.Either;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNEntityCreator;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.GearboxBlock;
import corundum.rubinated_nether.content.blocks.TarnishingBronze;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import javax.annotation.Nullable;

public class GearboxBlockEntity extends BlockEntity implements Spawner, TickableBlockEntity {
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


    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.spawner.load(this.level, this.worldPosition, tag);
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.spawner.save(tag);
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

    @Override
    public void tick() {
        if(level.getBlockEntity(this.worldPosition) instanceof GearboxBlockEntity blockEntity) {
            if(level.getBlockState(this.worldPosition).getValue(GearboxBlock.LIT)) {
                blockEntity.getSpawner().serverTick((ServerLevel) level, this.worldPosition);
            }
        }
    }
}
