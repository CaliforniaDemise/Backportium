package surreal.backportium.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface CoralBlock {
    default boolean canPickleGrow(World world, BlockPos pos, IBlockState state) {
        return true;
    }
}
