package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.bases.AbstractLaserBlock;
import corundum.rubinated_nether.content.blocks.bases.TarnishingBronze;
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

public class BronzeLaserBlock extends AbstractLaserBlock implements BEBlock<BronzeLaserBlockEntity>, TarnishingBronze {

	public static final MapCodec<BronzeLaserBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					TarnishStage.CODEC.fieldOf("tarnishing_state").forGetter(BronzeLaserBlock::getAge),
					propertiesCodec()
			).apply(instance, BronzeLaserBlock::new)
	);
	private final TarnishStage tarnishStage;


	public BronzeLaserBlock(TarnishStage state, BlockBehaviour.Properties props) {
		super(props);
		this.tarnishStage = state;
	}


	@Override
	public BlockEntityType<? extends BronzeLaserBlockEntity> getBlockEntityType() {
		return RNBlockEntities.BRONZE_LASER.get();
	}

	@Override
	public Class<? extends BronzeLaserBlockEntity> getBlockEntityClass() {
		return BronzeLaserBlockEntity.class;
	}

	// ---- Tarnish logic ----
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		this.onTarnishTick(state, level, pos, random);
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return TarnishingBronze.canCrystallize(state.getBlock());
	}

	@Override
	public TarnishStage getAge() {
		return tarnishStage;
	}

	@Override
	protected MapCodec<? extends BronzeLaserBlock> codec() {
		return CODEC;
	}
}
