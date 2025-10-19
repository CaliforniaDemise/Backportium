package surreal.backportium.api.entity;

import net.minecraft.entity.EntityLivingBase;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.annotations.Extension;

@Extension(EntityLivingBase.class)
public interface EntityWithState {

    @NotNull
    EntityState getState();

    static EntityWithState cast(EntityLivingBase entity) { return (EntityWithState) entity; }
}
