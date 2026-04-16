package corundum.rubinated_nether.event;

import com.mojang.logging.LogUtils;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNDamageTypes;
import corundum.rubinated_nether.content.RNDataComponents;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.ChandelierBlock;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import corundum.rubinated_nether.content.items.DrillItem;
import corundum.rubinated_nether.misc.DatapackRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import org.slf4j.Logger;

@EventBusSubscriber(modid = RubinatedNether.MODID)
public class RNCommonEvents {
	public static final Logger LOGGER = LogUtils.getLogger();

	@SubscribeEvent
	public static void onLivingHurt(LivingDamageEvent.Post event) {
		var entity = event.getEntity();
		var source = event.getSource();

		if (!source.is(RNDamageTypes.CHANDELIER)) return;
        if (!(source.getDirectEntity() instanceof FallingBlockEntity fallingBlock)) return;

        BlockState blockState = fallingBlock.getBlockState();
        if (!(blockState.getBlock() instanceof ChandelierBlock chandelier)) return;

        TarnishStage tarnishStage = chandelier.getAge();
        if (tarnishStage != TarnishStage.CRYSTALLIZED) return;

        entity.addEffect(new MobEffectInstance(RNEffects.BRONZE_DISEASED, 72000, 0));
	}

    @SubscribeEvent
    public static void changeBronzeSize(EntityEvent.Size event) {
        if (!(event.getEntity() instanceof BronzeEntity bronzeEntity)) return;

        event.setNewSize(bronzeEntity.isBurrowed() ?
                EntityDimensions.fixed(0.35F, 0.375F) :
                EntityDimensions.scalable(0.7F, 1.4F));
    }

	@SubscribeEvent
	public static void modifyBreakSpeed(PlayerEvent.BreakSpeed event) {
		var player = event.getEntity();
		var itemStack = player.getMainHandItem();

		if (!(itemStack.getItem() instanceof DrillItem drillItem)) return;

        var modified = drillItem.calcModifier(itemStack, event.getOriginalSpeed());
        if (modified > event.getOriginalSpeed())
            event.setNewSpeed(modified);
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
}