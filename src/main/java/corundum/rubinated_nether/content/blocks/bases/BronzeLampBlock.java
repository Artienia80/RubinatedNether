package corundum.rubinated_nether.content.blocks.bases;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import corundum.rubinated_nether.utils.ShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BronzeLampBlock extends RotatedPillarBlock {

	protected static final Map<Direction.Axis, VoxelShape> AXIS_SHAPES = ShapeUtils.allAxis(
			Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D)
	);


	public BronzeLampBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(
			BlockState state,
			BlockGetter view,
			BlockPos pos,
			CollisionContext context
	) {
		return AXIS_SHAPES.get(state.getValue(AXIS));
	}

	@Override
	public void playerDestroy(
			Level level,
			Player player,
			BlockPos pos,
			BlockState state,
			@Nullable BlockEntity blockEntity,
			ItemStack tool
	) {
		super.playerDestroy(level, player, pos, state, blockEntity, tool);

		if(level.getRandom().nextInt(1_000_000) == 0)
			level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
	}
}