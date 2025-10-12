package surreal.backportium.block.properties.wall;

import net.minecraft.block.BlockWall;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;

public final class NewWallProperties {

    public static final PropertyBool UP = BlockWall.UP;
    public static final PropertyEnum<Connection> NORTH_NEW = PropertyEnum.create("north", Connection.class);
    public static final PropertyEnum<Connection> SOUTH_NEW = PropertyEnum.create("south", Connection.class);
    public static final PropertyEnum<Connection> WEST_NEW = PropertyEnum.create("west", Connection.class);
    public static final PropertyEnum<Connection> EAST_NEW = PropertyEnum.create("east", Connection.class);

    private NewWallProperties() {}
}
