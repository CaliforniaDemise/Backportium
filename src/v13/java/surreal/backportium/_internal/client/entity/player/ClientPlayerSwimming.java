package surreal.backportium._internal.client.entity.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import surreal.backportium.api.annotations.Extension;
import surreal.backportium.api.annotations.Final;

@Extension(EntityPlayerSP.class)
public interface ClientPlayerSwimming {

    @Final
    default boolean isActuallySneaking() {
        return false;
    }

    @Final
    default boolean isForcedDown() {
        return false;
    }

    @Final
    default boolean isUsingSwimmingAnimation() {
        return false;
    }

//    @Final
//    default boolean isUsingSwimmingAnimation(float moveForward, float moveStrafe) {
//        return false;
//    }

    default boolean canSwim() {
        return false;
    }

    default boolean isMovingForward(float moveForward, float moveStrafe) {
        return false;
    }

    default boolean shouldBlockPushPlayer(BlockPos pos) {
        return false;
    }

    static ClientPlayerSwimming cast(EntityPlayerSP player) {
        return (ClientPlayerSwimming) player;
    }
}
