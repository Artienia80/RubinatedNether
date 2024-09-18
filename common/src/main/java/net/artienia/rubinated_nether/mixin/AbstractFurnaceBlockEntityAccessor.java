package net.artienia.rubinated_nether.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
    @Accessor("quickCheck")
    RecipeManager.CachedCheck<Container, ? extends AbstractCookingRecipe> rubinatedNether$getQuickCheck();

    @Invoker
    boolean callCanBurn(RegistryAccess registryAccess, @Nullable Recipe<?> recipe, NonNullList<ItemStack> stacks, int stackSize);

    @Accessor("litTime")
    int rubinatedNether$getLitTime();

    @Accessor("litTime")
    void rubinatedNether$setLitTime(int litTime);

    @Accessor("litDuration")
    void rubinatedNether$setLitDuration(int litDuration);

    @Accessor("cookingProgress")
    int rubinatedNether$getCookingProgress();

    @Accessor("cookingProgress")
    void rubinatedNether$setCookingProgress(int cookingProgress);

    @Accessor("cookingTotalTime")
    int rubinatedNether$getCookingTotalTime();

    @Accessor("cookingTotalTime")
    void rubinatedNether$setCookingTotalTime(int cookingTotalTime);

    @Invoker
    boolean callIsLit();

    @Invoker
    static int callGetTotalCookTime(Level level, AbstractFurnaceBlockEntity blockEntity) {
        throw new AssertionError();
    }

    @Accessor("items")
    NonNullList<ItemStack> rubinatedNether$getItems();

    @Invoker
    int callGetBurnDuration(ItemStack pFuel);
}
