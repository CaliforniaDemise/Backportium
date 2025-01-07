package surreal.backportium.block.v13;

import net.minecraft.block.BlockSand;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import surreal.backportium.block.BlockSlabDef;

import javax.annotation.Nonnull;

public class BlockSlabSmoothSandstone extends BlockSlabDef {

    protected static final PropertyEnum<BlockSand.EnumType> VARIANT = BlockSand.VARIANT;

    public BlockSlabSmoothSandstone(Material materialIn) {
        this(materialIn, materialIn.getMaterialMapColor());
    }

    public BlockSlabSmoothSandstone(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setHardness(2F).setResistance(6F);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState().withProperty(VARIANT, BlockSand.EnumType.byMetadata(meta & 1));
        if (!isDouble()) {
            state = state.withProperty(HALF, (meta & 2) == 2 ? EnumBlockHalf.TOP : EnumBlockHalf.BOTTOM);
        }
        return state;
    }

    @Nonnull
    @Override
    public String getTranslationKey(int meta) {
        BlockSand.EnumType type = BlockSand.EnumType.byMetadata(meta);
        return "tile.backportium.smooth_sandstone_slab." + type.getTranslationKey();
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
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return BlockSand.EnumType.byMetadata(stack.getMetadata());
    }

    public static class Double extends BlockSlabSmoothSandstone {

        public Double(Material materialIn) {
            super(materialIn);
        }

        public Double(Material material, MapColor mapColor) {
            super(material, mapColor);
        }

        @Override
        public boolean isDouble() {
            return true;
        }
    }
}
