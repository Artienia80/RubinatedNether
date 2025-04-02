package corundum.rubinated_nether.content.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.menu.RubinationMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class RubinationScreen extends AbstractContainerScreen<RubinationMenu> {
    private static final ResourceLocation RUBINATION_SLOT_DISABLED_SPRITE = RubinatedNether.id("textures/gui/rubination_altar/rubination_slot_disabled");
    private static final ResourceLocation RUBINATION_SLOT_HIGHLIGHTED_SPRITE = RubinatedNether.id("textures/gui/rubination_altar/rubination_slot_highlighted");
    private static final ResourceLocation RUBINATION_SLOT_SPRITE = RubinatedNether.id("textures/gui/rubination_altar/rubination_slot");
    private static final ResourceLocation RUBINATION_ALTAR_LOCATION = RubinatedNether.id("textures/gui/rubination_altar.png");
    private final RandomSource random = RandomSource.create();
    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last;

    public RubinationScreen(RubinationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.last = ItemStack.EMPTY;
        this.imageHeight = 183;
    }

    protected void init() {
        super.init();
    }

    public void containerTick() {
        super.containerTick();
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        for(int k = 0; k < 3; ++k) {
            double d0 = mouseX - (double)(i + 60);
            double d1 = mouseY - (double)(j + 14 + 19 * k);
            if (d0 >= (double)0.0F && d1 >= (double)0.0F && d0 < (double)108.0F && d1 < (double)19.0F && this.menu.clickMenuButton(this.minecraft.player, k)) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, k);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RUBINATION_ALTAR_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        int k = this.menu.getGoldCount();

        for(int l = 0; l < 3; ++l) {
            int i1 = i + 42 + (l * 37);
            int j1 = j + 11;
            int k1 = this.menu.costs[l];
            if (k1 == 0) {
                RenderSystem.enableBlend();
                guiGraphics.blitSprite(RUBINATION_SLOT_DISABLED_SPRITE, i + 42 + (l * 37), j + 11, 19, 57);
                RenderSystem.disableBlend();
            } else {
                String s = "" + k1;
                int l1 = 19 - this.font.width(s);
                FormattedText formattedtext = FormattedText.of(this.font.getSplitter().formattedHeadByWidth("Greed", 10, Style.EMPTY));
                int i2 = 6839882;
                if ((k >= l + 1 && this.minecraft.player.experienceLevel >= k1 || this.minecraft.player.getAbilities().instabuild) && (this.menu).rubinationClue[l] != -1) {
                    int j2 = mouseX - (i + 42 + (l * 37));
                    int k2 = mouseY - (j + 11);
                    RenderSystem.enableBlend();
                    if (j2 >= 0 && k2 >= 0 && j2 < 108 && k2 < 19) {
                        guiGraphics.blitSprite(RUBINATION_SLOT_HIGHLIGHTED_SPRITE, i + 42 + (l * 37), j + 11, 19, 57);
                        i2 = 16777088;
                    } else {
                        guiGraphics.blitSprite(RUBINATION_SLOT_SPRITE, i + 42 + (l * 37), j + 11, 19, 57);
                    }

                    RenderSystem.disableBlend();
                    guiGraphics.drawWordWrap(this.font, formattedtext, i + 42 + (l * 37), j + 11, l1, i2);
                    i2 = 8453920;
                } else {
                    RenderSystem.enableBlend();
                    guiGraphics.blitSprite(RUBINATION_SLOT_DISABLED_SPRITE, i + 42 + (l * 37), j + 11, 19, 57);
                    RenderSystem.disableBlend();
                    guiGraphics.drawWordWrap(this.font, formattedtext, i + 42 + (l * 37), j + 11, l1, (i2 & 16711422) >> 1);
                    i2 = 4226832;
                }

                guiGraphics.drawString(this.font, s, i + 42 + (l * 37) - this.font.width(s), j + 11, i2);
            }
        }

    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        boolean flag = this.minecraft.player.getAbilities().instabuild;
        int i = this.menu.getGoldCount();

        for(int j = 0; j < 3; ++j) {
            int k = this.menu.costs[j];
            Optional<Holder.Reference<Enchantment>> optional = this.minecraft.level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder((this.menu).rubinationClue[j]);
            int i1 = j + 1;
            if (this.isHovering(42 + (39 * j), 11, 19, 57, mouseX, mouseY) && k > 0) {
                List<Component> list = Lists.newArrayList();
                list.add(Component.translatable("container.enchant.clue", optional.isEmpty() ? "" : Enchantment.getFullname(optional.get(), 1)).withStyle(ChatFormatting.WHITE));
                if (optional.isEmpty()) {
                    list.add(Component.literal(""));
                    list.add(Component.translatable("neoforge.container.enchant.limitedEnchantability").withStyle(ChatFormatting.RED));
                } else if (!flag) {
                    list.add(CommonComponents.EMPTY);
                    if (this.minecraft.player.experienceLevel < k) {
                        list.add(Component.translatable("container.enchant.level.requirement", (this.menu).costs[j]).withStyle(ChatFormatting.RED));
                    } else {
                        MutableComponent mutablecomponent;
                        if (i1 == 1) {
                            mutablecomponent = Component.translatable("container.enchant.lapis.one");
                        } else {
                            mutablecomponent = Component.translatable("container.enchant.lapis.many", new Object[]{i1});
                        }

                        list.add(mutablecomponent.withStyle(i >= i1 ? ChatFormatting.GRAY : ChatFormatting.RED));
                        MutableComponent mutablecomponent1;
                        if (i1 == 1) {
                            mutablecomponent1 = Component.translatable("container.enchant.level.one");
                        } else {
                            mutablecomponent1 = Component.translatable("container.enchant.level.many", new Object[]{i1});
                        }

                        list.add(mutablecomponent1.withStyle(ChatFormatting.GRAY));
                    }
                }

                guiGraphics.renderComponentTooltip(this.font, list, mouseX, mouseY);
                break;
            }
        }

    }
}
