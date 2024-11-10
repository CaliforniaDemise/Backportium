package surreal.backportium.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public abstract class ItemBlockSpecialDef extends ItemBlockSpecial {

    public ItemBlockSpecialDef(Block block) {
        super(block);
        this.setCreativeTab(block.getCreativeTab());
    }

    @Nonnull
    @Override
    public String getTranslationKey(@Nonnull ItemStack stack) {
        return this.getBlock().getTranslationKey();
    }
}
