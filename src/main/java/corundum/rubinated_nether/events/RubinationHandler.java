package corundum.rubinated_nether.events;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class RubinationHandler {

    private static Map<Block, Block> getRubinatableBlocks() {
        Map<Block, Block> map = new HashMap<>();
        map.put(RNBlocks.CHISELED_SHRINE_STONE_BRICKS.get(), RNBlocks.RUBINATED_CHISELED_SHRINE_STONE_BRICKS.get());
        map.put(RNBlocks.SHRINE_STONE_BRICKS.get(), RNBlocks.RUBINATED_SHRINE_STONE_BRICKS.get());
        map.put(Blocks.CRYING_OBSIDIAN, RNBlocks.BLEEDING_OBSIDIAN.get());

        return map;
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        InteractionHand hand = event.getHand();
        ItemStack stack = player.getItemInHand(hand);
        BlockState state = level.getBlockState(pos);

        if (!isRubinationItem(stack)) {
            return;
        }

        Block rubinatedVersion = getRubinatableBlocks().get(state.getBlock());
        if (rubinatedVersion == null) {
            return;
        }

        if (!level.isClientSide) {
            BlockState newState = rubinatedVersion.defaultBlockState();
            level.setBlock(pos, newState, 11);

            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.2F);

            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
        event.setCanceled(true);
    }

    private static boolean isRubinationItem(ItemStack stack) {
        return stack.is(RNItems.RUBY_SHARD_ITEM.get()) || stack.is(RNItems.MOLTEN_RUBY_NUGGET_ITEM.get());
    }
}