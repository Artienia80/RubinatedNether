package corundum.rubinated_nether.content.blocks.bases;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class TarnishingBronzeChainBlock extends ChainBlock implements TarnishingBronze {
    public static final MapCodec<TarnishingBronzeChainBlock> CODEC = RecordCodecBuilder.mapCodec(
            blockInstance -> blockInstance.group(
                            TarnishStage.CODEC
                                    .fieldOf("tarnishing_state")
                                    .forGetter(ChangeOverTimeBlock::getAge),
                            propertiesCodec()
                    )
                    .apply(blockInstance, TarnishingBronzeChainBlock::new)
    );
    private final TarnishStage tarnishStage;

    public TarnishingBronzeChainBlock(TarnishStage tarnishStage, BlockBehaviour.Properties properties) {
        super(properties);
        this.tarnishStage = tarnishStage;
        this.registerDefaultState(defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, WATERLOGGED);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.onTarnishTick(state, level, pos, random);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return TarnishingBronze.canCrystallize(state.getBlock());
    }

    public TarnishStage getAge() {
        return this.tarnishStage;
    }
}