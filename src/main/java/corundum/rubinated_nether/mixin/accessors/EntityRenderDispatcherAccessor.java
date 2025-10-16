package corundum.rubinated_nether.mixin.accessors;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public interface EntityRenderDispatcherAccessor {
	@Accessor("renderers")
	public Map<EntityType<?>, EntityRenderer<?>> getRenderers();

	@Accessor("renderers")
	public void setRenderers(Map<EntityType<?>, EntityRenderer<?>> renderers);
}
