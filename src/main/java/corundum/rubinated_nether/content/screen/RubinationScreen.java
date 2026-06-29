package corundum.rubinated_nether.content.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.client.render.RubinatedTextRenderer;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
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
	private static final ResourceLocation RUBINATION_SLOT_DISABLED_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot_brown");
	private static final ResourceLocation RUBINATION_SLOT_HIGHLIGHTED_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot_highlighted");
	private static final ResourceLocation RUBINATION_SLOT_UNDISCOVERED_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot_gray");
	private static final ResourceLocation RUBINATION_SLOT_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot_normal");
	private static final ResourceLocation RUBINATION_SLOT_UNRUBINATED_SPRITE = RubinatedNether.id("rubination_altar/rubination_slot_red");

	private static final ResourceLocation DISABLED_RUNE = RubinatedNether.id("textures/item/rune.png");
	private static final ResourceLocation RUBINATION_ALTAR_LOCATION = RubinatedNether.id("textures/gui/rubination_altar.png");

	private static final int COLOR_ENABLED = 6839882;
	private static final int COLOR_HIGHLIGHTED = 16777088;
	private static final int COLOR_DISABLED = 0x494949;
	private static final int COLOR_RUBINATED = 0xAA3333;

	private boolean hasEnoughRubinatedBlocks = false;
	private int tickCounter = 0;
	private static final int NAME_UPDATE_INTERVAL = 3;
	private FormattedText[] cachedNames = new FormattedText[3];
	private boolean namesNeedUpdate = true;

	public RubinationScreen(RubinationMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageHeight = 208;
		this.inventoryLabelY = this.imageHeight - 107;
	}

	private void updateRubinatedBlockStatus() {
		if (this.minecraft.player != null && this.minecraft.level != null) {
			if (this.minecraft.player.getAbilities().instabuild) {
				hasEnoughRubinatedBlocks = true;
			} else {
				hasEnoughRubinatedBlocks = this.menu.hasEnoughRubinatedBlocks();
			}
		}
	}

	protected void init() {
		super.init();
		updateRubinatedBlockStatus();
		updateCachedNames();
	}

	private void updateCachedNames() {
		for (int i = 0; i < 3; i++) {
			cachedNames[i] = RubinationNames.getInstance().getRandomName(this.font, 40);
		}
		namesNeedUpdate = false;
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		var i = (this.width - this.imageWidth) / 2;
		var j = (this.height - this.imageHeight) / 2;

		for(var k = 0; k < 3; ++k) {
			var i1 = i + 42 + (k * 36);
			var j1 = j + 16;
			var d0 = mouseX - i1;
			var d1 = mouseY - j1;
			if (d0 >= 0.0 && d1 >= 0.0 && d0 < 21.0 && d1 < 59.0) {
				this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, k);
				return true;
			}
		}

		return super.mouseClicked(mouseX, mouseY, button);
	}

	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		var i = (this.width - this.imageWidth) / 2;
		var j = (this.height - this.imageHeight) / 2;

		guiGraphics.blit(RUBINATION_ALTAR_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);

		for(int l = 0; l < 3; ++l) {
			var i1 = i + 42 + (l * 36);
			var j1 = j + 16;

			if (namesNeedUpdate) updateCachedNames();
			var formattedtext = cachedNames[l];
			int textColor;

			boolean hasClue = this.menu.rubinationClue[l][l] != -1;
			boolean isCreative = this.minecraft.player.getAbilities().instabuild;
			boolean canAfford = hasEnoughRubinatedBlocks || isCreative;

			if (hasClue && canAfford) {
				var j2 = mouseX - i1;
				var k2 = mouseY - j1;
				RenderSystem.enableBlend();
				if (j2 >= 0 && k2 >= 0 && j2 < 21 && k2 < 59) {
					guiGraphics.blitSprite(RUBINATION_SLOT_HIGHLIGHTED_SPRITE, i1, j1, 21, 59);
					textColor = COLOR_HIGHLIGHTED;
				} else {
					guiGraphics.blitSprite(RUBINATION_SLOT_UNRUBINATED_SPRITE, i1, j1, 21, 59);
					textColor = COLOR_RUBINATED;
				}
				var result = Rubination.parseRubinationFromEnchantList(getRegistryAccess(), getEnchantReferences(l));
				guiGraphics.blit(RubinatedNether.id("textures/item/rune_" + Rubination.parseRubinationTextureName(result) + ".png"), i1 + 3, j1 + 2, 0.5f, 0.5f, 16, 16, 16, 16);
				RenderSystem.disableBlend();
				guiGraphics.pose().pushPose();
				guiGraphics.pose().translate(-1.5f, 0, 0);
				guiGraphics.drawWordWrap(this.font, formattedtext, i1 + 8, j1 + 20, 1, textColor);
				guiGraphics.pose().popPose();

			} else if (hasClue) {
				RenderSystem.enableBlend();
				guiGraphics.blitSprite(RUBINATION_SLOT_SPRITE, i1, j1, 21, 59);
				var result = Rubination.parseRubinationFromEnchantList(getRegistryAccess(), getEnchantReferences(l));
				guiGraphics.blit(RubinatedNether.id("textures/item/rune_" + Rubination.parseRubinationTextureName(result) + ".png"), i1 + 3, j1 + 2, 0.5f, 0.5f, 16, 16, 16, 16);
				RenderSystem.disableBlend();
				textColor = COLOR_ENABLED;
				guiGraphics.pose().pushPose();
				guiGraphics.pose().translate(-1.5f, 0, 0);
				guiGraphics.drawWordWrap(this.font, formattedtext, i1 + 8, j1 + 20, 1, textColor);
				guiGraphics.pose().popPose();

			} else {
				RenderSystem.enableBlend();
				var optionalList = getEnchantReferences(l);
				ItemStack currentItem = this.menu.getItemInSlot();
				boolean hasRubinationOptions = !optionalList.getFirst().isEmpty();
				boolean hasRubinatable = currentItem != ItemStack.EMPTY && currentItem.is(RNTags.Items.RUBINATABLE);
				boolean hasItemButNoRubination = hasRubinatable && !hasRubinationOptions;

				if (hasItemButNoRubination) {
					guiGraphics.blitSprite(RUBINATION_SLOT_DISABLED_SPRITE, i1, j1, 21, 59);
					String categoryName = getItemCategoryTextureName(currentItem);
					guiGraphics.blit(RubinatedNether.id("textures/item/rune_" + categoryName + "_carved.png"), i1 + 3, j1 + 2, 0.5f, 0.5f, 16, 16, 16, 16);
					textColor = (COLOR_ENABLED & 16711422) >> 1;
				} else {
					guiGraphics.blitSprite(RUBINATION_SLOT_UNDISCOVERED_SPRITE, i1, j1, 21, 59);
					guiGraphics.blit(DISABLED_RUNE, i1 + 3, j1 + 2, 0.5f, 0.5f, 16, 16, 16, 16);
					textColor = COLOR_DISABLED;
				}
				RenderSystem.disableBlend();
				guiGraphics.pose().pushPose();
				guiGraphics.pose().translate(-1.5f, 0, 0);
				guiGraphics.drawWordWrap(this.font, formattedtext, i1 + 8, j1 + 20, 1, textColor);
				guiGraphics.pose().popPose();
			}
		}
	}

	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);

		for(int j = 0; j < 3; ++j) {
			var optionalList = getEnchantReferences(j);
			var result = Rubination.parseRubinationFromEnchantList(getRegistryAccess(), optionalList);

			if (this.isHovering(42 + (36 * j), 16, 21, 59, mouseX, mouseY) && this.menu.getItemInSlot() != ItemStack.EMPTY) {
				var list = new ArrayList<Component>();

				if (optionalList.getFirst().isEmpty()) {
					var randomName = RubinationNames.getInstance().getRandomName(this.font, 100);
					list.add(Component.literal(randomName.getString()).withStyle(ChatFormatting.DARK_RED, ChatFormatting.OBFUSCATED));
				} else {
					boolean useRubin = RubinatedTextRenderer.useRubinatedLang();

					Component clueComponent = Component.translatable("container." + result.getSerializedName() + ".clue")
							.withStyle(ChatFormatting.DARK_RED);
					list.add(useRubin ? RubinatedTextRenderer.applyRubin(clueComponent) : clueComponent);

					for (var h = 0; h < 3; ++h) {
						Component fullName = Enchantment.getFullname(
								optionalList.get(h).get(),
								result.getEnchantments(getRegistryAccess()).get(h).level
						);
						boolean isCurse = optionalList.get(h).get().is(corundum.rubinated_nether.content.RNTags.Enchantments.RUBINATED_CURSES);
						Component enchantLine = Component.translatable(
								"gui.rubinated_nether.rubination_altar.enchant", fullName
						).withStyle(ChatFormatting.WHITE);
						list.add(useRubin && isCurse ? RubinatedTextRenderer.applyRubin(enchantLine) : enchantLine);
					}
				}
				guiGraphics.renderComponentTooltip(this.font, list, mouseX, mouseY);
				break;
			}
		}
	}

	@Override
	public void containerTick() {
		super.containerTick();
		updateRubinatedBlockStatus();
		tickCounter++;
		if (tickCounter >= NAME_UPDATE_INTERVAL) {
			tickCounter = 0;
			namesNeedUpdate = true;
		}
	}

	private @NotNull RegistryAccess getRegistryAccess() {
		return this.minecraft.level.registryAccess();
	}

	private static String getItemCategoryTextureName(ItemStack stack) {
		if (stack.is(RNTags.Items.RUBINATION_WEAPON)) return "weapon";
		if (stack.is(RNTags.Items.RUBINATION_ARMOR)) return "armor";
		if (stack.is(RNTags.Items.RUBINATION_BOW)) return "bow";
		if (stack.is(RNTags.Items.RUBINATION_CROSSBOW)) return "crossbow";
		if (stack.is(RNTags.Items.RUBINATION_TRIDENT)) return "trident";
		if (stack.is(RNTags.Items.RUBINATION_MACE)) return "mace";
		return "tool";
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