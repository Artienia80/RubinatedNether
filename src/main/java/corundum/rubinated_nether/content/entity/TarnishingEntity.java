package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.misc.RNAttachments;
import corundum.rubinated_nether.networking.BronzeTarnishingData;
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
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public abstract class TarnishingEntity extends Monster {

    public static final int MAX_TARNISH = 4;
    public static final int TARNISHED = 3;
    public static final int CRYSTALLIZED = 4;

    private static final EntityDataAccessor<Boolean> WAXED =
            SynchedEntityData.defineId(TarnishingEntity.class, EntityDataSerializers.BOOLEAN);

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
    }

    public int getTarnishLevel() {
        return this.getData(RNAttachments.TARNISH_LEVEL.get());
    }

    public void setTarnishLevel(int level) {
        this.setData(RNAttachments.TARNISH_LEVEL.get(), level);
        if(!this.level().isClientSide())
            PacketDistributor.sendToPlayersTrackingEntity(this, new BronzeTarnishingData(this.getId(), level));
    }

    public void increaseTarnishLevel() {
        this.setTarnishLevel(this.getTarnishLevel() + 1);
    }

    public void decreaseTarnishLevel() {
        this.setTarnishLevel(this.getTarnishLevel() - 1);
    }

    public boolean isWaxed() {
        return this.entityData.get(WAXED);
    }

    public void setWaxed(boolean waxed) {
        this.entityData.set(WAXED, waxed);
    }

    private int getTarnishInterval(int level) {
        return switch (level) {
            case 0 -> 1200;
            case 1 -> 1600;
            case 2 -> 2000;
            case 3 -> 2400;
            default -> Integer.MAX_VALUE;
        };
    }

    private boolean isNearSoulFire() {
        BlockPos pos = blockPosition();
        for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-5, -5, -5), pos.offset(5, 5, 5))) {
            Block block = level().getBlockState(nearby).getBlock();
            if (block == Blocks.SOUL_FIRE || block == Blocks.SOUL_TORCH || block == Blocks.SOUL_WALL_TORCH || block == Blocks.SOUL_LANTERN) {
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
            if (!isWaxed() && this.getTarnishLevel() > 0 && this.getTarnishLevel() != 4) {
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
            } else if (this.getTarnishLevel() == CRYSTALLIZED) {
                if (random.nextFloat() < 0.05f) {
                    this.setTarnishLevel(0);
                    handleVFX(player);
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int level = getTarnishLevel();

        if (stack.is(Items.SOUL_TORCH)) {
            if (!isWaxed()) {
                if(!level().isClientSide())
                    setTarnishLevel(CRYSTALLIZED);
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
            if (!isWaxed() && level < 3) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                if(!level().isClientSide())
                    if (level().random.nextFloat() < 0.1F) {
                        increaseTarnishLevel();
                        return InteractionResult.sidedSuccess(level().isClientSide());
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setWaxed(tag.getBoolean("Waxed"));
    }

    private void tarnishingTickBehaviour() {
        if (!level().isClientSide() && !isWaxed()) {
            int current = getTarnishLevel();
            if (current < TARNISHED) {
                tarnishTimer++;
                if (tarnishTimer >= getTarnishInterval(current)) {
                    increaseTarnishLevel();
                    tarnishTimer = 0;
                }
            }
            if (isNearSoulFire()) {
                tarnishTimer++;
                if (tarnishTimer >= getTarnishInterval(current)) {
                    setTarnishLevel(CRYSTALLIZED);
                    tarnishTimer = 0;
                }
            }
        }
    }
}
