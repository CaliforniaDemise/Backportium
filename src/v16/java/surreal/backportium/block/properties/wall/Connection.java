package surreal.backportium.block.properties.wall;

import net.minecraft.util.IStringSerializable;
import org.jetbrains.annotations.NotNull;

public enum Connection implements IStringSerializable {

    NONE("none"),
    LOW("low"),
    TALL("tall");

    private final String name;

    Connection(String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String getName() {
        return this.name;
    }
}
