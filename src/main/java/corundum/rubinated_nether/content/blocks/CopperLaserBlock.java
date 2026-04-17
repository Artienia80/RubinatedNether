package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.blocks.bases.AbstractLaserBlock;
import corundum.rubinated_nether.content.blocks.entities.CopperLaserBlockEntity;
import corundum.rubinated_nether.utils.BEBlock;
import corundum.rubinated_nether.utils.ShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.WeatheringCopper;
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

public class CopperLaserBlock extends AbstractLaserBlock implements BEBlock<CopperLaserBlockEntity>, WeatheringCopper {

	public static final MapCodec<CopperLaserBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					WeatherState.CODEC.fieldOf("weathering_state").forGetter(CopperLaserBlock::getAge),
					propertiesCodec()
			).apply(instance, CopperLaserBlock::new)
	);

	private final WeatherState weatherState;

	public CopperLaserBlock(WeatherState state, BlockBehaviour.Properties props) {
		super(props);
		this.weatherState = state;

	}

	@Override
	public BlockEntityType<? extends CopperLaserBlockEntity> getBlockEntityType() {
		return RNBlockEntities.COPPER_LASER.get();
	}

	@Override
	public Class<? extends CopperLaserBlockEntity> getBlockEntityClass() {
		return CopperLaserBlockEntity.class;
	}


	// ---- WeatheringCopper Implementation ----
	@Override
	public WeatherState getAge() {
		return weatherState;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		this.changeOverTime(state, level, pos, random);
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return WeatheringCopper.getNext(state.getBlock()).isPresent();
	}

	@Override
	protected MapCodec<? extends CopperLaserBlock> codec() {
		return CODEC;
	}
}