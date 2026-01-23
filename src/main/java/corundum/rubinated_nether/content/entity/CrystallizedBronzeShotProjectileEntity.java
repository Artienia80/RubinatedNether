package corundum.rubinated_nether.content.entity;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNEntityCreator;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.TarnishStage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class CrystallizedBronzeShotProjectileEntity extends AbstractArrow {
    private static final double CRYSTAL_SPREAD_RADIUS = 5.0;
    private static final int MAX_CRYSTAL_ATTEMPTS = 150;

    private float rotation;

    public CrystallizedBronzeShotProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public CrystallizedBronzeShotProjectileEntity(LivingEntity shooter, Level level) {
        super(
                RNEntityCreator.CRYSTALLIZED_BRONZE_SHOT.get(),
                shooter,
                level,
                new ItemStack(RNItems.CRYSTALLIZED_BRONZE_SHOT.get()),
                null
        );
        this.setBaseDamage(1.0);
    }

    public CrystallizedBronzeShotProjectileEntity(Level level, Position pos) {
        super(
                RNEntityCreator.CRYSTALLIZED_BRONZE_SHOT.get(),
                pos.x(),
                pos.y(),
                pos.z(),
                level,
                new ItemStack(RNItems.CRYSTALLIZED_BRONZE_SHOT.get()),
                null
        );
        this.setBaseDamage(1.0);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(RNItems.CRYSTALLIZED_BRONZE_SHOT.get());
    }

    public float getRenderingRotation() {
        rotation += 2.5f;
        if (rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    public boolean isGrounded() {
        return inGround;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();

        Entity targetEntity = entity;
        if (entity instanceof BronzePart part) {
            targetEntity = part.parentMob;
        }

        if (targetEntity instanceof BronzeEntity bronze) {
            bronze.setTarnishLevel(TarnishStage.CRYSTALLIZED);
            this.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 2.0F, 1.2F);

            if (!this.level().isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                spreadCrystalsAroundHit(serverLevel, bronze.blockPosition());
            }

            shatterWithoutDrops();
            return;
        }

        entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float) getBaseDamage());

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(
                    new MobEffectInstance(RNEffects.BRONZE_DISEASED, 100, 0, true, false)
            );
        }

        shatterWithoutDrops();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        this.playSound(SoundEvents.AMETHYST_BLOCK_BREAK, 1.0F, 1.2F);

        if (!this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            spreadCrystalsAroundHit(serverLevel, this.blockPosition());
        }

        shatterWithoutDrops();
    }

    private void spreadCrystalsAroundHit(ServerLevel level, BlockPos centerPos) {
        spawnParticles(level, centerPos.getX() + 0.5, centerPos.getY() + 1.0, centerPos.getZ() + 0.5, 30, 1.5, 0.1);

        level.playSound(
                null,
                centerPos,
                SoundEvents.AMETHYST_BLOCK_BREAK,
                SoundSource.HOSTILE,
                2.0F,
                0.8F + this.random.nextFloat() * 0.4F
        );

        int successfulConversions = 0;

        for (int attempt = 0; attempt < MAX_CRYSTAL_ATTEMPTS; attempt++) {
            BlockPos targetPos = generateRandomPositionInSphere(centerPos, CRYSTAL_SPREAD_RADIUS);

            BlockState currentState = level.getBlockState(targetPos);

            if (!currentState.isAir()) {
                continue;
            }

            Direction attachmentDirection = findAttachmentDirection(level, targetPos);
            if (attachmentDirection == null) {
                continue;
            }

            double distance = centerPos.distSqr(targetPos);
            double normalizedDistance = Math.sqrt(distance) / CRYSTAL_SPREAD_RADIUS;
            double replacementChance = 1.0 - normalizedDistance;

            if (random.nextDouble() < replacementChance) {
                BlockState crystalState = RNBlocks.CRYSTALLIZED_BRONZE_CRYSTAL.get().defaultBlockState();

                if (crystalState.hasProperty(BlockStateProperties.FACING)) {
                    crystalState = crystalState.setValue(BlockStateProperties.FACING, attachmentDirection.getOpposite());
                }

                level.setBlockAndUpdate(targetPos, crystalState);

                spawnParticles(level, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, 5, 0.6, 0.05);

                successfulConversions++;
            }
        }
    }

    private void spawnParticles(ServerLevel level, double x, double y, double z, int count, double spread, double speed) {
        for (int i = 0; i < count; i++) {
            double offsetX = (random.nextDouble() - 0.5) * spread;
            double offsetY = (random.nextDouble() - 0.5) * spread;
            double offsetZ = (random.nextDouble() - 0.5) * spread;

            level.sendParticles(
                    ParticleTypes.GLOW,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    1,
                    0, 0, 0,
                    speed
            );
        }
    }

    private Direction findAttachmentDirection(Level level, BlockPos airPos) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = airPos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (neighborState.isSolidRender(level, neighborPos) && !neighborState.isAir()) {
                return direction;
            }
        }
        return null;
    }

    private BlockPos generateRandomPositionInSphere(BlockPos center, double radius) {
        double x, y, z;
        do {
            x = (random.nextDouble() * 2.0 - 1.0) * radius;
            y = (random.nextDouble() * 2.0 - 1.0) * radius;
            z = (random.nextDouble() * 2.0 - 1.0) * radius;
        } while (x * x + y * y + z * z > radius * radius);

        return center.offset((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }

    private void shatterWithoutDrops() {
        if (!this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            spawnParticles(serverLevel, this.getX(), this.getY(), this.getZ(), 10, 0.3, 0.05);
        }

        this.discard();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double d0 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d0)) {
            d0 = 4.0;
        }

        d0 *= 64.0;
        return distance < d0 * d0;
    }
}