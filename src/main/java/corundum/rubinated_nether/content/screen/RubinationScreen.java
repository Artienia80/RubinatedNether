package corundum.rubinated_nether.content.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.menu.RubinationMenu;
import corundum.rubinated_nether.utils.RubinationNames;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class RubinationScreen extends AbstractContainerScreen<RubinationMenu> {
    private static final ResourceLocation RUBINATION_SLOT_DISABLED_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot_disabled");
    private static final ResourceLocation RUBINATION_SLOT_HIGHLIGHTED_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot_highlighted");
    private static final ResourceLocation RUBINATION_SLOT_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot");
    private static final ResourceLocation DISABLED_RUNE = RubinatedNether.id("textures/gui/sprites/rubination_altar/disabled_rune.png");
    private static final ResourceLocation RUBINATION_ALTAR_LOCATION = RubinatedNether.id("textures/gui/rubination_altar.png");
    private final RandomSource random = RandomSource.create();

    public RubinationScreen(RubinationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 208;
        this.inventoryLabelY = this.imageHeight - 95;
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
            int i1 = i + 43 + (k * 36);
            int j1 = j + 17;
            double d0 = mouseX - i1;
            double d1 = mouseY - j1;
            if (d0 >= (double)0.0F && d1 >= (double)0.0F && d0 < (double)19.0F && d1 < (double)57.0F && this.menu.clickMenuButton(this.minecraft.player, k)) {
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
        int k = this.menu.getRubyCount();

        for(int l = 0; l < 3; ++l) {
            int i1 = i + 43 + (l * 36);
            int j1 = j + 17;

            FormattedText formattedtext = RubinationNames.getInstance().getRandomName(this.font, 20);
            int i2 = 6839882;
            if ((k >= 1 || this.minecraft.player.getAbilities().instabuild) && this.menu.rubinationClue[l][l] != -1) {
                int j2 = mouseX - i1;
                int k2 = mouseY - j1;
                RenderSystem.enableBlend();
                if (j2 >= 0 && k2 >= 0 && j2 < 19 && k2 < 57) {
                    guiGraphics.blitSprite(RUBINATION_SLOT_HIGHLIGHTED_SPRITE, i1, j1, 19, 57);
                    i2 = 16777088;
                } else {
                    guiGraphics.blitSprite(RUBINATION_SLOT_SPRITE, i1, j1, 19, 57);
                }

                List<Optional<Holder.Reference<Enchantment>>> optionalList = new ArrayList<>();
                for(int h = 0; h < 3; ++h) {
                    optionalList.add(this.minecraft.level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(this.menu.rubinationClue[l][h]));
                }

                var result = Rubination.parseRubinationFromEnchantList(this.minecraft.level.registryAccess(), optionalList);

                guiGraphics.blit(RubinatedNether.id("textures/item/" + Rubination.parseRubinationTextureName(result) + "_rune.png"), i1 + 2, j1 + 1, 0, 0, 16, 16, 16, 16);

                RenderSystem.disableBlend();
                guiGraphics.drawWordWrap(this.font, formattedtext, i1 + 7, j1 + 19, 1, i2);
                i2 = 8453920;
            } else {
                RenderSystem.enableBlend();
                guiGraphics.blitSprite(RUBINATION_SLOT_DISABLED_SPRITE, i1, j1, 19, 57);
                guiGraphics.blit(DISABLED_RUNE, i1 + 2, j1 + 1,0, 0, 16, 16, 16, 16);
                RenderSystem.disableBlend();
                guiGraphics.drawWordWrap(this.font, formattedtext, i1 + 7, j1 + 19, 1, (i2 & 16711422) >> 1);
                i2 = 4226832;
            }

                //guiGraphics.drawString(this.font, s, i1 - this.font.width(s), j1, i2);
        }

    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        boolean flag = this.minecraft.player.getAbilities().instabuild;

        for(int j = 0; j < 3; ++j) {
            List<Optional<Holder.Reference<Enchantment>>> optionalList = new ArrayList<>();
            for(int h = 0; h < 3; ++h) {
                optionalList.add(this.minecraft.level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(this.menu.rubinationClue[j][h]));
            }

            var result = Rubination.parseRubinationFromEnchantList(this.minecraft.level.registryAccess(), optionalList);

            if (this.isHovering(43 + (36 * j), 17, 19, 57, mouseX, mouseY) && this.menu.getItemInSlot() != ItemStack.EMPTY) {
                List<Component> list = Lists.newArrayList();
                if (!flag) {
                    list.add(CommonComponents.EMPTY);
                } else {
                    if (optionalList.getFirst().isEmpty()) {
                        //list.add(Component.translatable("gui.rubinated_nether.rubination_altar.unusable").withStyle(ChatFormatting.RED));
                        FormattedText randomName = RubinationNames.getInstance().getRandomName(this.font, 100);
                        list.add(Component.literal(randomName.getString()).withStyle(ChatFormatting.RED, ChatFormatting.OBFUSCATED));
                    } else {
                        list.add(Component.translatable("container." + result.getSerializedName() + ".clue").withStyle(ChatFormatting.RED));
                        for (int h = 0; h < 3; ++h) {
                            list.add(Component.translatable("gui.rubinated_nether.rubination_altar.enchant", Enchantment.getFullname(optionalList.get(h).get(), result.getEnchantments(this.minecraft.level.registryAccess()).get(h).level)).withStyle(ChatFormatting.WHITE));
                        }
                    }
                }
                guiGraphics.renderComponentTooltip(this.font, list, mouseX, mouseY);
                break;
            }
        }

    }
}
