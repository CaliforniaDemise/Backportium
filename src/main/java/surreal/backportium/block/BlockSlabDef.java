package surreal.backportium.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import surreal.backportium.util.RandomHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
public abstract class BlockSlabDef extends BlockSlab {

    private Block doubleSlab = Blocks.AIR;

    public BlockSlabDef(Material materialIn) {
        this(materialIn, materialIn.getMaterialMapColor());
    }

    public BlockSlabDef(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.useNeighborBrightness = true;
    }

    public BlockSlabDef getDoubleSlab() {
        assert doubleSlab instanceof BlockSlabDef;
        return (BlockSlabDef) doubleSlab;
    }

    public BlockSlabDef setDoubleSlab(Block doubleSlab) {
        assert doubleSlab instanceof BlockSlab;
        this.doubleSlab = doubleSlab;
        return this;
    }

    @Override
    public int getMetaFromState(@Nonnull IBlockState state) {
        int variant = RandomHelper.getMetaFromVariant(state, getVariantProperty());
        if (!isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP) {
            variant |= 8;
        }
        return variant;
    }

    @Nonnull
    @Override
    public abstract IBlockState getStateFromMeta(int meta);

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return isDouble() ? new BlockStateContainer(this, getVariantProperty()) : new BlockStateContainer(this, HALF, getVariantProperty());
    }

    @Override
    public int damageDropped(@Nonnull IBlockState state) {
        return RandomHelper.getMetaFromVariant(state, getVariantProperty());
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this, 1, RandomHelper.getMetaFromVariant(state, getVariantProperty()));
    }
}
