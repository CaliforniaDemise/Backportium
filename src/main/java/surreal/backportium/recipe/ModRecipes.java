package surreal.backportium.recipe;

import net.minecraft.block.BlockPlanks;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistry;
import surreal.backportium.Tags;
import surreal.backportium.potion.ModPotions;
import surreal.backportium.util.RandomHelper;

import java.util.ArrayList;
import java.util.List;

import static surreal.backportium.block.ModBlocks.*;
import static surreal.backportium.item.ModItems.*;

import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.*;

public class ModRecipes {

    private static final List<IRecipe> RECIPES = new ArrayList<>();

    public static <T extends IRecipe> T register(T recipe, String name) {
        recipe.setRegistryName(new ResourceLocation(Tags.MOD_ID, name));
        RECIPES.add(recipe);
        return recipe;
    }

    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        IForgeRegistry<IRecipe> registry = event.getRegistry();
        RECIPES.forEach(registry::register);
        registerRecipes();
    }

    private static void registerRecipes() {
        {
            ItemStack packedIce = new ItemStack(PACKED_ICE);
            packingRecipe(new ItemStack(BLUE_ICE), packedIce);
            packingRecipe(packedIce, new ItemStack(ICE));
        }

        addShaped(new ItemStack(CONDUIT), "AAA", "ABA", "AAA", 'A', NAUTILUS_SHELL, 'B', SEA_HEART);
        addShaped(new ItemStack(TURTLE_HELMET), "AAA", "A A", 'A', SCUTE);

        {
            ItemStack driedKelp = new ItemStack(DRIED_KELP);
            packingUnpackingRecipe(new ItemStack(DRIED_KELP_BLOCK), new ItemStack(DRIED_KELP));

            GameRegistry.addSmelting(RandomHelper.getItemFromBlock(KELP), driedKelp, 0.1F);
        }

        {
            for (int i = 1; i < 6; i++) {
                BlockPlanks.EnumType type = BlockPlanks.EnumType.byMetadata(i);
                Ingredient plank = Ingredient.fromStacks(new ItemStack(PLANKS, 1, i));

                addShaped(new ItemStack(getWoodTrapdoor(type)), "AAA", "AAA", 'A', plank);
                addShaped(new ItemStack(getWoodPlate(type)), "AA", 'A', plank);
                addShaped(new ItemStack(getWoodButton(type)), "A", 'A', plank);
            }
        }

        {
            Ingredient redstoneIng = Ingredient.fromItem(REDSTONE);

            PotionHelper.addMix(PotionTypes.AWKWARD, PHANTOM_MEMBRANE, ModPotions.SLOW_FALLING_TYPE);
            PotionHelper.addMix(ModPotions.SLOW_FALLING_TYPE, redstoneIng, ModPotions.LONG_SLOW_FALLING_TYPE);

            // Normal turtle master implementation
            PotionHelper.addMix(ModPotions.TURTLE_MASTER_TYPE, redstoneIng, ModPotions.LONG_TURTLE_MASTER_TYPE);
            PotionHelper.addMix(ModPotions.STRONG_TURTLE_MASTER_TYPE, GLOWSTONE_DUST, ModPotions.STRONG_TURTLE_MASTER_TYPE);
        }
    }

    private static void addShaped(ItemStack output, Object... params) {
        addShaped(output.getItem().getRegistryName(), output, params);
    }

    private static void addShaped(ResourceLocation location, ItemStack output, Object... params) {
        GameRegistry.addShapedRecipe(location, null, output, params);
    }

    private static void addShapeless(ItemStack output, Object... inputs) {
        addShapeless(output.getItem().getRegistryName(), output, inputs);
    }

    private static void addShapeless(ResourceLocation location, ItemStack output, Object... inputs) {
        GameRegistry.addShapelessRecipe(location, null, output, toIngredient(inputs));
    }

    private static void packingRecipe(ItemStack block, ItemStack item) {
        addShaped(block, "AAA", "AAA", "AAA", 'A', item);
    }

    private static void unpackingRecipe(ItemStack block, ItemStack item) {
        addShapeless(item, block);
    }

    private static void packingUnpackingRecipe(ItemStack block, ItemStack item) {
        packingRecipe(block, item);
        ItemStack output = item.copy();
        output.setCount(9);
        unpackingRecipe(block, output);
    }

    private static Ingredient[] toIngredient(Object... inputs) {
        Ingredient[] out = new Ingredient[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            out[i] = toIngredient(inputs[i]);
        }
        return out;
    }

    private static Ingredient toIngredient(Object input) {
        if (input == ItemStack.EMPTY) return Ingredient.EMPTY;
        if (input instanceof Item[]) return Ingredient.fromItems((Item[]) input);
        if (input instanceof Item) return Ingredient.fromItem((Item) input);
        if (input instanceof ItemStack[]) return Ingredient.fromStacks((ItemStack[]) input);
        if (input instanceof ItemStack) return Ingredient.fromStacks((ItemStack) input);
        if (input instanceof String) return new OreIngredient((String) input);
        return Ingredient.EMPTY;
    }
}
