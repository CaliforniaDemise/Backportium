package surreal.backportium._internal.entity;

import net.minecraft.entity.Entity;
import surreal.backportium.api.annotations.Extension;

@Extension(Entity.class)
public interface SetSize {

    default void setSize(float width, float height) {}

    static SetSize cast(Entity entity) { return (SetSize) entity; }
}
