package surreal.backportium.block.v13;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import surreal.backportium.block.BlockDef;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class BlockBlueIce extends BlockDef {

    public BlockBlueIce() {
        super(Material.ICE);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setDefaultSlipperiness(0.989F);
        this.setSoundType(SoundType.GLASS);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return true;
    }
}
