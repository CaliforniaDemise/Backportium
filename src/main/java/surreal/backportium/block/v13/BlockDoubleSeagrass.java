package surreal.backportium.block.v13;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.block.plant.BlockPlantDoubleWater;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class BlockDoubleSeagrass extends BlockPlantDoubleWater {

    public BlockDoubleSeagrass(Material material) {
        super(material);
    }

    public BlockDoubleSeagrass(Material material, MapColor mapColor) {
        super(material, mapColor);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(ModBlocks.SEAGRASS, 2));
    }

    @NotNull
    @Override
    public ItemStack getPickBlock(@NotNull IBlockState state, @NotNull RayTraceResult target, @NotNull World world, @NotNull BlockPos pos, @NotNull EntityPlayer player) {
        return new ItemStack(ModBlocks.SEAGRASS);
    }
}
