package surreal.backportium.block;

import net.minecraft.block.BlockButtonWood;
import net.minecraft.block.SoundType;

// TODO Remake the new button state handling. (face property)
public class BlockButtonWoodDef extends BlockButtonWood {

    public BlockButtonWoodDef() {
        this.setHardness(0.5F).setResistance(0.5F);
        this.setSoundType(SoundType.WOOD);
    }
}
