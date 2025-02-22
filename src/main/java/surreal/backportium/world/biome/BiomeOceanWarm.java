package surreal.backportium.world.biome;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.chunk.ChunkPrimer;

import java.util.Random;

// TODO Simple seagrass gen and sea pickle gen.
public class BiomeOceanWarm extends BiomeOcean {

    public BiomeOceanWarm(BiomeProperties properties) {
        super(properties);
    }

    @SuppressWarnings("unused")
    // @Override
    public IBlockState getTheSurface(World world, Random random, ChunkPrimer primer, int x, int y, double noiseVal) {
        return Blocks.SAND.getDefaultState();
    }
}
