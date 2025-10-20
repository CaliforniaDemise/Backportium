package surreal.backportium.block;

import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.api.block.Loggable;
import surreal.backportium.block.state.DoublePlantContainer;
import surreal.backportium.init.ModSoundTypes;
import surreal.backportium.integration.ModList;
import surreal.backportium.util.FluidUtil;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public abstract class BlockDoublePlantWater extends BlockDoublePlant implements Loggable {

    private final Material material;
    private final MapColor mapColor;

    public BlockDoublePlantWater(Material material, MapColor mapColor) {
        this.material = !ModList.FLUIDLOGGED && material == Material.PLANTS ? Material.GRASS : material;
        this.mapColor = mapColor;
        this.setTickRandomly(false);
        this.setSoundType(ModSoundTypes.WET_GRASS);
    }

    public BlockDoublePlantWater(Material material) {
        this(material, material.getMaterialMapColor());
    }

    @NotNull
    @Override
    public Material getMaterial(@NotNull IBlockState state) {
        return this.material;
    }

    @NotNull
    @Override
    public MapColor getMapColor(@NotNull IBlockState state, @NotNull IBlockAccess worldIn, @NotNull BlockPos pos) {
        return this.mapColor;
    }

    @Override
    public boolean canPlaceBlockAt(@NotNull World worldIn, @NotNull BlockPos pos) {
        IBlockState soil = worldIn.getBlockState(pos.down());
        boolean canPlace = soil.isSideSolid(worldIn, pos.down(), EnumFacing.UP);
        return canPlace && FluidUtil.getFluid(worldIn.getBlockState(pos)) == FluidRegistry.WATER && FluidUtil.getFluid(worldIn.getBlockState(pos.up())) == FluidRegistry.WATER;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, @NotNull BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock() != this;
    }

    @Override
    public int damageDropped(@NotNull IBlockState state) {
        return 0;
    }

    @NotNull
    @Override
    public Item getItemDropped(@NotNull IBlockState state, @NotNull Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public void onBlockHarvested(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityPlayer player) {
    }

    @Override
    public boolean canBlockStay(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        if (state.getBlock() != this) return super.canBlockStay(worldIn, pos, state);
        return super.canBlockStay(worldIn, pos, state) && FluidUtil.getFluid(worldIn, pos, worldIn.getBlockState(pos)) == FluidRegistry.WATER;
    }

    @Override
    public void updateTick(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random rand) {}

    @Override
    protected void checkAndDropBlock(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        if (!worldIn.isRemote && !this.canBlockStay(worldIn, pos, state)) {
            boolean isTop = state.getValue(HALF) == EnumBlockHalf.UPPER;
            if (isTop) {
                worldIn.setBlockState(pos, Loggable.cast(state.getBlock()).getLoggedState(worldIn, pos, state), 2);
            }
            else if (worldIn.getBlockState(pos.up()).getBlock() != this || !worldIn.isSideSolid(pos.down(), EnumFacing.UP)) {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockState(pos, Loggable.cast(state.getBlock()).getLoggedState(worldIn, pos, state), 3);
            }
        }
    }

    @Override
    public boolean canSustainPlant(IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos, @NotNull EnumFacing direction, @NotNull IPlantable plantable) {
        if (state.getBlock() == this) {
            return state.getValue(HALF) == EnumBlockHalf.LOWER;
        }
        return super.canSustainPlant(state, world, pos, direction, plantable);
    }

    @NotNull
    @Override
    public net.minecraftforge.common.EnumPlantType getPlantType(@NotNull IBlockAccess world, @NotNull BlockPos pos) {
        return net.minecraftforge.common.EnumPlantType.Plains;
    }

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

    @NotNull
    @Override
    protected BlockStateContainer createBlockState() {
        return new DoublePlantContainer(this, BlockDoublePlant.HALF);
    }

    @Override
    public abstract boolean isShearable(@NotNull ItemStack item, @NotNull IBlockAccess world, @NotNull BlockPos pos);

    @NotNull
    @Override
    public abstract List<ItemStack> onSheared(@NotNull ItemStack item, @NotNull IBlockAccess world, @NotNull BlockPos pos, int fortune);

    @Override
    public void placeAt(@NotNull World worldIn, @NotNull BlockPos lowerPos, @Nullable EnumPlantType variant, int flags) {
        worldIn.setBlockState(lowerPos, this.getDefaultState().withProperty(HALF, EnumBlockHalf.LOWER), flags);
        worldIn.setBlockState(lowerPos.up(), this.getDefaultState().withProperty(HALF, EnumBlockHalf.UPPER), flags);
    }
}
