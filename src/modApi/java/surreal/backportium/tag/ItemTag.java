package surreal.backportium.tag;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

public class ItemTag extends Tag<ItemStack> {

    public ItemTag() {
        super(ITEM_STRATEGY);
    }

    @Override
    public void add(String name, ItemStack object) {
        if (object.getMetadata() == OreDictionary.WILDCARD_VALUE) {
            this.add(name, object.getItem());
        }
        else super.add(name, object);
    }

    public void add(String name, Item item) {
        NonNullList<ItemStack> list = NonNullList.create();
        item.getSubItems(CreativeTabs.SEARCH, list);
        list.forEach(s -> this.add(name, s));
    }

    public void remove(String name, Item item) {
        NonNullList<ItemStack> list = NonNullList.create();
        item.getSubItems(CreativeTabs.SEARCH, list);
        list.forEach(s -> this.remove(name, s));
    }

    private static final Hash.Strategy<ItemStack> ITEM_STRATEGY = new Hash.Strategy<ItemStack>() {
        @Override
        public int hashCode(ItemStack stack) {
            if (stack == null || stack.isEmpty()) return 0;
            return Objects.hashCode(stack.getItem());
        }

        @Override
        public boolean equals(ItemStack a, ItemStack b) {
            if (a == null) return b == null;
            if (b == null) return false;
            return ItemStack.areItemStacksEqual(a, b);
        }
    };
}
