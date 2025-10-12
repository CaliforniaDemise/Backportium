package surreal.backportium.block;

import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockPlant extends BlockBush implements IGrowable, IShearable {

    protected final Block doublePlant;

    public BlockPlant(Material material, MapColor mapColor, Block doublePlant) {
        super(material, mapColor);
        if (doublePlant != Blocks.AIR && !(doublePlant instanceof BlockDoublePlant)) throw new IllegalArgumentException("Given double plant " + doublePlant.getClass().getName() + " is not a BlockDoublePlant");
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
    public boolean canUseBonemeal(@NotNull World worldIn, @NotNull Random rand, @NotNull BlockPos pos, @NotNull IBlockState state) {
        return true;
    }

    @Override
    public void grow(@NotNull World worldIn, @Nullable Random rand, @NotNull BlockPos pos, @NotNull IBlockState state) {
        ((BlockDoublePlant) this.doublePlant).placeAt(worldIn, pos, BlockDoublePlant.EnumPlantType.GRASS, 2);
    }

    @Override
    public boolean canGrow(@NotNull World worldIn, @NotNull BlockPos pos, @Nullable IBlockState state, boolean isClient) {
        BlockPos upPos = pos.up();
        IBlockState upState = worldIn.getBlockState(upPos);
        return this.doublePlant != Blocks.AIR && upState.getBlock().isAir(upState, worldIn, upPos);
    }

    @Override
    public boolean isSideSolid(@Nullable IBlockState base_state, @Nullable IBlockAccess world, @Nullable BlockPos pos, @Nullable EnumFacing side) {
        return false;
    }

    @NotNull
    @Override
    public Item getItemDropped(@Nullable IBlockState state, @Nullable Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public boolean isShearable(@NotNull ItemStack item, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @NotNull
    @Override
    public List<ItemStack> onSheared(@NotNull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(this));
    }
}
