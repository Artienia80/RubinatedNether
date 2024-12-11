package net.artienia.rubinated_nether.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RunestoneBlock extends Block {

	protected static final VoxelShape SHAPE_BOTTOM = Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0);
	protected static final VoxelShape SHAPE_MIDDLE = Block.box(1.0, 10.0, 1.0, 15.0, 24.0, 15.0);
	protected static final VoxelShape SHAPE_TOP = Block.box(0.0, 24.0, 0.0, 16.0, 32.0, 16.0);

	protected static final VoxelShape SHAPE_HALF = Shapes.or(SHAPE_BOTTOM, SHAPE_MIDDLE);
	protected static final VoxelShape SHAPE = Shapes.or(SHAPE_HALF, SHAPE_TOP);


	public RunestoneBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("tooltip.rubinated_nether.wip.tooltip"));
		super.appendHoverText(stack, level, tooltip, flag);
	}

	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity blockEntity = level.getBlockEntity(pos);
				player.openMenu((BarrelBlockEntity)blockEntity);

			return InteractionResult.CONSUME;
		}
	}
}
