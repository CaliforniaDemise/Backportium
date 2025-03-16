package surreal.backportium.world.biome;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

public class BiomeOceanLukewarm extends BiomeOcean {

    public BiomeOceanLukewarm(BiomeProperties properties) {
        super(properties);
    }

    @SuppressWarnings("unused")
    // @Override
    public IBlockState getTheSurface(World world, Random random, ChunkPrimer primer, int x, int y, double noiseVal) {
        return Blocks.SAND.getDefaultState();
    }
}
