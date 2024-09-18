package net.artienia.rubinated_nether.utils;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.EnumMap;
import java.util.Map;

/**
 * Code modified from Create by simibubi, licensed under MIT
 */
public class ShapeUtils {

    public static Map<Direction, VoxelShape> allDirections(VoxelShape facingUp) {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        for(Direction direction : Direction.values()) {
            Vec3 dir = new Vec3(
                direction == Direction.UP ? 0 : (Direction.Plane.VERTICAL.test(direction) ? 180 : 90),
                -((Math.max(direction.get2DDataValue(), 0) & 3) * 90f), 0
            );
            map.put(direction, rotatedCopy(facingUp, dir));
        }
        return map;
    }

    public static VoxelShape rotatedCopy(VoxelShape shape, Vec3 rotation) {
        if (rotation.equals(Vec3.ZERO))
            return shape;

        MutableObject<VoxelShape> result = new MutableObject<>(Shapes.empty());
        Vec3 center = new Vec3(8, 8, 8);

        shape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
            Vec3 v1 = new Vec3(x1, y1, z1).scale(16)
                .subtract(center);
            Vec3 v2 = new Vec3(x2, y2, z2).scale(16)
                .subtract(center);

            v1 = rotateVec(v1, (float) rotation.x, Direction.Axis.X);
            v1 = rotateVec(v1, (float) rotation.y, Direction.Axis.Y);
            v1 = rotateVec(v1, (float) rotation.z, Direction.Axis.Z)
                .add(center);

            v2 = rotateVec(v2, (float) rotation.x, Direction.Axis.X);
            v2 = rotateVec(v2, (float) rotation.y, Direction.Axis.Y);
            v2 = rotateVec(v2, (float) rotation.z, Direction.Axis.Z)
                .add(center);

            VoxelShape rotated = Block.box(
                Math.min(v1.x, v2.x),
                Math.min(v1.y, v2.y),
                Math.min(v1.z, v2.z),
                Math.max(v1.x, v2.x),
                Math.max(v1.y, v2.y),
                Math.max(v1.z, v2.z)
            );
            result.setValue(Shapes.or(result.getValue(), rotated));
        });

        return result.getValue();
    }

    public static Vec3 rotateVec(Vec3 vec, double deg, Direction.Axis axis) {
        if (deg == 0)
            return vec;
        if (vec == Vec3.ZERO)
            return vec;

        float angle = (float) (deg / 180f * Math.PI);
        double sin = Mth.sin(angle);
        double cos = Mth.cos(angle);
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;

        if (axis == Direction.Axis.X)
            return new Vec3(x, y * cos - z * sin, z * cos + y * sin);
        if (axis == Direction.Axis.Y)
            return new Vec3(x * cos + z * sin, y, z * cos - x * sin);
        if (axis == Direction.Axis.Z)
            return new Vec3(x * cos - y * sin, y * cos + x * sin, z);
        return vec;
    }
}
