package surreal.backportium.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import surreal.backportium.api.block.FluidLogged;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

// Sea Pickles and Turtle Eggs
@SuppressWarnings("deprecation")
public class BlockClustered extends Block implements FluidLogged {

    public static final PropertyInteger AMOUNT = PropertyInteger.create("amount", 0, 3);

    public BlockClustered(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setDefaultState(getDefaultState().withProperty(AMOUNT, 0));
    }

    public BlockClustered(Material material) {
        this(material, material.getMaterialMapColor());
    }

    public void setForce(float force) {
        this.setHardness(force).setResistance(force);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (fromPos.getY() < pos.getY() && !worldIn.isSideSolid(fromPos, EnumFacing.UP)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    //    @Override
//    @ParametersAreNonnullByDefault
//    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
//        if (!(pos.getY() > fromPos.getY()) && !worldIn.isSideSolid(fromPos, EnumFacing.UP)) {
//            worldIn.destroyBlock(pos, true);
//        }
//    }

    @Override
    @ParametersAreNonnullByDefault
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return state.getValue(AMOUNT) + 1;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Nonnull
    @Override
    public EnumPushReaction getPushReaction(@Nonnull IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Override
    public boolean isOpaqueCube(@Nonnull IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(@Nonnull IBlockState state) {
        return false;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AMOUNT);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AMOUNT, meta);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AMOUNT);
    }
}
