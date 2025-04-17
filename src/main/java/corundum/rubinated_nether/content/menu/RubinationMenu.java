package corundum.rubinated_nether.content.menu;

import com.mojang.datafixers.util.Pair;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.blocks.RubinationAltarBlock;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RubinationMenu extends AbstractContainerMenu {
    static final ResourceLocation EMPTY_SLOT_RUBIES = RubinatedNether.id("item/empty_slot_ruby");
    private final Container rubinationSlots;
    private final ContainerLevelAccess access;
    public final int[] costs;
    public final int[] rubinationClue;
    public final List<RuneItem> runes = new ArrayList<>();

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
        this.costs = new int[3];
        this.rubinationClue = new int[]{-1, -1, -1};
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

        this.addDataSlot(DataSlot.shared(this.costs, 0));
        this.addDataSlot(DataSlot.shared(this.costs, 1));
        this.addDataSlot(DataSlot.shared(this.costs, 2));
        this.addDataSlot(DataSlot.shared(this.rubinationClue, 0));
        this.addDataSlot(DataSlot.shared(this.rubinationClue, 1));
        this.addDataSlot(DataSlot.shared(this.rubinationClue, 2));
    }

    public void slotsChanged(Container inventory) {
        if (inventory == this.rubinationSlots) {
            ItemStack itemstack = inventory.getItem(0);
            if (!itemstack.isEmpty() && itemstack.isEnchantable()) {
                this.access.execute((level, blockPos) -> {
                    IdMap<Holder<Enchantment>> idmap = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap();
                    float j = 0.0F;

                    for(BlockPos blockpos : RubinationAltarBlock.RUNESTONE_OFFSETS) {
                        if (RubinationAltarBlock.isValidCatalyst(level, blockPos, blockpos))
                            if(RubinationAltarBlock.getRuneFromCatalyst(level, blockPos, blockpos) instanceof RuneItem runeItem)
                                runes.add(runeItem);
                    }

                    for(int k = 0; k < 3; ++k) {
                        this.costs[k] = EnchantmentHelper.getEnchantmentCost(RandomSource.create(), k, (int)j, itemstack);
                        this.rubinationClue[k] = -1;
                        if (this.costs[k] < k + 1) {
                            this.costs[k] = 0;
                        }

                        this.costs[k] = EventHooks.onEnchantmentLevelSet(level, blockPos, k, (int)j, itemstack, this.costs[k]);
                    }

                    for(int l = 0; l < 3; ++l) {
                        if (this.costs[l] > 0) {
                            List<EnchantmentInstance> list = this.getRubinationList(level.registryAccess(), itemstack, runes, l, this.costs[l]);
                            if (list != null && !list.isEmpty()) {
                                EnchantmentInstance enchantmentinstance = list.get(list.size() - 1);
                                this.rubinationClue[l] = idmap.getId(enchantmentinstance.enchantment);
                            }
                        }
                    }

                    this.broadcastChanges();
                });
            } else {
                for(int i = 0; i < 3; ++i) {
                    this.costs[i] = 0;
                    this.rubinationClue[i] = -1;
                }
            }
        }

    }

    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < this.costs.length) {
            ItemStack itemstack = this.rubinationSlots.getItem(0);
            ItemStack itemstack1 = this.rubinationSlots.getItem(1);
            int i = id + 1;
            if ((itemstack1.isEmpty() || itemstack1.getCount() < i) && !player.hasInfiniteMaterials()) {
                return false;
            } else if (this.costs[id] > 0 && !itemstack.isEmpty() && (player.experienceLevel >= i && player.experienceLevel >= this.costs[id] || player.getAbilities().instabuild)) {
                this.access.execute((level, blockPos) -> {
                    List<EnchantmentInstance> list = this.getRubinationList(level.registryAccess(), itemstack, runes, id, this.costs[id]);
                    list = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
                    if (!list.isEmpty()) {
                        player.onEnchantmentPerformed(itemstack, i);
                        ItemStack itemstack2 = itemstack.getItem().applyEnchantments(itemstack, list);
                        this.rubinationSlots.setItem(0, itemstack2);
                        CommonHooks.onPlayerEnchantItem(player, itemstack2, list);
                        itemstack1.consume(i, player);
                        if (itemstack1.isEmpty()) {
                            this.rubinationSlots.setItem(1, ItemStack.EMPTY);
                        }

                        player.awardStat(Stats.ENCHANT_ITEM);
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)player, itemstack2, i);
                        }

                        this.rubinationSlots.setChanged();
                        this.slotsChanged(this.rubinationSlots);
                        level.playSound(null, blockPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);
                    }

                });
                return true;
            } else {
                return false;
            }
        } else {
            String var10000 = String.valueOf(player.getName());
            Util.logAndPauseIfInIde(var10000 + " pressed invalid button id: " + id);
            return false;
        }
    }

    private List<EnchantmentInstance> getRubinationList(RegistryAccess registryAccess, ItemStack stack, List<RuneItem> runes, int slot, int cost) {
        List<EnchantmentInstance> list = new ArrayList<>();
        List<EnchantmentInstance> cleanList = new ArrayList<>();
        for(RuneItem rune : runes) {
            if(stack.is(rune.getRubination().getItemTag()))
                list.addAll(rune.getRubination().getEnchantments(registryAccess));
        }

        for(int k = 0; k < 3; k++) {
            cleanList.add(list.get(k + (slot * 3)));
        }

        return cleanList;
    }

    public int getGoldCount() {
        ItemStack itemstack = this.rubinationSlots.getItem(1);
        return itemstack.isEmpty() ? 0 : itemstack.getCount();
    }

    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> this.clearContainer(player, this.rubinationSlots));
    }

    public boolean stillValid(Player player) {
        return stillValid(this.access, player, RNBlocks.RUBINATION_ALTAR.get());
    }

    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
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

                ItemStack itemstack2 = itemstack1.copyWithCount(1);
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