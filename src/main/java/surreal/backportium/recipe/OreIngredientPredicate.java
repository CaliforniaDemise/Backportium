package surreal.backportium.recipe;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class OreIngredientPredicate extends Ingredient {

    protected final String oreName;
    protected final int oreId;
    protected final Predicate<ItemStack> predicate;

    private ItemStack[] stacks;
    private IntList stacksPacked;

    public OreIngredientPredicate(String oreName, Predicate<ItemStack> predicate) {
        super(0);
        this.oreName = oreName;
        this.oreId = OreDictionary.getOreID(oreName);
        this.predicate = predicate;
    }

    @Nonnull
    @Override
    public ItemStack[] getMatchingStacks() {
        if (this.stacks == null) {
            NonNullList<ItemStack> stacks = OreDictionary.getOres(this.oreName);
            NonNullList<ItemStack> array = NonNullList.create();
            stacks.forEach(s -> {
                if (s.getMetadata() == OreDictionary.WILDCARD_VALUE) s.getItem().getSubItems(CreativeTabs.SEARCH, array);
                else array.add(s);
            });
            array.removeIf(s -> !this.predicate.test(s));
            this.stacks = array.toArray(new ItemStack[0]);
        }
        return this.stacks;
    }

    @Nonnull
    @Override
    public IntList getValidItemStacksPacked() {
        if (this.stacksPacked == null) {
            this.stacksPacked = new IntArrayList(this.stacks.length);
            for (ItemStack s : this.stacks) {
               if (s.getMetadata() == OreDictionary.WILDCARD_VALUE) {
                   NonNullList<ItemStack> l = NonNullList.create();
                   s.getItem().getSubItems(CreativeTabs.SEARCH, l);
                   for (ItemStack i : l) {
                       if (this.predicate.test(i)) {
                           this.stacksPacked.add(RecipeItemHelper.pack(i));
                       }
                   }
               }
               else if (this.predicate.test(s)) this.stacksPacked.add(RecipeItemHelper.pack(s));
            }
            this.stacksPacked.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return this.stacksPacked;
    }

    @Override
    public boolean apply(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (!this.predicate.test(stack)) return false;
        int[] ids = OreDictionary.getOreIDs(stack);
        for (int i : ids) {
            if (i == oreId) return true;
        }
        return false;
    }
}
