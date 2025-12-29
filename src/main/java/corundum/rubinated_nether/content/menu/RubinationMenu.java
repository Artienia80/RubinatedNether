package corundum.rubinated_nether.content.menu;

import com.mojang.datafixers.util.Pair;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNItems;
import corundum.rubinated_nether.content.RNRarity;
import corundum.rubinated_nether.content.RNTags;
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
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.*;

public class RubinationMenu extends AbstractContainerMenu {
    static final ResourceLocation EMPTY_SLOT_KEY = RubinatedNether.id("item/empty_slot_key");
    static final ResourceLocation EMPTY_SLOT_RUNE = RubinatedNether.id("item/empty_slot_rune");
    static final ResourceLocation EMPTY_SLOT_COG = RubinatedNether.id("item/empty_slot_cog");
    private final Container rubinationSlots;
    private final ContainerLevelAccess access;
    public final int[][] rubinationClue;
    public final Set<RuneItem> runes = new HashSet<>();
    private final List<Rubination> currentInscriptionOptions = new ArrayList<>();

    private int axeCycle = 0;
    private boolean hadAxeInSlot = false;
    private boolean hasEnoughRubinatedBlocks = false;

    // New fields for category cycling
    private int inscriptionCategoryCycle = -1;
    private boolean hadRuneInSlot = false;

    public final ContainerData data;

    private static final List<RuneItem> ALL_RUNES = List.of(
            (RuneItem) RNItems.GREED_RUNE.get(),
            (RuneItem) RNItems.WRATH_RUNE.get(),
            (RuneItem) RNItems.SLOTH_RUNE.get(),
            (RuneItem) RNItems.GLUTTONY_RUNE.get(),
            (RuneItem) RNItems.ENVY_RUNE.get(),
            (RuneItem) RNItems.VAINGLORY_RUNE.get(),
            (RuneItem) RNItems.PRIDE_RUNE.get(),
            (RuneItem) RNItems.ACEDIA_RUNE.get(),
            (RuneItem) RNItems.LUXURIA_RUNE.get(),
            (RuneItem) RNItems.INSIDIAE_RUNE.get(),
            (RuneItem) RNItems.SUPERBIA_RUNE.get(),
            (RuneItem) RNItems.TRISTIA_RUNE.get(),
            (RuneItem) RNItems.STUDIOSE_RUNE.get(),
            (RuneItem) RNItems.ARDENTER_RUNE.get(),
            (RuneItem) RNItems.NIMIS_RUNE.get(),
            (RuneItem) RNItems.IRA_RUNE.get(),
            (RuneItem) RNItems.INVIDIA_RUNE.get(),
            (RuneItem) RNItems.GULA_RUNE.get(),
            (RuneItem) RNItems.IGNAVIA_RUNE.get(),
            (RuneItem) RNItems.KENODOXIA_RUNE.get(),
            (RuneItem) RNItems.PHILARGYRIA_RUNE.get()
    );

    // Category mapping: 1-7 correspond to the 7 rubination categories
    private static final List<TagKey<Item>> RUBINATION_CATEGORIES = List.of(
            RNTags.Items.RUBINATION_TOOL,      // 1
            RNTags.Items.RUBINATION_WEAPON,    // 2
            RNTags.Items.RUBINATION_ARMOR,     // 3
            RNTags.Items.RUBINATION_BOW,       // 4
            RNTags.Items.RUBINATION_CROSSBOW,  // 5
            RNTags.Items.RUBINATION_TRIDENT,   // 6
            RNTags.Items.RUBINATION_MACE       // 7
    );

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

        // Initialize category cycle with random value 0-6 (representing categories 1-7)
        this.inscriptionCategoryCycle = new Random().nextInt(7);

        // LEFT SLOT - RubinatableSlot (index 0)
        this.addSlot(new Slot(this.rubinationSlots, 0, 70, 84) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                ItemStack consumableSlot = rubinationSlots.getItem(1);

                // If consumable has inscription item, only accept blank runes
                if (consumableSlot.is(RNTags.Items.ALTAR_INSCRIPTION_ITEM)) {
                    return itemStack.is(RNItems.RUNE.get());
                }

                // If consumable has rubination item (key/cogwheel), only accept enchantable items
                if (consumableSlot.is(RNTags.Items.ALTAR_RUBINATION_ITEM)) {
                    return itemStack.isEnchantable();
                }

                // If consumable is empty, accept either blank runes or enchantable items
                if (consumableSlot.isEmpty()) {
                    return itemStack.is(RNItems.RUNE.get()) || itemStack.isEnchantable();
                }

                return false;
            }

            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                ItemStack consumableSlot = rubinationSlots.getItem(1);

                // If key (inscription item) in right slot, show rune icon
                if (consumableSlot.is(RNTags.Items.ALTAR_INSCRIPTION_ITEM)) {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, RubinationMenu.EMPTY_SLOT_RUNE);
                }

                // If cog (rubination item) in right slot, show nothing
                if (consumableSlot.is(RNTags.Items.ALTAR_RUBINATION_ITEM)) {
                    return null;
                }

                // If consumable is empty, show nothing (let consumable slot do the cycling)
                return null;
            }
        });

        // RIGHT SLOT - ConsumableSlot (index 1)
        this.addSlot(new Slot(this.rubinationSlots, 1, 90, 84) {
            @Override
            public boolean mayPlace(ItemStack itemStack) {
                ItemStack rubinatableSlot = rubinationSlots.getItem(0);

                // If rubinatable has blank rune, only accept inscription items
                if (rubinatableSlot.is(RNItems.RUNE.get())) {
                    return itemStack.is(RNTags.Items.ALTAR_INSCRIPTION_ITEM);
                }

                // If rubinatable has enchantable item, only accept rubination items (keys/cogwheels)
                if (!rubinatableSlot.isEmpty() && rubinatableSlot.isEnchantable()) {
                    return itemStack.is(RNTags.Items.ALTAR_RUBINATION_ITEM);
                }

                // If rubinatable is empty, accept any valid consumable
                if (rubinatableSlot.isEmpty()) {
                    return itemStack.is(RNTags.Items.ALTAR_RUBINATION_ITEM)
                            || itemStack.is(RNTags.Items.ALTAR_INSCRIPTION_ITEM);
                }

                return false;
            }

            public int getMaxStackSize() {
                ItemStack currentItem = this.getItem();
                if (!currentItem.isEmpty() && currentItem.is(RNItems.RUNE.get())) {
                    return 1;
                }
                return super.getMaxStackSize();
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                ItemStack rubinatableSlot = rubinationSlots.getItem(0);

                // If rune in left slot, show key icon
                if (rubinatableSlot.is(RNItems.RUNE.get())) {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, RubinationMenu.EMPTY_SLOT_KEY);
                }

                // If tool/weapon (rubinatable) in left slot, show cog icon
                if (!rubinatableSlot.isEmpty() && rubinatableSlot.is(RNTags.Items.RUBINATABLE)) {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, RubinationMenu.EMPTY_SLOT_COG);
                }

                // If both slots empty, cycle between key and cog
                long currentTime = System.currentTimeMillis();
                boolean showCog = (currentTime / 5000) % 2 == 1;

                if (showCog) {
                    return Pair.of(InventoryMenu.BLOCK_ATLAS, RubinationMenu.EMPTY_SLOT_COG);
                }
                return Pair.of(InventoryMenu.BLOCK_ATLAS, RubinationMenu.EMPTY_SLOT_KEY);
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
                if (index == 0) return hasEnoughRubinatedBlocks ? 1 : 0;
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
    }

    public static InteractionResult handleRitualOffering(Level level, BlockPos blockPos, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.is(RNTags.Items.ALTAR_OFFERING_ITEM)) {
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
            player.addEffect(new MobEffectInstance(RNEffects.BLESSED, newDuration, 0, false, true, true));
        } else {
            newDuration = durationTicks;
            player.addEffect(new MobEffectInstance(RNEffects.BLESSED, durationTicks, 0, false, true, true));
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

    private boolean isInscriptionMode() {
        ItemStack rubinatableSlot = this.rubinationSlots.getItem(0);
        ItemStack consumableSlot = this.rubinationSlots.getItem(1);

        return rubinatableSlot.is(RNItems.RUNE.get()) || consumableSlot.is(RNTags.Items.ALTAR_INSCRIPTION_ITEM);
    }

    private boolean isRubinationMode() {
        ItemStack consumableSlot = this.rubinationSlots.getItem(1);
        return consumableSlot.is(RNTags.Items.ALTAR_RUBINATION_ITEM);
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
            var consumableItem = this.rubinationSlots.getItem(1);

            if (isInscriptionMode()) {
                return handleInscription(player, id, rubinatableItem, consumableItem);
            } else if (isRubinationMode()) {
                return handleRubination(player, id, rubinatableItem, consumableItem);
            }
        } else {
            var var10000 = String.valueOf(player.getName());
            Util.logAndPauseIfInIde(var10000 + " pressed invalid button id: " + id);
            return false;
        }
        return false;
    }

    private boolean handleInscription(Player player, int id, ItemStack rubinatableItem, ItemStack consumableItem) {
        if (!rubinatableItem.is(RNItems.RUNE.get())) {
            return false;
        }

        var itemCost = 1;
        boolean isCreative = player.getAbilities().instabuild;
        boolean hasEnoughKeys = isCreative || (!consumableItem.isEmpty() && consumableItem.getCount() >= itemCost);

        if (!hasEnoughKeys) {
            return false;
        }

        this.access.execute((level, blockPos) -> {
            if (!isCreative && !RubinationConverter.hasEnoughBlocksForInscription(level, blockPos)) {
                return;
            }

            var arrayList = this.currentInscriptionOptions;
            if (arrayList.isEmpty() || id >= arrayList.size()) {
                return;
            }

            var selectedRubination = arrayList.get(id);

            ItemStack inscribedRune = getRuneItemFromRubination(selectedRubination);

            this.rubinationSlots.setItem(0, inscribedRune);

            // Consume the inscription item
            if (!isCreative) {
                consumableItem.consume(itemCost, player);
                if (consumableItem.isEmpty()) {
                    this.rubinationSlots.setItem(1, ItemStack.EMPTY);
                }
                RubinationConverter.derubinateBlocks(level, blockPos, 20, RNConfig.altarInscriptionCost);
            }

            level.playSound(
                    null,
                    blockPos,
                    SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.BLOCKS,
                    1.0F,
                    level.random.nextFloat() * 0.1F + 0.9F
            );

            if (player instanceof ServerPlayer serverPlayer) {
                AdvancementHolder advancementHolder = serverPlayer.server.getAdvancements()
                        .get(RubinatedNether.id("inscribe_rune"));

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
        });

        return true;
    }

    private boolean handleRubination(Player player, int id, ItemStack rubinatableItem, ItemStack consumableItem) {
        var itemCost = 1;

        boolean isCreative = player.getAbilities().instabuild;
        boolean hasEnoughKeys = isCreative || (!consumableItem.isEmpty() && consumableItem.getCount() >= itemCost);

        if (!hasEnoughKeys) {
            return false;
        } else if (!rubinatableItem.isEmpty()) {
            this.access.execute((level, blockPos) -> {
                var arrayList = this.getRubinationMap(rubinatableItem, runes);
                var selectedEnchantments = this.getSelectedEnchants(level.registryAccess(), arrayList, id);

                if (!arrayList.isEmpty() && selectedEnchantments != null) {
                    var itemstack2 = rubinatableItem.getItem().applyEnchantments(rubinatableItem, selectedEnchantments);

                    itemstack2 = itemstack2.copy();
                    itemstack2.set(net.minecraft.core.component.DataComponents.RARITY, RNRarity.RUBINATED_NETHER_RUBY.get());

                    this.rubinationSlots.setItem(0, itemstack2);
                    CommonHooks.onPlayerEnchantItem(player, itemstack2, selectedEnchantments);

                    if (!isCreative) {
                        consumableItem.consume(itemCost, player);
                        if (consumableItem.isEmpty()) {
                            this.rubinationSlots.setItem(1, ItemStack.EMPTY);
                        }
                    }

                    BlessPlayer(player, RNConfig.altarGreaterBlessingTime*60*20);

                    player.awardStat(Stats.ENCHANT_ITEM);
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer) player, itemstack2, itemCost);
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
                }
            });
            return true;
        } else {
            return false;
        }
    }

    private List<Rubination> getRubinationMapForInscription(ItemStack stack) {
        var arrayList = new ArrayList<Rubination>();

        // Get the current category based on the cycle
        TagKey<Item> currentCategory = RUBINATION_CATEGORIES.get(inscriptionCategoryCycle);

        // Get all runes that match the current category
        for (RuneItem rune : ALL_RUNES) {
            Rubination rubination = rune.getRubination();
            if (rubination.getItemTag().equals(currentCategory)) {
                arrayList.add(rubination);
            }
        }

        currentInscriptionOptions.clear();
        currentInscriptionOptions.addAll(arrayList);
        return arrayList;
    }

    private Set<RuneItem> getAllAvailableRunes() {
        return runes;
    }

    private ItemStack getRuneItemFromRubination(Rubination rubination) {
        for (RuneItem rune : ALL_RUNES) {
            if (rune.getRubination().equals(rubination)) {
                return new ItemStack(rune);
            }
        }
        return new ItemStack(RNItems.RUNE.get());
    }

    private List<Rubination> getRubinationMap(ItemStack stack, Set<RuneItem> runes) {
        var arrayList = new ArrayList<Rubination>(3);

        if (stack.is(net.minecraft.tags.ItemTags.AXES)) {
            var toolRubinations = new ArrayList<Rubination>();
            var weaponRubinations = new ArrayList<Rubination>();

            for(RuneItem rune : runes) {
                Rubination rubination = rune.getRubination();

                if(stack.is(rubination.getItemTag())) {
                    if (rubination.getItemTag().equals(RNTags.Items.RUBINATION_TOOL)) {
                        toolRubinations.add(rubination);
                    } else if (rubination.getItemTag().equals(RNTags.Items.RUBINATION_WEAPON)) {
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

            // Handle rune cycling for inscription mode
            boolean currentlyHasRune = !rubinatableItem.isEmpty() && rubinatableItem.is(RNItems.RUNE.get());

            if (currentlyHasRune && !hadRuneInSlot) {
                // Rune was just placed
                hadRuneInSlot = true;
            } else if (!currentlyHasRune && hadRuneInSlot) {
                // Rune was just removed, increment category cycle for next insertion
                inscriptionCategoryCycle = (inscriptionCategoryCycle + 1) % RUBINATION_CATEGORIES.size();
                hadRuneInSlot = false;
            }

            if (isInscriptionMode()) {
                this.access.execute((level, blockPos) -> {
                    hasEnoughRubinatedBlocks = RubinationConverter.hasEnoughBlocksForInscription(level, blockPos);
                });
            } else {
                hasEnoughRubinatedBlocks = false;
            }

            if (isInscriptionMode() || (!rubinatableItem.isEmpty() && rubinatableItem.isEnchantable())) {
                this.access.execute((level, blockPos) -> {
                    var idmap = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).asHolderIdMap();

                    runes.clear();

                    if (isRubinationMode()) {
                        for(var blockpos : RubinationAltarBlock.RUNESTONE_OFFSETS) {
                            if (RubinationAltarBlock.isValidCatalyst(level, blockPos, blockpos))
                                if(RubinationAltarBlock.getRuneFromCatalyst(level, blockPos, blockpos) instanceof RuneItem runeItem)
                                    runes.add(runeItem);
                        }
                    }

                    for(int k = 0; k < 3; ++k) {
                        for(int i = 0; i < 3; ++i) {
                            this.rubinationClue[k][i] = -1;
                        }
                    }

                    List<Rubination> arrayList;
                    if (isInscriptionMode()) {
                        arrayList = this.getRubinationMapForInscription(rubinatableItem);
                    } else {
                        arrayList = this.getRubinationMap(rubinatableItem, runes);
                    }

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

    public int getItemCount() {
        var itemstack = this.rubinationSlots.getItem(1);
        return itemstack.isEmpty() ? 0 : itemstack.getCount();
    }

    public ItemStack getItemInSlot() {
        var itemstack = this.rubinationSlots.getItem(0);
        return itemstack.isEmpty() ? ItemStack.EMPTY : itemstack;
    }

    public void removed(Player player) {
        super.removed(player);
        axeCycle = 0;
        hadAxeInSlot = false;
        hadRuneInSlot = false;
        inscriptionCategoryCycle = -1;
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
            } else if (itemstack1.is(RNItems.RUNE.get())) {
                // Runes go to the LEFT slot (index 0), but only if slot allows it
                if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemstack1.is(RNTags.Items.ALTAR_RUBINATION_ITEM) || itemstack1.is(RNTags.Items.ALTAR_INSCRIPTION_ITEM)) {
                // Consumables (keys/cogs) go to the RIGHT slot (index 1)
                if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Other items (tools/weapons) go to LEFT slot (index 0)
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