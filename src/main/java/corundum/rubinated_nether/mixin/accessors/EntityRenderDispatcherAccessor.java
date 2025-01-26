package corundum.rubinated_nether.mixin.accessors;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;

@Mixin(EntityRenderDispatcher.class)
public interface EntityRenderDispatcherAccessor {
	@Accessor("renderers")
	public Map<EntityType<?>, EntityRenderer<?>> getRenderers();

	@Accessor("renderers")
	public void setRenderers(Map<EntityType<?>, EntityRenderer<?>> renderers);
}
