package net.artienia.rubinated_nether.fabric.mixin.client;

import net.artienia.rubinated_nether.client.RubinatedNetherClient;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

	@Shadow @Final private EntityModelSet entityModels;

	@Shadow private Map<String, EntityRenderer<? extends Player>> playerRenderers;

	@Inject(
		method = "onResourceManagerReload",
		at = @At("TAIL")
	)
	public void onLoadEntityLayers(ResourceManager resourceManager, CallbackInfo ci) {
		RubinatedNetherClient.registerEntityLayers((EntityRenderDispatcher) (Object) this, this.entityModels, skin -> (PlayerRenderer) playerRenderers.get(skin));
	}
}
