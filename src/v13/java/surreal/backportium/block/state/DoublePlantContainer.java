package surreal.backportium.block.state;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DoublePlantContainer extends BlockStateContainer {

    public DoublePlantContainer(Block blockIn, IProperty<?>... properties) {
        super(blockIn, properties);
    }

    @NotNull
    @Override
    protected StateImplementation createState(@NotNull Block block, @NotNull ImmutableMap<IProperty<?>, Comparable<?>> properties, @Nullable ImmutableMap<IUnlistedProperty<?>, Optional<?>> unlistedProperties) {
        return new State(block, properties);
    }

    private static class State extends StateImplementation {

        protected State(Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> propertiesIn) {
            super(blockIn, propertiesIn);
        }

        @SuppressWarnings("unchecked")
        @NotNull
        @Override
        public <T extends Comparable<T>> T getValue(@NotNull IProperty<T> property) {
            if (property == BlockDoublePlant.VARIANT) return (T) BlockDoublePlant.EnumPlantType.GRASS;
            if (property == BlockDoublePlant.FACING) return (T) EnumFacing.NORTH;
            return super.getValue(property);
        }

        @NotNull
        @Override
        public <T extends Comparable<T>, V extends T> IBlockState withProperty(@NotNull IProperty<T> property, @NotNull V value) {
            if (property == BlockDoublePlant.VARIANT || property == BlockDoublePlant.FACING) return this;
            return super.withProperty(property, value);
        }
    }
}
