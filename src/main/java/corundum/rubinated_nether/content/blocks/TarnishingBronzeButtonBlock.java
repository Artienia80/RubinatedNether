package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class TarnishingBronzeButtonBlock extends ButtonBlock implements TarnishingBronze {
	public static final MapCodec<ButtonBlock> TARNISHING_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(button -> ((TarnishingBronzeButtonBlock) button).getAge()),
			BlockSetType.CODEC.fieldOf("block_set_type").forGetter(button -> ((TarnishingBronzeButtonBlock) button).setType),
			Codec.intRange(1, 1024).fieldOf("ticks_to_stay_pressed").forGetter(button -> ((TarnishingBronzeButtonBlock) button).pressTicks),
			propertiesCodec()
		).apply(instance, TarnishingBronzeButtonBlock::new)
	);

	private final TarnishStage tarnishStage;
	private final BlockSetType setType;
	private final int pressTicks;

	public TarnishingBronzeButtonBlock(TarnishStage tarnishStage, int pressTicks, Properties properties) {
		this(tarnishStage, BlockSetType.COPPER, pressTicks, properties);
	}

	public TarnishingBronzeButtonBlock(TarnishStage tarnishStage, BlockSetType setType, int pressTicks, Properties properties) {
		super(setType, pressTicks, properties);
		this.tarnishStage = tarnishStage;
		this.setType = setType;
		this.pressTicks = pressTicks;
		this.registerDefaultState(this.defaultBlockState().setValue(WAXED, false));
	}

	@Override
	public MapCodec<ButtonBlock> codec() {
		return TARNISHING_CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WAXED);
		super.createBlockStateDefinition(builder);
	}

	@Override
	public TarnishStage getAge() {
		return this.tarnishStage;
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return !state.getValue(WAXED) && TarnishingBronze.canCrystallize(state.getBlock());
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(WAXED) || state.getValue(POWERED))
			return;

		boolean hasCatalystNearby = BlockPos.betweenClosedStream(
			pos.offset(-1, -1, -1), pos.offset(1, 1, 1)
		)
		.anyMatch(neighborPos -> level.getBlockState(neighborPos).is(RNTags.Blocks.CRYSTALLIZATION_CATALYST));

		if (hasCatalystNearby)
			this.getCrystallized(state).ifPresent(blockState -> level.setBlockAndUpdate(pos, blockState));
		else
			this.changeOverTime(state, level, pos, random);
	}

	@Override
	protected ItemInteractionResult useItemOn(
		ItemStack stack,
		BlockState state,
		Level level,
		BlockPos pos,
		Player player,
		InteractionHand hand,
		BlockHitResult hitResult
	) {
		return !state.getValue(POWERED) && waxing(stack, state, level, pos, player, hand, hitResult)
			? ItemInteractionResult.SUCCESS
			: super.useItemOn(stack, state, level, pos, player, hand, hitResult);
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
			state.getValue(WAXED)
				? BuiltInRegistries.ITEM.get(RubinatedNether.id(WaxableBlockItem.getWaxableItem(this)))
				: this
		);
	}
}
