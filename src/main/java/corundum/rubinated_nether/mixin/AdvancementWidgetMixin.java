package corundum.rubinated_nether.mixin;

import corundum.rubinated_nether.utils.RNAdvancementLinks;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

/**
 * Draws extra connection lines from additional parents (see {@link RNAdvancementLinks}) into an advancement,
 * so that two advancements can visually merge into one. Uses the same line geometry and colors as vanilla's
 * AdvancementWidget#drawConnectivity.
 */
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
	@Mutable
	private int y;

	/**
	 * Vanilla's layout places an advancement on the row of its single real parent. For advancements with extra
	 * visual parents, re-center it vertically between all of its parents (real + extra), the same way vanilla
	 * centers a parent between two children. The resulting vertical shift is inherited by all descendants, so
	 * their subtree moves along with it and lines stay straight.
	 */
	@Inject(method = "<init>", at = @At("RETURN"))
	private void rubinated_nether$centerBetweenParents(CallbackInfo ci) {
		AdvancementNode root = this.advancementNode.root();
		float shift = rubinated_nether$shift(this.advancementNode, root);
		if (shift != 0.0F) {
			this.y = Mth.floor((rubinated_nether$displayY(this.advancementNode) + shift) * 27.0F);
		}
	}

	/** Vertical shift (in rows) applied to a node relative to the vanilla layout. */
	@Unique
	private static float rubinated_nether$shift(AdvancementNode node, AdvancementNode root) {
		AdvancementNode parent = node.parent();
		float inherited = parent == null ? 0.0F : rubinated_nether$shift(parent, root);

		List<ResourceLocation> extraParents = RNAdvancementLinks.extraParents(node.holder().id());
		if (extraParents.isEmpty() || parent == null) return inherited;

		float sum = rubinated_nether$displayY(parent) + inherited;
		int count = 1;
		for (ResourceLocation id : extraParents) {
			AdvancementNode extra = rubinated_nether$find(root, id);
			if (extra == null) continue;
			sum += rubinated_nether$displayY(extra) + rubinated_nether$shift(extra, root);
			count++;
		}
		if (count < 2) return inherited;
		return sum / count - rubinated_nether$displayY(node);
	}

	@Unique
	private static float rubinated_nether$displayY(AdvancementNode node) {
		return node.advancement().display().map(DisplayInfo::getY).orElse(0.0F);
	}

	@Unique
	private static AdvancementNode rubinated_nether$find(AdvancementNode node, ResourceLocation id) {
		if (node.holder().id().equals(id)) return node;
		for (AdvancementNode child : node.children()) {
			AdvancementNode found = rubinated_nether$find(child, id);
			if (found != null) return found;
		}
		return null;
	}

	@Inject(method = "drawConnectivity", at = @At("TAIL"))
	private void rubinated_nether$drawExtraParents(GuiGraphics graphics, int originX, int originY, boolean dropShadow, CallbackInfo ci) {
		List<ResourceLocation> extraParents = RNAdvancementLinks.extraParents(this.advancementNode.holder().id());
		if (extraParents.isEmpty()) return;

		for (Map.Entry<AdvancementHolder, AdvancementWidget> entry : this.tab.widgets.entrySet()) {
			if (!extraParents.contains(entry.getKey().id())) continue;
			AdvancementWidget parent = entry.getValue();

			int parentCenterX = originX + parent.getX() + 13;
			int midX = originX + parent.getX() + 26 + 4;
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
