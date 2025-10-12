package surreal.backportium._internal.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemBlockDriedKelp extends ItemBlock {

    public ItemBlockDriedKelp(Block block) {
        super(block);
    }

    @Override
    public int getItemBurnTime(@NotNull ItemStack itemStack) {
        return 4000;
    }
}
