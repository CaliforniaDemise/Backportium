package surreal.backportium.util;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Map;

public class RandomHelper {

    public static int getMetaFromVariant(IBlockState state, IProperty<?> property) {
        if (property instanceof PropertyInteger) {
            PropertyInteger intProperty = (PropertyInteger) property;
            return state.getValue(intProperty);
        }
        else if (property instanceof PropertyBool) {
            PropertyBool boolProperty = (PropertyBool) property;
            return state.getValue(boolProperty) ? 1 : 0;
        }
        else if (property instanceof PropertyEnum<?>) {
            PropertyEnum<?> enumProperty = (PropertyEnum<?>) property;
            return state.getValue(enumProperty).ordinal();
        }
        throw new AssertionError("Property types that are not vanilla isn't allowed");
    }

    public static String getNameFromVariant(IProperty<?> property, int meta) {
        if (property instanceof PropertyInteger) {
            return "" + meta;
        }
        else if (property instanceof PropertyBool) {
            return meta == 1 ? "true" : "false";
        }

        return null; // Handle Property Enum properly. We don't need to clone values for every meta.
    }

    public static String getNameFromVariant(Comparable<?> comparable) {
        if (comparable.getClass().isEnum()) {
            return ((IStringSerializable) comparable).getName();
        }
        else return comparable.toString();
    }

    public static String getVariantFromState(IBlockState state) {
        StringBuilder builder = new StringBuilder();
        int i = 1;
        for (Map.Entry<IProperty<?>, Comparable<?>> properties : state.getProperties().entrySet()) {
            builder.append(properties.getKey().getName()).append('=').append(getNameFromVariant(properties.getValue()));
            if (i != state.getProperties().size()) {
                builder.append(',');
                i++;
            }
        }
        return builder.toString();
    }

    // Game doesn't care about special item blocks (might be a bug on Forge's part too.
    @Nonnull
    public static Item getItemFromBlock(Block block) {
        Item item = Item.getItemFromBlock(block);
        if (item == Items.AIR) item = ForgeRegistries.ITEMS.getValue(block.getRegistryName());
        if (item == null) return Items.AIR;
        return item;
    }

    @Nonnull
    public static ItemStack getStackFromState(IBlockState state) {
        Item item = getItemFromBlock(state.getBlock());
        if (item != Items.AIR) return new ItemStack(item, 1, state.getBlock().getMetaFromState(state));
        return ItemStack.EMPTY;
    }
}
