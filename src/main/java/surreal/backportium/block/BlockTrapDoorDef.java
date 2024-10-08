package surreal.backportium.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;

public class BlockTrapDoorDef extends BlockTrapDoor {

    public BlockTrapDoorDef(Material materialIn) {
        super(materialIn);
    }

    public BlockTrapDoorDef setForce(float force) {
        this.setHardness(force).setResistance(force);
        return this;
    }
}
