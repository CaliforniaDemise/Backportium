package surreal.backportium.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import surreal.backportium.api.block.PropertyNormal;

import javax.annotation.Nonnull;

public class BlockSlabImpl extends BlockSlabDef {

    protected static final PropertyEnum<PropertyNormal.Variant> VARIANT = PropertyNormal.VARIANT;

    public BlockSlabImpl(Material materialIn) {
        this(materialIn, materialIn.getMaterialMapColor());
    }

    public BlockSlabImpl(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState().withProperty(VARIANT, PropertyNormal.Variant.DEFAULT);
        if (!isDouble()) {
            state = state.withProperty(HALF, (meta & 1) == 1 ? EnumBlockHalf.TOP : EnumBlockHalf.BOTTOM);
        }
        return state;
    }

    @Nonnull
    @Override
    public String getTranslationKey(int meta) {
        return getTranslationKey();
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Nonnull
    @Override
    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    @Nonnull
    @Override
    public Comparable<?> getTypeForItem(@Nonnull ItemStack stack) {
        return PropertyNormal.Variant.DEFAULT;
    }

    public static class Double extends BlockSlabImpl {

        public Double(Material materialIn) {
            super(materialIn);
        }

        @Override
        public boolean isDouble() {
            return true;
        }
    }
}
