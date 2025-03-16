package surreal.backportium.world.biome;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class BiomeOceanCold extends BiomeOcean {

    public BiomeOceanCold(BiomeProperties properties) {
        super(properties);
    }

    // TODO Not one to one
    @SuppressWarnings("unused")
    // @Override
    public IBlockState getTheSurface(World world, Random random, ChunkPrimer primer, int x, int y, double noiseVal) {
        if (noiseVal > 2.7) return Blocks.DIRT.getDefaultState();
        return Blocks.GRAVEL.getDefaultState();
    }
}
