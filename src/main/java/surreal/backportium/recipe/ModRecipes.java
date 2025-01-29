package surreal.backportium.recipe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.commons.lang3.tuple.Pair;
import surreal.backportium.Tags;
import surreal.backportium.core.BPHooks;
import surreal.backportium.core.util.LogSystem;
import surreal.backportium.potion.ModPotions;
import surreal.backportium.util.RandomHelper;
import surreal.backportium.util.Tuple;

import java.util.*;

import static net.minecraft.init.Blocks.*;
import static net.minecraft.init.Items.GLOWSTONE_DUST;
import static net.minecraft.init.Items.REDSTONE;
import static surreal.backportium.block.ModBlocks.*;
import static surreal.backportium.item.ModItems.*;

public class ModRecipes {

    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        registerRecipes();
    }

    public static void registerLateRecipes(RegistryEvent.Register<IRecipe> event) {
        registerLateRecipes();
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

                Block trapdoor = getWoodTrapdoor(type);
                Block plate = getWoodPlate(type);
                Block button = getWoodButton(type);

                addShaped(trapdoor.getRegistryName(), new ItemStack(trapdoor), "AAA", "AAA", 'A', plank);
                addShaped(plate.getRegistryName(), new ItemStack(plate), "AA", 'A', plank);
                addShaped(button.getRegistryName(), new ItemStack(button), "A", 'A', plank);
            }

            BlockPlanks.EnumType type = BlockPlanks.EnumType.OAK;
            OreIngredientPredicate ingredient = new OreIngredientPredicate("plankWood", s -> s.getMetadata() == 0 || !Objects.requireNonNull(s.getItem().getRegistryName()).getNamespace().equals("minecraft"));

            Block trapdoor = getWoodTrapdoor(type);
            Block plate = getWoodPlate(type);
            Block button = getWoodButton(type);

            addShaped(trapdoor.getRegistryName(), new ItemStack(trapdoor), "AAA", "AAA", 'A', ingredient);
            addShaped(plate.getRegistryName(), new ItemStack(plate), "AA", 'A', ingredient);
            addShaped(button.getRegistryName(), new ItemStack(button), "A", 'A', ingredient);
        }

        {
            Ingredient redstoneIng = Ingredient.fromItem(REDSTONE);

            PotionHelper.addMix(PotionTypes.AWKWARD, PHANTOM_MEMBRANE, ModPotions.SLOW_FALLING_TYPE);
            PotionHelper.addMix(ModPotions.SLOW_FALLING_TYPE, redstoneIng, ModPotions.LONG_SLOW_FALLING_TYPE);

            // Normal turtle master implementation
            PotionHelper.addMix(ModPotions.TURTLE_MASTER_TYPE, redstoneIng, ModPotions.LONG_TURTLE_MASTER_TYPE);
            PotionHelper.addMix(ModPotions.TURTLE_MASTER_TYPE, GLOWSTONE_DUST, ModPotions.STRONG_TURTLE_MASTER_TYPE);
        }

        {
//            GameRegistry.addShapedRecipe(new ResourceLocation("purple_shulker_box"), new ResourceLocation("shulker_box"), new ItemStack(SHULKER_BOX), "A", "B", "A", 'A', new ItemStack(Items.SHULKER_SHELL), 'B', new ItemStack(CHEST));
        }
    }

    public static void registerLateRecipes() {
        {
            List<Pair<ItemStack, ItemStack>> furnaceRecipes = new ArrayList<>();
            List<Pair<ItemStack, ItemStack>> craftingRecipes = new ArrayList<>();
            for (Map.Entry<ItemStack, ItemStack> entry : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
                Block keyBlock = Block.getBlockFromItem(entry.getKey().getItem());
                if (BPHooks.Logs$isOriginal(keyBlock)) {
                    LogSystem system = LogSystem.INSTANCE;
                    Tuple<Block, Block, Block> tuple = system.getLogs(keyBlock);
                    Block stripped = tuple.getFirst();
                    Block bark = tuple.getSecond();
                    Block strippedBark = tuple.getThird();
                    if (stripped != null) furnaceRecipes.add(Pair.of(new ItemStack(stripped, 1, entry.getKey().getMetadata()), entry.getValue()));
                    if (bark != null) furnaceRecipes.add(Pair.of(new ItemStack(bark, 1, entry.getKey().getMetadata()), entry.getValue()));
                    if (strippedBark != null) furnaceRecipes.add(Pair.of(new ItemStack(strippedBark, 1, entry.getKey().getMetadata()), entry.getValue()));
                }
            }
            for (IRecipe recipe : ForgeRegistries.RECIPES) {
                if (recipe.canFit(1, 1) && !recipe.getIngredients().isEmpty()) {
                    Ingredient ingredient = recipe.getIngredients().get(0);
                    if (!(ingredient instanceof OreIngredient)) {
                        for (ItemStack s : ingredient.getMatchingStacks()) {
                            if (!(s.getItem() instanceof ItemBlock)) continue;
                            Block block = Block.getBlockFromItem(s.getItem());
                            if (!BPHooks.Logs$isOriginal(block)) continue;
                            LogSystem system = LogSystem.INSTANCE;
                            Tuple<Block, Block, Block> tuple = system.getLogs(block);
                            Block stripped = tuple.getFirst();
                            Block bark = tuple.getSecond();
                            Block strippedBark = tuple.getThird();
                            if (stripped != null) craftingRecipes.add(Pair.of(new ItemStack(stripped, 1, s.getMetadata()), recipe.getRecipeOutput()));
                            if (bark != null) craftingRecipes.add(Pair.of(new ItemStack(bark, 1, s.getMetadata()), recipe.getRecipeOutput()));
                            if (strippedBark != null) craftingRecipes.add(Pair.of(new ItemStack(strippedBark, 1, s.getMetadata()), recipe.getRecipeOutput()));
                        }
                    }
                }
            }
            furnaceRecipes.forEach(p -> FurnaceRecipes.instance().addSmeltingRecipe(p.getKey(), p.getValue(), FurnaceRecipes.instance().getSmeltingExperience(p.getValue())));
            ResourceLocation group = new ResourceLocation("planks");
            craftingRecipes.forEach(p -> GameRegistry.addShapedRecipe(new ResourceLocation(Tags.MOD_ID, Objects.requireNonNull(p.getKey().getItem().getRegistryName()).getPath() + "_" + p.getKey().getMetadata()), group, p.getValue(), "A", 'A', p.getKey()));
            { // Barks
                LogSystem system = LogSystem.INSTANCE;
                ResourceLocation barkGroup = new ResourceLocation("bark");
                system.forEachBlock(origLog -> {
                    Tuple<Block, Block, Block> tuple = system.getLogs(origLog);
                    Block stripped = tuple.getFirst();
                    Block bark = tuple.getSecond();
                    Block strippedBark = tuple.getThird();
                    if (bark != null || (stripped != null && strippedBark != null)) {
                        NonNullList<ItemStack> list = NonNullList.create();
                        origLog.getSubBlocks(CreativeTabs.SEARCH, list);
                        list.forEach(origStack -> {
                            if (bark != null) GameRegistry.addShapedRecipe(new ResourceLocation(Tags.MOD_ID, Objects.requireNonNull(origLog.getRegistryName()).getPath() + "_bark"), barkGroup, new ItemStack(bark, 3, origStack.getMetadata()), "AA", "AA", 'A', origStack);
                            if ((stripped != null && strippedBark != null)) GameRegistry.addShapedRecipe(new ResourceLocation(Tags.MOD_ID, Objects.requireNonNull(origLog.getRegistryName()).getPath() + "_stripped_bark"), barkGroup, new ItemStack(strippedBark, 3, origStack.getMetadata()), "AA", "AA", 'A', new ItemStack(stripped, 1, origStack.getMetadata()));
                        });
                    }
                });
            }
        }
    }

    private static void addShaped(ItemStack output, Object... params) {
        addShaped(new ResourceLocation(Tags.MOD_ID, Objects.requireNonNull(output.getItem().getRegistryName()).getPath()), output, params);
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
