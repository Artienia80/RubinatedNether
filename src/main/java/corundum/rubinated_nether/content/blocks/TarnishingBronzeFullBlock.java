package corundum.rubinated_nether.content.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TarnishingBronzeFullBlock extends RNBlocks implements TarnishingBronze {
    public static final MapCodec<TarnishingBronzeFullBlock> CODEC = RecordCodecBuilder.mapCodec(
            p_308850_ -> p_308850_.group(TarnishingBronze.TarnishState.CODEC.fieldOf("tarnishing_state").forGetter(ChangeOverTimeBlock::getAge), propertiesCodec())
                    .apply(p_308850_, TarnishingBronzeFullBlock::new)
    );
    private final TarnishingBronze.TarnishState tarnishState;

    @Override
    public MapCodec<TarnishingBronzeFullBlock> codec() {
        return CODEC;
    }

    public TarnishingBronzeFullBlock(TarnishingBronze.TarnishState tarnishState, BlockBehaviour.Properties properties) {
        super(properties);
        this.tarnishState = tarnishState;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.changeOverTime(state, level, pos, random);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return TarnishingBronze.getNext(state.getBlock()).isPresent();
    }

    public TarnishingBronze.TarnishState getAge() {
        return this.tarnishState;
    }
}
