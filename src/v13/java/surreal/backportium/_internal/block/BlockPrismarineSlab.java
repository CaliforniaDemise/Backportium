package surreal.backportium._internal.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.init.ModBlocks;

import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class BlockPrismarineSlab extends BlockDefault.Slab {

    private final Block singleSlab;

    public BlockPrismarineSlab() {
        this(Blocks.AIR);
        this.setHardness(1.5F).setResistance(10.0F);
    }

    protected BlockPrismarineSlab(Block singleSlab) {
        super(Material.ROCK);
        this.singleSlab = singleSlab;
    }

    @NotNull
    @Override
    public MapColor getMapColor(@NotNull IBlockState state, @NotNull IBlockAccess worldIn, @NotNull BlockPos pos) {
        if (this.getSingleSlab() == ModBlocks.ROUGH_PRISMARINE_SLAB) return MapColor.CYAN;
        return MapColor.DIAMOND;
    }

    @Override
    protected Block getSingleSlab() {
        if (!this.isDouble()) return this;
        return this.singleSlab;
    }

    @Override
    public Supplier<BlockSlab> getDoubleSlab() {
        return () -> new BlockPrismarineSlab(this) {
            @Override
            public boolean isDouble() {
                return true;
            }
        };
    }
}
