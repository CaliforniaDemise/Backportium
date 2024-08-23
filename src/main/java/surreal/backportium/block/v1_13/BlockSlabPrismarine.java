package surreal.backportium.block.v1_13;

import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import surreal.backportium.block.BlockSlabDef;

import javax.annotation.Nonnull;

public class BlockSlabPrismarine extends BlockSlabDef {

    protected static final PropertyEnum<BlockPrismarine.EnumType> VARIANT = BlockPrismarine.VARIANT;

    public BlockSlabPrismarine(Material materialIn) {
        this(materialIn, materialIn.getMaterialMapColor());
    }

    public BlockSlabPrismarine(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Nonnull
    @Override
    public String getTranslationKey(int meta) {
        BlockPrismarine.EnumType type = BlockPrismarine.EnumType.byMetadata(meta);
        return "prismarine." + type.getTranslationKey() + ".slab";
    }

    @Nonnull
    @Override
    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    @Nonnull
    @Override
    public Comparable<?> getTypeForItem(@Nonnull ItemStack stack) {
        return BlockPrismarine.EnumType.byMetadata(stack.getMetadata());
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = getDefaultState().withProperty(VARIANT, BlockPrismarine.EnumType.byMetadata(meta & 7));
        if (!isDouble()) {
            state = state.withProperty(HALF, (meta & 8) == 8 ? EnumBlockHalf.TOP : EnumBlockHalf.BOTTOM);
        }
        return state;
    }

    public static class Double extends BlockSlabPrismarine {

        public Double(Material materialIn) {
            this(materialIn, materialIn.getMaterialMapColor());
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
