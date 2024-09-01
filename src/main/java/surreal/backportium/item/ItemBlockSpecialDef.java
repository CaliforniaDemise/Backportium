package surreal.backportium.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockSpecial;

public abstract class ItemBlockSpecialDef extends ItemBlockSpecial {

    public ItemBlockSpecialDef(Block block) {
        super(block);
        this.setCreativeTab(block.getCreativeTab());
    }
}
