package surreal.backportium._internal.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlockBlueIce extends Block {

    public BlockBlueIce() {
        super(Material.ICE);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setHardness(2.8F).setResistance(2.8F);
        this.setDefaultSlipperiness(0.989F);
    }

    @NotNull
    @Override
    public Item getItemDropped(@NotNull IBlockState state, @NotNull Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public boolean canSilkHarvest(@NotNull World world, @NotNull BlockPos pos, @NotNull IBlockState state, @NotNull EntityPlayer player) {
        return true;
    }
}
