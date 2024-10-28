package surreal.backportium.item.v1_13;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import surreal.backportium.util.RandomHelper;

import javax.annotation.Nonnull;

public class ItemBlockDebarkedLog extends ItemBlock {

    private final Block origLog;

    public ItemBlockDebarkedLog(Block block, Block origLog) {
        super(block);
        this.origLog = origLog;
        this.setTranslationKey(origLog.getTranslationKey());
    }

    @Override
    public boolean getHasSubtypes() {
        return this.getOrigItem().getHasSubtypes();
    }

    @Override
    public int getMetadata(int damage) {
        return this.getOrigItem().getMetadata(damage);
    }

    @Nonnull
    @Override
    public String getTranslationKey(@Nonnull ItemStack stack) {
        return this.getOrigItem().getTranslationKey(stack);
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        return I18n.translateToLocalFormatted("tile.backportium.debarked_log", super.getItemStackDisplayName(stack));
    }

    private Item getOrigItem() {
        return RandomHelper.getItemFromBlock(this.origLog);
    }
}
