package surreal.backportium.api.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import surreal.backportium.api.annotations.Extension;
import surreal.backportium.api.annotations.Final;

/**
 * Extension for {@link EntityLivingBase} for Trident Riptide effect.
 * This is done for riptide animations and for other stuff.
 */
@Extension(EntityLivingBase.class)
public interface RiptideEntity {

    /**
     * If entity is in riptide. Used in riptide animations and rendering riptide layer
     * Implementation is the same. It just checks getRiptideTimeLeft is not zero by default
     * @return If entity is in riptide effect or not
     */
    default boolean inRiptide() { return getRiptideTimeLeft() != 0; }

    /**
     * Time left until riptide effect ends. Used in riptide animations.
     * By default, it will return 'riptideTime' fields value
     * @return Integer between 0 and 20, inclusive
     */
    default int getRiptideTimeLeft() { return 0; }

    /**
     * Sets the time left until riptide ends
     * By default, it will set 'riptideTime' field
     * @param riptideTime Time to set, needs to not be a negative integer
     */
    @Final
    default void setRiptideTimeLeft(int riptideTime) {}

    /**
     * Runs when entity gets into riptide effect
     * By default, does nothing
     * @param stack The stack entity holds. It is trident or an item that can handle riptide effect
     */
    default void riptideStart(ItemStack stack) {}

    /**
     * Runs when entity gets out of from riptide effect
     * By default, does nothing
     */
    default void riptideEnd() {}

    static RiptideEntity cast(EntityLivingBase living) { return (RiptideEntity) living; }
}
