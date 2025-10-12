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
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockClustered extends Block {

    public static final PropertyInteger AMOUNT = PropertyInteger.create("amount", 0, 3);

    public BlockClustered(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setDefaultState(getDefaultState().withProperty(AMOUNT, 0));
    }

    public BlockClustered(Material material) {
        this(material, material.getMaterialMapColor());
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    public void neighborChanged(@NotNull IBlockState state, @NotNull World worldIn, BlockPos pos, @NotNull Block blockIn, BlockPos fromPos) {
        if (fromPos.getY() < pos.getY() && !worldIn.isSideSolid(fromPos, EnumFacing.UP)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, @NotNull Random random) {
        return state.getValue(AMOUNT) + 1;
    }

    @NotNull
    @Override
    public BlockFaceShape getBlockFaceShape(@NotNull IBlockAccess worldIn, @NotNull IBlockState state, @NotNull BlockPos pos, @NotNull EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @NotNull
    @Override
    public EnumPushReaction getPushReaction(@NotNull IBlockState state) {
        return EnumPushReaction.DESTROY;
    }

    @Override
    public boolean isOpaqueCube(@NotNull IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(@NotNull IBlockState state) {
        return false;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AMOUNT);
    }

    @NotNull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AMOUNT, meta);
    }

    @NotNull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AMOUNT);
    }
}
