package surreal.backportium.block.v1_13;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.block.BlockTile;
import surreal.backportium.tile.v1_13.TileConduit;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
public class BlockConduit extends BlockTile<TileConduit> implements FluidLogged, ITileEntityProvider {

    protected static final AxisAlignedBB CONDUIT_AABB;

    public BlockConduit() {
        super(Material.ROCK, MapColor.ADOBE);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setForce(3.0F);
        this.setLightLevel(1F);
        this.useNeighborBrightness = true;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CONDUIT_AABB;
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
    @ParametersAreNonnullByDefault
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(@Nonnull IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileConduit();
    }

    public TileConduit getTile(IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return (TileConduit) te;
    }

    static {
        float aPix = 1F / 16;
        float pix5 = aPix * 5;
        float pix11 = 1F - pix5;
        CONDUIT_AABB = new AxisAlignedBB(pix5, pix5, pix5, pix11, pix11, pix11);
    }
}
