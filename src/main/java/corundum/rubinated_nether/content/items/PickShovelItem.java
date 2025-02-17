package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItemAbilities;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PickShovelItem extends DiggerItem {
    private static final Map<Block, Block> CRACKABLES = new HashMap<>();

    static {
        CRACKABLES.put(Blocks.STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        CRACKABLES.put(Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS);
        CRACKABLES.put(Blocks.DEEPSLATE_BRICKS, Blocks.CRACKED_DEEPSLATE_BRICKS);
        CRACKABLES.put(Blocks.DEEPSLATE_TILES, Blocks.CRACKED_DEEPSLATE_TILES);
        CRACKABLES.put(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        CRACKABLES.put(Blocks.INFESTED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
    }

    public PickShovelItem(Tier tier, Properties properties) {
        super(tier, RNTags.Blocks.MINEABLE_WITH_DRILL, properties);
    }

    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility)
                || ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility)
                || itemAbility == RNItemAbilities.DRILL_CRACK;
    }

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);

        if (context.getClickedFace() == Direction.DOWN) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        BlockState crackedState = getCrackedVersion(blockstate);

        if (crackedState != null && level.getBlockState(blockpos.above()).isAir()) {
            if (!level.isClientSide) {
                level.setBlock(blockpos, crackedState, 11);
                level.playSound(player, blockpos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, crackedState));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Nullable
    private BlockState getCrackedVersion(BlockState state) {
        Block crackedBlock = CRACKABLES.get(state.getBlock());
        return crackedBlock != null ? crackedBlock.defaultBlockState() : null;
    }
}
