package surreal.backportium.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.client.item.ModelProvider;
import surreal.backportium.api.enums.ModArmorMaterials;
import surreal.backportium.core.util.LogSystem;
import surreal.backportium.item.v13.ItemArmorTurtle;
import surreal.backportium.item.v13.ItemTrident;
import surreal.backportium.util.Registry;

import java.util.Objects;

public class ModItems extends Registry<Item> {

    // 1.13
    @ObjectHolder("backportium:dried_kelp") public static final Item DRIED_KELP = null;
    // fish bucket
    @ObjectHolder("backportium:sea_heart") public static final Item SEA_HEART = null;
    @ObjectHolder("backportium:nautilus_shell") public static final Item NAUTILUS_SHELL = null;
    @ObjectHolder("backportium:phantom_membrane") public static final Item PHANTOM_MEMBRANE = null;
    @ObjectHolder("backportium:scute") public static final Item SCUTE = null;
    @ObjectHolder("backportium:trident") public static final Item TRIDENT = null;
    @ObjectHolder("backportium:turtle_helmet") public static final Item TURTLE_HELMET = null;

    public ModItems() {
        super(8);
    }

    public Item register(String name) {
        return this.register(new Item(), name);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        this.forEach(item -> {
            if (item instanceof ModelProvider) ((ModelProvider) item).registerModels();
            else ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
        });
        LogSystem.INSTANCE.registerModels(event);
    }

    @Override
    public Item register(@NotNull Item entry, @NotNull ResourceLocation location) {
        return super.register(entry, location).setRegistryName(location).setTranslationKey(location.getNamespace() + "." + location.getPath());
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        this.register();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        this.registerOres(event);
    }

    private void register() {
        this.register(new ItemFoodDefault(1, 16, false), "dried_kelp").setCreativeTab(CreativeTabs.FOOD);
        this.register("sea_heart").setCreativeTab(CreativeTabs.MATERIALS);
        this.register("nautilus_shell").setCreativeTab(CreativeTabs.MATERIALS);
        this.register("phantom_membrane").setCreativeTab(CreativeTabs.MATERIALS);
        this.register("scute").setCreativeTab(CreativeTabs.MATERIALS);
        this.register(new ItemTrident(), "trident").setFull3D().setMaxDamage(250);
        this.register(new ItemArmorTurtle(ModArmorMaterials.TURTLE_SHELL, EntityEquipmentSlot.HEAD), "turtle_helmet");
    }

    private void registerOres(FMLInitializationEvent event) {
        LogSystem.INSTANCE.registerOres(event);
    }

//    @SideOnly(Side.CLIENT)
//    public static void registerModels(ModelRegistryEvent event) {
//        ITEMS.forEach(item -> {
//            if (item instanceof ModelProvider) ((ModelProvider) item).registerModels();
//            else {
//                setModelLocation(item, 0, "inventory");
//            }
//        });
//        for (ItemBlock itemBlock : BPHooks.DEBARKED_LOG_ITEMS) {
//            NonNullList<ItemStack> list = NonNullList.create();
//            itemBlock.getSubItems(CreativeTabs.SEARCH, list);
//            for (ItemStack stack : list) {
//                int metadata = stack.getMetadata();
//                IBlockState state = itemBlock.getBlock().getStateFromMeta(metadata);
//                IProperty<?> property = itemBlock.getBlock().getBlockState().getProperty("axis");
//                if (property != null) {
//                    if (property.getValueClass() == EnumFacing.Axis.class) {
//                        state = state.withProperty((IProperty<EnumFacing.Axis>) property, EnumFacing.Axis.Y);
//                    }
//                    else if (property.getValueClass() == BlockLog.EnumAxis.class) {
//                        state = state.withProperty((IProperty<BlockLog.EnumAxis>) property, BlockLog.EnumAxis.Y);
//                    }
//                }
//                String variantIn = RandomHelper.getVariantFromState(state);
//                ModelLoader.setCustomModelResourceLocation(itemBlock, metadata, new ModelResourceLocation(Objects.requireNonNull(itemBlock.getBlock().getRegistryName()), variantIn));
//            }
//        }
//    }
}
