package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class BronzeBulbBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public final BlockState nextStateWaxed;
    public final BlockState nextStateScraped;
    private final boolean isWaxed;

    public BronzeBulbBlock(BlockBehaviour.Properties properties, Block block, Block block2) {
        this(properties, block, block2, false);
    }

    public BronzeBulbBlock(BlockBehaviour.Properties properties, Block block, Block block2, boolean isWaxed) {
        super(properties);
        this.isWaxed = isWaxed;
        // Handle null blocks gracefully
        this.nextStateWaxed = block != null ? block.defaultBlockState() : null;
        this.nextStateScraped = block2 != null ? block2.defaultBlockState() : null;
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false)));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (oldState.getBlock() != state.getBlock() && level instanceof ServerLevel serverlevel) {
            this.checkAndFlip(state, serverlevel, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level instanceof ServerLevel serverlevel) {
            this.checkAndFlip(state, serverlevel, pos);
        }
    }

    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
        var flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(POWERED)) {
            BlockState blockstate = state;
            if (!state.getValue(POWERED)) {
                blockstate = state.cycle(LIT);
                level.playSound(
                        null,
                        pos,
                        blockstate.getValue(LIT) ? SoundEvents.AMETHYST_BLOCK_BREAK : SoundEvents.AMETHYST_BLOCK_PLACE,
                        SoundSource.BLOCKS
                );
            }
            level.setBlock(pos, blockstate.setValue(POWERED, Boolean.valueOf(flag)), 3);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    // Returns the analog signal this block emits. This is the signal a comparator can read from it.
    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockState(pos).getValue(LIT) ? 15 : 0;
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.getAbilities().mayBuild) {
            if (player.getItemInHand(hand).is(Items.HONEYCOMB)) {
                setWaxed(player, state, level, pos);
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            if (player.getItemInHand(hand).is(ItemTags.AXES)) {
                setScraped(player, state, level, pos);
                if (!player.isCreative()) {
                    itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        } else {
            return ItemInteractionResult.FAIL;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public ItemStack getCloneItemStack(
            BlockState state,
            HitResult target,
            LevelReader level,
            BlockPos pos,
            Player player
    ) {
        return new ItemStack(
                isWaxed
                        ? BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(this)))
                        : this
        );
    }

    // this is unused, it's for reference
    //public static void extinguish(@javax.annotation.Nullable Player, player, BlockState state, LevelAccessor level, BlockPos pos) {
    //    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, (double)pos.getX() + 1 , (double)pos.getY() + 1 , (double)pos.getZ() + 1 , 0.0, 0.0, 0.0);
    //    level.playSound((Player)null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
    //    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    //}

    public void setScraped(@Nullable Player player, BlockState state, LevelAccessor world, BlockPos pos) {
        if (this.nextStateScraped != null) {
            world.setBlock(pos, this.nextStateScraped.setValue(BronzeBulbBlock.LIT, state.getValue(BronzeBulbBlock.LIT)), 11);
            world.playSound((Player) null, pos, SoundEvents.ENDERMAN_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    public void setWaxed(@Nullable Player player, BlockState state, LevelAccessor world, BlockPos pos) {
        if (this.nextStateWaxed != null) {
            world.setBlock(pos, this.nextStateWaxed.setValue(BronzeBulbBlock.LIT, state.getValue(BronzeBulbBlock.LIT)), 11);
            world.playSound((Player) null, pos, SoundEvents.ENDERMAN_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F);
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, POWERED);
    }
}