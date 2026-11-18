package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNBlocks;
import corundum.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class Petrification {
	private record Rule(TagKey<Block> source, Supplier<? extends Block> result) {
	}

	private static final List<Rule> RULES = List.of(
			new Rule(RNTags.Blocks.PETRIFIABLE_SNAGS, RNBlocks.PETRIFIED_SNAG),
			new Rule(RNTags.Blocks.PETRIFIABLE_STRIPPED_SNAGS, RNBlocks.STRIPPED_PETRIFIED_SNAG),
			new Rule(RNTags.Blocks.PETRIFIABLE_RINDS, RNBlocks.PETRIFIED_RIND),
			new Rule(RNTags.Blocks.PETRIFIABLE_STRIPPED_RINDS, RNBlocks.STRIPPED_PETRIFIED_RIND),
			new Rule(BlockTags.PLANKS, RNBlocks.PETRIFIED_FLAGS),
			new Rule(BlockTags.WOODEN_STAIRS, RNBlocks.PETRIFIED_STAIRS),
			new Rule(BlockTags.WOODEN_SLABS, RNBlocks.PETRIFIED_SLAB),
			new Rule(BlockTags.WOODEN_FENCES, RNBlocks.PETRIFIED_FENCE),
			new Rule(BlockTags.FENCE_GATES, RNBlocks.PETRIFIED_FENCE_GATE),
			new Rule(BlockTags.WOODEN_DOORS, RNBlocks.PETRIFIED_DOOR),
			new Rule(BlockTags.WOODEN_TRAPDOORS, RNBlocks.PETRIFIED_TRAPDOOR),
			new Rule(BlockTags.WOODEN_BUTTONS, RNBlocks.PETRIFIED_BUTTON),
			new Rule(BlockTags.WOODEN_PRESSURE_PLATES, RNBlocks.PETRIFIED_PRESSURE_PLATE),
			new Rule(BlockTags.STANDING_SIGNS, RNBlocks.PETRIFIED_SIGN),
			new Rule(BlockTags.WALL_SIGNS, RNBlocks.PETRIFIED_WALL_SIGN),
			new Rule(BlockTags.CEILING_HANGING_SIGNS, RNBlocks.PETRIFIED_HANGING_SIGN),
			new Rule(BlockTags.WALL_HANGING_SIGNS, RNBlocks.PETRIFIED_WALL_HANGING_SIGN)
	);

	private Petrification() {
	}

	public static Optional<BlockState> petrifiedState(BlockState state) {
		for (Rule rule : RULES) {
			if (!state.is(rule.source())) {
				continue;
			}
			Block result = rule.result().get();
			if (state.is(result)) {
				return Optional.empty();
			}
			return Optional.of(result.withPropertiesOf(state));
		}
		return Optional.empty();
	}

	private static boolean tryPetrifyDoor(ServerLevel level, BlockPos upperPos, BlockState upperState) {
		if (upperState.getValue(DoorBlock.HALF) != DoubleBlockHalf.UPPER) {
			return false;
		}
		BlockPos lowerPos = upperPos.below();
		BlockState lowerState = level.getBlockState(lowerPos);
		if (!(lowerState.getBlock() instanceof DoorBlock) || lowerState.getValue(DoorBlock.HALF) != DoubleBlockHalf.LOWER) {
			return false;
		}
		if (!level.getBlockState(lowerPos.below()).is(RNTags.Blocks.SOUL_BLOCKS)) {
			return false;
		}

		Optional<BlockState> petrifiedUpper = petrifiedState(upperState);
		Optional<BlockState> petrifiedLower = petrifiedState(lowerState);
		if (petrifiedUpper.isEmpty() || petrifiedLower.isEmpty()) {
			return false;
		}

		level.setBlock(lowerPos, petrifiedLower.get(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
		level.setBlock(upperPos, petrifiedUpper.get(), Block.UPDATE_ALL);
		level.blockUpdated(lowerPos, petrifiedLower.get().getBlock());

		level.sendParticles(ParticleTypes.SOUL, upperPos.getX() + 0.5, upperPos.getY(), upperPos.getZ() + 0.5, 12, 0.3, 0.5, 0.3, 0.02);
		level.playSound(null, upperPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 0.6F);
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

		Optional<BlockState> petrified = petrifiedState(level.getBlockState(woodPos));
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

		level.sendParticles(ParticleTypes.SOUL, woodPos.getX() + 0.5, woodPos.getY() + 0.5, woodPos.getZ() + 0.5, 8, 0.3, 0.3, 0.3, 0.02);
		level.playSound(null, woodPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 0.6F);
		return true;
	}
}
