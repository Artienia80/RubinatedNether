package corundum.rubinated_nether.misc;

import corundum.rubinated_nether.content.blocks.RunestoneBlock;
import corundum.rubinated_nether.content.blocks.entities.RunestoneBlockEntity;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class RNJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerItemStorage(RunestoneStorageProvider.INSTANCE, RunestoneBlockEntity.class);
        registration.registerItemStorage(RunestoneStorageProvider.INSTANCE, RunestoneBlock.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(RubinatedNameProvider.INSTANCE, Block.class);
        registration.registerItemStorageClient(RunestoneStorageClientProvider.INSTANCE);
    }
}
