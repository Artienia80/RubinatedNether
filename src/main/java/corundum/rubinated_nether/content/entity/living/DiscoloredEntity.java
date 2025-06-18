package corundum.rubinated_nether.content.entity.living;

import corundum.rubinated_nether.content.BronzeTarnishingStep;
import corundum.rubinated_nether.content.RNEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

public final class DiscoloredEntity extends AbstractBronzeEntity {

    public DiscoloredEntity(EntityType<? extends AbstractBronzeEntity> entityType, Level level) {
        super(entityType, level);
    }

    public DiscoloredEntity(Level level) {
        super(RNEntities.DISCOLORED_ENTITY.get(), level);
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
        return BronzeTarnishingStep.DISCOLORED;
    }

    @Override
    public BronzeTarnishingStep getNextTarnishingLevel() {
        return BronzeTarnishingStep.CORRODED;
    }

    @Override
    public BronzeTarnishingStep getPreviousTarnishingLevel() {
        return BronzeTarnishingStep.BRONZE;
    }
}
