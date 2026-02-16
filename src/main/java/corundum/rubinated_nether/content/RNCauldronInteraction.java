package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Map;

public class RNCauldronInteraction {

    public static final CauldronInteraction.InteractionMap MOLTEN_RUBY = CauldronInteraction.newInteractionMap("molten_ruby");

    public static void bootStrap() {
        // Empty bucket fills the cauldron with molten ruby
        CauldronInteraction.EMPTY.map().put(RNItems.MOLTEN_RUBY_BUCKET.get(), (state, level, pos, player, hand, stack) -> {
            return emptyMoltenRubyBucket(
                    level,
                    pos,
                    player,
                    hand,
                    stack,
                    RNBlocks.MOLTEN_RUBY_CAULDRON.get().defaultBlockState(),
                    SoundEvents.BUCKET_EMPTY_LAVA
            );
        });

        // Fill empty bucket from molten ruby cauldron
        MOLTEN_RUBY.map().put(Items.BUCKET, (state, level, pos, player, hand, stack) -> {
            return fillMoltenRubyBucket(
                    state,
                    level,
                    pos,
                    player,
                    hand,
                    stack,
                    new ItemStack(RNItems.MOLTEN_RUBY_BUCKET.get()),
                    blockState -> true,
                    SoundEvents.BUCKET_FILL_LAVA
            );
        });
    }

    private static ItemInteractionResult emptyMoltenRubyBucket(
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack filledBucket,
            BlockState cauldronState,
            net.minecraft.sounds.SoundEvent soundEvent
    ) {
        if (!level.isClientSide) {
            Item item = filledBucket.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(filledBucket, player, new ItemStack(Items.BUCKET)));
            player.awardStat(Stats.FILL_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));
            level.setBlockAndUpdate(pos, cauldronState);
            level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private static ItemInteractionResult fillMoltenRubyBucket(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack emptyStack,
            ItemStack filledStack,
            java.util.function.Predicate<BlockState> statePredicate,
            net.minecraft.sounds.SoundEvent soundEvent
    ) {
        if (!level.isClientSide) {
            Item item = emptyStack.getItem();
            player.setItemInHand(hand, ItemUtils.createFilledResult(emptyStack, player, filledStack));
            player.awardStat(Stats.USE_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(item));
            level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}