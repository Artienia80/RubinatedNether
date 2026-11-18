
package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNRecipes;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.content.recipe.PetrificationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Optional;

public final class Petrification {

	private Petrification() {
	}

	public static Optional<BlockState> petrifiedState(Level level, BlockState state) {
		for (RecipeHolder<PetrificationRecipe> holder :
				level.getRecipeManager().getAllRecipesFor(RNRecipes.PETRIFICATION.get())) {
			PetrificationRecipe recipe = holder.value();

			if (recipe.matches(new PetrificationRecipe.Input(state), level)) {
				return Optional.of(recipe.getResultBlock().withPropertiesOf(state));
			}
		}

		return Optional.empty();
	}

	private static boolean tryPetrifyDoor(ServerLevel level, BlockPos upperPos, BlockState upperState) {
		if (upperState.getValue(DoorBlock.HALF) != DoubleBlockHalf.UPPER) {
			return false;
		}

		BlockPos lowerPos = upperPos.below();
		BlockState lowerState = level.getBlockState(lowerPos);

		if (!(lowerState.getBlock() instanceof DoorBlock)
				|| lowerState.getValue(DoorBlock.HALF) != DoubleBlockHalf.LOWER) {
			return false;
		}

		if (!level.getBlockState(lowerPos.below()).is(RNTags.Blocks.SOUL_BLOCKS)) {
			return false;
		}

		Optional<BlockState> petrifiedUpper = petrifiedState(level, upperState);
		Optional<BlockState> petrifiedLower = petrifiedState(level, lowerState);

		if (petrifiedUpper.isEmpty() || petrifiedLower.isEmpty()) {
			return false;
		}

		level.setBlock(lowerPos, petrifiedLower.get(),
				Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
		level.setBlock(upperPos, petrifiedUpper.get(), Block.UPDATE_ALL);
		level.blockUpdated(lowerPos, petrifiedLower.get().getBlock());

		level.sendParticles(ParticleTypes.SOUL,
				upperPos.getX() + 0.5, upperPos.getY(), upperPos.getZ() + 0.5,
				12, 0.3, 0.5, 0.3, 0.02);
		level.playSound(null, upperPos, SoundEvents.STONE_PLACE,
				SoundSource.BLOCKS, 1.0F, 0.6F);

		return true;
	}

	public static boolean tryPetrify(ServerLevel level, BlockPos coverPos) {
		BlockPos woodPos = coverPos.below();

		if (!level.getBlockState(coverPos.above()).is(RNTags.Blocks.PETRIFICATION_CATALYST)) {
			return false;
		}

		BlockState woodState = level.getBlockState(woodPos);

		if (woodState.getBlock() instanceof DoorBlock) {
			return tryPetrifyDoor(level, woodPos, woodState);
		}

		if (!level.getBlockState(woodPos.below()).is(RNTags.Blocks.SOUL_BLOCKS)) {
			return false;
		}

		Optional<BlockState> petrified = petrifiedState(level, woodState);

		if (petrified.isEmpty()) {
			return false;
		}

		BlockState converted = Block.updateFromNeighbourShapes(petrified.get(), level, woodPos);

		BlockEntity oldEntity = level.getBlockEntity(woodPos);
		CompoundTag saved = null;

		if (oldEntity != null) {
			saved = oldEntity.saveWithFullMetadata(level.registryAccess());
			level.removeBlockEntity(woodPos);
		}

		level.setBlock(woodPos, converted, Block.UPDATE_ALL);

		if (saved != null) {
			BlockEntity newEntity = level.getBlockEntity(woodPos);

			if (newEntity != null) {
				newEntity.loadWithComponents(saved, level.registryAccess());
				newEntity.setChanged();
				level.sendBlockUpdated(woodPos, converted, converted, Block.UPDATE_ALL);
			}
		}

		level.sendParticles(ParticleTypes.SOUL,
				woodPos.getX() + 0.5, woodPos.getY() + 0.5, woodPos.getZ() + 0.5,
				8, 0.3, 0.3, 0.3, 0.02);
		level.playSound(null, woodPos, SoundEvents.STONE_PLACE,
				SoundSource.BLOCKS, 1.0F, 0.6F);

		return true;
	}
}
