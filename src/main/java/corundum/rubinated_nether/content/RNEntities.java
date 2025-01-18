package corundum.rubinated_nether.content;

import corundum.rubinated_nether.RubinatedNether;
import corundum.rubinated_nether.content.entity.BronzeShotProjectileEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RNEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, RubinatedNether.MODID);

    public static final Supplier<EntityType<BronzeShotProjectileEntity>> BRONZE_SHOT =
            ENTITY_TYPES.register("bronze_shot", () -> EntityType.Builder.<BronzeShotProjectileEntity>of(BronzeShotProjectileEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f).build("bronze_shot"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}