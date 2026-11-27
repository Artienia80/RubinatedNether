package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.utils.RNAdvancementLinks;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(AdvancementWidget.class)
public abstract class AdvancementWidgetMixin {
	@Shadow
	@Final
	private AdvancementTab tab;

	@Shadow
	@Final
	private AdvancementNode advancementNode;

	@Shadow
	@Final
	private int x;

	@Shadow
	@Final
	private int y;

	@Inject(method = "drawConnectivity", at = @At("TAIL"))
	private void rubinated_nether$drawExtraParents(GuiGraphics graphics, int originX, int originY, boolean dropShadow, CallbackInfo ci) {
		List<ResourceLocation> extraParents = RNAdvancementLinks.extraParents(this.advancementNode.holder().id());
		if (extraParents.isEmpty()) return;

		for (Map.Entry<AdvancementHolder, AdvancementWidget> entry : this.tab.widgets.entrySet()) {
			if (!extraParents.contains(entry.getKey().id())) continue;
			AdvancementWidget parent = entry.getValue();

			int parentCenterX = originX + parent.getX() + 13;
			int midX = originX + this.x + 2;
			int parentCenterY = originY + parent.getY() + 13;
			int childCenterX = originX + this.x + 13;
			int childCenterY = originY + this.y + 13;
			int color = dropShadow ? -16777216 : -1;

			if (dropShadow) {
				graphics.hLine(midX, parentCenterX, parentCenterY - 1, color);
				graphics.hLine(midX + 1, parentCenterX, parentCenterY, color);
				graphics.hLine(midX, parentCenterX, parentCenterY + 1, color);
				graphics.hLine(childCenterX, midX - 1, childCenterY - 1, color);
				graphics.hLine(childCenterX, midX - 1, childCenterY, color);
				graphics.hLine(childCenterX, midX - 1, childCenterY + 1, color);
				graphics.vLine(midX - 1, childCenterY, parentCenterY, color);
				graphics.vLine(midX + 1, childCenterY, parentCenterY, color);
			} else {
				graphics.hLine(midX, parentCenterX, parentCenterY, color);
				graphics.hLine(childCenterX, midX, childCenterY, color);
				graphics.vLine(midX, childCenterY, parentCenterY, color);
			}
		}
	}
}
