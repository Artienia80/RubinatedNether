package corundum.rubinated_nether.content;

import corundum.rubinated_nether.content.blocks.RunestoneBlock;
import corundum.rubinated_nether.content.blocks.entities.CofferItemHandler;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import corundum.rubinated_nether.content.blocks.entities.RunestoneBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class RNCapabilities {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(RNCapabilities::onRegisterCapabilities);
    }

    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                RNBlockEntities.FREEZER.get(),
                FreezerBlockEntity::getItemHandler
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                RNBlockEntities.COFFER.get(),
                (coffer, side) -> new CofferItemHandler(coffer)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                RNBlockEntities.RUBINATED_COFFER.get(),
                (coffer, side) -> new CofferItemHandler(coffer)
        );
        event.registerBlock(
                Capabilities.ItemHandler.BLOCK,
                (level, pos, state, blockEntity, side) -> {
                    var lowerPos = state.getValue(RunestoneBlock.HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
                    BlockEntity lowerEntity = lowerPos.equals(pos) ? blockEntity : level.getBlockEntity(lowerPos);
                    if (!(lowerEntity instanceof RunestoneBlockEntity runestone)) return null;

                    IItemHandler handler = side == null ? new InvWrapper(runestone) : new SidedInvWrapper(runestone, side);
                    return handler;
                },
                RNBlocks.RUNESTONE.get()
        );
    }
}
