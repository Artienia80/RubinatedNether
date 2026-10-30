package corundum.rubinated_nether.client.render;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNRarity;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.items.RuneItem;
import corundum.rubinated_nether.utils.RNConfig;
import corundum.rubinated_nether.utils.RubinatedCurseText;
import corundum.rubinated_nether.utils.RubinatedText;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.io.InputStream;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class RubinatedTextRenderer {

    public static final ResourceLocation RUBIN_FONT = RubinatedText.RUBIN_FONT;

    public static void register(IEventBus modBus) {
        RubinatedText.setClientCheck(RubinatedTextRenderer::clientShouldUseRubinatedLang);
        NeoForge.EVENT_BUS.addListener(RubinatedTextRenderer::onItemTooltip);
        modBus.addListener(RubinatedTextRenderer::onRegisterReloadListeners);
    }

    private static Map<String, String> englishStrings = new HashMap<>();
    private static boolean englishLoaded = false;

    private static void loadEnglish(ResourceManager resourceManager) {
        englishStrings.clear();
        try {
            var resources = resourceManager.listResources(
                    "lang",
                    loc -> loc.getPath().endsWith("en_us.json")
            );
            for (var entry : resources.entrySet()) {
                try (InputStream stream = entry.getValue().open()) {
                    Language.loadFromJson(stream, englishStrings::put);
                } catch (Exception ignored) {}
            }
            englishLoaded = true;
        } catch (Exception e) {
            englishLoaded = false;
        }
    }

    public static String getEnglishOrFallback(String key) {
        if (englishLoaded && englishStrings.containsKey(key)) {
            return englishStrings.get(key);
        }
        return Language.getInstance().getOrDefault(key);
    }

    private static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(VaseEngravingAtlas.INSTANCE);
        event.registerReloadListener(new SimplePreparableReloadListener<Void>() {
            @Override
            protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
                loadEnglish(resourceManager);
                return null;
            }
            @Override
            protected void apply(Void object, ResourceManager resourceManager, ProfilerFiller profiler) {}
        });
    }

    /** Common-safe entry point (kept for existing client callers). */
    public static boolean useRubinatedLang() {
        return RubinatedText.useRubinatedLang();
    }

    /** Client-side decision, installed into {@link RubinatedText} by {@link #register}. */
    private static boolean clientShouldUseRubinatedLang() {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || !mc.isSameThread()) return true;
            var player = mc.player;
            if (player == null) return true;
            return !(player.getItemBySlot(EquipmentSlot.HEAD).getItem()
                    instanceof corundum.rubinated_nether.content.items.RubyLensItem);
        } catch (Exception e) {
            return true;
        }
    }

    private static String applySubs(String text) {
        return RubinatedText.applySubs(text);
    }

    private static void onItemTooltip(ItemTooltipEvent event) {
        var stack = event.getItemStack();

        List<Component> lines = event.getToolTip();
        if (lines.isEmpty()) return;

        restyleCurseLines(stack, lines);

        if (!isRubinated(stack)) return;

        boolean useRubin = useRubinatedLang();

        if (!stack.has(DataComponents.CUSTOM_NAME)) {
            if (useRubin) {
                lines.set(0, restyleWithEnglishName(lines.get(0), stack));
            }
        }

        if (!useRubin) {
            stripRubinFromCurses(lines, stack);
        }
    }

    /**
     * Vanilla builds enchantment lines with {@link Enchantment#getFullname}; this rewrites the lines of Rubinated
     * curses to {@link RubinatedCurseText#fullName} (replaces the former EnchantmentMixin). Independent of the
     * stack's rarity, like the mixin was. Runes build their own lines through RubinatedCurseText already.
     */
    private static void restyleCurseLines(ItemStack stack, List<Component> lines) {
        if (lines.size() < 2 || stack.getItem() instanceof RuneItem) return;

        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        if (enchantments.isEmpty()) return;

        Map<String, Component> styled = null;
        for (var entry : enchantments.entrySet()) {
            var holder = entry.getKey();
            if (!holder.is(RNTags.Enchantments.RUBINATED_CURSES)) continue;
            if (styled == null) styled = new HashMap<>();
            styled.put(
                    Enchantment.getFullname(holder, entry.getIntValue()).getString(),
                    RubinatedCurseText.fullName(holder, entry.getIntValue())
            );
        }
        if (styled == null) return;

        for (int i = 1; i < lines.size(); i++) {
            Component replacement = styled.get(lines.get(i).getString());
            if (replacement != null) lines.set(i, replacement);
        }
    }

    private static void stripRubinFromCurses(List<Component> lines, ItemStack stack) {
        Set<String> curseTexts = getRubinatedCurseTexts(stack);
        for (int i = 1; i < lines.size(); i++) {
            if (curseTexts.contains(lines.get(i).getString())) {
                lines.set(i, stripRubin(lines.get(i)));
            }
        }
    }

    private static Set<String> getRubinatedCurseTexts(ItemStack stack) {
        Set<String> texts = new HashSet<>();
        if (stack.getItem() instanceof RuneItem runeItem) {
            var level = Minecraft.getInstance().level;
            if (level != null) {
                for (var enchant : runeItem.getRubination().getEnchantments(level.registryAccess())) {
                    if (enchant != null && enchant.enchantment.is(RNTags.Enchantments.RUBINATED_CURSES)) {
                        texts.add(RubinatedCurseText.fullName(enchant.enchantment, enchant.level).getString());
                    }
                }
            }
        } else {
            EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet().forEach(entry -> {
                if (entry.getKey().is(RNTags.Enchantments.RUBINATED_CURSES)) {
                    texts.add(RubinatedCurseText.fullName(entry.getKey(), entry.getIntValue()).getString());
                }
            });
        }
        return texts;
    }

    public static boolean isRubinated(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Rarity rarity = stack.getOrDefault(DataComponents.RARITY, Rarity.COMMON);
        return rarity.equals(RNRarity.RUBINATED_NETHER_RUBY.get());
    }

    public static MutableComponent restyleWithEnglishName(Component component, ItemStack stack) {
        String key = stack.getItem().getDescriptionId(stack);
        String englishName = getEnglishOrFallback(key);
        String currentName = Language.getInstance().getOrDefault(key);

        MutableComponent result = Component.empty();
        component.visit((style, text) -> {
            String out = text.equals(currentName) ? englishName : text;
            result.append(Component.literal(applySubs(out)).withStyle(style.withFont(RUBIN_FONT)));
            return Optional.empty();
        }, Style.EMPTY);
        return result;
    }

    public static MutableComponent applyRubin(Component component) {
        return RubinatedText.applyRubin(component);
    }

    public static MutableComponent applyRubin(String text) {
        return RubinatedText.applyRubin(text);
    }

    public static MutableComponent stripRubin(Component component) {
        MutableComponent result = Component.empty();
        component.visit((style, text) -> {
            result.append(Component.literal(text).withStyle(style.withFont(null)));
            return Optional.empty();
        }, Style.EMPTY);
        return result;
    }
}