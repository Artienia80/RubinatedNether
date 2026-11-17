package corundum.rubinated_nether.content.blocks;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.function.Supplier;

public class PetrifiedLogBlock extends RotatedPillarBlock {
	private final Supplier<? extends Block> stripped;

	public PetrifiedLogBlock(Supplier<? extends Block> stripped, Properties properties) {
		super(properties);
		this.stripped = stripped;
	}

	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		if (itemAbility == ItemAbilities.AXE_STRIP) {
			return this.stripped.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
		}
		return super.getToolModifiedState(state, context, itemAbility, simulate);
	}
}
