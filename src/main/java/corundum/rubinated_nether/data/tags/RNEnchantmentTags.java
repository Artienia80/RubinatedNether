package corundum.rubinated_nether.data.tags;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.enchantment.RNEnchantments;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class RNEnchantmentTags extends EnchantmentTagsProvider {

    public RNEnchantmentTags(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, registries, RubinatedNether.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.tag(RNTags.Enchantments.RUBINATED_CURSES).add(
                RNEnchantments.DEFICIENCY_CURSE,
                RNEnchantments.MISFORTUNE_CURSE,
                RNEnchantments.FRAGILITY_CURSE,
                RNEnchantments.BLUNTNESS_CURSE,
                RNEnchantments.RAVAGING_CURSE,
                RNEnchantments.EXPOSURE_CURSE,
                RNEnchantments.LEECHING_CURSE,
                RNEnchantments.DULLNESS_CURSE,
                RNEnchantments.HOOKING_CURSE,
                RNEnchantments.SLOW_CHARGE_CURSE,
                RNEnchantments.CROOKED_SHOT_CURSE,
                RNEnchantments.RECOIL_CURSE,
                RNEnchantments.BUOYANCY_CURSE,
                RNEnchantments.SINKING_CURSE
        );

    }
}