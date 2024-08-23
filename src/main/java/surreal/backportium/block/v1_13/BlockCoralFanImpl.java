package surreal.backportium.block.v1_13;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import surreal.backportium.api.enums.CoralType;
import surreal.backportium.block.plant.coral.BlockCoralFan;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
public class BlockCoralFanImpl extends BlockCoralFan {

    protected final CoralType type;

    public BlockCoralFanImpl(Material material, CoralType type) {
        this(material, material.getMaterialMapColor(), type);
    }

    public BlockCoralFanImpl(Material material, MapColor mapColor, CoralType type) {
        super(material, mapColor);
        this.type = type;
        this.setDefaultState(getDefaultState().withProperty(FACING, EnumFacing.UP).withProperty(ALIVE, true));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 1));
        items.add(new ItemStack(this, 1, 9));
    }

    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        int facing = state.getValue(FACING).getIndex();
        return facing | (state.getValue(ALIVE) ? 0 : 8);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 6)).withProperty(ALIVE, (meta & 8) != 8);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ALIVE);
    }
}
