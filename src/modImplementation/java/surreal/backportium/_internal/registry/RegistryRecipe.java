package surreal.backportium._internal.registry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreIngredient;
import surreal.backportium.util.BlockUtil;

public class RegistryRecipe extends Registry<IRecipe> implements Recipes {

    protected RegistryRecipe(RegistryManager manager) {
        super(manager);
    }

    public void shaped(String name, ItemStack output, Object... inputs) {
        shaped(new ResourceLocation(manager.getModId(), name), output, inputs);
    }

    public void shaped(ResourceLocation name, ItemStack output, Object... inputs) {
        GameRegistry.addShapedRecipe(name, null, output, inputs);
    }

    public void shapeless(ResourceLocation name, ItemStack output, Object... inputs) {
        GameRegistry.addShapelessRecipe(name, null, output, ingredients(inputs));
    }

    public void shapeless(String name, ItemStack output, Object... inputs) {
        shapeless(new ResourceLocation(manager.getModId()), output, inputs);
    }

    private static Ingredient[] ingredients(Object[] objects) {
        Ingredient[] ingredients = new Ingredient[objects.length];
        for (int i = 0; i < objects.length; i++) {
            ingredients[i] = ingredient(objects[i]);
            if (ingredients[i] == null) {
                throw new IllegalArgumentException("Cannot generate an ingredient from " + objects[i]);
            }
        }
        return ingredients;
    }

    private static Ingredient ingredient(Object object) {
        if (object instanceof Ingredient) return (Ingredient) object;
        if (object instanceof ItemStack) {
            ItemStack stack = (ItemStack) object;
            if (stack.isEmpty()) return Ingredient.EMPTY;
            return Ingredient.fromStacks(stack);
        }
        if (object instanceof Item) {
            Item item = (Item) object;
            if (item == Items.AIR) return Ingredient.EMPTY;
            return Ingredient.fromItem(item);
        }
        if (object instanceof Block) {
            Block block = (Block) object;
            if (block == Blocks.AIR) return Ingredient.EMPTY;
            Item item = BlockUtil.getItemFromBlock(block);
            if (item == Items.AIR) return Ingredient.EMPTY;
            return Ingredient.fromItem(item);
        }
        if (object instanceof String) {
            String string = (String) object;
            if (string.isEmpty()) return Ingredient.EMPTY;
            return new OreIngredient(string);
        }
        return null;
    }
}
