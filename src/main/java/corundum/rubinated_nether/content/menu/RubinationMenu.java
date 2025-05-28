package corundum.rubinated_nether.content.menu;

import com.mojang.datafixers.util.Pair;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.RubinationAltarBlock;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.RuneItem;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.*;

public class RubinationMenu extends AbstractContainerMenu {
	static final ResourceLocation EMPTY_SLOT_RUBIES = RubinatedNether.id("item/empty_slot_ruby");
	private final Container rubinationSlots;
	private final ContainerLevelAccess access;
	public final int[][] rubinationClue;
	public final Set<RuneItem> runes = new HashSet<>();

	public RubinationMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, ContainerLevelAccess.NULL);
	}

	public RubinationMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
		super(RNMenuTypes.RUBINATION_MENU.get(), containerId);
		this.rubinationSlots = new SimpleContainer(2) {
			public void setChanged() {
				super.setChanged();
				RubinationMenu.this.slotsChanged(this);
			}
		};

		this.rubinationClue = new int[][]{{-1, -1, -1}, {-1, -1, -1}, {-1, -1, -1}};
		this.access = access;
		this.addSlot(new Slot(this.rubinationSlots, 0, 70, 92) {
			public int getMaxStackSize() {
				return 1;
			}
		});
		this.addSlot(new Slot(this.rubinationSlots, 1, 90, 92) {
			public boolean mayPlace(ItemStack itemStack) {
				return itemStack.is(RNItems.RUBY_ITEM.get());
			}

			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(InventoryMenu.BLOCK_ATLAS, RubinationMenu.EMPTY_SLOT_RUBIES);
			}
		});

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 126 + i * 18));
			}
		}

		for(int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 184));
		}

		this.addDataSlot(DataSlot.shared(this.rubinationClue[0], 0));
		this.addDataSlot(DataSlot.shared(this.rubinationClue[1], 0));
		this.addDataSlot(DataSlot.shared(this.rubinationClue[2], 0));
		this.addDataSlot(DataSlot.shared(this.rubinationClue[0], 1));
		this.addDataSlot(DataSlot.shared(this.rubinationClue[1], 1));
		this.addDataSlot(DataSlot.shared(this.rubinationClue[2], 1));
		this.addDataSlot(DataSlot.shared(this.rubinationClue[0], 2));
		this.addDataSlot(DataSlot.shared(this.rubinationClue[1], 2));
		this.addDataSlot(DataSlot.shared(this.rubinationClue[2], 2));
	}

	public void slotsChanged(Container inventory) {
		if (inventory == this.rubinationSlots) {
			var itemstack = inventory.getItem(0);
			if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
				this.access.execute((level, blockPos) -> {
					var idmap = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap();

					for(var blockpos : RubinationAltarBlock.RUNESTONE_OFFSETS) {
						if (RubinationAltarBlock.isValidCatalyst(level, blockPos, blockpos))
							if(RubinationAltarBlock.getRuneFromCatalyst(level, blockPos, blockpos) instanceof RuneItem runeItem)
								runes.add(runeItem);
					}

					for(int k = 0; k < 3; ++k) {
						for(int i = 0; i < 3; ++i) {
							this.rubinationClue[k][i] = -1;
						}
					}

					var arrayList = this.getRubinationMap(itemstack, runes);
					for(int l = 0; l < 3; l++) {
							for(int c = 0; c < 3; c++) {
								if (arrayList != null && !arrayList.isEmpty()) {
									var list = this.getSelectedEnchants(level.registryAccess(), arrayList, l);
									if (list != null) {
										var enchantmentinstance = this.getSelectedEnchants(level.registryAccess(), arrayList, l).get(c);
										this.rubinationClue[l][c] = idmap.getId(enchantmentinstance.enchantment);
									} else {
										this.rubinationClue[l][c] = -1;
									}
								}
							}
					}
					this.broadcastChanges();
				});
			} else {
				for(int i = 0; i < 3; ++i) {
					for(int k = 0; k < 3; ++k) {
						this.rubinationClue[i][k] = -1;
					}
				}
			}
		}
	}

	public boolean clickMenuButton(Player player, int id) {
		if (id >= 0) {
			var itemstack = this.rubinationSlots.getItem(0);
			var itemstack1 = this.rubinationSlots.getItem(1);
			var i = id + 1;
	
			if ((itemstack1.isEmpty() || itemstack1.getCount() < i)) {
				return false;
			} else if (!itemstack.isEmpty() && (player.experienceLevel >= i || player.getAbilities().instabuild)) {
				this.access.execute((level, blockPos) -> {
					var arrayList = this.getRubinationMap(itemstack, runes);
					var selectedEnchantments = this.getSelectedEnchants(level.registryAccess(), arrayList, id);

					if (!arrayList.isEmpty() && selectedEnchantments != null) {
						player.onEnchantmentPerformed(itemstack, i);

						var itemstack2 = itemstack.getItem().applyEnchantments(itemstack, selectedEnchantments);
						this.rubinationSlots.setItem(0, itemstack2);
						CommonHooks.onPlayerEnchantItem(player, itemstack2, selectedEnchantments);
						itemstack1.consume(i, player);

						if (itemstack1.isEmpty()) {
							this.rubinationSlots.setItem(1, ItemStack.EMPTY);
						}

						player.awardStat(Stats.ENCHANT_ITEM);
						if (player instanceof ServerPlayer) {
							CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer) player, itemstack2, i);
						}

						this.rubinationSlots.setChanged();
						this.slotsChanged(this.rubinationSlots);
						level.playSound(
							null, 
							blockPos, 
							SoundEvents.ENCHANTMENT_TABLE_USE, 
							SoundSource.BLOCKS, 
							1.0F, 
							level.random.nextFloat() * 0.1F + 0.9F
						);
					}

				});
				return true;
			} else {
				return false;
			}
		} else {
			var var10000 = String.valueOf(player.getName());
			Util.logAndPauseIfInIde(var10000 + " pressed invalid button id: " + id);
			return false;
		}
	}

	private List<Rubination> getRubinationMap(ItemStack stack, Set<RuneItem> runes) {
		var arrayList = new ArrayList<Rubination>(3);
	
		for(RuneItem rune : runes) {
			if(stack.is(rune.getRubination().getItemTag()))
				arrayList.add(rune.getRubination());
		}

		return arrayList;
	}

	private List<EnchantmentInstance> getSelectedEnchants(RegistryAccess registryAccess, List<Rubination> arrayList, int id) {
		if(arrayList.isEmpty() || id >= arrayList.size()) return null;

		return arrayList.get(id).getEnchantments(registryAccess);
	}

	public int getRubyCount() {
		var itemstack = this.rubinationSlots.getItem(1);
		return itemstack.isEmpty() ? 0 : itemstack.getCount();
	}

	public ItemStack getItemInSlot() {
		var itemstack = this.rubinationSlots.getItem(0);
		return itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
	}

	public void removed(Player player) {
		super.removed(player);
		this.access.execute((level, blockPos) -> this.clearContainer(player, this.rubinationSlots));
	}

	public boolean stillValid(Player player) {
		return stillValid(this.access, player, RNBlocks.RUBINATION_ALTAR.get());
	}

	public ItemStack quickMoveStack(Player player, int index) {
		var itemstack = ItemStack.EMPTY;
		var slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			var itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
	
			if (index == 0) {
				if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
					return ItemStack.EMPTY;
				}
			} else if (index == 1) {
				if (!this.moveItemStackTo(itemstack1, 2, 38, true)) {
					return ItemStack.EMPTY;
				}
			} else if (itemstack1.is(RNItems.RUBY_ITEM.get())) {
				if (!this.moveItemStackTo(itemstack1, 1, 2, true)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(itemstack1)) {
					return ItemStack.EMPTY;
				}

				var itemstack2 = itemstack1.copyWithCount(1);
				itemstack1.shrink(1);
				this.slots.get(0).setByPlayer(itemstack2);
			}

			if (itemstack1.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}
}
