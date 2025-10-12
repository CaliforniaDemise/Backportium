package surreal.backportium._internal.entity;

import net.minecraft.entity.item.EntityBoat;
import surreal.backportium.api.annotations.Extension;

@Extension(EntityBoat.class)
public interface RockableBoat {
    int getRockingType();
    int getTicks();
    float getIntensity();
    float getAngle();
    float getPrevAngle();

    void setRocking(int type);
    void setTicks(int tick);
    void setIntensity(float angle);
    void setAngle(float angle);
    void setPrevAngle(float angle);
}
