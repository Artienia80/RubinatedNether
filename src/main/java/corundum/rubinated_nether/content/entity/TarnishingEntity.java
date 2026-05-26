package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.utils.RNEntityDataSerializers;
import corundum.rubinated_nether.utils.RNParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public abstract class TarnishingEntity extends Monster {

    private static final EntityDataAccessor<Boolean> WAXED =
            SynchedEntityData.defineId(TarnishingEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TARNISH_STAGE =
            SynchedEntityData.defineId(TarnishingEntity.class, EntityDataSerializers.INT);

    private int tarnishTimer = 0;

    protected TarnishingEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        tarnishingTickBehaviour();
        super.tick();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WAXED, false);
        builder.define(TARNISH_STAGE, 0);
    }

    public TarnishStage getTarnishLevel() {
        return TarnishStage.byId(this.entityData.get(TARNISH_STAGE));
    }

    public void setTarnishLevel(TarnishStage stage) {
        this.entityData.set(TARNISH_STAGE, stage.getId());
    }

    public void increaseTarnishLevel() {
        this.setTarnishLevel(
                TarnishStage.byId(this.getTarnishLevel().getId() + 1)
        );
    }

    public void decreaseTarnishLevel() {
        this.setTarnishLevel(
                TarnishStage.byId(this.getTarnishLevel().getId() - 1)
        );
    }

    public boolean isWaxed() {
        return this.entityData.get(WAXED);
    }

    public void setWaxed(boolean waxed) {
        this.entityData.set(WAXED, waxed);
    }

    private int getTarnishInterval(TarnishStage stage) {
        return stage.getTarnishDuration();
    }

    private boolean isNearCatalyst() {
        BlockPos pos = blockPosition();
        for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-5, -5, -5), pos.offset(5, 5, 5))) {
            if (level().getBlockState(nearby).is(corundum.rubinated_nether.content.RNTags.Blocks.CRYSTALLIZATION_CATALYST)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handleDamageEvent(DamageSource damageSource) {
        super.handleDamageEvent(damageSource);
        if (!(damageSource.getDirectEntity() instanceof Player player)) return;

        var stack = damageSource.getWeaponItem();

        if (stack.is(ItemTags.AXES)) {
            if (!isWaxed() && this.getTarnishLevel().getId() > 0 && this.getTarnishLevel().getId() != 4) {
                if(!level().isClientSide())
                    if (level().random.nextFloat() < 0.05f) {
                        this.decreaseTarnishLevel();
                        handleVFX(player);
                        if (level().random.nextFloat() < 0.5f) {
                            ItemEntity powder = new ItemEntity(level(), getX(), getY() + 1, getZ(),
                                    new ItemStack(RNItems.BRONZE_POWDER.get()));
                            level().addFreshEntity(powder);
                        }
                    }
            } else if (this.getTarnishLevel().equals(TarnishStage.CRYSTALLIZED)) {
                if (random.nextFloat() < 0.05f) {
                    this.setTarnishLevel(TarnishStage.UNAFFECTED);
                    handleVFX(player);
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        TarnishStage stage = getTarnishLevel();

        if (stack.is(Items.SOUL_TORCH)) {
            if (!isWaxed()) {
                if(!level().isClientSide())
                    setTarnishLevel(TarnishStage.CRYSTALLIZED);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                this.level().playSound(player, blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 0.8F);
                RNParticleUtils.spawnParticles(this.level(), new Vec3(this.getX(), this.getY(), this.getZ()), 25, 0.5F, 1.75F, ParticleTypes.SOUL_FIRE_FLAME);
                return InteractionResult.sidedSuccess(level().isClientSide());
            }
        }

        if (stack.is(Items.HONEYCOMB) && !isWaxed()) {
            setWaxed(true);
            if (!player.isCreative()) stack.shrink(1);
            this.level().playSound(player, blockPosition(), SoundEvents.HONEYCOMB_WAX_ON, SoundSource.PLAYERS, 1.0F, 0.8F);
            RNParticleUtils.spawnParticles(this.level(), new Vec3(this.getX(), this.getY(), this.getZ()), 15, 0.5F, 1.75F, ParticleTypes.HAPPY_VILLAGER);
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        if(stack.is(ItemTags.AXES) && isWaxed()){
            setWaxed(false);
            handleScrapeEffects(player);
        }

        if (stack.is(RNItems.BRONZE_POWDER.get())) {
            if (!isWaxed() && stage.getId() < 3) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                if(!level().isClientSide()) {
                    // 100% chance in creative, 10% chance in survival
                    float chance = player.isCreative() ? 1.0F : 0.1F;
                    if (level().random.nextFloat() < chance) {
                        increaseTarnishLevel();
                        return InteractionResult.sidedSuccess(level().isClientSide());
                    }
                }
                handleVFX(player);

                return InteractionResult.sidedSuccess(level().isClientSide());
            }
        }


        return super.mobInteract(player, hand);
    }

    private void handleVFX(Player player) {
        this.level().playSound(player, blockPosition(), SoundEvents.AXE_SCRAPE, SoundSource.PLAYERS, 1.0F, 0.8F);
        RNParticleUtils.spawnParticles(this.level(), new Vec3(this.getX(), this.getY(), this.getZ()), 15, 0.5F, 1.75F, ParticleTypes.HAPPY_VILLAGER);
    }

    private void handleScrapeEffects(Player player) {
        this.level().playSound(player, blockPosition(), SoundEvents.AXE_SCRAPE, SoundSource.PLAYERS, 1.0F, 0.8F);
        RNParticleUtils.spawnParticles(this.level(), new Vec3(this.getX(), this.getY(), this.getZ()), 15, 0.5F, 1.75F, ParticleTypes.WAX_OFF);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Waxed", isWaxed());
        tag.putInt("TarnishStage", this.getTarnishLevel().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setWaxed(tag.getBoolean("Waxed"));
        setTarnishLevel(TarnishStage.byId(tag.getByte("TarnishStage")));
    }

    private void tarnishingTickBehaviour() {
        if (!level().isClientSide() && !isWaxed()) {
            TarnishStage current = getTarnishLevel();
            if (current.getId() < TarnishStage.TARNISHED.getId()) {
                tarnishTimer++;
                if (tarnishTimer >= getTarnishInterval(current)) {
                    increaseTarnishLevel();
                    tarnishTimer = 0;
                }
            }
            if (isNearCatalyst()) {
                tarnishTimer++;
                if (tarnishTimer >= getTarnishInterval(current)) {
                    setTarnishLevel(TarnishStage.CRYSTALLIZED);
                    tarnishTimer = 0;
                }
            }
        }
    }
}