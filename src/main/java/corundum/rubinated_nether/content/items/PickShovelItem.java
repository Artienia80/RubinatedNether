package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNItemAbilities;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class PickShovelItem extends DiggerItem {
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
        } else {
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
            // No cracked version found, do nothing
            return InteractionResult.PASS;
        }
    }

    @Nullable
    private BlockState getCrackedVersion(BlockState state) {
        Block block = state.getBlock();

        ResourceLocation blockID = BuiltInRegistries.BLOCK.getKey(block);
        if (blockID == null) return null;

        ResourceLocation crackedID = ResourceLocation.tryParse(blockID.getNamespace() + ":cracked_" + blockID.getPath());

        if (crackedID != null) {
            Block crackedBlock = BuiltInRegistries.BLOCK.get(crackedID);
            if (crackedBlock != null) {
                return crackedBlock.defaultBlockState();
            }
        }

        // If no cracked version found, return null (do nothing)
        return null;
    }
}