package fuzs.puzzleslib.common.api.util.v1;

import com.mojang.math.OctahedralGroup;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

/**
 * A simple helper class for {@link VoxelShape}, mainly used for applying rotations.
 */
@Deprecated
public final class ShapesHelper {
    /**
     * @see Shapes#BLOCK_CENTER
     */
    private static final Vec3 BLOCK_CENTER = new Vec3(0.5, 0.5, 0.5);

    private ShapesHelper() {
        // NO-OP
    }

    /**
     * Returns a map with the provided {@link VoxelShape} rotated in all six {@link Direction Directions}.
     * <p>
     * Note that the provided shape is assumed to be facing {@link Direction#UP}, meaning providing {@link Direction#UP}
     * will return the original shape.
     *
     * @param shape the voxel shape to rotate
     * @return the rotated shapes
     */
    public static Map<Direction, VoxelShape> rotate(VoxelShape shape) {
        return Shapes.rotateAll(shape, OctahedralGroup.BLOCK_ROT_X_90, BLOCK_CENTER);
    }

    /**
     * Returns a map with the provided {@link VoxelShape} rotated in all four horizontal {@link Direction Directions}.
     * <p>
     * Note that the provided shape is assumed to be facing {@link Direction#SOUTH}, meaning providing
     * {@link Direction#SOUTH} will return the original shape.
     *
     * @param shape the voxel shape to rotate
     * @return the rotated shapes
     */
    public static Map<Direction, VoxelShape> rotateHorizontally(VoxelShape shape) {
        return Shapes.rotateHorizontal(shape, OctahedralGroup.BLOCK_ROT_Y_180);
    }

    /**
     * Constructs a new {@link VoxelShape}, just like
     * {@link Shapes#box(double, double, double, double, double, double)}, but in contrast properly sorts start and end
     * coordinates automatically.
     * <p>
     * Values should ideally range from {@code 0.0} to {@code 1.0}.
     *
     * @param startX start x coordinate
     * @param startY start y coordinate
     * @param startZ start z coordinate
     * @param endX   end x coordinate
     * @param endY   end y coordinate
     * @param endZ   end z coordinate
     * @return the new shape
     */
    public static VoxelShape box(double startX, double startY, double startZ, double endX, double endY, double endZ) {
        return Shapes.box(Math.min(startX, endX),
                Math.min(startY, endY),
                Math.min(startZ, endZ),
                Math.max(startX, endX),
                Math.max(startY, endY),
                Math.max(startZ, endZ));
    }
}
