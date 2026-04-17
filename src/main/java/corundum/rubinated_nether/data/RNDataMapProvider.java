package corundum.rubinated_nether.data;

import corundum.rubinated_nether.content.RNBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

import java.util.concurrent.CompletableFuture;

public class RNDataMapProvider extends DataMapProvider {

    protected RNDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        var waxables = this.builder(NeoForgeDataMaps.WAXABLES);
        this.addWaxable(waxables, RNBlocks.BRONZE_BLOCK.get());
    }

    private void addWaxable(DataMapProvider.Builder<Waxable, Block> map, Block block) {
        map.add(Holder.direct(block), new Waxable(block), false);
    }
}
