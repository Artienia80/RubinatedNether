package net.artienia.rubinated_nether.content.block.brazier;

import net.artienia.rubinated_nether.config.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import uwu.serenity.critter.utils.TickableBlockEntity;

import java.util.function.Predicate;

public class BrazierBlockEntity extends BlockEntity implements TickableBlockEntity {

    public BrazierBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void tick() {

        int x = worldPosition.getX();
        int y = worldPosition.getY();
        int z = worldPosition.getZ();

        AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierRange);

        Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierRange)
            .and(EntitySelector.NO_SPECTATORS);

        level.getEntitiesOfClass(ServerPlayer.class, area, selector).forEach(entity -> {
            if(entity.tickCount % 100 == 0) {
                entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Mth.floor(20 * RNConfig.brazierDuration)));
            }
        });
    }
}
