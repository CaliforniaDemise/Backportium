package surreal.backportium.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import surreal.backportium.Tags;
import surreal.backportium.api.block.DebarkedLog;
import surreal.backportium.api.client.model.ModelProvider;
import surreal.backportium.api.enums.ModArmorMaterials;
import surreal.backportium.api.item.OredictProvider;
import surreal.backportium.core.BPHooks;
import surreal.backportium.item.v1_13.ItemArmorTurtle;
import surreal.backportium.item.v1_13.ItemTrident;
import surreal.backportium.util.RandomHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModItems {

    private static final List<Item> ITEMS = new ArrayList<>();

    // 1.13
    public static final ItemFood DRIED_KELP = register((ItemFood) new ItemFoodDefault(1, 16, false).setCreativeTab(CreativeTabs.FOOD), "dried_kelp");
    // fish bucket
    public static final Item SEA_HEART = register("sea_heart").setCreativeTab(CreativeTabs.MATERIALS);
    public static final Item NAUTILUS_SHELL = register("nautilus_shell").setCreativeTab(CreativeTabs.MATERIALS);
    public static final Item PHANTOM_MEMBRANE = register("phantom_membrane").setCreativeTab(CreativeTabs.MATERIALS);
    public static final Item SCUTE = register("scute").setCreativeTab(CreativeTabs.MATERIALS);
    public static final Item TRIDENT = register(new ItemTrident(), "trident");
    public static final ItemArmorTurtle TURTLE_HELMET = register(new ItemArmorTurtle(ModArmorMaterials.TURTLE_SHELL, EntityEquipmentSlot.HEAD), "turtle_helmet");

    public static Item register(String name) {
        return register(new Item(), name);
    }

    public static Item registerOre(String name, String oreEntry) {
        return register(new ItemOreDict(oreEntry), name);
    }

    public static <T extends Item> T register(T item, String name) {
        return register(item, new ResourceLocation(Tags.MOD_ID, name));
    }

    public static <T extends Item> T register(T item, ResourceLocation name) {
        item.setRegistryName(name).setTranslationKey(name.getNamespace() + "." + name.getPath());
        ITEMS.add(item);
        return item;
    }

    public static void registerItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        ITEMS.forEach(registry::register);
    }

    public static void registerOres() {
        for (Item item : ITEMS) {
            if (item instanceof OredictProvider) {
                OredictProvider provider = (OredictProvider) item;
                provider.registerOreEntries();
            }
        }
        for (ItemBlock itemBlock : BPHooks.DEBARKED_LOG_ITEMS) {
            Block origLog = ((DebarkedLog) itemBlock.getBlock()).getOriginal();
            Item origItem = Item.getItemFromBlock(origLog);
            OreDictionary.registerOre("logStripped", itemBlock);
            int[] ids = OreDictionary.getOreIDs(new ItemStack(origItem, 1, OreDictionary.WILDCARD_VALUE));
            if (ids.length == 0) {
                ids = OreDictionary.getOreIDs(new ItemStack(origItem));
                if (ids.length == 0) {
                    OreDictionary.registerOre("logWood", new ItemStack(itemBlock, 1, OreDictionary.WILDCARD_VALUE));
                    continue;
                }
            }
            for (int i : ids) {
                OreDictionary.registerOre(OreDictionary.getOreName(i), new ItemStack(itemBlock, 1, OreDictionary.WILDCARD_VALUE));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        ITEMS.forEach(item -> {
            if (item instanceof ModelProvider) ((ModelProvider) item).registerModels();
            else {
                setModelLocation(item, 0, "inventory");
            }
        });
        for (ItemBlock itemBlock : BPHooks.DEBARKED_LOG_ITEMS) {
            NonNullList<ItemStack> list = NonNullList.create();
            itemBlock.getSubItems(CreativeTabs.SEARCH, list);
            for (ItemStack stack : list) {
                int metadata = stack.getMetadata();
                IBlockState state = itemBlock.getBlock().getStateFromMeta(metadata);
                IProperty<?> property = itemBlock.getBlock().getBlockState().getProperty("axis");
                if (property != null) {
                    if (property.getValueClass() == EnumFacing.Axis.class) {
                        state = state.withProperty((IProperty<EnumFacing.Axis>) property, EnumFacing.Axis.Y);
                    }
                    else if (property.getValueClass() == BlockLog.EnumAxis.class) {
                        state = state.withProperty((IProperty<BlockLog.EnumAxis>) property, BlockLog.EnumAxis.Y);
                    }
                }
                String variantIn = RandomHelper.getVariantFromState(state);
                ModelLoader.setCustomModelResourceLocation(itemBlock, metadata, new ModelResourceLocation(Objects.requireNonNull(itemBlock.getBlock().getRegistryName()), variantIn));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void setModelLocation(Item item, int metadata, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), variant));
    }
}
