package corundum.rubinated_nether.content.entity.living;

import corundum.rubinated_nether.content.BronzeTarnishingStep;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public final class TarnishedEntity extends AbstractBronzeEntity {
    private TarnishedEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public BronzeTarnishingStep getTarnishingLevel() {
        return BronzeTarnishingStep.TARNISHED;
    }
}
