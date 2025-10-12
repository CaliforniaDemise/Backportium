package surreal.backportium._internal.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.annotations.Extension;

import java.util.Objects;

@Extension(IBlockAccess.class)
public interface LoggedAccess {

    @NotNull
    default IBlockState getLoggedState(BlockPos pos) {
        return Objects.requireNonNull(Blocks.AIR).getDefaultState();
    }

    default void setLoggedState(BlockPos pos, IBlockState state) {}

    static LoggedAccess cast(IBlockAccess world) { return (LoggedAccess) world; }
}
