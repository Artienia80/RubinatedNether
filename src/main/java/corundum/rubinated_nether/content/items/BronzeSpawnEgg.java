package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.minecraft.world.InteractionResult;

import java.util.function.Supplier;

public class BronzeSpawnEgg extends DeferredSpawnEggItem {
    private final TarnishStage tarnishStage;

    public BronzeSpawnEgg(Supplier<? extends EntityType<? extends Mob>> entityType,
                          TarnishStage tarnishStage,
                          int backgroundColor,
                          int highlightColor,
                          Item.Properties properties) {
        super(entityType, backgroundColor, highlightColor, properties);
        this.tarnishStage = tarnishStage;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ItemStack itemstack = context.getItemInHand();
        BlockPos blockpos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos spawnPos = blockpos.relative(direction);

        EntityType<?> entityType = this.getType(itemstack);
        if (entityType != null) {
            var entity = entityType.spawn(
                    (ServerLevel) level,
                    itemstack,
                    context.getPlayer(),
                    spawnPos,
                    net.minecraft.world.entity.MobSpawnType.SPAWN_EGG,
                    true,
                    !blockpos.equals(spawnPos) && direction == Direction.UP
            );

            if (entity instanceof BronzeEntity bronze) {
                bronze.setTarnishLevel(this.tarnishStage);
            }

            if (entity != null) {
                itemstack.shrink(1);
                level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
            }

            return InteractionResult.CONSUME;
        }

        return InteractionResult.FAIL;
    }
}