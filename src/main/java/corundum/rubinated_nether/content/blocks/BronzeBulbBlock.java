package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class BronzeBulbBlock extends Block {
    public static final BooleanProperty POWERED;
    public static final BooleanProperty LIT;
    public final BlockState nextStateWaxed;
    public final BlockState nextStateScraped;

    public BronzeBulbBlock(BlockBehaviour.Properties properties, Block block, Block block2) {
        super(properties);
        this.nextStateWaxed = block.defaultBlockState();
        this.nextStateScraped = block.defaultBlockState();
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, Boolean.valueOf(false)).setValue(POWERED, Boolean.valueOf(false)));
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (oldState.getBlock() != state.getBlock() && level instanceof ServerLevel serverlevel) {
            this.checkAndFlip(state, serverlevel, pos);
        }
    }

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

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    // Returns the analog signal this block emits. This is the signal a comparator can read from it.

    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockState(pos).getValue(LIT) ? 15 : 0;
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (player.getAbilities().mayBuild) {
            if (player.getItemInHand(hand).is(Items.HONEYCOMB)) {
                setWaxed(player, state, level, pos);
                if (!player.isCreative()) {
                    itemStack.shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (player.getItemInHand(hand).is(ItemTags.AXES)) {
                setScraped(player, state, level, pos);
                if (!player.isCreative()) {
                    itemStack.hurtAndBreak(1, player, p ->
                            p.broadcastBreakEvent(hand));
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else {
            return InteractionResult.PASS;
        }
        return null;
    }

    // this is unused, it's for reference
    //public static void extinguish(@javax.annotation.Nullable Player, player, BlockState state, LevelAccessor level, BlockPos pos) {
    //    level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, (double)pos.getX() + 1 , (double)pos.getY() + 1 , (double)pos.getZ() + 1 , 0.0, 0.0, 0.0);
    //    level.playSound((Player)null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
    //    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    //}

    public void setScraped(@Nullable Player player, BlockState state, LevelAccessor world, BlockPos pos) {
        world.setBlock(pos, this.nextStateScraped.setValue(BronzeBulbBlock.LIT, state.getValue(BronzeBulbBlock.LIT)), 11);
        world.playSound((Player) null, pos, SoundEvents.ENDERMAN_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F);
        world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    public void setWaxed(@Nullable Player player, BlockState state, LevelAccessor world, BlockPos pos) {
        world.setBlock(pos, this.nextStateWaxed.setValue(BronzeBulbBlock.LIT, state.getValue(BronzeBulbBlock.LIT)), 11);
        world.playSound((Player) null, pos, SoundEvents.ENDERMAN_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F);
        world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, POWERED);
    }

    static {
        LIT = BlockStateProperties.LIT;
        POWERED = BlockStateProperties.POWERED;
    }
