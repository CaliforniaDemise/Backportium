package surreal.backportium._internal.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.block.BlockDoublePlantWater;
import surreal.backportium.init.ModBlocks;

import java.util.Collections;
import java.util.List;

public class BlockTallSeagrass extends BlockDoublePlantWater {

    public BlockTallSeagrass() {
        super(Material.PLANTS, MapColor.WATER);
    }

    @Override
    public boolean isShearable(@NotNull ItemStack item, @NotNull IBlockAccess world, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public @NotNull List<ItemStack> onSheared(@NotNull ItemStack item, @NotNull IBlockAccess world, @NotNull BlockPos pos, int fortune) {
        return Collections.singletonList(new ItemStack(ModBlocks.SEAGRASS, 2));
    }
}
