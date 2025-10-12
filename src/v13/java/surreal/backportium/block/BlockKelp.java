package surreal.backportium.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium._internal.client.renderer.model.ModelProvider;
import surreal.backportium.item.ItemBlockKelp;
import surreal.backportium.util.BlockUtil;
import surreal.backportium.util.FluidUtil;

import java.util.Objects;
import java.util.Random;

/**
 * If you're thinking about adding a kelp-like block, you should
 * consider using {@link ItemBlockKelp} for its item.
 */
@SuppressWarnings("deprecation")
public class BlockKelp extends BlockPlantWater implements ModelProvider {

    protected static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = BlockDoublePlant.HALF;

    public static final AxisAlignedBB TOP_AABB = new AxisAlignedBB(0F, 0F, 0F, 1F, 0.6F, 1F);

    public BlockKelp(Material material) {
        super(material, MapColor.WATER);
        this.setDefaultState(getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setTickRandomly(true);
    }

    @NotNull
    @Override
    public Item getItemDropped(@Nullable IBlockState state, @Nullable Random rand, int fortune) {
        return BlockUtil.getItemFromBlock(this);
    }

    @NotNull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, @NotNull IBlockAccess source, @NotNull BlockPos pos) {
        return state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER ? TOP_AABB : FULL_BLOCK_AABB;
    }

    @Override
    public void neighborChanged(@NotNull IBlockState state, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos) {
        if (fromPos.getY() > pos.getY()) {
            IBlockState upperState = worldIn.getBlockState(fromPos);
            if (upperState.getBlock() == this) {
                worldIn.setBlockState(pos, state.withProperty(HALF, BlockDoublePlant.EnumBlockHalf.LOWER), 3);
            } else worldIn.setBlockState(pos, state.withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 3);
        } else if (fromPos.getY() < pos.getY() && worldIn.getBlockState(fromPos).getBlock() != this) {
            worldIn.scheduleBlockUpdate(pos, this, 2, 10);
        }
    }

    @Override
    public void updateTick(World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull Random rand) {
        worldIn.destroyBlock(pos, true);
    }

    @Override
    public boolean canPlaceBlockAt(@NotNull World worldIn, @NotNull BlockPos pos) {
        BlockPos downPos = pos.down();
        IBlockState stateDown = worldIn.getBlockState(downPos);
        return (stateDown.isSideSolid(worldIn, downPos, EnumFacing.UP) || stateDown.getBlock() == this) && FluidUtil.getFluid(worldIn.getBlockState(pos)) == FluidRegistry.WATER;
    }

    @Override
    public boolean canBlockStay(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        boolean waterCheck = FluidUtil.getFluid(worldIn.getBlockState(pos)) == FluidRegistry.WATER;
        BlockPos downPos = pos.down();
        IBlockState soil = worldIn.getBlockState(downPos);
        if (state.getBlock() == this) {
            return waterCheck && soil.getBlock().canSustainPlant(soil, worldIn, downPos, EnumFacing.UP, this);
        }
        return (this.canSustainBush(soil) || (soil.getBlock() == this && soil.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.LOWER)) && waterCheck;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER ? 0 : 1;
    }

    @NotNull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(HALF, meta == 0 ? BlockDoublePlant.EnumBlockHalf.UPPER : BlockDoublePlant.EnumBlockHalf.LOWER);
    }

    @NotNull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF);
    }

    @Override
    public boolean canGrow(@NotNull World worldIn, BlockPos pos, @Nullable IBlockState state, boolean isClient) {
        if (worldIn.getBlockState(pos.up()).getMaterial() == Material.WATER) {
            return state == null || state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER;
        }
        else return false;
    }

    @Override
    public void grow(@NotNull World worldIn, @Nullable Random rand, @NotNull BlockPos pos, @NotNull IBlockState state) {
        worldIn.setBlockState(pos.up(), state);
    }

    @Override
    public void randomTick(@NotNull World worldIn, @NotNull BlockPos pos, IBlockState state, @NotNull Random random) {
        if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER && random.nextInt(100 * 25) < 14) {
            BlockPos upPos = pos.up();
            if (state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER && worldIn.getBlockState(upPos).getMaterial() == Material.WATER) worldIn.setBlockState(upPos, state);
        }
    }

    @NotNull
    @Override
    public ItemStack getPickBlock(@NotNull IBlockState state, @NotNull RayTraceResult target, @NotNull World world, @NotNull BlockPos pos, @NotNull EntityPlayer player) {
        return new ItemStack(BlockUtil.getItemFromBlock(this));
    }

    @Override
    public void registerModels() {
        Item item = BlockUtil.getItemFromBlock(this);
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), "inventory"));
    }
}
