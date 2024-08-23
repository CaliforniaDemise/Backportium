package surreal.backportium.block.v1_13;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import surreal.backportium.api.enums.CoralType;
import surreal.backportium.block.plant.coral.BlockCoral;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
public class BlockCoralImpl extends BlockCoral {

    public BlockCoralImpl(Material material) {
        this(material, material.getMaterialMapColor());
    }

    public BlockCoralImpl(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setDefaultState(getDefaultState().withProperty(VARIANT, CoralType.TUBE).withProperty(ALIVE, true));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (int i = 0; i < 5; i++) {
            items.add(new ItemStack(this, 1, i));
            items.add(new ItemStack(this, 1, i | 8));
        }
    }

    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        int type = state.getValue(VARIANT).ordinal();
        return type | (!state.getValue(ALIVE) ? 8 : 0);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, CoralType.byMetadata(meta & 7)).withProperty(ALIVE, (meta & 8) != 8);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
       return new BlockStateContainer(this, VARIANT, ALIVE);
    }
}
