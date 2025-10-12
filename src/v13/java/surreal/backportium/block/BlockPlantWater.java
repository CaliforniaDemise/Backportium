package surreal.backportium.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium._internal.world.LoggedAccess;
import surreal.backportium.api.block.Loggable;
import surreal.backportium.init.ModSoundTypes;
import surreal.backportium.integration.ModList;
import surreal.backportium.util.FluidUtil;

public class BlockPlantWater extends BlockPlant implements Loggable {

    public BlockPlantWater(Material material, MapColor mapColor, Block doublePlant) {
        super(!ModList.FLUIDLOGGED && material == Material.PLANTS ? Material.GRASS : material, mapColor, doublePlant);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setSoundType(ModSoundTypes.WET_GRASS);
    }

    public BlockPlantWater(Material material, MapColor mapColor) {
        this(material, mapColor, Blocks.AIR);
    }

    public BlockPlantWater(Material material) {
        this(material, material.getMaterialMapColor());
    }

    @Override
    public boolean canGrow(@NotNull World worldIn, @NotNull BlockPos pos, @Nullable IBlockState state, boolean isClient) {
        return this.doublePlant != null && FluidUtil.getFluid(worldIn.getBlockState(pos.up())) == FluidRegistry.WATER;
    }

    @Override
    public boolean canPlaceBlockAt(@NotNull World worldIn, @NotNull BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && FluidUtil.getFluid(worldIn, pos, worldIn.getBlockState(pos)) == FluidRegistry.WATER && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    public boolean canBlockStay(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        return super.canBlockStay(worldIn, pos, state) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    public boolean canSustainPlant(IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull EnumFacing direction, @NotNull IPlantable plantable) {
        return state.isSideSolid(world, pos, EnumFacing.UP);
    }

    @Override
    protected boolean canSustainBush(@NotNull IBlockState state) {
        return true;
    }

    @NotNull
    @Override
    public EnumPlantType getPlantType(@NotNull IBlockAccess world, @NotNull BlockPos pos) {
        return EnumPlantType.Plains;
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    public Vec3d getFogColor(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Entity entity, @NotNull Vec3d originalColor, float partialTicks) {
        if (ModList.FLUIDLOGGED) return super.getFogColor(world, pos, state, entity, originalColor, partialTicks);
        return this.getLoggedState(world, pos, state).getBlock().getFogColor(world, pos, state, entity, originalColor, partialTicks);
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }
}
