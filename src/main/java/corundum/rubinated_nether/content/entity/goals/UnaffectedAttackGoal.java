package corundum.rubinated_nether.content.entity.goals;

import corundum.rubinated_nether.content.entity.BronzeEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;

public class UnaffectedAttackGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    private final BronzeEntity entity;

    public UnaffectedAttackGoal(BronzeEntity entity, Class<T> targetType) {
        super(entity, targetType, true);
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return entity.getTarnishLevel() == 0 && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return entity.getTarnishLevel() == 0 && super.canContinueToUse();
    }

    @Override
    public void start() {
        super.start();
        if(!entity.level().isClientSide){
            entity.level().broadcastEntityEvent(entity, (byte) 61);
        }
    }

    @Override
    public void tick(){
        if(!entity.level().isClientSide){
            entity.level().broadcastEntityEvent(entity, (byte) 61);
        }

        if(entity.getTarnishLevel() != 0) stop();
    }
    @Override
    public void stop() {
        super.stop();
        if(!entity.level().isClientSide){
            entity.level().broadcastEntityEvent(entity, (byte) 64);
        }
    }
}
