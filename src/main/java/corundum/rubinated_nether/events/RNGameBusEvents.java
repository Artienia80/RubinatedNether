package corundum.rubinated_nether.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNDamageTypes;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.blocks.ChandelierBlock;
import corundum.rubinated_nether.content.blocks.SoakStoneBlock;
import corundum.rubinated_nether.content.blocks.TarnishingBronze;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import corundum.rubinated_nether.content.effect.renderer.BronzeDiseasedEffectOverlay;
import corundum.rubinated_nether.content.items.DrillItem;
import corundum.rubinated_nether.misc.DatapackRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class RNGameBusEvents {

	public static final Logger LOGGER = LogUtils.getLogger();

	private static final Map<UUID, Long> lastMiningTick = new HashMap<>();
	private static final Map<UUID, Integer> decayTicks = new HashMap<>();
	private static final Map<UUID, Long> lastLoggedTick = new HashMap<>();

	@SubscribeEvent
	public static void onLivingHurt(LivingDamageEvent.Post event) {
		var entity = event.getEntity();
		var source = event.getSource();

		if (source.is(RNDamageTypes.CHANDELIER)) {
			if (source.getDirectEntity() instanceof FallingBlockEntity fallingBlock) {
				BlockState blockState = fallingBlock.getBlockState();
				if (blockState.getBlock() instanceof ChandelierBlock chandelier) {
					TarnishingBronze.TarnishState tarnishState = chandelier.getAge();
					if (tarnishState == TarnishingBronze.TarnishState.CRYSTALLIZED) {
						boolean effectApplied = entity.addEffect(new MobEffectInstance(RNEffects.BRONZE_DISEASED, 72000, 0));

						if (effectApplied) {
						} else {
						}
					} else {
					}
				} else {
				}
			} else {
			}
		}
	}
	@SubscribeEvent
	public static void modifyBreakSpeed(PlayerEvent.BreakSpeed event) {
		var player = event.getEntity();
		var itemStack = player.getMainHandItem();

		if (!event.isCanceled() && itemStack.getItem() instanceof DrillItem drillItem) {
			var currentTick = player.level().getGameTime();
			var playerId = player.getUUID();
			lastMiningTick.put(playerId, currentTick);

			// Retrieve current counter
			var tag = drillItem.getNBT();
			var ticksUsed = tag.getInt("ticksUsed");

			// Calculate multiplier
			var multiplier = 1.0f + ((float) ticksUsed / DrillItem.MAX_USE_TICKS) *
					(DrillItem.MAX_MULTIPLIER_BOOST - 1.0f);

			// Increment ticksUsed but cap at MAX_USE_TICKS
			if (ticksUsed < DrillItem.MAX_USE_TICKS) {
				tag.putInt("ticksUsed", ticksUsed + 1);
			}

			event.setNewSpeed(event.getNewSpeed() * multiplier);
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		var player = event.getEntity();
		var stack = player.getMainHandItem();
		var playerId = player.getUUID();
		var currentTick = player.level().getGameTime();

		if (stack.getItem() instanceof DrillItem drill) {
			CompoundTag tag = drill.getNBT();
			if (tag == null) return;

			// Log multiplier every 2 seconds (40 ticks)
			if (!lastLoggedTick.containsKey(playerId) || currentTick - lastLoggedTick.get(playerId) >= 40) {
				var ticksUsed = tag.getInt("ticksUsed");
				var multiplier = 1.0f + ((float) ticksUsed / DrillItem.MAX_USE_TICKS) *
						(DrillItem.MAX_MULTIPLIER_BOOST - 1.0f);
				lastLoggedTick.put(playerId, currentTick);
			}

			// Handle multiplier decay when stopping
			if (!lastMiningTick.containsKey(playerId)) {
				return;
			}

			var lastTick = lastMiningTick.get(playerId);

			if (currentTick - lastTick <= 20) { // Raised from 15 to 20 ticks
				return;
			}

			if (!decayTicks.containsKey(playerId)) {
				decayTicks.put(playerId, 0);
			}

			int decayCount = decayTicks.get(playerId);

			if (currentTick % 20 != 0) { // Every 20 ticks
				return;
			}

			var ticksUsed = tag.getInt("ticksUsed");

			if (ticksUsed > 0) {
				var reduction = (int) Math.ceil(ticksUsed * 0.25);
				tag.putInt("ticksUsed", Math.max(ticksUsed - reduction, 0));
			} else {
				lastMiningTick.remove(playerId);
				decayTicks.remove(playerId);
			}

			decayTicks.put(playerId, decayCount + 1);
		}
	}

	@SubscribeEvent
	public static void freezerFuel(ServerAboutToStartEvent event) {
		FreezerBlockEntity.cleanFreezingTimes();

		var entries = event.getServer().registryAccess().registryOrThrow(DatapackRegistry.FREEZER_FUELS).entrySet();
		LOGGER.info("Registered Freezer Fuels: {}", entries.size());

		for (var entry : entries) {
			var x = entry.getValue();
			var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(x.item()));

			LOGGER.info(x.toString());
			FreezerBlockEntity.addItemFreezingTime(item, x.freezeTime());
		}
	}

	@SubscribeEvent
	public static void onRenderWorldOverlay(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;

		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || !player.hasEffect(RNEffects.BRONZE_DISEASED)) return;

		RenderSystem.enableBlend();
		RenderSystem.disableDepthTest();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
		RenderSystem.setShaderTexture(0, BronzeDiseasedEffectOverlay.PARANOIA_OVERLAY);

		blitFullScreen();

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableDepthTest();
		RenderSystem.disableBlend();
	}

	private static void blitFullScreen() {
		Minecraft mc = Minecraft.getInstance();
		GuiGraphics guiGraphics = new GuiGraphics(mc, mc.renderBuffers().bufferSource());

		int screenWidth = mc.getWindow().getGuiScaledWidth();
		int screenHeight = mc.getWindow().getGuiScaledHeight();

		guiGraphics.blit(BronzeDiseasedEffectOverlay.PARANOIA_OVERLAY, 0, 0, 0, 0.0F, 0.0F, screenWidth, screenHeight, screenWidth, screenHeight);

		guiGraphics.flush();
	}
}