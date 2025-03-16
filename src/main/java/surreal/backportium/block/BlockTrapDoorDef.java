package surreal.backportium.block;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockTrapDoorDef extends BlockTrapDoor {

    public BlockTrapDoorDef(Material materialIn) {
        super(materialIn);
        if (materialIn == Material.WOOD) this.setSoundType(SoundType.WOOD);
    }

    public BlockTrapDoorDef setForce(float force) {
        this.setHardness(force).setResistance(force);
        return this;
    }
}
