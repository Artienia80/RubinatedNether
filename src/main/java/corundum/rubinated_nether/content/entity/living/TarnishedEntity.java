package corundum.rubinated_nether.content.entity.living;

import corundum.rubinated_nether.content.BronzeTarnishingStep;
import corundum.rubinated_nether.content.RNEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public final class TarnishedEntity extends AbstractBronzeEntity {
    public TarnishedEntity(EntityType<? extends AbstractBronzeEntity> entityType, Level level) {
        super(entityType, level);
    }

    public TarnishedEntity(Level level) {
        super(RNEntities.TARNISHED_ENTITY.get(), level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.FOLLOW_RANGE, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    public BronzeTarnishingStep getTarnishingLevel() {
        return BronzeTarnishingStep.TARNISHED;
    }

    @Override
    public BronzeTarnishingStep getNextTarnishingLevel() {
        return null;
    }

    @Override
    public BronzeTarnishingStep getPreviousTarnishingLevel() {
        return BronzeTarnishingStep.CORRODED;
    }
}
