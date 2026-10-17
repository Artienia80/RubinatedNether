package corundum.rubinated_nether.client.particles;

import corundum.rubinated_nether.content.RNTags;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SteamParticle extends TextureSheetParticle {

    public enum Tint {
        NEUTRAL(1.00f, 1.00f, 1.00f,   1.00f, 1.00f, 1.00f,   false),
        HOT    (1.00f, 0.58f, 0.24f,   0.94f, 0.86f, 0.80f,   true),
        COLD   (0.25f, 0.75f, 1.00f,   0.88f, 0.94f, 1.00f,   true);

        final float r0, g0, b0, r1, g1, b1;
        final boolean glows;

        Tint(float r0, float g0, float b0, float r1, float g1, float b1, boolean glows) {
            this.r0 = r0; this.g0 = g0; this.b0 = b0;
            this.r1 = r1; this.g1 = g1; this.b1 = b1;
            this.glows = glows;
        }
    }

    private static final float HOT_GLOW_FRACTION = 0.4f;
    private static final float EXPANSION = 0.6f;

    private final SpriteSet sprites;
    private final Tint tint;
    private final float initialAlpha;
    private final int fadeStartTick;
    private final float baseSize;
    private final float shade;

    protected SteamParticle(ClientLevel level, double x, double y, double z,
                            double xSpeed, double ySpeed, double zSpeed,
                            SpriteSet sprites, Tint tint) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.tint = tint;

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.friction = 0.975f;

        float speed = (float) Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed);

        int travelTicks = speed > 0
                ? Math.max(30, (int)(Math.log(0.01 / speed) / Math.log(0.975)) + 10)
                : 80;

        this.lifetime = travelTicks + this.random.nextInt(8);
        this.fadeStartTick = (int)(this.lifetime * 0.5f);

        this.initialAlpha = 0.75f + this.random.nextFloat() * 0.2f;
        this.alpha = initialAlpha;

        this.scale(2.5f + this.random.nextFloat() * 1.0f);
        this.baseSize = this.quadSize;
        this.gravity = 0.0f;

        this.shade = 0.94f + this.random.nextFloat() * 0.06f;
        applyColour(0f);

        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        float progress = (float) this.age / this.lifetime;

        this.setSpriteFromAge(this.sprites);
        this.quadSize = baseSize * (1.0f + EXPANSION * progress);
        applyColour(progress);

        if (age >= fadeStartTick) {
            float fadeFraction = (float)(age - fadeStartTick) / (lifetime - fadeStartTick);
            this.alpha = initialAlpha * (1.0f - fadeFraction);
        }

        this.yd -= 0.04 * (double)this.gravity;
        this.moveWithPassthrough(this.xd, this.yd, this.zd);
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;
    }

    private void applyColour(float progress) {
        float t = Mth.clamp(progress * 1.4f, 0f, 1f);
        this.rCol = Mth.lerp(t, tint.r0, tint.r1) * shade;
        this.gCol = Mth.lerp(t, tint.g0, tint.g1) * shade;
        this.bCol = Mth.lerp(t, tint.b0, tint.b1) * shade;
    }

    @Override
    public int getLightColor(float partialTick) {
        if (tint.glows && this.age < this.lifetime * HOT_GLOW_FRACTION) {
            return LightTexture.FULL_BRIGHT;
        }
        return super.getLightColor(partialTick);
    }


    private void moveWithPassthrough(double dx, double dy, double dz) {
        if (dx == 0 && dy == 0 && dz == 0) return;

        AABB box = this.getBoundingBox();
        Vec3 movement = collideSteam(new Vec3(dx, dy, dz), box);

        double mx = movement.x;
        double my = movement.y;
        double mz = movement.z;

        if (mx != 0 || my != 0 || mz != 0) {
            this.setBoundingBox(box.move(mx, my, mz));
            this.setLocationFromBoundingbox();
        }

        this.onGround = dy != my && dy < 0;

        if (dx != mx) this.xd = 0;
        if (dy != my) this.yd = 0;
        if (dz != mz) this.zd = 0;
    }

    private Vec3 collideSteam(Vec3 movement, AABB box) {
        double dx = movement.x;
        double dy = movement.y;
        double dz = movement.z;

        AABB swept = box.expandTowards(dx, dy, dz);

        java.util.List<VoxelShape> shapes = new java.util.ArrayList<>();
        for (BlockPos pos : BlockPos.betweenClosed(
                Mth.floor(swept.minX - 1), Mth.floor(swept.minY - 1), Mth.floor(swept.minZ - 1),
                Mth.ceil(swept.maxX + 1),  Mth.ceil(swept.maxY + 1),  Mth.ceil(swept.maxZ + 1)
        )) {
            BlockState state = this.level.getBlockState(pos);
            if (state.is(RNTags.Blocks.SMOKE_PASSTHROUGH)) continue;

            VoxelShape shape = state.getCollisionShape(this.level, pos, CollisionContext.empty());
            if (!shape.isEmpty()) {
                shapes.add(shape.move(pos.getX(), pos.getY(), pos.getZ()));
            }
        }

        dy = Shapes.collide(Direction.Axis.Y, box, shapes, dy);
        dx = Shapes.collide(Direction.Axis.X, box.move(0, dy, 0), shapes, dx);
        dz = Shapes.collide(Direction.Axis.Z, box.move(0, dy, 0), shapes, dz);

        return new Vec3(dx, dy, dz);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final Tint tint;

        public Provider(SpriteSet sprites, Tint tint) {
            this.sprites = sprites;
            this.tint = tint;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new SteamParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites, tint);
        }
    }
}
