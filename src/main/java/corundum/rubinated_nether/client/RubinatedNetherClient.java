package corundum.rubinated_nether.client;

import corundum.rubinated_nether.client.particles.BloodDripParticle;
import corundum.rubinated_nether.client.particles.SteamParticle;
import corundum.rubinated_nether.client.render.entity.RubyLensModel;
import corundum.rubinated_nether.client.render.entity.RubyLensRenderLayer;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.TarnishingBronzeVentBlock;
import corundum.rubinated_nether.mixin.accessors.EntityRenderDispatcherAccessor;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin.Model;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class RubinatedNetherClient {
	public static final int WHITE = 0xFFFFFFFF;

	private static final VoxelShape SMOKE_SEGMENT_BASE = Shapes.box(0.3, 0, 0.3, 0.7, 1, 0.7);
	private static final double CRYSTALLIZED_DETECTION_RANGE = 15.0;

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

	private static VoxelShape rotateSmokeShape(Direction facing) {
		return switch (facing.getAxis()) {
			case Z -> SMOKE_SEGMENT_BASE;
			case X -> Shapes.box(0, 0.3, 0.3, 1, 0.7, 0.7);
			case Y -> Shapes.box(0.3, 0.3, 0, 0.7, 0.7, 1);
		};
	}

	private static int calculateSmokeRange(BlockGetter level, BlockPos pos, Direction facing, int maxRange) {
		BlockPos.MutableBlockPos cursor = pos.mutable();
		VoxelShape smokeColumn = rotateSmokeShape(facing);

		for (int i = 0; i < maxRange; i++) {
			cursor.move(facing);
			BlockState state = level.getBlockState(cursor);

			if (state.is(RNTags.Blocks.SMOKE_PASSTHROUGH)) continue;

			VoxelShape collision = Shapes.join(
					state.getCollisionShape(level, cursor),
					smokeColumn,
					BooleanOp.AND
			);

			if (!collision.isEmpty()) {
				return i;
			}
		}

		return maxRange;
	}

	private static AABB buildDetectionAABB(BlockPos pos, Direction facing, int range) {
		double cx = pos.getX() + 0.5;
		double cy = pos.getY() + 0.5;
		double cz = pos.getZ() + 0.5;
		double hw = 0.6;

		double ex = facing.getStepX() * range;
		double ey = facing.getStepY() * range;
		double ez = facing.getStepZ() * range;

		double minX = cx + Math.min(0, ex) - (facing.getAxis() != Direction.Axis.X ? hw : 0);
		double maxX = cx + Math.max(0, ex) + (facing.getAxis() != Direction.Axis.X ? hw : 0);
		double minY = cy + Math.min(0, ey) - (facing.getAxis() != Direction.Axis.Y ? hw : 0);
		double maxY = cy + Math.max(0, ey) + (facing.getAxis() != Direction.Axis.Y ? hw : 0);
		double minZ = cz + Math.min(0, ez) - (facing.getAxis() != Direction.Axis.Z ? hw : 0);
		double maxZ = cz + Math.max(0, ez) + (facing.getAxis() != Direction.Axis.Z ? hw : 0);

		if (facing.getStepX() > 0) minX += 1.0;
		else if (facing.getStepX() < 0) maxX -= 1.0;
		if (facing.getStepY() > 0) minY += 1.0;
		else if (facing.getStepY() < 0) maxY -= 1.0;
		if (facing.getStepZ() > 0) minZ += 1.0;
		else if (facing.getStepZ() < 0) maxZ -= 1.0;

		return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
	}

	private static void spawnSteamParticles(net.minecraft.client.multiplayer.ClientLevel level,
	                                        BlockPos pos, Direction facing, int smokeRange,
	                                        double initialSpeed, RandomSource random) {
		double ox = pos.getX() + 0.5;
		double oy = pos.getY() + 0.5;
		double oz = pos.getZ() + 0.5;
		double dx = facing.getStepX();
		double dy = facing.getStepY();
		double dz = facing.getStepZ();

		double rawCount = RNConfig.ventSmokeParticleMultiplier;
		int baseCount = (int) rawCount;
		if (random.nextDouble() < (rawCount - baseCount)) baseCount++;

		for (int i = 0; i < baseCount; i++) {
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
			if (!(state.getBlock() instanceof TarnishingBronzeVentBlock ventBlock)) return;

			Direction facing = state.getValue(TarnishingBronzeVentBlock.FACING);
			RandomSource random = level.getRandom();

			if (ventBlock.getAge() == TarnishStage.CRYSTALLIZED) {
				AABB detectionBox = buildDetectionAABB(pos, facing, (int) CRYSTALLIZED_DETECTION_RANGE);
				List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, detectionBox);
				if (entities.isEmpty()) return;

				int smokeRange = calculateSmokeRange(level, pos, facing, (int) CRYSTALLIZED_DETECTION_RANGE);
				if (smokeRange <= 0) return;

				spawnSteamParticles(level, pos, facing, smokeRange, CRYSTALLIZED_DETECTION_RANGE / 40.0, random);
				return;
			}

			if (!state.getValue(TarnishingBronzeVentBlock.POWERED)) return;

			int signal = state.getValue(TarnishingBronzeVentBlock.SIGNAL_STRENGTH);
			int smokeRange = calculateSmokeRange(level, pos, facing, signal);
			if (smokeRange <= 0) return;

			spawnSteamParticles(level, pos, facing, smokeRange, signal / 40.0, random);
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