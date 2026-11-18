package corundum.rubinated_nether.content.blocks;

import corundum.rubinated_nether.content.RNEffects;
import corundum.rubinated_nether.content.RNTags;
import corundum.rubinated_nether.misc.RNAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulStarvedSandBlock extends Block {
	private static final int PETRIFY_CHANCE = 3;
	public static final int DAMAGE_INTERVAL = 10;
	public static final int EFFECT_INTERVAL = 20;
	public static final int MAX_AMPLIFIER = 8;
	private static final float MIN_HEALTH = 1.0F;
	private static final Vec3 STUCK_MULTIPLIER = new Vec3(0.25, 0.3, 0.25);
	private static final VoxelShape FALLING_COLLISION_SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9F, 1.0);

	public SoulStarvedSandBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
		return adjacentState.is(this) || super.skipRendering(state, adjacentState, direction);
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.empty();
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext entityContext) {
			Entity entity = entityContext.getEntity();
			if (entity != null) {
				if (entity.fallDistance > 2.5F) {
					return FALLING_COLLISION_SHAPE;
				}
				boolean falling = entity instanceof FallingBlockEntity;
				if (falling || hasSoulSpeedBoots(entity) && context.isAbove(Shapes.block(), pos, false) && !context.isDescending()) {
					return super.getCollisionShape(state, level, pos, context);
				}
			}
		}
		return Shapes.empty();
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity.getType().is(RNTags.EntityTypes.SOUL_STARVED_SAND_IMMUNE)) return;
		if (entity instanceof Player player && player.getAbilities().flying) return;

		if (!(entity instanceof LivingEntity) || level.getBlockState(entity.blockPosition()).is(this)) {
			entity.makeStuckInBlock(state, STUCK_MULTIPLIER);
			if (level.isClientSide && (entity.xOld != entity.getX() || entity.zOld != entity.getZ()) && level.getRandom().nextInt(6) == 0) {
				RandomSource random = level.getRandom();
				level.addParticle(
						ParticleTypes.SCULK_SOUL,
						entity.getX(),
						pos.getY() + 1,
						entity.getZ(),
						Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F,
						0.05F,
						Mth.randomBetween(random, -1.0F, 1.0F) * 0.083333336F
				);
			}
		}

		if (level.isClientSide || !(entity instanceof LivingEntity living)) return;
		if (living instanceof Player player && player.isCreative()) return;
		if (!BlockPos.containing(living.getEyePosition()).equals(pos)) return;

		long now = level.getGameTime();
		float health = living.getHealth();
		if (health > MIN_HEALTH) {
			if (living.tickCount % DAMAGE_INTERVAL != 0) return;
			living.hurt(level.damageSources().inWall(), Math.min(1.0F, health - MIN_HEALTH));
			living.setData(RNAttachments.SOUL_STARVED_SAND_STEP, now);
			return;
		}

		if (now - living.getData(RNAttachments.SOUL_STARVED_SAND_STEP) < EFFECT_INTERVAL) return;

		MobEffectInstance current = living.getEffect(RNEffects.SOUL_STARVED);
		int next = current == null ? 0 : current.getAmplifier() + 1;
		if (next > MAX_AMPLIFIER) return;

		living.setData(RNAttachments.SOUL_STARVED_SAND_STEP, now);
		living.addEffect(new MobEffectInstance(RNEffects.SOUL_STARVED, MobEffectInstance.INFINITE_DURATION, next, false, false, true));
	}

	private static boolean hasSoulSpeedBoots(Entity entity) {
		if (!(entity instanceof LivingEntity living)) return false;
		ItemEnchantments enchantments = living.getItemBySlot(EquipmentSlot.FEET).getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		for (Holder<Enchantment> enchantment : enchantments.keySet()) {
			if (enchantment.is(Enchantments.SOUL_SPEED)) return true;
		}
		return false;
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.getBlockState(pos.above()).is(RNTags.Blocks.PETRIFICATION_CATALYST)) return;
		if (random.nextInt(PETRIFY_CHANCE) != 0) return;
		Petrification.tryPetrify(level, pos);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (!level.getBlockState(pos.below()).is(this)) return;
		if (level.getBlockState(pos.above()).is(this)) return;
		if (random.nextInt(4) != 0) return;

		level.addParticle(
				ParticleTypes.SOUL,
				pos.getX() + random.nextDouble(),
				pos.getY() + 1.05,
				pos.getZ() + random.nextDouble(),
				0.0,
				0.03,
				0.0
		);
	}
}
