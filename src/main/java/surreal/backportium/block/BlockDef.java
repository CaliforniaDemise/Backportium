package surreal.backportium.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

import javax.annotation.Nonnull;

public class BlockDef extends Block {

    public BlockDef(Material material) {
        super(material);
    }

    public BlockDef(Material blockMaterial, MapColor blockMapColor) {
        super(blockMaterial, blockMapColor);
    }

    public void setForce(float force) {
        this.setHardness(force).setResistance(force);
    }

    @Nonnull
    @Override
    public Block setSoundType(@Nonnull SoundType sound) {
        return super.setSoundType(sound);
    }
}
