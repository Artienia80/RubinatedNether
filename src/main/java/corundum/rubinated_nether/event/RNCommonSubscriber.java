package corundum.rubinated_nether.event;

import com.mojang.logging.LogUtils;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNDamageTypes;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.TarnishStage;
import corundum.rubinated_nether.content.blocks.ChandelierBlock;
import corundum.rubinated_nether.content.blocks.entities.FreezerBlockEntity;
import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import corundum.rubinated_nether.content.entity.BronzeEntity;
import corundum.rubinated_nether.content.items.DrillItem;
import corundum.rubinated_nether.content.items.MusicBoxItem;
import corundum.rubinated_nether.content.module.ToolModules;
import corundum.rubinated_nether.misc.DatapackRegistry;
import corundum.rubinated_nether.networking.CollectDiscsPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;


@EventBusSubscriber(modid = RubinatedNether.MODID)
public class RNCommonSubscriber {
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
    public static void modifyCurseBreakSpeed(PlayerEvent.BreakSpeed event) {
        var player = event.getEntity();
        var itemStack = player.getMainHandItem();
        int curseLevel = itemStack.getEnchantmentLevel(player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(RNEnchantments.DEFICIENCY_CURSE));

        if (curseLevel > 0) {
            event.setNewSpeed(Math.max(0.1f, event.getOriginalSpeed() - (float) curseLevel));
        }
    }

    private static final TagKey<Block> ORES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("c", "ores"));

    /**
     * Blessed Fortune: while Blessed, mining an ore has a chance (scaling with remaining effect time)
     * to roll the block's loot a second time. Replaces BlessedFortuneMixin.
     * <p>
     * Runs first so that other BlockDropsEvent listeners also see the bonus drops, like they did when
     * the mixin added them to NeoForge's captured drop list.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void doubleBlessedOreDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player)) return;

        MobEffectInstance blessed = player.getEffect(RNEffects.BLESSED);
        if (blessed == null || !event.getState().is(ORES)) return;

        ServerLevel level = event.getLevel();
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) || level.restoringBlockSnapshots) return;

        if (actsAsSilkTouch(level, event.getTool())) return;

        float chance = Math.min(1.0f, blessed.getDuration() / 384000.0f);
        if (player.getRandom().nextFloat() >= chance) return;

        BlockPos pos = event.getPos();
        for (ItemStack stack : Block.getDrops(event.getState(), level, pos, event.getBlockEntity(), player, event.getTool())) {
            if (stack.isEmpty()) continue;
            // Same placement as Block.popResource
            double halfHeight = EntityType.ITEM.getHeight() / 2.0;
            double x = pos.getX() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
            double y = pos.getY() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25) - halfHeight;
            double z = pos.getZ() + 0.5 + Mth.nextDouble(level.random, -0.25, 0.25);
            ItemEntity entity = new ItemEntity(level, x, y, z, stack);
            entity.setDefaultPickUpDelay();
            event.getDrops().add(entity);
        }
    }

    private static boolean actsAsSilkTouch(ServerLevel level, ItemStack tool) {
        if (ToolModules.has(tool, ToolModules.DAMPENING)) return true;
        Holder<Enchantment> silkTouch = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
        return tool.getEnchantmentLevel(silkTouch) > 0;
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
    public static void registerPocketwatchSave(RegisterPayloadHandlersEvent event) {
        // Must be registered on BOTH sides, otherwise the NeoForge handshake rejects clients
        // ("channel is missing on the server side"). The handler body only runs on the client.
        event.registrar("1").playToClient(
                PocketwatchSavePayload.TYPE,
                PocketwatchSavePayload.STREAM_CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> RNClientSubscriber.handlePocketwatchSave())
        );
    }

    @SubscribeEvent
    public static void registerCollectDiscs(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(CollectDiscsPayload.TYPE, CollectDiscsPayload.CODEC,
                (payload, ctx) -> ctx.enqueueWork(() -> {
                    Player player = ctx.player();
                    ItemStack carried = player.containerMenu.getCarried();
                    LOGGER.info("CollectDiscs received. menu={}, carried={}", player.containerMenu.getClass().getSimpleName(), carried);
                    player.displayClientMessage(Component.literal("DEBUG server: menu="
                            + player.containerMenu.getClass().getSimpleName() + ", carried=" + carried), false);
                    if (carried.getItem() instanceof MusicBoxItem) {
                        BundleContents before = carried.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
                        MusicBoxItem.collectDiscs(carried, player.containerMenu.slots, player);
                        BundleContents after = carried.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
                        player.displayClientMessage(Component.literal("DEBUG server: box discs " + before.size() + " -> " + after.size()), false); // remove once it works
                        player.containerMenu.broadcastChanges();
                    }
                }));
    }
}
