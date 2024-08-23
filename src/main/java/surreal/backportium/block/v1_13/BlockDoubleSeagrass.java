package surreal.backportium.block.v1_13;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.block.plant.BlockPlantDoubleWater;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class BlockDoubleSeagrass extends BlockPlantDoubleWater {

    public BlockDoubleSeagrass(Material material) {
        super(material);
    }

    public BlockDoubleSeagrass(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(ModBlocks.SEAGRASS, 2));
    }
}
