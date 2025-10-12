package surreal.backportium._internal.block;

import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import org.jetbrains.annotations.NotNull;
import surreal.backportium._internal.client.renderer.model.ModelProvider;
import surreal.backportium._internal.client.renderer.model.StateMapProvider;
import surreal.backportium.util.BlockUtil;

import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;

public class BlockDefault {

    public static class ButtonWood extends BlockButtonWood implements ModelProvider {

        public ButtonWood() {}

        @Override
        public void registerModels() {
            Item item = BlockUtil.getItemFromBlock(this);
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
        }
    }

    public static class PressurePlate extends BlockPressurePlate {
        public PressurePlate(Material materialIn, Sensitivity sensitivityIn) { super(materialIn, sensitivityIn); }
    }

    public static class Trapdoor extends BlockTrapDoor {
        public Trapdoor(Material materialIn) { super(materialIn); }
    }

    @SuppressWarnings("deprecation")
    public static class Stairs extends BlockStairs implements ModelProvider {

        public Stairs(IBlockState modelState) {
            super(modelState);
            this.useNeighborBrightness = true;
        }

        public Stairs(Block block) { this(block.getDefaultState()); }
        @SuppressWarnings("deprecation") public Stairs(Block block, int metadata) { this(block.getStateFromMeta(metadata)); }

        @Override
        public boolean isOpaqueCube(@NotNull IBlockState state) {
            return false;
        }

        @Override
        public boolean isFullCube(@NotNull IBlockState state) {
            return false;
        }

        @Override
        public void registerModels() {
            Item item = Item.getItemFromBlock(this);
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(this.getRegistryName()), "inventory"));
        }
    }

    @SuppressWarnings("deprecation")
    public static abstract class Slab extends BlockSlab implements StateMapProvider, surreal.backportium._internal.block.Slab {

        public static final PropertyEnum<Normal> NORMAL = PropertyEnum.create("normal", Normal.class);

        public Slab(Material materialIn, MapColor mapColor) {
            super(materialIn, mapColor);
            IBlockState state = this.getDefaultState();
            if (!this.isDouble()) state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
            this.setDefaultState(state.withProperty(NORMAL, Normal.NORMAL));
            this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            this.useNeighborBrightness = !this.isDouble();
        }

        public Slab(Material material) {
            this(material, material.getMaterialMapColor());
        }

        @Override
        public boolean isOpaqueCube(@NotNull IBlockState state) {
            return this.isDouble();
        }

        @Override
        public boolean isFullCube(@NotNull IBlockState state) {
            return this.isDouble();
        }

        @NotNull
        @Override
        public ItemStack getItem(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
            return new ItemStack(this.getSingleSlab());
        }

        @NotNull
        @Override
        public Item getItemDropped(@NotNull IBlockState state, @NotNull Random rand, int fortune) {
            return Item.getItemFromBlock(this.getSingleSlab());
        }

        protected abstract Block getSingleSlab();

        @Override
        public void registerStateMap() {
            ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(NORMAL).build());
        }

        @Override
        public int getMetaFromState(@NotNull IBlockState state) {
            if (this.isDouble()) return 0;
            return state.getValue(HALF) == EnumBlockHalf.BOTTOM ? 0 : 1;
        }

        @NotNull
        @Override
        public IBlockState getStateFromMeta(int meta) {
            if (this.isDouble()) return this.getDefaultState();
            else return meta == 0 ? this.getDefaultState().withProperty(HALF, EnumBlockHalf.BOTTOM) : this.getDefaultState().withProperty(HALF, EnumBlockHalf.TOP);
        }

        @NotNull
        @Override
        protected BlockStateContainer createBlockState() {
            return this.isDouble() ? new BlockStateContainer(this, NORMAL) : new BlockStateContainer(this, NORMAL, HALF);
        }

        @NotNull
        @Override
        public String getTranslationKey(int meta) {
            return this.getTranslationKey();
        }

        @Override
        public boolean isDouble() {
            return false;
        }

        @NotNull
        @Override
        public IProperty<?> getVariantProperty() {
            return NORMAL;
        }

        @NotNull
        @Override
        public Comparable<?> getTypeForItem(@NotNull ItemStack stack) {
            return Normal.NORMAL;
        }

        @Override
        public BiFunction<BlockSlab, BlockSlab, Item> getSlabItem() {
            return (a, b) -> new ItemSlab(this, a, b);
        }

        public enum Normal implements IStringSerializable {

            NORMAL;

            @NotNull
            @Override
            public String getName() {
                return "normal";
            }
        }
    }
}
