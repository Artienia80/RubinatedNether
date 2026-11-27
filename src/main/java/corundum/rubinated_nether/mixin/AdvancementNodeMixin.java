package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.utils.RNAdvancementLinks;
import net.minecraft.advancements.AdvancementNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Mixin(AdvancementNode.class)
public abstract class AdvancementNodeMixin {
	@Inject(method = "children", at = @At("RETURN"), cancellable = true)
	private void rubinated_nether$orderChildren(CallbackInfoReturnable<Iterable<AdvancementNode>> cir) {
		List<AdvancementNode> sorted = new ArrayList<>();
		boolean hasOrdered = false;
		for (AdvancementNode child : cir.getReturnValue()) {
			sorted.add(child);
			hasOrdered |= RNAdvancementLinks.hasOrder(child.holder().id());
		}
		if (!hasOrdered) return;

		sorted.sort(Comparator.comparingInt((AdvancementNode child) -> RNAdvancementLinks.order(child.holder().id())));
		cir.setReturnValue(sorted);
	}
}
