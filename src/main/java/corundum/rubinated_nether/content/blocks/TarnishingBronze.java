package corundum.rubinated_nether.content.blocks;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.ChangeOverTimeBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

public interface TarnishingBronze extends ChangeOverTimeBlock<TarnishingBronze.TarnishState> {

    Supplier<BiMap<RNBlocks, RNBlocks>> NEXT_BY_BLOCK = Suppliers.memoize(
            () -> ImmutableBiMap.<DeferredBlock, RNBlocks>builder()
                    .put(RNBlocks.BRONZE_BLOCK, RNBlocks.DISCOLORED_BRONZE_BLOCK)
                    .put(RNBlocks.DISCOLORED_BRONZE_BLOCK, RNBlocks.CORRODED_BRONZE_BLOCK)
                    .put(RNBlocks.CORRODED_BRONZE_BLOCK, RNBlocks.TARNISHED_BRONZE_BLOCK)
                    .build()
    );

    Supplier<BiMap<RNBlocks, RNBlocks>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> NEXT_BY_BLOCK.get().inverse());

    static final Map<RNBlocks, RNBlocks> INVERSE_TARNISHABLES_DATAMAP_INTERNAL = new HashMap<>();

    public static final Map<RNBlocks, RNBlocks> INVERSE_TARNISHABLES_DATAMAP = Collections.unmodifiableMap(INVERSE_TARNISHABLES_DATAMAP_INTERNAL);

    public static RNBlocks getPreviousTarnishStage(RNBlocks rnBlocks) {
        return INVERSE_TARNISHABLES_DATAMAP.containsKey(rnBlocks) ? INVERSE_TARNISHABLES_DATAMAP.get(rnBlocks) : TarnishingBronze.PREVIOUS_BY_BLOCK.get().get(rnBlocks);
    }

    static Optional<RNBlocks> getPrevious(RNBlocks rnBlocks) {
        return Optional.ofNullable(getPreviousTarnishStage(rnBlocks));
    }

    static RNBlocks getFirst(RNBlocks p_block) {
        RNBlocks block = p_block;

        for (RNBlocks block1 = getPreviousTarnishStage(p_block); block1 != null; block1 =getPreviousTarnishStage(block1)) {
            block = block1;
        }

        return block;
    }

    static Optional<BlockState> getPrevious(BlockState state) {
        return getPrevious(state.getBlock().defaultBlockState()).map(p_154903_ -> p_154903_.getBlock().withPropertiesOf(state));
    }

    public record Tarnishable(RNBlocks nextTarnishStage) {
        public static final Codec<Tarnishable> TARNISHABLE_CODEC = BuiltInRegistries.BLOCK.byNameCodec()
                .xmap(Tarnishable::new, Tarnishable::nextTarnishStage);
        public static final Codec<Tarnishable> CODEC = Codec.withAlternative(
                RecordCodecBuilder.create(in -> in.group(
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("next_tarnish_stage").forGetter(Tarnishable::nextTarnishStage)).apply(in, Tarnishable::new)),
                TARNISHABLE_CODEC);
    }

    public static RNBlocks getNextTarnishStage(RNBlocks rnBlocks) {
        Tarnishable tarnishable = rnBlocks.builtInRegistryHolder().getData(NeoForgeDataMaps.OXIDIZABLES);
        return tarnishable != null ? tarnishable.nextTarnishStage() : TarnishingBronze.NEXT_BY_BLOCK.get().get(rnBlocks);
    }

    static Optional<RNBlocks> getNext(RNBlocks rnBlocks) {
        return Optional.ofNullable(getNextTarnishStage(rnBlocks));
    }

    static BlockState getFirst(BlockState state) {
        return getFirst(state.getBlock()).withPropertiesOf(state);
    }

    @Override
    default Optional<BlockState> getNext(BlockState state) {
        return getNext(state.getBlock()).map(p_154896_ -> p_154896_.getBlock().withPropertiesOf(state));

    }

    @Override
    default float getChanceModifier() {
        return this.getAge() == TarnishingBronze.TarnishState.UNAFFECTED ? 0.75F : 1.0F;
    }

    public static enum TarnishState implements StringRepresentable {
        UNAFFECTED("unaffected"),
        DISCOLORED("discolored"),
        CORRODED("corroded"),
        TARNISHED("tarnished");
        CRYSTALIZED("crystalized");


        public static final Codec<TarnishingBronze.TarnishState> CODEC = StringRepresentable.fromEnum(TarnishingBronze.TarnishState::values);
        private final String name;

        private TarnishState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
