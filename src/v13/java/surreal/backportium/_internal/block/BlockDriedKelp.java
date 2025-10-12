package surreal.backportium._internal.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.Nullable;

public class BlockDriedKelp extends Block {

    public BlockDriedKelp() {
        super(Material.PLANTS, MapColor.GREEN);
        this.setSoundType(SoundType.PLANT);
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setHardness(0.5F).setResistance(2.5F);
    }

    @Override
    public int getFlammability(@Nullable IBlockAccess world, @Nullable BlockPos pos, @Nullable EnumFacing face) {
        return 30;
    }

    @Override
    public int getFireSpreadSpeed(@Nullable IBlockAccess world, @Nullable BlockPos pos, @Nullable EnumFacing face) {
        return 20;
    }
}
