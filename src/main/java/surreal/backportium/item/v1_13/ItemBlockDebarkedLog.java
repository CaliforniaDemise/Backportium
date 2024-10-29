package surreal.backportium.item.v1_13;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import surreal.backportium.util.RandomHelper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ItemBlockDebarkedLog extends ItemBlock {

    private final Block origLog;

    public ItemBlockDebarkedLog(Block block, Block origLog) {
        super(block);
        this.origLog = origLog;
        this.setTranslationKey(origLog.getTranslationKey());
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return this.getOrigItem().getCreativeTab();
    }

    @Nonnull
    @Override
    public CreativeTabs[] getCreativeTabs() {
        return this.getOrigItem().getCreativeTabs();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        NonNullList<ItemStack> ass = NonNullList.create();
        this.getOrigItem().getSubItems(tab, ass);
        ass.forEach(stack -> {
            ItemStack s = new ItemStack(this, 1, stack.getMetadata());
            s.setTagCompound(stack.getTagCompound());
            items.add(s);
        });
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
    public String getTranslationKey() {
        return this.getOrigItem().getTranslationKey();
    }

    @Nonnull
    @Override
    public String getTranslationKey(@Nonnull ItemStack stack) {
        return this.getOrigItem().getTranslationKey(stack);
    }

    private Item getOrigItem() {
        return RandomHelper.getItemFromBlock(this.origLog);
    }
}
