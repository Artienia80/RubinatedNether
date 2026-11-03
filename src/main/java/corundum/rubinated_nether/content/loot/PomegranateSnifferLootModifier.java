package corundum.rubinated_nether.content.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import corundum.rubinated_nether.content.RNItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class PomegranateSnifferLootModifier extends LootModifier {
    public static final Supplier<MapCodec<PomegranateSnifferLootModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.mapCodec(instance ->
                    codecStart(instance).apply(instance, PomegranateSnifferLootModifier::new)));

    public PomegranateSnifferLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!BuiltInLootTables.SNIFFER_DIGGING.location().equals(context.getQueriedLootTableId())) {
            return generatedLoot;
        }

        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        Entity sniffer = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (origin == null || sniffer == null) {
            return generatedLoot;
        }

        BlockPos ground = BlockPos.containing(origin.x, sniffer.getY() + 0.5, origin.z).below();
        BlockState state = context.getLevel().getBlockState(ground);
        if (state.is(Blocks.CRIMSON_NYLIUM) || state.is(Blocks.WARPED_NYLIUM)) {
            generatedLoot.clear();
            generatedLoot.add(new ItemStack(RNItems.POMEGRANATE.get()));
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
