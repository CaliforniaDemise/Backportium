package surreal.backportium.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockSmooth extends Block {

    public BlockSmooth(MapColor blockMapColorIn) {
        super(Material.ROCK, blockMapColorIn);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setHardness(2.0F).setResistance(6.0F);
        this.setHarvestLevel("pickaxe", 0);
    }
}
