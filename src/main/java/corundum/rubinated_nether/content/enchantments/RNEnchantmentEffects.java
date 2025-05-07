package corundum.rubinated_nether.content.enchantments;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.enchantments.custom.SluggishnessEnchantment;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RNEnchantmentEffects {
	public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_ENCHANTMENT_EFFECTS =
			DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, RubinatedNether.MODID);

	public static final Supplier<MapCodec<? extends EnchantmentEntityEffect>> SLUGGISHNESS =
			ENTITY_ENCHANTMENT_EFFECTS.register("sluggishness", () -> SluggishnessEnchantment.CODEC);

	public static void register(IEventBus eventBus) {
		ENTITY_ENCHANTMENT_EFFECTS.register(eventBus);
	}
}
