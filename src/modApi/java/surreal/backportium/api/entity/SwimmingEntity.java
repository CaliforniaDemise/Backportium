package surreal.backportium.api.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium.api.annotations.Extension;
import surreal.backportium.api.annotations.Final;

@Extension(EntityLivingBase.class)
public interface SwimmingEntity {

    default boolean isSwimming() {
        return false;
    }

    @Final
    default void setSwimming(boolean swimming) {}

    default boolean canSwim() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    default float getSwimAnimation(float partialTicks) {
        return 0.0F;
    }

    static SwimmingEntity cast(EntityLivingBase entity) { return (SwimmingEntity) entity; }
}
