package surreal.backportium.api.enums;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;
import java.util.Locale;

public enum CoralType implements IStringSerializable {
    TUBE,
    BRAIN,
    BUBBLE,
    FIRE,
    HORN;

    final String name;

    CoralType() {
        this.name = this.name().toLowerCase(Locale.US);
    }

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }

    public static CoralType byMetadata(int meta) {
        switch (meta) {
            default: return TUBE;
            case 1: return BRAIN;
            case 2: return BUBBLE;
            case 3: return FIRE;
            case 4: return HORN;
        }
    }
}
