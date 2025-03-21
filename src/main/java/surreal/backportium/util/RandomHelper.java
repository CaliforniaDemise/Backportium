package surreal.backportium.util;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Map;

public class RandomHelper {

    /**
     * Used in various methods of {@link surreal.backportium.core.v13.PlayerMoveTransformer}
     **/
    @SuppressWarnings("unused")
    public static float lerp(float pct, float start, float end) {
        return start + pct * (end - start);
    }

    /**
     * Used in {@link surreal.backportium.core.v13.PlayerMoveTransformer#transformEntityPlayer(byte[])}
     **/
    @SuppressWarnings("unused")
    @SideOnly(Side.CLIENT)
    public static boolean isPlayerMoving(EntityPlayerSP player, boolean verticalSpeed) {
        MovementInput input = player.movementInput;
        boolean vert = verticalSpeed && input.jump;
        return vert || input.forwardKeyDown || input.backKeyDown || input.leftKeyDown || input.rightKeyDown;
    }

    public static int getMetaFromVariant(IBlockState state, IProperty<?> property) {
        if (property instanceof PropertyInteger) {
            PropertyInteger intProperty = (PropertyInteger) property;
            return state.getValue(intProperty);
        } else if (property instanceof PropertyBool) {
            PropertyBool boolProperty = (PropertyBool) property;
            return state.getValue(boolProperty) ? 1 : 0;
        } else if (property instanceof PropertyEnum<?>) {
            PropertyEnum<?> enumProperty = (PropertyEnum<?>) property;
            return state.getValue(enumProperty).ordinal();
        }
        throw new AssertionError("Property types that are not vanilla isn't allowed");
    }

    public static String getNameFromVariant(IProperty<?> property, int meta) {
        if (property instanceof PropertyInteger) {
            return "" + meta;
        } else if (property instanceof PropertyBool) {
            return meta == 1 ? "true" : "false";
        }

        return null; // Handle Property Enum properly. We don't need to clone values for every meta.
    }

    public static String getNameFromVariant(Comparable<?> comparable) {
        if (comparable.getClass().isEnum()) {
            return ((IStringSerializable) comparable).getName();
        } else return comparable.toString();
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

    public static <T extends Comparable<T>> IBlockState copyState(IBlockState from, Block to) {
        IBlockState state = to.getDefaultState();
        for (IProperty<?> property : from.getPropertyKeys()) {
            state = state.withProperty((IProperty<T>) property, (T) from.getValue(property));
        }
        return state;
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
