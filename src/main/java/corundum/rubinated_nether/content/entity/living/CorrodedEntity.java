package corundum.rubinated_nether.content.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public final class CorrodedEntity extends AbstractBronzeEntity {
    private CorrodedEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }
}
