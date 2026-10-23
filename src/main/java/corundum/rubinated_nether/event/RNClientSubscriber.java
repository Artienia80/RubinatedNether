package corundum.rubinated_nether.event;

import com.mojang.blaze3d.systems.RenderSystem;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.client.particles.*;
import corundum.rubinated_nether.client.render.*;
import corundum.rubinated_nether.client.render.entity.RubyLensModel;
import corundum.rubinated_nether.client.render.entity.RubyLensRenderLayer;
import corundum.rubinated_nether.content.*;
import corundum.rubinated_nether.content.effect.renderer.BronzeDiseasedEffectOverlay;
import corundum.rubinated_nether.content.entity.client.BronzeShotProjectileModel;
import corundum.rubinated_nether.content.entity.client.BronzeShotProjectileRenderer;
import corundum.rubinated_nether.content.gui.RubyLensOverlay;
import corundum.rubinated_nether.content.items.MusicBoxItem;
import corundum.rubinated_nether.content.items.WaxableBlockItem;
import corundum.rubinated_nether.content.menu.RNMenuTypes;
import corundum.rubinated_nether.content.screen.CofferScreen;
import corundum.rubinated_nether.content.screen.FreezerScreen;
import corundum.rubinated_nether.content.screen.RubinationScreen;
import corundum.rubinated_nether.content.screen.VaseScreen;
import corundum.rubinated_nether.mixin.accessors.EntityRenderDispatcherAccessor;
import corundum.rubinated_nether.networking.CollectDiscsPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.core.component.DataComponents;
import corundum.rubinated_nether.content.items.MusicBoxData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = RubinatedNether.MODID, value = Dist.CLIENT)
public class RNClientSubscriber {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        IEventBus modBus = net.neoforged.fml.ModLoadingContext.get().getActiveContainer().getEventBus();
        RubinatedTextRenderer.register(modBus);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(
                BronzeShotProjectileModel.LAYER_LOCATION,
                BronzeShotProjectileModel::createBodyLayer
        );
        event.registerLayerDefinition(RNModelLayers.COFFER, CofferRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(RNModelLayers.GEARBOX, GearboxRenderer::createSingleBodyLayer);
        event.registerLayerDefinition(RubyLensModel.LAYER_LOCATION, RubyLensModel::createBodyLayer);
    }

    @SubscribeEvent
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

        PlayerRenderer defaultSkin = event.getSkin(PlayerSkin.Model.WIDE);
        PlayerRenderer slimSkin = event.getSkin(PlayerSkin.Model.SLIM);
        defaultSkin.addLayer(new RubyLensRenderLayer<>(defaultSkin, models, defaultSkin.getModel()));
        slimSkin.addLayer(new RubyLensRenderLayer<>(slimSkin, models, slimSkin.getModel()));
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(RNMenuTypes.FREEZER_MENU.get(), FreezerScreen::new);
        event.register(RNMenuTypes.RUBINATION_MENU.get(), RubinationScreen::new);
        event.register(RNMenuTypes.COFFER_MENU.get(), CofferScreen::new);
        event.register(RNMenuTypes.VASE_MENU.get(), VaseScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(RNBlockEntities.COFFER.get(), CofferRenderer::new);
        event.registerBlockEntityRenderer(RNBlockEntities.GEARBOX.get(), GearboxRenderer::new);
        event.registerBlockEntityRenderer(RNBlockEntities.BRONZE_LASER.get(), BronzeLaserRenderer::new);
        event.registerBlockEntityRenderer(RNBlockEntities.COPPER_LASER.get(), CopperLaserRenderer::new);
        event.registerBlockEntityRenderer(RNBlockEntities.BRONZE_VASE.get(), VaseBlockEntityRenderer::new);
        event.registerEntityRenderer(RNEntityCreator.BRONZE_SHOT.get(), BronzeShotProjectileRenderer::new);
    }

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(VaseEngravingAtlas.INSTANCE);
    }

    @SubscribeEvent
    public static void registerRenderBuffers(RegisterRenderBuffersEvent event) {
        event.registerRenderBuffer(RNRenderTypes.RUBINATED_GLINT);
        event.registerRenderBuffer(RNRenderTypes.RUBINATED_ENTITY_GLINT);
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        Minecraft minecraft = Minecraft.getInstance();
        event.registerAbove(
                VanillaGuiLayers.DEMO_OVERLAY,
                RubinatedNether.id("ruby_lens_overlay"),
                (guiGraphics, deltaTracker) -> RubyLensOverlay.renderHud(new Gui(minecraft), guiGraphics)
        );
        event.registerBelowAll(
                RubinatedNether.id("bronze_overlay"),
                (guiGraphics, deltaTracker) -> BronzeDiseasedEffectOverlay.renderHud(new Gui(minecraft), guiGraphics)
        );
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(RNParticleTypes.BLOOD_DRIP.get(), BloodDripParticle.Provider::new);
        event.registerSpriteSet(RNParticleTypes.STEAM.get(), sprites -> new SteamParticle.Provider(sprites, SteamParticle.Tint.NEUTRAL));
        event.registerSpriteSet(RNParticleTypes.STEAM_HOT.get(), sprites -> new SteamParticle.Provider(sprites, SteamParticle.Tint.HOT));
        event.registerSpriteSet(RNParticleTypes.STEAM_COLD.get(), sprites -> new SteamParticle.Provider(sprites, SteamParticle.Tint.COLD));
        event.registerSpriteSet(RNParticleTypes.STEAM_HOT_HIGH.get(), sprites -> new SteamParticle.Provider(sprites, SteamParticle.Tint.HOT_HIGH));
        event.registerSpriteSet(RNParticleTypes.STEAM_COLD_HIGH.get(), sprites -> new SteamParticle.Provider(sprites, SteamParticle.Tint.COLD_HIGH));
        event.registerSpriteSet(RNParticleTypes.RUBY_AURA.get(), RubyAuraParticle.Provider::new);
        event.registerSpriteSet(RNParticleTypes.RUBY_SPIRIT.get(), RubySpiritParticle.Provider::new);
        event.registerSpriteSet(RNParticleTypes.RUBINATE.get(), RubinationAltarParticle.Provider::new);
        event.registerSpriteSet(RNParticleTypes.BRONZE_SCRAPE.get(), RubinationAltarParticle.Provider::new);
        event.registerSpriteSet(RNParticleTypes.RUBIN_GLYPH.get(), RubinGlyphParticle.Provider::new);
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

    @SubscribeEvent
    public static void addLegacyTooltip(net.neoforged.neoforge.event.entity.player.ItemTooltipEvent event) {
        Item item = event.getItemStack().getItem();
        if (item == RNBlocks.NETHER_RUBY_ORE.get().asItem() || item == RNBlocks.RUBINATED_BLACKSTONE.get().asItem()) {
            event.getToolTip().add(net.minecraft.network.chat.Component.translatable("tooltip.rubinated_nether.legacy")
                    .withStyle(net.minecraft.ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC));
        }
    }

    @SubscribeEvent
    public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
        IClientItemExtensions vaseExtensions = new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return VaseItemRenderer.INSTANCE;
            }
        };

        DeferredBlock<?>[] vaseBlocks = new DeferredBlock<?>[] {
                RNBlocks.BRONZE_VASE,
                RNBlocks.DISCOLORED_BRONZE_VASE,
                RNBlocks.CORRODED_BRONZE_VASE,
                RNBlocks.TARNISHED_BRONZE_VASE,
                RNBlocks.CRYSTALLIZED_BRONZE_VASE
        };

        for (DeferredBlock<?> vaseBlock : vaseBlocks) {
            event.registerItem(vaseExtensions, vaseBlock.get().asItem());

            ResourceLocation waxedId = RubinatedNether.id(WaxableBlockItem.getWaxableItem(vaseBlock));
            Item waxedItem = BuiltInRegistries.ITEM.get(waxedId);
            event.registerItem(vaseExtensions, waxedItem);
        }
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        for (String name : new String[]{
                "bronze_vase", "discolored_bronze_vase", "corroded_bronze_vase",
                "tarnished_bronze_vase", "crystallized_bronze_vase"
        }) {
            event.register(ModelResourceLocation.standalone(RubinatedNether.id("item/" + name + "_base")));
        }
    }

    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                PocketwatchSavePayload.TYPE,
                PocketwatchSavePayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(RNClientSubscriber::handlePocketwatchSave)
        );
    }

    private static void handlePocketwatchSave() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        PocketwatchSaveAnimation.debug("handler reached");
        PocketwatchSaveAnimation.start();
        player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0F, 1.0F);
    }

    private static long musicBoxLastClick = 0;
    private static boolean musicBoxSuppressRelease = false;

    @SubscribeEvent
    public static void onMusicBoxClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (event.getButton() != 0 || !(event.getScreen() instanceof AbstractContainerScreen<?> screen)) {
            return;
        }
        musicBoxSuppressRelease = false;

        long now = System.currentTimeMillis();
        boolean holdingBox = screen.getMenu().getCarried().getItem() instanceof MusicBoxItem;

        if (holdingBox && now - musicBoxLastClick <= 300) {
            if (screen instanceof CreativeModeInventoryScreen creative) {
                if (!creative.isInventoryOpen()) {
                    musicBoxLastClick = now;
                    return;
                }
                collectDiscsCreative();
            } else {
                PacketDistributor.sendToServer(new CollectDiscsPayload());
            }
            musicBoxLastClick = 0;
            musicBoxSuppressRelease = true;
            event.setCanceled(true);
        } else {
            musicBoxLastClick = now;
        }
    }

    private static long musicBoxLastAnnounced = 0;

    @SubscribeEvent
    public static void onMusicBoxNowPlaying(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) {
            musicBoxLastAnnounced = 0;
            return;
        }

        MusicBoxData playing = null;
        ItemStack playingBox = ItemStack.EMPTY;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!(stack.getItem() instanceof MusicBoxItem)) continue;
            MusicBoxData data = stack.getOrDefault(RNDataComponents.MUSIC_BOX_DATA.get(), MusicBoxData.EMPTY);
            if (data.playing()) {
                playing = data;
                playingBox = stack;
                break;
            }
        }

        if (playing == null) {
            musicBoxLastAnnounced = 0;
            return;
        }
        if (playing.songEndTick() == musicBoxLastAnnounced) return;
        musicBoxLastAnnounced = playing.songEndTick();

        BundleContents contents = playingBox.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        if (playing.current() < 0 || playing.current() >= contents.size()) return;
        JukeboxSong.fromStack(mc.level.registryAccess(), contents.getItemUnsafe(playing.current()))
                .ifPresent(holder -> mc.gui.setNowPlaying(holder.value().description()));
    }

    @SubscribeEvent
    public static void onMusicBoxRelease(ScreenEvent.MouseButtonReleased.Pre event) {
        if (musicBoxSuppressRelease && event.getButton() == 0) {
            musicBoxSuppressRelease = false;
            event.setCanceled(true);
        }
    }

    private static void collectDiscsCreative() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.gameMode == null) return;

        var menu = player.inventoryMenu;
        ItemStack carried = menu.getCarried();
        if (!(carried.getItem() instanceof MusicBoxItem)) return;

        List<ItemStack> before = new ArrayList<>();
        for (var slot : menu.slots) {
            before.add(slot.getItem().copy());
        }

        MusicBoxItem.collectDiscs(carried, menu.slots, player);

        for (int i = 0; i < menu.slots.size(); i++) {
            ItemStack now = menu.slots.get(i).getItem();
            if (!ItemStack.matches(before.get(i), now)) {
                mc.gameMode.handleCreativeModeItemAdd(now, i);
            }
        }
    }
}