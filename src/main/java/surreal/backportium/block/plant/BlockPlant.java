package surreal.backportium.block.plant;

import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class BlockPlant extends BlockBush implements IGrowable, IShearable {

    private final Block doublePlant;

    public BlockPlant(Material material, MapColor mapColor, Block doublePlant) {
        super(material, mapColor);
        this.setTickRandomly(false);
        this.setSoundType(SoundType.PLANT);
        this.doublePlant = doublePlant;
    }

    public BlockPlant(Material material, MapColor mapColor) {
        this(material, mapColor, Blocks.AIR);
    }

    public BlockPlant(Material material) {
        this(material, material.getMaterialMapColor());
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        BlockPos posUp = pos.up();
        return this.doublePlant != Blocks.AIR && worldIn.getBlockState(posUp).getBlock().isReplaceable(worldIn, posUp);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        assert doublePlant instanceof BlockPlantDouble;
        ((BlockPlantDouble) doublePlant).place(worldIn, pos, state);
    }

    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(this));
    }
}
