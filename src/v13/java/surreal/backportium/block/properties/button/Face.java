package surreal.backportium.block.properties.button;

import net.minecraft.util.IStringSerializable;
import org.jetbrains.annotations.NotNull;

public enum Face implements IStringSerializable {

    CEILING("ceiling"),
    WALL("wall"),
    FLOOR("floor");

    private final String name;

    Face(String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }

    public static Face byIndex(int index) {
        switch (index) {
            case 0: return Face.CEILING;
            case 2: return Face.FLOOR;
            default: return Face.WALL;
        }
    }
}
