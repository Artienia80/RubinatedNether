package corundum.rubinated_nether.compat;

import corundum.rubinated_nether.client.render.entity.CuriosRubyLensRenderer;
import corundum.rubinated_nether.content.RNItems;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosCompat {
	public static boolean hasLens(LivingEntity entity) {
		return CuriosApi.getCuriosInventory(entity)
			.map(handler -> handler.isEquipped(RNItems.RUBY_LENS.get()))
			.orElse(false);
	}

	public static void registerRenderer() {
		CuriosRendererRegistry.register(RNItems.RUBY_LENS.get(), CuriosRubyLensRenderer::new);
	}
}
