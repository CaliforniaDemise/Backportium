package surreal.backportium.util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.integration.ModList;

public final class FluidUtil {

    public static boolean surroundedByWater(IBlockAccess world, BlockPos pos) {
        for (int i = 0; i < 6; i++) {
            EnumFacing facing = EnumFacing.byIndex(i);
            BlockPos facingPos = pos.offset(facing);
            if (facing == EnumFacing.UP && world.isAirBlock(facingPos)) continue;
            IBlockState facingState = world.getBlockState(facingPos);
            if (facingState.getMaterial() != Material.WATER && !facingState.isSideSolid(world, facingPos, facing.getOpposite())) {
//                Block facingBlock = facingState.getBlock();
//                if (facingBlock instanceof Loggable) {
//                    facingState = Loggable.cast(facingBlock).getLoggedState(world, facingPos, facingState);
//                    if (facingState.getMaterial() != Material.WATER && !facingState.isSideSolid(world, facingPos, facing.getOpposite())) return false;
//                }
//                else return false;
                return false;
            }
        }
        return true;
    }

    @Nullable
    public static Fluid getFluid(IBlockState state) {
        Block block = state.getBlock();
        if (block instanceof IFluidBlock) return ((IFluidBlock) block).getFluid();
        if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) return FluidRegistry.WATER;
        if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) return FluidRegistry.LAVA;
        return null;
    }

    @Nullable
    public static Fluid getFluid(IBlockAccess world, BlockPos pos, IBlockState state) {
        if (ModList.FLUIDLOGGED) {

        }
        return getFluid(state);
    }

    private FluidUtil() {}
}
