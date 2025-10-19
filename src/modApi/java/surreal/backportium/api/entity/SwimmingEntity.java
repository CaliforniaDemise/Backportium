package surreal.backportium.api.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium.api.annotations.Extension;

@Extension(EntityLivingBase.class)
public interface SwimmingEntity {

    default boolean canSwim() {
        return false;
    }

    default void updateSwimming() {}

    default boolean isSwimming() {
        return false;
    }

    default boolean isActuallySwimming() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    default boolean isVisuallySwimming() {
        return false;
    }

    default void setSwimming(boolean swimming) {}

    default float getSwimAnimation(float partialTicks) {
        return 0.0F;
    }

    static SwimmingEntity cast(EntityLivingBase entity) { return (SwimmingEntity) entity; }
}
