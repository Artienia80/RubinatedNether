package corundum.rubinated_nether.misc;

import com.mojang.serialization.Codec;
import corundum.rubinated_nether.RubinatedNether;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class RNAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RubinatedNether.MODID);

    public static final Supplier<AttachmentType<Float>> REAPING_POOL = ATTACHMENT_TYPES.register(
            "reaping_pool", () -> AttachmentType.builder(() -> 0.0F).serialize(Codec.FLOAT).build()
    );

    public static final Supplier<AttachmentType<Integer>> TARNISH_LEVEL = ATTACHMENT_TYPES.register(
            "tarnish_level", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );

    public static final Supplier<AttachmentType<Long>> SOUL_STARVED_SAND_STEP = ATTACHMENT_TYPES.register(
            "soul_starved_sand_step", () -> AttachmentType.builder(() -> Long.MIN_VALUE / 2).build()
    );
}
