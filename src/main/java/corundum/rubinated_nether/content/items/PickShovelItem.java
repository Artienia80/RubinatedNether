package corundum.rubinated_nether.content.items;

import corundum.rubinated_nether.content.RNItemAbilities;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.RubinationConverter;
import corundum.rubinated_nether.content.module.ToolModules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PickShovelItem extends DiggerItem implements MechanicalTool {
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

	// ---------- Fuel slot (bundle-like) ----------
	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		return stack.getCount() == 1 && MechanicalFuel.onStackedOnOther(stack, slot, action, player);
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		return stack.getCount() == 1 && MechanicalFuel.onOtherStackedOnMe(stack, other, slot, action, player, access);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		MechanicalFuel.appendTooltip(stack, tooltip);
		ToolModules.appendTooltip(stack, tooltip);
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		return MechanicalFuel.tooltipImage(stack);
	}

	// Dead tool: cannot start or finish breaking a block (also covers creative mode)
	@Override
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
		return MechanicalFuel.hasFuel(player.getMainHandItem()) && canWorkOn(state);
	}

	@Override
	public boolean canWorkOn(BlockState state) {
		return state.is(RNTags.Blocks.MINEABLE_WITH_DRILL);
	}

	// Fuelled = rubinated glint (rendered by ItemRendererMixin)
	@Override
	public boolean isFoil(ItemStack stack) {
		return MechanicalFuel.hasFuel(stack) || super.isFoil(stack);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return MechanicalFuel.isBarVisible(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return MechanicalFuel.getBarWidth(stack);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		return MechanicalFuel.getBarColor(stack);
	}

	// Burning rewrites the fuel component every second; that must not restart block breaking or replay the equip animation.
	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return oldStack.getItem() != newStack.getItem();
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || oldStack.getItem() != newStack.getItem();
	}

	public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
		return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility)
				|| ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility)
				|| itemAbility == RNItemAbilities.DRILL_CRACK;
	}

	public InteractionResult useOn(UseOnContext context) {
		if (!MechanicalFuel.hasFuel(context.getItemInHand())) {
			return InteractionResult.PASS; // dead tool: no crack / unrubinate either
		}
		var level = context.getLevel();
		var blockpos = context.getClickedPos();
		var blockstate = level.getBlockState(blockpos);

		if (context.getClickedFace() == Direction.DOWN) {
			return InteractionResult.PASS;
		}

		var player = context.getPlayer();

		var derubinatedState = RubinationConverter.getDerubinatedVersion(blockstate);
		if (derubinatedState != null && level.getBlockState(blockpos.above()).isAir()) {
			if (!level.isClientSide) {
				level.setBlock(blockpos, derubinatedState, 11);
				level.playSound(player, blockpos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, derubinatedState));

				ItemEntity rubyShard = new ItemEntity(level, blockpos.getX() + 0.5, blockpos.getY() + 0.5, blockpos.getZ() + 0.5,
						new ItemStack(RNItems.RUBY_SHARD.get()));
				rubyShard.setDefaultPickUpDelay();
				level.addFreshEntity(rubyShard);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		var crackedState = getCrackedVersion(blockstate);
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
		var crackedBlock = CRACKABLES.get(state.getBlock());
		return crackedBlock != null ? crackedBlock.defaultBlockState() : null;
	}
}