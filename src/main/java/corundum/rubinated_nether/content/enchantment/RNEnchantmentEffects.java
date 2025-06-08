package corundum.rubinated_nether.content.enchantment;

import com.mojang.serialization.MapCodec;
import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.enchantment.custom.HookingCurseEffect;
import corundum.rubinated_nether.content.enchantment.custom.LeechingCurseEffect;
// ExposureCurseEffect no longer needed - using built-in attribute effects
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RNEnchantmentEffects {
	public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_ENCHANTMENT_EFFECTS =
			DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, RubinatedNether.MODID);

	public static final Supplier<MapCodec<? extends EnchantmentEntityEffect>> HOOKING_CURSE =
			ENTITY_ENCHANTMENT_EFFECTS.register("hooking_curse", () -> HookingCurseEffect.CODEC);

	public static final Supplier<MapCodec<? extends EnchantmentEntityEffect>> LEECHING_CURSE =
			ENTITY_ENCHANTMENT_EFFECTS.register("leeching_curse", () -> LeechingCurseEffect.CODEC);


	public static void register(IEventBus eventBus) {
		ENTITY_ENCHANTMENT_EFFECTS.register(eventBus);
	}
}