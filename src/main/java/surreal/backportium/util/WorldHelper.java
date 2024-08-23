package surreal.backportium.util;

import com.google.common.collect.ImmutableList;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Loader;
import surreal.backportium.api.block.FluidLogged;

import javax.annotation.Nullable;
import java.util.List;

public class WorldHelper {

    // No, I'm not going to use AABB
    public static <T extends Entity> List<T> getEntitiesInRadius(World world, BlockPos pos, int blockRadius, Class<T> entityClass) {
        if (blockRadius <= 0) return null;

        Chunk posChunk = world.getChunk(pos);
        int chunkX = posChunk.x;
        int chunkZ = posChunk.z;

        ImmutableList.Builder<T> list = ImmutableList.builder();

        int chunkRadius = blockRadius / 16; // I don't think MAX_ENTITY_RADIUS is needed, but we'll see

        int radiusSq = blockRadius * blockRadius;

        for (int x = -chunkRadius; Math.abs(x) <= chunkRadius; x++) {
            for (int z = -chunkRadius; Math.abs(z) <= chunkRadius; z++) {

                Chunk chunk = world.getChunk(chunkX + x, chunkZ + z);

                int minY = MathHelper.clamp(pos.getY() - blockRadius, 0, chunk.getEntityLists().length - 1);
                int maxY = MathHelper.clamp(pos.getY() + blockRadius, blockRadius, chunk.getEntityLists().length - 1);

                if (chunk.isLoaded()) {
                    for (int i = minY; i <= maxY; i++) {
                        for (T entity : chunk.getEntityLists()[i].getByClass(entityClass)) {
                            int d0 = pos.getX() - (int) entity.posX;
                            int d1 = pos.getY() - (int) entity.posY;
                            int d2 = pos.getZ() - (int) entity.posZ;
                            int sq = d0 * d0 + d1 * d1 + d2 * d2;

                            if (sq <= radiusSq) list.add(entity);
                        }
                    }
                }
            }
        }

        return list.build();
    }

    public static <T extends Entity> List<T> getEntitiesInBlock(World world, BlockPos pos, Class<T> entityClass) {
        ImmutableList.Builder<T> list = ImmutableList.builder();

        Chunk chunk = world.getChunk(pos);

        if (chunk.isLoaded()) {
            for (T entity : chunk.getEntityLists()[Math.min(pos.getY(), chunk.getEntityLists().length - 1)].getByClass(entityClass)) {
                if (entity.posX < pos.getX() + 1 && entity.posX > pos.getX() - 1 && entity.posZ < pos.getZ() + 1 && entity.posZ > pos.getZ() - 1) {
                    list.add(entity);
                }
            }
        }

        return list.build();
    }

    @Nullable
    public static Fluid getFluid(IBlockAccess world, BlockPos pos) {

        if (Loader.isModLoaded(IntegrationHelper.FLUIDLOGGED)) {
            FluidState fluidState = FluidloggedUtils.getFluidState(world, pos);
            return fluidState.getFluid();
        }

        Block block = world.getBlockState(pos).getBlock();

        if (block instanceof FluidLogged) {
            return ((FluidLogged) block).getFluid();
        }

        if (block instanceof IFluidBlock) {
            return ((IFluidBlock) block).getFluid();
        }

        return null;
    }

    public static boolean isWater(IBlockAccess world, BlockPos pos) {
        return getFluid(world, pos) == FluidRegistry.WATER || world.getBlockState(pos).getMaterial() == Material.WATER;
    }

    public static boolean inWater(IBlockAccess world, BlockPos pos) {
        if (Loader.isModLoaded(IntegrationHelper.FLUIDLOGGED)) {
            return WorldHelper.isWater(world, pos);
        }
        else {
            for (int i = 1; i < 6; i++) {
                EnumFacing facing = EnumFacing.byIndex(i);
                if (!WorldHelper.isWater(world, pos.offset(facing))) return false;
            }
        }

        return false;
    }
}
