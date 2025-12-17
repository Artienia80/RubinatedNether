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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int currentLevel = state.getValue(LEVEL);
        BlockEntity be = level.getBlockEntity(pos);

        if (!(be instanceof BrazierBlockEntity brazier)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Molten Ruby Block - fill to 9 levels with 5% bonus
        if (stack.is(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem())) {
            if (currentLevel < 9) {
                if (!level.isClientSide) {
                    // 9 rubies worth + 5% bonus = 9.45 rubies worth
                    brazier.addFuelWithBonus(9, 1.05f);

                    // Update to visual level based on actual fuel
                    int newLevel = brazier.calculateLevelFromFuel();
                    level.setBlock(pos, state.setValue(LEVEL, Math.min(9, newLevel)), 3);
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);

                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Molten Ruby - add one level
        if (stack.is(RNItems.MOLTEN_RUBY_ITEM.get())) {
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
        if (stack.is(RNItems.MOLTEN_RUBY_NUGGET_ITEM.get())) {
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

        // Shovel - extract fuel (gives ruby or nuggets based on remaining time)
        if (stack.getItem() instanceof net.minecraft.world.item.ShovelItem) {
            if (currentLevel > 0) {
                if (!level.isClientSide) {
                    ItemStack extracted = brazier.extractFuel();

                    if (!extracted.isEmpty()) {
                        // Special handling for ruby block extraction
                        boolean wasOverfilled = false;
                        int secondsRemoved;

                        if (extracted.is(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem())) {
                            int secondsPerLevel = RNConfig.getBrazierSecondsPerLevel();
                            int oldFuelSeconds = brazier.getRemainingFuelSeconds() + (secondsPerLevel * 9); // Add back what was removed

                            // Check if it was overfilled (had bonus fuel)
                            if (oldFuelSeconds > secondsPerLevel * 9) {
                                wasOverfilled = true;
                                secondsRemoved = oldFuelSeconds; // Remove ALL fuel including bonus
                            } else {
                                secondsRemoved = secondsPerLevel * 9; // Normal 9 ruby removal
                            }
                        } else {
                            // Calculate seconds removed for rubies and nuggets normally
                            secondsRemoved = 0; // Will be set below
                        }

                        // Calculate durability damage based on what was extracted
                        int baseDamage;
                        if (extracted.is(RNBlocks.MOLTEN_RUBY_BLOCK.get().asItem())) {
                            baseDamage = 81; // 9 rubies * 9
                        } else if (extracted.is(RNItems.MOLTEN_RUBY_ITEM.get())) {
                            baseDamage = 9;
                            secondsRemoved = RNConfig.getBrazierSecondsPerLevel();
                        } else if (extracted.is(RNItems.MOLTEN_RUBY_NUGGET_ITEM.get())) {
                            baseDamage = extracted.getCount(); // 1 per nugget
                            int secondsPerNugget = RNConfig.getBrazierSecondsPerLevel() / 9;
                            secondsRemoved = extracted.getCount() * secondsPerNugget;
                        } else {
                            baseDamage = 0; // Fallback
                            secondsRemoved = 0;
                        }

                        // Double damage if not netherite shovel
                        boolean isNetheriteShovel = stack.is(Items.NETHERITE_SHOVEL);
                        int finalDamage = isNetheriteShovel ? baseDamage : baseDamage * 2;

                        // Apply tool damage
                        if (!player.isCreative() && stack.isDamageableItem()) {
                            // Use the hand's equipment slot (MAINHAND or OFFHAND)
                            net.minecraft.world.entity.EquipmentSlot equipmentSlot = hand == InteractionHand.MAIN_HAND ?
                                    net.minecraft.world.entity.EquipmentSlot.MAINHAND :
                                    net.minecraft.world.entity.EquipmentSlot.OFFHAND;
                            stack.hurtAndBreak(finalDamage, player, equipmentSlot);
                        }

                        // Reduce effect duration for all players in range
                        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
                        AABB area = new AABB(x, y, z, x + 1, y + 1, z + 1).inflate(RNConfig.brazierEffectRange);
                        Predicate<Entity> selector = EntitySelector.withinDistance(x + 0.5, y + 0.5, z + 0.5, RNConfig.brazierEffectRange)
                                .and(EntitySelector.NO_SPECTATORS);

                        int ticksToReduce = secondsRemoved * 20; // Convert seconds to ticks

                        // If overfilled ruby block was removed, immediately clear all effects
                        if (wasOverfilled) {
                            for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
                                if (serverPlayer.hasEffect(RNEffects.BRAZIER_POWER)) {
                                    serverPlayer.removeEffect(RNEffects.BRAZIER_POWER);
                                }
                            }
                        } else {
                            // Normal duration reduction
                            for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, area, selector)) {
                                MobEffectInstance currentEffect = serverPlayer.getEffect(RNEffects.BRAZIER_POWER);
                                if (currentEffect != null) {
                                    int currentDuration = currentEffect.getDuration();
                                    int newDuration = Math.max(0, currentDuration - ticksToReduce);

                                    if (newDuration > 0) {
                                        // Reapply with reduced duration
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
                                        // Duration would be 0 or negative, remove effect
                                        serverPlayer.removeEffect(RNEffects.BRAZIER_POWER);
                                    }
                                }
                            }
                        }

                        // Update visual level based on remaining fuel
                        int newLevel = brazier.calculateLevelFromFuel();
                        level.setBlock(pos, state.setValue(LEVEL, newLevel), 3);
                        level.playSound(null, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);

                        // Give back the extracted item
                        if (!player.getInventory().add(extracted)) {
                            player.drop(extracted, false);
                        }
                    } else {
                        // Not enough fuel to extract even a nugget
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