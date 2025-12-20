package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.entities.BrazierBlockEntity;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class BrazierBlock extends BaseEntityBlock {
    public static final MapCodec<BrazierBlock> CODEC = simpleCodec(BrazierBlock::new);
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 9);

    protected static final VoxelShape SHAPE = Stream.of(
            box(0, 0, 0, 16, 3, 16),
            box(2, 2, 2, 14, 5, 14),
            box(2, 5, 2, 14, 8, 14),
            box(2, 6, 2, 14, 16, 14)
    ).reduce(Shapes::or).get();

    protected static final VoxelShape COLLISION_SHAPE =
            Shapes.join(SHAPE, box(1.5, 6, 1.5, 14.5, 16, 14.5), BooleanOp.NOT_SAME);

    public BrazierBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BrazierBlockEntity brazier) {
                blockEntity.setChanged();
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int currentLevel = state.getValue(LEVEL);
        BlockEntity be = level.getBlockEntity(pos);

        if (!(be instanceof BrazierBlockEntity brazier)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Molten Ruby Block - fill with or without bonus
        if (stack.is(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem())) {
            if (!level.isClientSide) {
                int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();
                int maxSeconds = secondsPerLevel * 9;

                // Only allow if it fits
                if (brazier.getRemainingFuelSeconds() == 0) {
                    // Empty brazier - add with bonus
                    brazier.addFuelWithBonus(9, 1.05f);

                    int newLevel = brazier.calculateLevelFromFuel();
                    level.setBlock(pos, state.setValue(LEVEL, Math.min(9, newLevel)), 3);
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);

                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                } else if (brazier.getRemainingFuelSeconds() + (secondsPerLevel * 9) <= maxSeconds) {
                    // Partially filled - add without bonus
                    brazier.addFuel(9);

                    int newLevel = brazier.calculateLevelFromFuel();
                    level.setBlock(pos, state.setValue(LEVEL, Math.min(9, newLevel)), 3);
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);

                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            } else {
                // Client side - just return success if it would fit
                int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();
                int maxSeconds = secondsPerLevel * 9;
                if (brazier.getRemainingFuelSeconds() == 0 ||
                        brazier.getRemainingFuelSeconds() + (secondsPerLevel * 9) <= maxSeconds) {
                    return ItemInteractionResult.sidedSuccess(true);
                }
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Molten Ruby - add one level
        if (stack.is(RNItems.MOLTEN_RUBY.get())) {
            if (currentLevel < 9) {
                if (!level.isClientSide) {
                    brazier.addFuel(1);

                    // Update to visual level based on actual fuel
                    int newLevel = brazier.calculateLevelFromFuel();
                    level.setBlock(pos, state.setValue(LEVEL, Math.min(9, newLevel)), 3);
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0F, 1.5F);

                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Molten Ruby Nugget - add 1/9th of a level
        if (stack.is(RNItems.MOLTEN_RUBY_NUGGET.get())) {
            if (currentLevel < 9) {
                if (!level.isClientSide) {
                    int oldLevel = brazier.calculateLevelFromFuel();
                    brazier.addFuelNuggets(1);
                    int newLevel = brazier.calculateLevelFromFuel();

                    // Only update blockstate if we crossed a level threshold
                    if (newLevel != oldLevel) {
                        level.setBlock(pos, state.setValue(LEVEL, Math.min(9, newLevel)), 3);
                        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 0.8F, 1.8F);
                    } else {
                        // Still play a quieter sound for feedback
                        level.playSound(null, pos, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.5F, 1.5F);
                    }

                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Bronze Rod - convert fuel to ritual offering
        if (stack.is(RNItems.BRONZE_ROD.get())) {
            int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();

            // Check if there's more than 1 ruby's worth of fuel
            if (brazier.getRemainingFuelSeconds() > secondsPerLevel) {
                if (!level.isClientSide) {
                    // Reduce fuel by one level
                    brazier.addFuel(-1);

                    // Update visual level
                    int newLevel = brazier.calculateLevelFromFuel();
                    level.setBlock(pos, state.setValue(LEVEL, newLevel), 3);

                    // Reduce effect duration for all players in range
                    int x = pos.getX(), y = pos.getY(), z = pos.getZ();
                    AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierEffectRange);
                    Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierEffectRange)
                            .and(EntitySelector.NO_SPECTATORS);

                    int ticksToReduce = secondsPerLevel * 20;
                    for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
                        MobEffectInstance currentEffect = serverPlayer.getEffect(RNEffects.BRAZIER_POWER);
                        if (currentEffect != null) {
                            int currentDuration = currentEffect.getDuration();
                            int newDuration = Math.max(0, currentDuration - ticksToReduce);

                            if (newDuration > 0) {
                                serverPlayer.removeEffect(RNEffects.BRAZIER_POWER);
                                serverPlayer.addEffect(new MobEffectInstance(
                                        RNEffects.BRAZIER_POWER,
                                        newDuration,
                                        currentEffect.getAmplifier(),
                                        currentEffect.isAmbient(),
                                        currentEffect.isVisible(),
                                        currentEffect.showIcon()
                                ));
                            } else {
                                serverPlayer.removeEffect(RNEffects.BRAZIER_POWER);
                            }
                        }
                    }

                    // Consume bronze rod and give ritual offering
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }

                    ItemStack ritualOffering = new ItemStack(RNItems.RITUAL_OFFERING.get(), 1);
                    if (!player.getInventory().add(ritualOffering)) {
                        player.drop(ritualOffering, false);
                    }

                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.2F);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Shovel - extract fuel (gives ruby or nuggets based on remaining time)
        if (stack.getItem() instanceof net.minecraft.world.item.ShovelItem) {
            if (currentLevel > 0) {
                if (!level.isClientSide) {
                    // Store old fuel amount before extraction
                    int oldFuelSeconds = brazier.getRemainingFuelSeconds();
                    int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();

                    ItemStack extracted = brazier.extractFuel();

                    if (!extracted.isEmpty()) {
                        boolean shouldClearEffects = false;
                        int secondsRemoved;

                        if (extracted.is(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem())) {
                            // Ruby block was extracted
                            // Check if it had bonus fuel (more than 9 rubies worth)
                            if (oldFuelSeconds > secondsPerLevel * 9) {
                                // Had bonus fuel - clear all effects immediately
                                shouldClearEffects = true;
                                secondsRemoved = oldFuelSeconds - brazier.getRemainingFuelSeconds();
                            } else {
                                // Normal 9 ruby removal
                                secondsRemoved = secondsPerLevel * 9;
                            }
                        } else if (extracted.is(RNItems.MOLTEN_RUBY.get())) {
                            secondsRemoved = secondsPerLevel;
                        } else if (extracted.is(RNItems.MOLTEN_RUBY_NUGGET.get())) {
                            int secondsPerNugget = secondsPerLevel / 9;
                            secondsRemoved = extracted.getCount() * secondsPerNugget;
                        } else {
                            secondsRemoved = 0;
                        }

                        // Calculate durability damage
                        int baseDamage;
                        if (extracted.is(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem())) {
                            baseDamage = 81; // 9 rubies * 9
                        } else if (extracted.is(RNItems.MOLTEN_RUBY.get())) {
                            baseDamage = 9;
                        } else if (extracted.is(RNItems.MOLTEN_RUBY_NUGGET.get())) {
                            baseDamage = extracted.getCount();
                        } else {
                            baseDamage = 0;
                        }

                        // Double damage if not netherite shovel
                        boolean isNetheriteShovel = stack.is(Items.NETHERITE_SHOVEL);
                        int finalDamage = isNetheriteShovel ? baseDamage : baseDamage * 2;

                        // Apply tool damage
                        if (!player.isCreative() && stack.isDamageableItem()) {
                            net.minecraft.world.entity.EquipmentSlot equipmentSlot = hand == InteractionHand.MAIN_HAND ?
                                    net.minecraft.world.entity.EquipmentSlot.MAINHAND :
                                    net.minecraft.world.entity.EquipmentSlot.OFFHAND;
                            stack.hurtAndBreak(finalDamage, player, equipmentSlot);
                        }

                        // Update effects for all players in range
                        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
                        AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierEffectRange);
                        Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierEffectRange)
                                .and(EntitySelector.NO_SPECTATORS);

                        if (shouldClearEffects) {
                            // Clear all effects immediately (ruby block with bonus was removed)
                            for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
                                if (serverPlayer.hasEffect(RNEffects.BRAZIER_POWER)) {
                                    serverPlayer.removeEffect(RNEffects.BRAZIER_POWER);
                                }
                            }
                        } else {
                            // Normal duration reduction
                            int ticksToReduce = secondsRemoved * 20;
                            for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
                                MobEffectInstance currentEffect = serverPlayer.getEffect(RNEffects.BRAZIER_POWER);
                                if (currentEffect != null) {
                                    int currentDuration = currentEffect.getDuration();
                                    int newDuration = Math.max(0, currentDuration - ticksToReduce);

                                    if (newDuration > 0) {
                                        serverPlayer.removeEffect(RNEffects.BRAZIER_POWER);
                                        serverPlayer.addEffect(new MobEffectInstance(
                                                RNEffects.BRAZIER_POWER,
                                                newDuration,
                                                currentEffect.getAmplifier(),
                                                currentEffect.isAmbient(),
                                                currentEffect.isVisible(),
                                                currentEffect.showIcon()
                                        ));
                                    } else {
                                        serverPlayer.removeEffect(RNEffects.BRAZIER_POWER);
                                    }
                                }
                            }
                        }

                        // Update visual level
                        int newLevel = brazier.calculateLevelFromFuel();
                        level.setBlock(pos, state.setValue(LEVEL, newLevel), 3);
                        level.playSound(null, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);

                        // Give back the extracted item
                        if (!player.getInventory().add(extracted)) {
                            player.drop(extracted, false);
                        }
                    } else {
                        // Not enough fuel to extract even a nugget - fuel was voided
                        // Immediately remove effects from all players in range
                        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
                        AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierEffectRange);
                        Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierEffectRange)
                                .and(EntitySelector.NO_SPECTATORS);

                        for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
                            if (serverPlayer.hasEffect(RNEffects.BRAZIER_POWER)) {
                                serverPlayer.removeEffect(RNEffects.BRAZIER_POWER);
                            }
                        }

                        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.0F);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    // Override onRemove to remove effect from players when brazier is broken
    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        // Only remove effects if the block is actually being removed (not just changing state)
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof BrazierBlockEntity brazier) {
                    // Pass the current position so it's excluded from the search
                    brazier.removeEffectFromAllPlayersInRange(level, pos);
                }
            }
        }

        // Call super AFTER we've cleaned up effects
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BrazierBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return createTickerHelper(blockEntityType, RNBlockEntities.BRAZIER.get(), BrazierBlockEntity::clientTickStatic);
        }
        return createTickerHelper(blockEntityType, RNBlockEntities.BRAZIER.get(), BrazierBlockEntity::serverTickStatic);
    }
}