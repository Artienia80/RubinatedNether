package corundum.rubinated_nether.content.dowsing;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNRarity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class DowsingRods {
    public static final TagKey<Structure> TARGETS = TagKey.create(Registries.STRUCTURE, RubinatedNether.id("dowsing_targets"));
    public static final int SEARCH_RADIUS = 100;
    public static final int SEARCH_INTERVAL = 40;
    public static final float ROD_SCALE = 0.75F;
    public static final float ROD_HALF_LENGTH = 0.5F;
    public static final float ROD_PIXEL = ROD_SCALE / 16.0F;
    public static final double ARRIVAL_RADIUS = 5.0D;

    private DowsingRods() {
    }

    public static boolean isHolding(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return (main.is(Items.BLAZE_ROD) && off.is(Items.BREEZE_ROD))
                || (main.is(Items.BREEZE_ROD) && off.is(Items.BLAZE_ROD));
    }

    public static ItemStack withGlint(ItemStack stack) {
        ItemStack copy = stack.copy();
        copy.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        copy.set(DataComponents.RARITY, RNRarity.RUBINATED_NETHER_RUBY.get());
        return copy;
    }

    public static boolean isArrivedRod(ItemStack stack) {
        return isRod(stack) && RNRarity.RUBINATED_NETHER_RUBY.get().equals(stack.get(DataComponents.RARITY));
    }

    public static boolean isRod(ItemStack stack) {
        return stack.is(Items.BLAZE_ROD) || stack.is(Items.BREEZE_ROD);
    }
}
