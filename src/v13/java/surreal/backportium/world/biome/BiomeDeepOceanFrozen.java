package surreal.backportium.world.biome;

import net.minecraft.util.math.BlockPos;

public class BiomeDeepOceanFrozen extends BiomeOceanFrozen {

    public BiomeDeepOceanFrozen(BiomeProperties properties) {
        super(properties);
    }

    @Override
    public float getDefaultTemperature(BlockPos pos) {
        return 0.5F;
    }
}
