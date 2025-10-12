package surreal.backportium.block.state;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.init.ModBlocks;

import java.util.Optional;

import static surreal.backportium._internal.block.BlockHugeMushroom.*;

public class HugeMushroomContainer extends BlockStateContainer {

    public HugeMushroomContainer(Block blockIn) {
        super(blockIn, UP, DOWN, NORTH, SOUTH, WEST, EAST);
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

        @NotNull
        @Override
        public <T extends Comparable<T>, V extends T> IBlockState withProperty(@NotNull IProperty<T> property, @NotNull V value) {
            if (property == BlockHugeMushroom.VARIANT) {
                switch ((BlockHugeMushroom.EnumType) value) {
                    case ALL_INSIDE: return this.getBlock().getDefaultState().withProperty(UP, false).withProperty(DOWN, false).withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(EAST, false);
                    case NORTH_WEST: return this.getBlock().getDefaultState().withProperty(SOUTH, false).withProperty(EAST, false).withProperty(DOWN, false);
                    case NORTH: return this.getBlock().getDefaultState().withProperty(SOUTH, false).withProperty(WEST, false).withProperty(EAST, false).withProperty(DOWN, false).withProperty(EAST, false);
                    case NORTH_EAST: return this.getBlock().getDefaultState().withProperty(SOUTH, false).withProperty(WEST, false).withProperty(DOWN, false);
                    case WEST: return this.getBlock().getDefaultState().withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(DOWN, false).withProperty(EAST, false);
                    case CENTER: return this.getBlock().getDefaultState().withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(DOWN, false).withProperty(EAST, false);
                    case EAST: return this.getBlock().getDefaultState().withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(WEST, false).withProperty(DOWN, false);
                    case SOUTH_WEST: return this.getBlock().getDefaultState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(DOWN, false);
                    case SOUTH: return this.getBlock().getDefaultState().withProperty(WEST, false).withProperty(EAST, false).withProperty(NORTH, false).withProperty(DOWN, false);
                    case SOUTH_EAST: return this.getBlock().getDefaultState().withProperty(NORTH, false).withProperty(WEST, false).withProperty(DOWN, false);
                    case STEM: return ModBlocks.MUSHROOM_STEM.getDefaultState().withProperty(UP, false).withProperty(DOWN, false);
                    case ALL_OUTSIDE: return this.getBlock().getDefaultState();
                    case ALL_STEM: return ModBlocks.MUSHROOM_STEM.getDefaultState();
                }
                return this;
            }
            return super.withProperty(property, value);
        }

        @SuppressWarnings("unchecked")
        @NotNull
        @Override
        public <T extends Comparable<T>> T getValue(@NotNull IProperty<T> property) {
            if (property == BlockHugeMushroom.VARIANT) {
                if (this.getBlock() != ModBlocks.MUSHROOM_STEM) {
                    if (check(false, false, false, false, false, false)) return (T) BlockHugeMushroom.EnumType.ALL_INSIDE;
                    if (check(true, false, true, false, true, false)) return (T) BlockHugeMushroom.EnumType.NORTH_WEST;
                    if (check(true, false, true, false, false, false)) return (T) BlockHugeMushroom.EnumType.NORTH;
                    if (check(true, false, true, false, false, true)) return (T) BlockHugeMushroom.EnumType.NORTH_EAST;
                    if (check(true, false, false, false, true, false)) return (T) BlockHugeMushroom.EnumType.WEST;
                    if (check(true, false, false, false, false, false)) return (T) BlockHugeMushroom.EnumType.CENTER;
                    if (check(true ,false, false, false, false, true)) return (T) BlockHugeMushroom.EnumType.EAST;
                    if (check(true, false, false, true, true, false)) return (T) BlockHugeMushroom.EnumType.SOUTH_WEST;
                    if (check(true, false, false, true, false, false)) return (T) BlockHugeMushroom.EnumType.SOUTH;
                    if (check(true, false, false, true, false, true)) return (T) BlockHugeMushroom.EnumType.SOUTH_EAST;
                }
                else {
                    if (check(true, true, true, true, true, true)) return (T) BlockHugeMushroom.EnumType.ALL_STEM;
                    return (T) BlockHugeMushroom.EnumType.STEM;
                }
                return (T) BlockHugeMushroom.EnumType.ALL_OUTSIDE;
            }
            return super.getValue(property);
        }

        private boolean check(boolean up, boolean down, boolean north, boolean south, boolean west, boolean east) {
            if (super.getValue(UP) != up) return false;
            if (super.getValue(DOWN) != down) return false;
            if (super.getValue(NORTH) != north) return false;
            if (super.getValue(SOUTH) != south) return false;
            if (super.getValue(WEST) != west) return false;
            return super.getValue(EAST) == east;
        }
    }
}
