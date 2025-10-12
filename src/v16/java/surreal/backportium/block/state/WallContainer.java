package surreal.backportium.block.state;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.block.properties.wall.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static surreal.backportium.block.properties.wall.NewWallProperties.*;

public class WallContainer extends BlockStateContainer {

    public WallContainer(Block blockIn, BlockStateContainer oldContainer, IProperty<?>... properties) {
        super(blockIn, properties(properties, oldContainer));
    }

    private static IProperty<?>[] properties(IProperty<?>[] properties, BlockStateContainer oldContainer) {
        List<IProperty<?>> propertyList = new ArrayList<>();
        oldContainer.getProperties().forEach(p -> {
            if (p != BlockWall.UP && !isOldProperty(p, "up") && !isOldProperty(p, "east") && !isOldProperty(p, "west") && !isOldProperty(p, "north") && !isOldProperty(p, "south")) propertyList.add(p);
        });
        IProperty<?>[] out = new IProperty[properties.length + propertyList.size()];
        System.arraycopy(properties, 0, out, 0, properties.length);
        for (int i = 0; i < propertyList.size(); i++) {
            out[properties.length + i] = propertyList.get(i);
        }
        return out;
    }

    private static boolean isOldProperty(IProperty<?> property, String name) {
        if (property instanceof PropertyBool && property.getName().equals(name)) return !name.equals("up") || property != BlockWall.UP;
        return false;
    }

    @NotNull
    @Override
    protected StateImplementation createState(@NotNull Block block, @NotNull ImmutableMap<IProperty<?>, Comparable<?>> properties, @Nullable ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
        return new StateImpl(block, properties);
    }

    private static class StateImpl extends StateImplementation {

        protected StateImpl(Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn) {
            super(blockIn, propertiesIn);
        }

        @SuppressWarnings("unchecked")
        @NotNull
        @Override
        public <T extends Comparable<T>> T getValue(@NotNull IProperty<T> property) {
            if (isOldProperty(property, "up")) return (T) super.getValue(UP);
            if (isOldProperty(property, "north")) return (T) Boolean.valueOf(super.getValue(NORTH_NEW) != Connection.NONE);
            if (isOldProperty(property, "south")) return (T) Boolean.valueOf(super.getValue(SOUTH_NEW) != Connection.NONE);
            if (isOldProperty(property, "east")) return (T) Boolean.valueOf(super.getValue(EAST_NEW) != Connection.NONE);
            if (isOldProperty(property, "west")) return (T) Boolean.valueOf(super.getValue(WEST_NEW) != Connection.NONE);
            if (property == BlockWall.VARIANT && !this.getPropertyKeys().contains(property)) { // I hope there isn't someone that's more retarded than Vazkii
                return (T) BlockWall.EnumType.NORMAL;
            }
            return super.getValue(property);
        }

        @NotNull
        @Override
        public <T extends Comparable<T>, V extends T> IBlockState withProperty(@NotNull IProperty<T> property, @NotNull V value) {
            if (isOldProperty(property, "up")) return super.withProperty(UP, (Boolean) value);
            if (isOldProperty(property, "north")) return super.withProperty(NORTH_NEW, (Boolean) value ? Connection.LOW : Connection.NONE);
            if (isOldProperty(property, "south")) return super.withProperty(SOUTH_NEW, (Boolean) value ? Connection.LOW : Connection.NONE);
            if (isOldProperty(property, "east")) return super.withProperty(EAST_NEW, (Boolean) value ? Connection.LOW : Connection.NONE);
            if (isOldProperty(property, "west")) return super.withProperty(WEST_NEW, (Boolean) value ? Connection.LOW : Connection.NONE);
            if (property == BlockWall.VARIANT && !super.getPropertyKeys().contains(property)) return this;
            return super.withProperty(property, value);
        }
    }
}
