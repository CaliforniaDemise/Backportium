package surreal.backportium.api.entity;

import net.minecraft.entity.EntityLivingBase;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.api.annotations.Extension;

@Extension(EntityLivingBase.class)
public interface EntityWithState {

    @Nullable
    EntityState getMove();

    static EntityWithState cast(EntityLivingBase entity) { return (EntityWithState) entity; }
}
