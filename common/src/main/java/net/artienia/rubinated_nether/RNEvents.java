package net.artienia.rubinated_nether;

import net.artienia.rubinated_nether.content.RNBlocks;
import net.artienia.rubinated_nether.content.RNParticleTypes;
import net.artienia.rubinated_nether.content.RNTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class RNEvents {

	private RNEvents() {}

	public static InteractionResult interactBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		BlockPos pos = hit.getBlockPos();
		ItemStack stack = player.getItemInHand(hand);

		if(stack.is(RNTags.Items.LOW_RUBY) && level.getBlockState(pos).is(Blocks.CRYING_OBSIDIAN)) {
			level.setBlockAndUpdate(pos, RNBlocks.BLEEDING_OBSIDIAN.getDefaultState());
			return getInteractionResult(player, level, pos, stack);
		}

		if(stack.is(RNTags.Items.LOW_RUBY) && level.getBlockState(pos).is(Blocks.DEEPSLATE)) {
			level.setBlockAndUpdate(pos, RNBlocks.ALTAR_STONE.getDefaultState());
			return getInteractionResult(player, level, pos, stack);
		}

		return InteractionResult.PASS;
	}

	// TODO: Figure out sound and particles
	@NotNull
	private static InteractionResult getInteractionResult(Player player, Level level, BlockPos pos, ItemStack stack) {
		level.playLocalSound(pos, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1.0f, 0.4f, true);
		if (!player.getAbilities().instabuild) stack.shrink(1);

		if (level.isClientSide) {
			for (Direction direction : Direction.values()) {
				ParticleUtils.spawnParticlesOnBlockFace(level, pos,
						RNParticleTypes.RUBY_AURA.get(),
						UniformInt.of(4, 6),
						direction,
						() -> new Vec3(Mth.nextDouble(level.random, -0.1, 0.1),
								Mth.nextDouble(level.random, -0.1, 0.1),
								Mth.nextDouble(level.random, -0.1, 0.1)),
						0.15);
			}
		}

		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}
