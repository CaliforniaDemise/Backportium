package surreal.backportium.block;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;

public class BlockPressurePlateDef extends BlockPressurePlate {

    public BlockPressurePlateDef(Material materialIn, Sensitivity sensitivityIn) {
        super(materialIn, sensitivityIn);
    }

    public BlockPressurePlateDef setForce(float force) {
        this.setHardness(force).setResistance(force);
        return this;
    }
}
