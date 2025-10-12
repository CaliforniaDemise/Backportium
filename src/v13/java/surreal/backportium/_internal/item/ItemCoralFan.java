package surreal.backportium._internal.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemCoralFan extends ItemBlock {

    public ItemCoralFan(Block block) {
        super(block);
    }

    @Override
    public int getMetadata(int damage) {
        return 1;
    }
}
