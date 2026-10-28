package corundum.rubinated_nether.content.blocks.entities;

import corundum.rubinated_nether.content.RNBlockEntities;
import corundum.rubinated_nether.content.RNParticleTypes;
import corundum.rubinated_nether.content.RubinationConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class RubinationAltarBlockEntity extends BlockEntity implements Nameable {

	private static final int CHECK_INTERVAL = 40;
	private static final double SERVER_PLAYER_RANGE = 48.0;
	private static final double PARTICLE_RANGE = 32.0;
	private static final int GLYPH_CHANCE = 80;
	private static final double MIN_RISE = 2.0;
	private static final double MAX_RISE = 4.0;
	private static final double SIDEWAYS_SPREAD = 0.6;

	private boolean ready;

	private final List<BlockPos> emitters = new ArrayList<>();
	private int scanCooldown;

	public RubinationAltarBlockEntity(BlockPos pos, BlockState blockState) {
		super(RNBlockEntities.RUBINATION_ALTAR.get(), pos, blockState);
	}

	public static void tick(
		Level level,
		BlockPos pos,
		BlockState state,
		RubinationAltarBlockEntity altarBlockEntity
	) {
		if (level.isClientSide) {
			altarBlockEntity.clientTick(level, pos);
		} else {
			altarBlockEntity.serverTick(level, pos, state);
		}
	}

	private void serverTick(Level level, BlockPos pos, BlockState state) {
		if (Math.floorMod(level.getGameTime() + pos.asLong(), (long) CHECK_INTERVAL) != 0L) return;

		if (level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				SERVER_PLAYER_RANGE, e -> !e.isSpectator()) == null) return;

		int r = RubinationConverter.ALTAR_SCAN_RADIUS;
		if (!level.hasChunksAt(pos.offset(-r, -r, -r), pos.offset(r, r, r))) return;

		boolean nowReady = RubinationConverter.hasEnoughRubinatedBlocks(level, pos);
		if (nowReady != this.ready) {
			this.ready = nowReady;
			this.setChanged();
			level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
			level.updateNeighbourForOutputSignal(pos, state.getBlock());
		}
	}

	private void clientTick(Level level, BlockPos pos) {
		if (!this.ready) {
			if (!this.emitters.isEmpty()) this.emitters.clear();
			this.scanCooldown = 0;
			return;
		}

		if (level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
				PARTICLE_RANGE, e -> !e.isSpectator()) == null) return;

		if (--this.scanCooldown <= 0) {
			this.rescanEmitters(level, pos);
			this.scanCooldown = CHECK_INTERVAL;
		}

		RandomSource random = level.random;
		for (BlockPos blockPos : this.emitters) {
			if (random.nextInt(GLYPH_CHANCE) != 0) continue;

			double x = blockPos.getX() + 0.15 + random.nextDouble() * 0.7;
			double y = blockPos.getY() + 1.0;
			double z = blockPos.getZ() + 0.15 + random.nextDouble() * 0.7;

			double dx = (random.nextDouble() - 0.5) * SIDEWAYS_SPREAD;
			double dy = MIN_RISE + random.nextDouble() * (MAX_RISE - MIN_RISE);
			double dz = (random.nextDouble() - 0.5) * SIDEWAYS_SPREAD;

			level.addParticle(RNParticleTypes.RUBIN_GLYPH.get(), x, y, z, dx, dy, dz);
		}
	}

	private void rescanEmitters(Level level, BlockPos center) {
		this.emitters.clear();
		BlockPos.MutableBlockPos above = new BlockPos.MutableBlockPos();
		RubinationConverter.forEachRubinatedBlock(level, center, RubinationConverter.ALTAR_SCAN_RADIUS, pos -> {
			above.setWithOffset(pos, Direction.UP);
			if (!level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN)) {
				this.emitters.add(pos.immutable());
			}
			return true;
		});
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.ready = tag.getBoolean("Ready");
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putBoolean("Ready", this.ready);
	}

	public boolean isReady() {
		return this.ready;
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		tag.putBoolean("Ready", this.ready);
		return tag;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public Component getName() {
		return Component.translatable("gui.rubinated_nether.rubination_altar");
	}
}
