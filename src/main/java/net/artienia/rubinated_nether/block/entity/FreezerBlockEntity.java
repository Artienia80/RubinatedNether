package net.artienia.rubinated_nether.block.entity;

import net.artienia.rubinated_nether.RubinatedNether;
import net.artienia.rubinated_nether.mixin.AbstractFurnaceBlockEntityAccessor;
import net.artienia.rubinated_nether.recipe.FreezingRecipe;
import net.artienia.rubinated_nether.recipe.ModRecipeTypes;
import net.artienia.rubinated_nether.screen.FreezerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;



public class FreezerBlockEntity extends AbstractFreezerBlockEntity implements MenuProvider {
    private static final Map<Item, Integer> freezingMap = new LinkedHashMap<>();

    public FreezerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.FREEZER.get(), pos, state, ModRecipeTypes.FREEZING.get());
    }

    @Override
    public Component getDefaultName() {
        return Component.translatable("menu." + RubinatedNether.MOD_ID + ".freezer");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new FreezerMenu(id, playerInventory, this, this.dataAccess);
    }

    @Override
    public int getBurnDuration(ItemStack fuelStack) {
        if (fuelStack.isEmpty() || !getFreezingMap().containsKey(fuelStack.getItem())) {
            return 0;
        } else {
            return getFreezingMap().get(fuelStack.getItem());
        }
    }

    public static Map<Item, Integer> getFreezingMap() {
        return freezingMap;
    }

    public static void addItemFreezingTime(ItemLike itemProvider, int burnTime) {
        Item item = itemProvider.asItem();
        getFreezingMap().put(item, burnTime);
    }

    public static void addItemsFreezingTime(ItemLike[] itemProviders, int burnTime) {
        Stream.of(itemProviders).map(ItemLike::asItem).forEach((item) -> getFreezingMap().put(item, burnTime));
    }

    public static void addItemTagFreezingTime(TagKey<Item> itemTag, int burnTime) {
        ITagManager<Item> tags = ForgeRegistries.ITEMS.tags();
        if (tags != null) {
            tags.getTag(itemTag).stream().forEach((item) -> getFreezingMap().put(item, burnTime));
        }
    }

    public static void removeItemFreezingTime(ItemLike itemProvider) {
        Item item = itemProvider.asItem();
        getFreezingMap().remove(item);
    }

    public static void removeItemsFreezingTime(ItemLike[] itemProviders) {
        Stream.of(itemProviders).map(ItemLike::asItem).forEach((item) -> getFreezingMap().remove(item));
    }

    public static void removeItemTagFreezingTime(TagKey<Item> itemTag) {
        ITagManager<Item> tags = ForgeRegistries.ITEMS.tags();
        if (tags != null) {
            tags.getTag(itemTag).stream().forEach((item) -> getFreezingMap().remove(item));
        }
    }
}
