package corundum.rubinated_nether.content.module;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

@FunctionalInterface
public interface DropProcessor {
    Optional<ItemStack> process(ServerLevel level, ItemStack drop);
}
