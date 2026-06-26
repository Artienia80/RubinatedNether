package corundum.rubinated_nether.content.menu;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNRarity;
import corundum.rubinated_nether.content.RubinationConverter;
import corundum.rubinated_nether.content.blocks.RubinationAltarBlock;
import corundum.rubinated_nether.content.items.Rubination;
import corundum.rubinated_nether.content.items.RuneItem;
import corundum.rubinated_nether.utils.RNConfig;
import net.minecraft.Util;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.*;

public class RubinationMenu extends AbstractContainerMenu {
    private final Container rubinationSlots;
    private final ContainerLevelAccess access;
    public final int[][] rubinationClue;
    public final Set<RuneItem> runes = new HashSet<>();

    private int axeCycle = 0;
    private boolean hadAxeInSlot = false;
    private boolean hasEnoughRubinatedBlocks = false;

    public final ContainerData data;

    public RubinationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public RubinationMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(RNMenuTypes.RUBINATION_MENU.get(), containerId);
        this.rubinationSlots = new SimpleContainer(1) {
            public void setChanged() {
                super.setChanged();
                RubinationMenu.this.slotsChanged(this);
            }
        };

        this.rubinationClue = new int[][]{{-1, -1, -1}, {-1, -1, -1}, {-1, -1, -1}};
        this.access = access;

        // ITEM SLOT - the only altar slot, centered (index 0)
        this.addSlot(new Slot(this.rubinationSlots, 0, 80, 84) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                return itemStack.isEnchantable();
            }

            public int getMaxStackSize() {
                return 1;
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 113 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 171));
        }

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                if (index == 0) {
                    refreshRubinatedBlockStatus();
                    return hasEnoughRubinatedBlocks ? 1 : 0;
                }
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) hasEnoughRubinatedBlocks = value != 0;
            }

            @Override
            public int getCount() {
                return 1;
            }
        };

        this.addDataSlot(DataSlot.shared(this.rubinationClue[0], 0));
        this.addDataSlot(DataSlot.shared(this.rubinationClue[1], 0));
        this.addDataSlot(DataSlot.shared(this.rubinationClue[2], 0));
        this.addDataSlot(DataSlot.shared(this.rubinationClue[0], 1));
        this.addDataSlot(DataSlot.shared(this.rubinationClue[1], 1));
        this.addDataSlot(DataSlot.shared(this.rubinationClue[2], 1));
        this.addDataSlot(DataSlot.shared(this.rubinationClue[0], 2));
        this.addDataSlot(DataSlot.shared(this.rubinationClue[1], 2));
        this.addDataSlot(DataSlot.shared(this.rubinationClue[2], 2));

        this.addDataSlots(this.data);
    }

    public void refreshRubinatedBlockStatus() {
        this.access.execute((level, blockPos) -> {
            hasEnoughRubinatedBlocks = RubinationConverter.hasEnoughBlocksForInscription(level, blockPos);
        });
    }

    public static InteractionResult handleRitualOffering(Level level, BlockPos blockPos, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.is(corundum.rubinated_nether.content.RNTags.Items.ALTAR_OFFERING_ITEM)) {
            return InteractionResult.PASS;
        }

        if (!player.getAbilities().instabuild) {
            heldItem.shrink(1);
        }

        level.playSound(
                null,
                blockPos,
                SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.BLOCKS,
                1.0F,
                level.random.nextFloat() * 0.1F + 0.9F
        );

        BlessPlayer(player, RNConfig.altarLesserBlessingTime*60*20);

        if (player instanceof ServerPlayer serverPlayer) {
            AdvancementHolder advancementHolder = serverPlayer.server.getAdvancements()
                    .get(RubinatedNether.id("offer_ritual_offering"));

            if (advancementHolder != null) {
                var progress = serverPlayer.getAdvancements().getOrStartProgress(advancementHolder);
                if (!progress.isDone()) {
                    for (String criterion : progress.getRemainingCriteria()) {
                        serverPlayer.getAdvancements().award(advancementHolder, criterion);
                    }
                }
            }
        }

        RubinationConverter.RubinateAreaOffering(level, blockPos);

        return InteractionResult.CONSUME;
    }

    public static void BlessPlayer(Player player, int durationTicks) {
        MobEffectInstance currentBlessing = player.getEffect(RNEffects.BLESSED);

        int newDuration;
        if (currentBlessing != null) {
            newDuration = currentBlessing.getDuration() + durationTicks;
            player.addEffect(new MobEffectInstance(RNEffects.BLESSED, newDuration, 0, false, false, true));
        } else {
            newDuration = durationTicks;
            player.addEffect(new MobEffectInstance(RNEffects.BLESSED, durationTicks, 0, false, false, true));
        }

        // Check if duration exceeds Threshold, by default 5h:20m (384000 ticks) (64 Offerings)
        if (newDuration >= RNConfig.altarFullBlessingThreshold*60*20 && player instanceof ServerPlayer serverPlayer) {
            AdvancementHolder advancementHolder = serverPlayer.server.getAdvancements()
                    .get(RubinatedNether.id("divine_favor"));

            if (advancementHolder != null) {
                var progress = serverPlayer.getAdvancements().getOrStartProgress(advancementHolder);
                if (!progress.isDone()) {
                    for (String criterion : progress.getRemainingCriteria()) {
                        serverPlayer.getAdvancements().award(advancementHolder, criterion);
                    }
                }
            }
        }
    }

    public boolean hasEnoughRubinatedBlocks() {
        return hasEnoughRubinatedBlocks;
    }

    public int countRubinatedBlocks() {
        final int[] count = {0};
        this.access.execute((level, blockPos) -> {
            count[0] = RubinationConverter.countRubinatedBlocks(level, blockPos, 20);
        });
        return count[0];
    }

    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0) {
            var rubinatableItem = this.rubinationSlots.getItem(0);
            return handleRubination(player, id, rubinatableItem);
        } else {
            var var10000 = String.valueOf(player.getName());
            Util.logAndPauseIfInIde(var10000 + " pressed invalid button id: " + id);
            return false;
        }
    }

    private boolean handleRubination(Player player, int id, ItemStack rubinatableItem) {
        boolean isCreative = player.getAbilities().instabuild;

        if (rubinatableItem.isEmpty()) {
            return false;
        }

        final boolean[] success = {false};

        this.access.execute((level, blockPos) -> {
            if (!isCreative && !RubinationConverter.hasEnoughBlocksForInscription(level, blockPos)) {
                // reuses the generic "enough rubinated blocks nearby" check (see RNConfig.altarRubinationCost)
                return;
            }

            var arrayList = this.getRubinationMap(rubinatableItem, runes);
            var selectedEnchantments = this.getSelectedEnchants(level.registryAccess(), arrayList, id);

            if (arrayList.isEmpty() || selectedEnchantments == null) {
                return;
            }

            var itemstack2 = rubinatableItem.getItem().applyEnchantments(rubinatableItem, selectedEnchantments);

            itemstack2 = itemstack2.copy();
            itemstack2.set(net.minecraft.core.component.DataComponents.RARITY, RNRarity.RUBINATED_NETHER_RUBY.get());

            this.rubinationSlots.setItem(0, itemstack2);
            CommonHooks.onPlayerEnchantItem(player, itemstack2, selectedEnchantments);

            if (!isCreative) {
                RubinationConverter.derubinateBlocks(level, blockPos, 20, RNConfig.altarRubinationCost);
            }

            BlessPlayer(player, RNConfig.altarGreaterBlessingTime*60*20);

            player.awardStat(Stats.ENCHANT_ITEM);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer) player, itemstack2, 1);
            }

            if (player instanceof ServerPlayer serverPlayer) {
                AdvancementHolder advancementHolder = serverPlayer.server.getAdvancements()
                        .get(RubinatedNether.id("rubinate_item"));

                if (advancementHolder != null) {
                    var progress = serverPlayer.getAdvancements().getOrStartProgress(advancementHolder);
                    if (!progress.isDone()) {
                        for (String criterion : progress.getRemainingCriteria()) {
                            serverPlayer.getAdvancements().award(advancementHolder, criterion);
                        }
                    }
                }
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
            RubinationConverter.RubinateAreaKey(level, blockPos);
            success[0] = true;
        });

        return success[0];
    }

    private List<Rubination> getRubinationMap(ItemStack stack, Set<RuneItem> runes) {
        var arrayList = new ArrayList<Rubination>(3);

        if (stack.is(net.minecraft.tags.ItemTags.AXES)) {
            var toolRubinations = new ArrayList<Rubination>();
            var weaponRubinations = new ArrayList<Rubination>();

            for(RuneItem rune : runes) {
                Rubination rubination = rune.getRubination();

                if(stack.is(rubination.getItemTag())) {
                    if (rubination.getItemTag().equals(corundum.rubinated_nether.content.RNTags.Items.RUBINATION_TOOL)) {
                        toolRubinations.add(rubination);
                    } else if (rubination.getItemTag().equals(corundum.rubinated_nether.content.RNTags.Items.RUBINATION_WEAPON)) {
                        weaponRubinations.add(rubination);
                    }
                }
            }

            boolean showToolsOnly = (axeCycle % 2 == 0);

            if (showToolsOnly) {
                arrayList.addAll(toolRubinations);
            } else {
                arrayList.addAll(weaponRubinations);
            }
        } else {
            for(RuneItem rune : runes) {
                if(stack.is(rune.getRubination().getItemTag()))
                    arrayList.add(rune.getRubination());
            }
        }

        return arrayList;
    }

    public void slotsChanged(Container inventory) {
        if (inventory == this.rubinationSlots) {
            var rubinatableItem = inventory.getItem(0);

            boolean currentlyHasAxe = !rubinatableItem.isEmpty() && rubinatableItem.is(net.minecraft.tags.ItemTags.AXES);

            if (currentlyHasAxe && !hadAxeInSlot) {
                axeCycle++;
                hadAxeInSlot = true;
            } else if (!currentlyHasAxe && hadAxeInSlot) {
                hadAxeInSlot = false;
            } else if (!rubinatableItem.isEmpty() && !currentlyHasAxe) {
                axeCycle = 0;
                hadAxeInSlot = false;
            }

            refreshRubinatedBlockStatus();

            if (!rubinatableItem.isEmpty() && rubinatableItem.isEnchantable()) {
                this.access.execute((level, blockPos) -> {
                    var idmap = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap();

                    runes.clear();

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

                    var arrayList = this.getRubinationMap(rubinatableItem, runes);

                    for(int l = 0; l < 3; l++) {
                        for(int c = 0; c < 3; c++) {
                            if (arrayList != null && !arrayList.isEmpty()) {
                                var list = this.getSelectedEnchants(level.registryAccess(), arrayList, l);
                                if (list != null && c < list.size()) {
                                    var enchantmentinstance = list.get(c);
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

    private List<EnchantmentInstance> getSelectedEnchants(RegistryAccess registryAccess, List<Rubination> arrayList, int id) {
        if(arrayList.isEmpty() || id >= arrayList.size()) return null;

        return arrayList.get(id).getEnchantments(registryAccess);
    }

    public ItemStack getItemInSlot() {
        var itemstack = this.rubinationSlots.getItem(0);
        return itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
    }

    public void removed(Player player) {
        super.removed(player);
        axeCycle = 0;
        hadAxeInSlot = false;
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
                if (!this.moveItemStackTo(itemstack1, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Items go to the altar slot (index 0)
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