package surreal.backportium.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import surreal.backportium._internal.world.LoggedAccess;
import surreal.backportium.util.FluidUtil;

public interface Loggable {

    default IBlockState getLoggedState(IBlockAccess access, BlockPos pos, IBlockState state) {
        return LoggedAccess.cast(access).getLoggedState(pos);
//        if (true) return air;
//        if (ModList.FLUIDLOGGED) return air;
//        World world;
//        if (access instanceof World) world = (World) access;
//        else if (access instanceof ChunkCache) world = ChunkCacheWorld.cast((ChunkCache) access).getWorld();
//        else return air;
//        Chunk chunk = world.getChunk(pos);
//        if (!chunk.isLoaded()) return air;
//        return LoggingChunk.cast(chunk).getLoggedState(pos);
    }

    default boolean canLog(World world, BlockPos pos, IBlockState state, IBlockState stateToLog) {
        return FluidUtil.getFluid(stateToLog) == FluidRegistry.WATER;
    }

    static Loggable cast(Block block) { return (Loggable) block; }
}
