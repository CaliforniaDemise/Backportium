package surreal.backportium.block.plant;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.Loader;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.util.IntegrationHelper;
import surreal.backportium.util.WorldHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
public class BlockPlantWater extends BlockPlant implements FluidLogged {

    public BlockPlantWater(Material material, MapColor mapColor, Block doublePlant) {
        super(material, mapColor, doublePlant);
    }

    public BlockPlantWater(Material material, MapColor mapColor) {
        super(material, mapColor);
    }

    public BlockPlantWater(Material material) {
        super(material);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Nonnull
    @Override
    public Material getMaterial(@Nonnull IBlockState state) {
        return Loader.isModLoaded(IntegrationHelper.FLUIDLOGGED) ? super.getMaterial(state) : Material.WATER;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return super.canGrow(worldIn, pos, state, isClient) && WorldHelper.inWater(worldIn, pos.up());
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && WorldHelper.inWater(worldIn, pos) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        return super.canBlockStay(worldIn, pos, state) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
        return state.isSideSolid(world, pos, EnumFacing.UP);
    }

    @Override
    protected boolean canSustainBush(@Nonnull IBlockState state) {
        return true;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Beach;
    }
}
