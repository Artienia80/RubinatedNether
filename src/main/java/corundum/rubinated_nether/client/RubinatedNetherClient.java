package corundum.rubinated_nether.client;

import corundum.rubinated_nether.client.particles.BloodDripParticle;
import corundum.rubinated_nether.client.particles.SteamParticle;
import corundum.rubinated_nether.client.render.entity.RubyLensModel;
import corundum.rubinated_nether.client.render.entity.RubyLensRenderLayer;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.content.blocks.TarnishingBronzeVentBlock;
import corundum.rubinated_nether.mixin.accessors.EntityRenderDispatcherAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

@OnlyIn(Dist.CLIENT)
public class RubinatedNetherClient {
	public static final int WHITE = 0xFFFFFFFF;

	public static void client(IEventBus bussin) {
		bussin.addListener(RubinatedNetherClient::registerEntityLayers);
		bussin.addListener(RubinatedNetherClient::registeModelLayers);
		bussin.addListener(RubinatedNetherClient::registerParticleProviders);
		NeoForge.EVENT_BUS.addListener(RubinatedNetherClient::onClientTick);
	}

	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(RNParticleTypes.BLOOD_DRIP.get(), BloodDripParticle.Provider::new);
		event.registerSpriteSet(RNParticleTypes.STEAM.get(), SteamParticle.Provider::new);
	}

	public static void registeModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(
				RubyLensModel.LAYER_LOCATION,
				RubyLensModel::createBodyLayer
		);
	}

	public static void onClientTick(ClientTickEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null || mc.isPaused()) return;

		var level = mc.level;
		var player = mc.player;
		if (player == null) return;

		BlockPos playerPos = player.blockPosition();
		int range = 48;

		BlockPos.betweenClosed(
				playerPos.offset(-range, -range, -range),
				playerPos.offset(range, range, range)
		).forEach(pos -> {
			BlockState state = level.getBlockState(pos);
			if (!(state.getBlock() instanceof TarnishingBronzeVentBlock)) return;
			if (!state.getValue(TarnishingBronzeVentBlock.POWERED)) return;

			int signal = state.getValue(TarnishingBronzeVentBlock.SIGNAL_STRENGTH);
			Direction facing = state.getValue(TarnishingBronzeVentBlock.FACING);
			RandomSource random = level.getRandom();

			double ox = pos.getX() + 0.5;
			double oy = pos.getY() + 0.5;
			double oz = pos.getZ() + 0.5;
			double dx = facing.getStepX();
			double dy = facing.getStepY();
			double dz = facing.getStepZ();
			double initialSpeed = signal / 40.0;

			for (int i = 0; i < 2; i++) {
				Vec3 spread = new Vec3(
						facing.getAxis() != Direction.Axis.X ? (random.nextDouble() - 0.5) * 0.8 : 0,
						facing.getAxis() != Direction.Axis.Y ? (random.nextDouble() - 0.5) * 0.8 : 0,
						facing.getAxis() != Direction.Axis.Z ? (random.nextDouble() - 0.5) * 0.8 : 0
				);
				double vx = dx * initialSpeed + (random.nextDouble() - 0.5) * 0.01;
				double vy = dy * initialSpeed + (random.nextDouble() - 0.5) * 0.01;
				double vz = dz * initialSpeed + (random.nextDouble() - 0.5) * 0.01;

				level.addParticle(RNParticleTypes.STEAM.get(), true,
						ox + dx + spread.x,
						oy + dy + spread.y,
						oz + dz + spread.z,
						vx, vy, vz);
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void registerEntityLayers(EntityRenderersEvent.AddLayers event) {
		var dispatcher = event.getContext().getEntityRenderDispatcher();
		var renderers = ((EntityRenderDispatcherAccessor) dispatcher).getRenderers();
		var models = event.getEntityModels();

		renderers.forEach((type, renderer) -> {
			if (renderer instanceof LivingEntityRenderer<?, ?> livingRenderer && livingRenderer.getModel() instanceof HeadedModel) {
				livingRenderer.addLayer(new RubyLensRenderLayer(livingRenderer, models, livingRenderer.getModel()));
			}
		});

		((EntityRenderDispatcherAccessor) dispatcher).setRenderers(renderers);

		PlayerRenderer defaultSkin = event.getSkin(Model.WIDE);
		PlayerRenderer slimSkin = event.getSkin(Model.SLIM);
		defaultSkin.addLayer(new RubyLensRenderLayer<>(defaultSkin, models, defaultSkin.getModel()));
		slimSkin.addLayer(new RubyLensRenderLayer<>(slimSkin, models, slimSkin.getModel()));
	}
}