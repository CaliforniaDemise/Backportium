package surreal.backportium.block.state;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.block.properties.button.Face;
import surreal.backportium.block.properties.button.NewButtonProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ButtonContainer extends BlockStateContainer {

    public ButtonContainer(Block blockIn, BlockStateContainer oldContainer, IProperty<?>... properties) {
        super(blockIn, properties(properties, oldContainer));
    }

    private static IProperty<?>[] properties(IProperty<?>[] properties, BlockStateContainer oldContainer) {
        List<IProperty<?>> propertyList = new ArrayList<>();
        oldContainer.getProperties().forEach(p -> {
            if (!isOldProperty(p, "powered") && !isOldProperty(p, "facing")) propertyList.add(p);
        });
        IProperty<?>[] out = new IProperty[properties.length + propertyList.size()];
        System.arraycopy(properties, 0, out, 0, properties.length);
        for (int i = 0; i < propertyList.size(); i++) {
            out[properties.length + i] = propertyList.get(i);
        }
        return out;
    }

    private static boolean isOldProperty(IProperty<?> property, String name) {
        if (property.getName().equals(name)) {
            if (property instanceof PropertyBool) return name.equals("powered");
            if (property.getName().equals("facing")) return property != NewButtonProperties.FACING;
        }
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
            if (isOldProperty(property, "powered")) return (T) super.getValue(NewButtonProperties.POWERED);
            if (isOldProperty(property, "facing")) {
                Face face = super.getValue(NewButtonProperties.FACE);
                switch (face) {
                    case CEILING: return (T) EnumFacing.DOWN;
                    case WALL: return (T) super.getValue(NewButtonProperties.FACING);
                    case FLOOR: return (T) EnumFacing.UP;
                }
            }
            return super.getValue(property);
        }

        @NotNull
        @Override
        public <T extends Comparable<T>, V extends T> IBlockState withProperty(@NotNull IProperty<T> property, @NotNull V value) {
            if (isOldProperty(property, "powered")) return super.withProperty(NewButtonProperties.POWERED, (Boolean) value);
            if (isOldProperty(property, "facing")) {
                EnumFacing facing = (EnumFacing) value;
                if (facing == EnumFacing.UP) return super.withProperty(NewButtonProperties.FACE, Face.FLOOR).withProperty(NewButtonProperties.FACING, EnumFacing.NORTH);
                else if (facing == EnumFacing.DOWN) return super.withProperty(NewButtonProperties.FACE, Face.CEILING).withProperty(NewButtonProperties.FACING, EnumFacing.NORTH);
                else return super.withProperty(NewButtonProperties.FACE, Face.WALL).withProperty(NewButtonProperties.FACING, facing);
            }
            return super.withProperty(property, value);
        }
    }
}
