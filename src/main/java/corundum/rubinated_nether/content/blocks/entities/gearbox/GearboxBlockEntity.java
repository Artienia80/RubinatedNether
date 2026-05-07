package corundum.rubinated_nether.content.blocks.entities.gearbox;

import com.mojang.datafixers.util.Either;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.blocks.GearboxBlock;
import corundum.rubinated_nether.utils.TickableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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

import javax.annotation.Nullable;

public class GearboxBlockEntity extends BlockEntity implements Spawner, TickableBlockEntity {
    private final BaseSpawner spawner = new BaseSpawner() {
        public void broadcastEvent(Level level, BlockPos blockPos, int id) {
            level.blockEvent(blockPos, level.getBlockState(pos).getBlock(), id, 0);
        }

        public void setNextSpawnData(@Nullable Level level, BlockPos blockPos, SpawnData spawnData) {
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

    private final BlockPos pos;

    public GearboxBlockEntity(BlockPos pos, BlockState blockState) {
        super(RNBlockEntities.GEARBOX.get(), pos, blockState);
        this.pos = pos;
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
        if(!level.isClientSide() && level.getBlockEntity(pos) instanceof GearboxBlockEntity blockEntity) {
            var gearbox = (GearboxBlock) level.getBlockState(pos).getBlock();
            if(level.getBlockState(pos).getValue(GearboxBlock.LIT)) {
                blockEntity.getSpawner().serverTick((ServerLevel) level, pos);
            }
        }
    }


}
