package surreal.backportium.block.v13;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
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
import org.jetbrains.annotations.NotNull;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.block.plant.BlockPlantWater;
import surreal.backportium.util.RandomHelper;
import surreal.backportium.util.WorldHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockKelp extends BlockPlantWater {

    protected static final PropertyEnum<BlockDoublePlant.EnumBlockHalf> HALF = BlockDoublePlant.HALF;

    public static final AxisAlignedBB TOP_AABB = new AxisAlignedBB(0F, 0F, 0F, 1F, 0.6F, 1F);

    public BlockKelp() {
        super(Material.GRASS);
        this.setDefaultState(getDefaultState().withProperty(HALF, BlockDoublePlant.EnumBlockHalf.UPPER));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setTickRandomly(true);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return RandomHelper.getItemFromBlock(this);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER ? TOP_AABB : FULL_BLOCK_AABB;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
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
    @ParametersAreNonnullByDefault
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        worldIn.destroyBlock(pos, true);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        BlockPos downPos = pos.down();
        IBlockState stateDown = worldIn.getBlockState(downPos);
        return (stateDown.isSideSolid(worldIn, downPos, EnumFacing.UP) || stateDown.getBlock() == this) && WorldHelper.inWater(worldIn, pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        boolean waterCheck = WorldHelper.inWater(worldIn, pos);
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

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(HALF, meta == 0 ? BlockDoublePlant.EnumBlockHalf.UPPER : BlockDoublePlant.EnumBlockHalf.LOWER);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HALF);
    }

    @Override
    public boolean canGrow(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state, boolean isClient) {
        return state.getValue(HALF) == BlockDoublePlant.EnumBlockHalf.UPPER && worldIn.getBlockState(pos.up()).getMaterial() == Material.WATER;
    }

    @Override
    public void grow(@NotNull World worldIn, @NotNull Random rand, @NotNull BlockPos pos, @NotNull IBlockState state) {
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
        return new ItemStack(RandomHelper.getItemFromBlock(this));
    }
}
