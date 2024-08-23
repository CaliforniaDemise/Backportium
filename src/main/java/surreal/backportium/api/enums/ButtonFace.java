package surreal.backportium.api.enums;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.Locale;

public enum ButtonFace implements IStringSerializable {
    WALL,
    CEILING,
    FLOOR;

    final String name;

    ButtonFace() {
        this.name = this.name().toLowerCase(Locale.US);
    }

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }
}
