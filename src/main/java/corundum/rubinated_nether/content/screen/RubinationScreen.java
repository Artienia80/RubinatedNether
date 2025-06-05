package corundum.rubinated_nether.content.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.menu.RubinationMenu;
import corundum.rubinated_nether.utils.RubinationNames;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class RubinationScreen extends AbstractContainerScreen<RubinationMenu> {
	private static final ResourceLocation RUBINATION_SLOT_DISABLED_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot_disabled");
	private static final ResourceLocation RUBINATION_SLOT_HIGHLIGHTED_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot_highlighted");
	private static final ResourceLocation RUBINATION_SLOT_UNDISCOVERED_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot_undiscovered");
	private static final ResourceLocation RUBINATION_SLOT_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot");
	private static final ResourceLocation UNDISCOVERED_RUNE = RubinatedNether.id("textures/gui/sprites/rubination_altar/undiscovered_rune.png");
	private static final ResourceLocation DISABLED_RUNE = RubinatedNether.id("textures/gui/sprites/rubination_altar/disabled_rune.png");
	private static final ResourceLocation RUBINATION_ALTAR_LOCATION = RubinatedNether.id("textures/gui/rubination_altar.png");

	public RubinationScreen(RubinationMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageHeight = 208;
		this.inventoryLabelY = this.imageHeight - 109;
	}

	protected void init() {
		super.init();
	}

	public void containerTick() {
		super.containerTick();
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		var i = (this.width - this.imageWidth) / 2;
		var j = (this.height - this.imageHeight) / 2;

		for(var k = 0; k < 3; ++k) {
			var i1 = i + 42 + (k * 36);
			var j1 = j + 16;
			var d0 = mouseX - i1;
			var d1 = mouseY - j1;
			if (d0 >= 0.0 && d1 >= 0.0 && d0 < 21.0 && d1 < 59.0 && this.menu.clickMenuButton(this.minecraft.player, k)) {
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, k);
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		var i = (this.width - this.imageWidth) / 2;
		var j = (this.height - this.imageHeight) / 2;
		var k = this.menu.getRubyCount();

		guiGraphics.blit(RUBINATION_ALTAR_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);

		for(int l = 0; l < 3; ++l) {
			var i1 = i + 42 + (l * 36);
			var j1 = j + 16;

			var formattedtext = RubinationNames.getInstance().getRandomName(this.font, 20);
			var i2 = 6839882;
			if ((k >= 1 || this.minecraft.player.getAbilities().instabuild) && this.menu.rubinationClue[l][l] != -1) {
				var j2 = mouseX - i1;
				var k2 = mouseY - j1;
				RenderSystem.enableBlend();
				if (j2 >= 0 && k2 >= 0 && j2 < 21 && k2 < 59) {
					guiGraphics.blitSprite(RUBINATION_SLOT_HIGHLIGHTED_SPRITE, i1, j1, 21, 59);
					i2 = 16777088;
				} else {
					guiGraphics.blitSprite(RUBINATION_SLOT_SPRITE, i1, j1, 21, 59);
				}

				var result = Rubination.parseRubinationFromEnchantList(getRegistryAccess(), getEnchantReferences(l));

				guiGraphics.blit(RubinatedNether.id("textures/item/" + Rubination.parseRubinationTextureName(result) + "_rune.png"), i1 + 3, j1 + 2, 0.5f, 0.5f, 16, 16, 16, 16);

				RenderSystem.disableBlend();
				guiGraphics.drawWordWrap(this.font, formattedtext, i1 + 8, j1 + 20, 1, i2);
			} else {
				RenderSystem.enableBlend();
				var optionalList = getEnchantReferences(l);
				ItemStack currentItem = this.menu.getItemInSlot();
				boolean hasRubinationOptions = !optionalList.getFirst().isEmpty();
				boolean hasRubinatable = currentItem != ItemStack.EMPTY && currentItem.is(RNTags.Items.RUBINATABLE);
				boolean hasItemButNoRubination = hasRubinatable && !hasRubinationOptions;

				if (hasItemButNoRubination) {
					guiGraphics.blitSprite(RUBINATION_SLOT_DISABLED_SPRITE, i1, j1, 21, 59);
					guiGraphics.blit(UNDISCOVERED_RUNE, i1 + 3, j1 + 2, 0.5f, 0.5f, 16, 16, 16, 16);
				} else {
					guiGraphics.blitSprite(RUBINATION_SLOT_UNDISCOVERED_SPRITE, i1, j1, 21, 59);
					guiGraphics.blit(DISABLED_RUNE, i1 + 3, j1 + 2, 0.5f, 0.5f, 16, 16, 16, 16);
				}

				RenderSystem.disableBlend();

				int textColor;
				if (hasItemButNoRubination) {
					textColor = (i2 & 16711422) >> 1;
				} else {
					textColor = 0x494949;

				}
				guiGraphics.drawWordWrap(this.font, formattedtext, i1 + 8, j1 + 20, 1, textColor);
			}
		}
	}


	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		var flag = this.minecraft.player.getAbilities().instabuild;

		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);

		for(int j = 0; j < 3; ++j) {
			var optionalList = getEnchantReferences(j);
			var result = Rubination.parseRubinationFromEnchantList(getRegistryAccess(), optionalList);

			if (this.isHovering(42 + (36 * j), 16, 21, 59, mouseX, mouseY) && this.menu.getItemInSlot() != ItemStack.EMPTY) {
				var list = new ArrayList<Component>();

				if (optionalList.getFirst().isEmpty()) {
					var randomName = RubinationNames.getInstance().getRandomName(this.font, 100);
					list.add(Component.literal(randomName.getString()).withStyle(ChatFormatting.RED, ChatFormatting.OBFUSCATED));
				} else {
					list.add(Component.translatable("container." + result.getSerializedName() + ".clue").withStyle(ChatFormatting.RED));
					for (var h = 0; h < 3; ++h) {
						list.add(
								Component.translatable(
												"gui.rubinated_nether.rubination_altar.enchant",
												Enchantment.getFullname(optionalList.get(h).get(),
														result.getEnchantments(getRegistryAccess()).get(h).level)
										)
										.withStyle(ChatFormatting.WHITE)
						);
					}
				}
				guiGraphics.renderComponentTooltip(this.font, list, mouseX, mouseY);
				break;
			}
		}
	}

	private @NotNull RegistryAccess getRegistryAccess() {
		return this.minecraft.level.registryAccess();
	}

	private @NotNull ArrayList<Optional<Holder.Reference<Enchantment>>> getEnchantReferences(int l) {
		var optionalList = new ArrayList<Optional<Holder.Reference<Enchantment>>>();
		for(int h = 0; h < 3; ++h) {
			optionalList.add(
					getRegistryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(this.menu.rubinationClue[l][h])
			);
		}
		return optionalList;
	}
}