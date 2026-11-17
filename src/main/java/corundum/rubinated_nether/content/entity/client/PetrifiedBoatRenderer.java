package corundum.rubinated_nether.content.entity.client;

import com.mojang.datafixers.util.Pair;
import corundum.rubinated_nether.RubinatedNether;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public class PetrifiedBoatRenderer extends BoatRenderer {
	private final Pair<ResourceLocation, ListModel<Boat>> resources;

	public PetrifiedBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
		super(context, chestBoat);
		ResourceLocation texture = RubinatedNether.id(
				chestBoat ? "textures/entity/chest_boat/petrified.png" : "textures/entity/boat/petrified.png"
		);
		ListModel<Boat> model = chestBoat
				? new ChestBoatModel(context.bakeLayer(ModelLayers.createChestBoatModelName(Boat.Type.OAK)))
				: new BoatModel(context.bakeLayer(ModelLayers.createBoatModelName(Boat.Type.OAK)));
		this.resources = Pair.of(texture, model);
	}

	@Override
	public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
		return this.resources;
	}
}
