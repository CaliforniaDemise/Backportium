package surreal.backportium.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public abstract class BlockTile<T extends TileEntity> extends BlockContainer implements ITileEntityProvider {

    public BlockTile(Material material) {
        super(material);
    }

    public BlockTile(Material blockMaterial, MapColor blockMapColor) {
        super(blockMaterial, blockMapColor);
    }

    protected void setForce(float force) {
        this.setHardness(force).setResistance(force);
    }

    @SuppressWarnings("unchecked")
    protected T getTile(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return (T) tile;
    }
}
