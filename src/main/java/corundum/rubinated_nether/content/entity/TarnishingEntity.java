package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.utils.RNParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

public abstract class TarnishingEntity extends Monster {
    public static final int MAX_TARNISH = 4;
    public static final int TARNISHED = 3;
    public static final int CRYSTALLIZED = 4;

    private static final EntityDataAccessor<Integer> TARNISH_STATE =
            SynchedEntityData.defineId(TarnishingEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> WAXED =
            SynchedEntityData.defineId(TarnishingEntity.class, EntityDataSerializers.BOOLEAN);

    private int tarnishTimer = 0;

    public TarnishingEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TARNISH_STATE, 0);
        builder.define(WAXED, false);
    }

    public int getTarnishLevel() {
        return entityData.get(TARNISH_STATE);
    }

    public void setTarnishLevel(int level) {
        entityData.set(TARNISH_STATE, Mth.clamp(level, 0, MAX_TARNISH));
    }

    public boolean isWaxed() {
        return entityData.get(WAXED);
    }

    public void setWaxed(boolean waxed) {
        entityData.set(WAXED, waxed);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide() && !isWaxed()) {
            int current = getTarnishLevel();
            if (current < TARNISHED) {
                tarnishTimer++;
                if (tarnishTimer >= getTarnishInterval(current)) {
                    setTarnishLevel(current + 1);
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
        if (!(damageSource.getEntity() instanceof Player player)) return;

        var stack = damageSource.getWeaponItem();

        if (stack.getItem() instanceof AxeItem) {
            if (!isWaxed() && this.getTarnishLevel() > 0 && this.getTarnishLevel() != 4) {
                if (level().random.nextFloat() < 0.05f) {
                    this.setTarnishLevel(this.getTarnishLevel() - 1);
                    handleEffects(player);
                }
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int level = getTarnishLevel();

        if (stack.is(Items.HONEYCOMB) && !isWaxed()) {
            setWaxed(true);
            if (!player.isCreative()) stack.shrink(1);
            handleEffects(player);
            return InteractionResult.sidedSuccess(level().isClientSide());
        }

        if (stack.is(RNItems.BRONZE_POWDER.get())) {
            if (!isWaxed() && level < 3) {
                if (this.level().random.nextFloat() < 0.10f) {
                    setTarnishLevel(level + 1);
                }
                if (!player.isCreative())
                    stack.shrink(1);

                handleEffects(player);
                return InteractionResult.sidedSuccess(level().isClientSide());
            }
        }

        return super.mobInteract(player, hand);
    }

    private void handleEffects(Player player) {
        this.level().playSound(player, blockPosition(), SoundEvents.AXE_SCRAPE, SoundSource.PLAYERS, 1.0F, 0.8F);
        RNParticleUtils.spawnParticles(this.level(), new Vec3(this.getX(), this.getY(), this.getZ()), 15, 0.5F, 1.75F, ParticleTypes.HAPPY_VILLAGER);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("TarnishState", getTarnishLevel());
        tag.putBoolean("Waxed", isWaxed());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setTarnishLevel(tag.getInt("TarnishState"));
        setWaxed(tag.getBoolean("Waxed"));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide() && source.getEntity() instanceof Player player) {
            ItemStack weapon = player.getMainHandItem();
            int level = getTarnishLevel();

            if (!isWaxed() && weapon.is(ItemTags.AXES)) {
                if (level > 0 && level < CRYSTALLIZED) {
                    if (random.nextFloat() < 0.05f) {
                        setTarnishLevel(level - 1);
                        level().playSound(null, blockPosition(), SoundEvents.AXE_SCRAPE, SoundSource.PLAYERS, 1.0F, 1.0F);

                        if (level().random.nextFloat() < 0.5f) {
                            ItemEntity powder = new ItemEntity(level(), getX(), getY() + 1, getZ(),
                                    new ItemStack(RNItems.BRONZE_POWDER.get()));
                            level().addFreshEntity(powder);
                        }
                    }
                } else if (level == CRYSTALLIZED) {
                    if (random.nextFloat() < 0.05f) {
                        setTarnishLevel(0);
                        level().playSound(null, blockPosition(), SoundEvents.AXE_SCRAPE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }

        return super.hurt(source, amount);
    }

}
