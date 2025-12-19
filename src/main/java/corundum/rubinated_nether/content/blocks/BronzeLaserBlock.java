package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.entities.BronzeLaserBlockEntity;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import corundum.rubinated_nether.utils.BEBlock;
import corundum.rubinated_nether.utils.ShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class BronzeLaserBlock extends DirectionalBlock implements BEBlock<BronzeLaserBlockEntity>, TarnishingBronze {

	public static final MapCodec<BronzeLaserBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(BronzeLaserBlock::getAge),
					propertiesCodec()
			).apply(instance, BronzeLaserBlock::new)
	);

	public static final Map<Direction, VoxelShape> SHAPES = ShapeUtils.allDirections(Shapes.or(
			box(0, 0, 0, 16, 6, 16),
			box(2, 0, 2, 14, 16, 14)
	));

	public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);
	public static final EnumProperty<LaserMode> MODE = EnumProperty.create("mode", LaserMode.class);

	private final TarnishStage tarnishStage;

	public enum LaserMode implements StringRepresentable {
		SPECTRUM("spectrum"), // Blocks + Entities
		ULTRAVIOLET("uv"),    // Blocks Only
		INFRARED("ir");       // Entities Only

		private final String name;
		LaserMode(String name) { this.name = name; }

		@Override public String getSerializedName() { return this.name; }

		public LaserMode cycle() {
			return switch (this) {
				case SPECTRUM -> ULTRAVIOLET;
				case ULTRAVIOLET -> INFRARED;
				case INFRARED -> SPECTRUM;
			};
		}

		public boolean detectsBlocks() { return this == SPECTRUM || this == ULTRAVIOLET; }
		public boolean detectsEntities() { return this == SPECTRUM || this == INFRARED; }
	}

	public BronzeLaserBlock(TarnishStage state, BlockBehaviour.Properties props) {
		super(props);
		this.tarnishStage = state;
		this.registerDefaultState(this.defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(POWER, 0)
				.setValue(MODE, LaserMode.SPECTRUM)
				.setValue(WAXED, false)
		);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWER, MODE, WAXED);
	}

	@Override
	public BlockEntityType<? extends BronzeLaserBlockEntity> getBlockEntityType() {
		return RNBlockEntities.BRONZE_LASER.get();
	}

	@Override
	public Class<? extends BronzeLaserBlockEntity> getBlockEntityClass() {
		return BronzeLaserBlockEntity.class;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(FACING));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		LaserMode nextMode = state.getValue(MODE).cycle();
		float pitch = switch (nextMode) {
			case SPECTRUM -> 0.6f;
			case ULTRAVIOLET -> 0.7f;
			case INFRARED -> 0.5f;
		};

		level.playLocalSound(pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.5f, pitch, true);
		level.setBlockAndUpdate(pos, state.setValue(MODE, nextMode));
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	// ---- Tarnish logic ----
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(WAXED))
			return;

		boolean hasCatalystNearby = BlockPos.betweenClosedStream(
				pos.offset(-1, -1, -1), pos.offset(1, 1, 1)
		).anyMatch(neighbor -> level.getBlockState(neighbor).is(RNTags.Blocks.CRYSTALLIZATION_CATALYST));

		if (hasCatalystNearby) {
			this.getCrystallized(state).ifPresent(next -> level.setBlockAndUpdate(pos, next));
		} else {
			this.changeOverTime(state, level, pos, random);
		}
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return TarnishingBronze.canCrystallize(state.getBlock());
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
											  BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		return waxing(stack, state, level, pos, player, hand, hitResult)
				? ItemInteractionResult.SUCCESS
				: super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
		return new ItemStack(
				state.getValue(WAXED)
						? BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(this)))
						: this
		);
	}

	@Override
	public TarnishStage getAge() {
		return tarnishStage;
	}

	// Redstone stuff stays the same
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		BlockEntity be = level.getBlockEntity(pos);
		if(!(be instanceof BronzeLaserBlockEntity laser)) return;
		level.setBlockAndUpdate(pos, state.setValue(POWER, laser.getPowerLevel()));

		Direction direction = state.getValue(FACING);
		BlockPos blockPos = pos.relative(direction.getOpposite());
		level.neighborChanged(blockPos, this, pos);
		level.updateNeighborsAtExceptFromFacing(blockPos, this, direction);
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return getSignal(state, level, pos, direction);
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return state.getValue(FACING) == direction ? state.getValue(POWER) : 0;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected MapCodec<? extends BronzeLaserBlock> codec() {
		return CODEC;
	}
}
