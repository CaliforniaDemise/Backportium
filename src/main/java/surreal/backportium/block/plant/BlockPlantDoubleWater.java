package surreal.backportium.block.plant;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.core.BPPlugin;
import surreal.backportium.util.WorldHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
public class BlockPlantDoubleWater extends BlockPlantDouble implements FluidLogged {

    public BlockPlantDoubleWater(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    public BlockPlantDoubleWater(Material material) {
        super(material);
    }

    @Nonnull
    @Override
    public Material getMaterial(@Nonnull IBlockState state) {
        return BPPlugin.FLUIDLOGGED ? Material.PLANTS : super.getMaterial(state);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && WorldHelper.inWater(worldIn, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        return super.canBlockStay(worldIn, pos, state) && WorldHelper.inWater(worldIn, pos);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Beach;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks) {
        if (BPPlugin.FLUIDLOGGED) return super.getFogColor(world, pos, state, entity, originalColor, partialTicks);
        return Blocks.WATER.getFogColor(world, pos, state, entity, originalColor, partialTicks);
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public Boolean isEntityInsideMaterial(IBlockAccess world, BlockPos blockpos, IBlockState iblockstate, Entity entity, double yToTest, Material materialIn, boolean testingHead) {
        if (BPPlugin.FLUIDLOGGED) return super.isEntityInsideMaterial(world, blockpos, iblockstate, entity, yToTest, materialIn, testingHead);
        return materialIn == Material.WATER;
    }
}
