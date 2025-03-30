package corundum.rubinated_nether.content;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.ChatFormatting;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.fml.common.asm.enumextension.IndexedEnum;
import net.neoforged.fml.common.asm.enumextension.NamedEnum;
import net.neoforged.fml.common.asm.enumextension.NetworkedEnum;

@NetworkedEnum(NetworkedEnum.NetworkCheck.BIDIRECTIONAL)
@IndexedEnum
@NamedEnum(1)
public class RNRarity {
    public static final Supplier<Rarity> RUBINATED_NETHER_RUBY = Suppliers.memoize(() ->Rarity.valueOf("RUBINATED_NETHER_RUBY"));
}
