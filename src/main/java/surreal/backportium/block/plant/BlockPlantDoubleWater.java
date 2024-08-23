package surreal.backportium.block.plant;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.Loader;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.util.IntegrationHelper;
import surreal.backportium.util.WorldHelper;

import javax.annotation.Nonnull;
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
        return Loader.isModLoaded(IntegrationHelper.FLUIDLOGGED) ? super.getMaterial(state) : Material.WATER;
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
}
