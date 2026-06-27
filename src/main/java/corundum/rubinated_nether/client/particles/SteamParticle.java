package corundum.rubinated_nether.client.particles;

import corundum.rubinated_nether.content.RNTags;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
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

    private final float initialAlpha;
    private final int fadeStartTick;

    protected SteamParticle(ClientLevel level, double x, double y, double z,
                            double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.friction = 0.975f;

        float speed = (float) Math.sqrt(xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed);

        int travelTicks = speed > 0
                ? Math.max(30, (int)(Math.log(0.01 / speed) / Math.log(0.975)) + 10)
                : 80;

        this.fadeStartTick = (int)(travelTicks * 0.5f);
        this.lifetime = travelTicks;

        this.initialAlpha = 0.9f;
        this.alpha = initialAlpha;

        this.scale(3.0f);
        this.gravity = 0.0f;
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

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            SteamParticle particle = new SteamParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(sprites);
            return particle;
        }
    }
}