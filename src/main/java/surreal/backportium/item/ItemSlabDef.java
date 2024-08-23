package surreal.backportium.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import surreal.backportium.api.client.model.ModelProvider;
import surreal.backportium.util.RandomHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class ItemSlabDef extends ItemSlab implements ModelProvider {

    private final BlockSlab slab;

    public ItemSlabDef(Block block, BlockSlab singleSlab, BlockSlab doubleSlab) {
        super(block, singleSlab, doubleSlab);
        this.slab = singleSlab;
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < slab.getVariantProperty().getAllowedValues().size(); i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public void registerModels() {
        IProperty<?> property = slab.getVariantProperty();
        Class<?> valueClass = property.getValueClass();

        int loopAmount = property.getAllowedValues().size();

        String variant = "half=bottom," + property.getName() + "=";

        if (valueClass.isEnum()) { // Enum properties are always IStringSerializable
            IStringSerializable[] objects = (IStringSerializable[]) valueClass.getEnumConstants();
            for (int i = 0; i < loopAmount; i++) {
                String variantIn = variant + objects[i].getName();
                ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), variantIn));
            }
        }
        else {
            for (int i = 0; i < loopAmount; i++) {
                String variantIn = variant + RandomHelper.getNameFromVariant(property, i);
                ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), variantIn));
            }
        }
    }
}
