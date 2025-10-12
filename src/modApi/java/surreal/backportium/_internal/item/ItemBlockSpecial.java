package surreal.backportium._internal.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemBlockSpecial extends net.minecraft.item.ItemBlockSpecial {

    public ItemBlockSpecial(Block block) {
        super(block);
        this.setCreativeTab(block.getCreativeTab());
    }

    @NotNull
    @Override
    public String getTranslationKey(@NotNull ItemStack stack) {
        return this.getBlock().getTranslationKey();
    }
}
