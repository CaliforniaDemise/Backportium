package surreal.backportium._internal.client.model;

import net.minecraft.client.model.ModelBiped;
import surreal.backportium.api.annotations.Extension;

@Extension(ModelBiped.class)
public interface ModelBipedSwimming {

    default float getSwimAnimation() { return 0.0F; }
    default void setSwimAnimation(float value) {}

    static ModelBipedSwimming cast(ModelBiped model) { return (ModelBipedSwimming) model; }
}
