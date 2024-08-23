package surreal.backportium.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemBlockBurnable extends ItemBlock {

    private final int burnTime;

    public ItemBlockBurnable(Block block, int burnTime) {
        super(block);
        this.burnTime = burnTime;
    }

    @Override
    public int getItemBurnTime(@Nonnull ItemStack itemStack) {
        return burnTime;
    }
}
