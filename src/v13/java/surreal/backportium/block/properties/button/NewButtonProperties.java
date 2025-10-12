package surreal.backportium.block.properties.button;

import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.math.AxisAlignedBB;

public final class NewButtonProperties {

    public static final AxisAlignedBB AABB_DOWN_AXIS_X_OFF, AABB_DOWN_AXIS_X_ON;
    public static final AxisAlignedBB AABB_UP_AXIS_X_OFF, AABB_UP_AXIS_X_ON;

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool POWERED = BlockButton.POWERED;
    public static final PropertyEnum<Face> FACE = PropertyEnum.create("face", Face.class);

    private NewButtonProperties() {}

    static {
        AABB_DOWN_AXIS_X_OFF = new AxisAlignedBB(0.375, 0.875, 0.3125, 0.625, 1.0, 0.6875);
        AABB_DOWN_AXIS_X_ON = new AxisAlignedBB(0.375, 0.9375, 0.3125, 0.625, 1.0, 0.6875);
        AABB_UP_AXIS_X_OFF = new AxisAlignedBB(0.375, 0.0, 0.3125, 0.625, 0.125, 0.6875);
        AABB_UP_AXIS_X_ON = new AxisAlignedBB(0.375, 0.0, 0.3125, 0.625, 0.0625, 0.6875);
    }
}
