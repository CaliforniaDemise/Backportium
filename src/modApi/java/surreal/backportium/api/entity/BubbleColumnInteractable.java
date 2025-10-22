package surreal.backportium.api.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import surreal.backportium.api.annotations.Extension;

/**
 * Handles additional bubble column interactions with entities.
 * By default, this is only used for boat rowing.
 **/
// TODO Maybe give the position of block that spawns bubble column. Maybe even use extended blockstates to define how far bubble column is from the core block.
@Extension(Entity.class)
public interface BubbleColumnInteractable {

    /**
     * Runs when entity is on top of bubble column.
     * Used for boats on top of bubble columns rowing before actually sinking or "jumping".
     **/
    default void onBubbleColumn(IBlockState state, boolean downwards) {
        Entity entity = (Entity) this;
        if(!downwards) entity.motionY = Math.min(1.8, entity.motionY + 0.1);
        else entity.motionY = Math.max(-0.9, entity.motionY - 0.03);
    }

    /**
     * Runs when entity is inside the bubble column.
     **/
    default void inBubbleColumn(IBlockState state, boolean downwards) {
        Entity entity = (Entity) this;
        if(!downwards) entity.motionY = Math.min(0.7, entity.motionY + 0.06);
        else entity.motionY = Math.max(-0.3, entity.motionY - 0.03);
        entity.fallDistance = 0.0F;
    }

    @SuppressWarnings("unchecked")
    static <T extends Entity & BubbleColumnInteractable> T cast(Entity entity) { return (T) entity; }
}
