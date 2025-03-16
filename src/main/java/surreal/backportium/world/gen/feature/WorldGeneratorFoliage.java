package surreal.backportium.world.gen.feature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.lang.reflect.Field;
import java.util.Random;

public abstract class WorldGeneratorFoliage extends WorldGenerator {

    protected static final NoiseGeneratorPerlin GRASS_COLOR_NOISE;

    protected final int noiseToCountRatio;
    protected final double noiseFactor;
    protected final double noiseOffset;

    public WorldGeneratorFoliage(int noiseToCountRatio, double noiseFactor, double noiseOffset) {
        this.noiseToCountRatio = noiseToCountRatio;
        this.noiseFactor = noiseFactor;
        this.noiseOffset = noiseOffset;
    }

    protected int getCount(Random random, BlockPos pos) {
        double d = GRASS_COLOR_NOISE.getValue((double) pos.getX() / this.noiseFactor, (double) pos.getZ() / this.noiseFactor);
        return (int) Math.ceil((d + this.noiseOffset) * (double) this.noiseToCountRatio);
    }

    static {
        try {
            Field f_GRASS_COLOR_NOISE = Biome.class.getDeclaredField(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "GRASS_COLOR_NOISE" : "field_180281_af");
            f_GRASS_COLOR_NOISE.setAccessible(true);
            GRASS_COLOR_NOISE = (NoiseGeneratorPerlin) f_GRASS_COLOR_NOISE.get(null);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Encountered an Error while trying to get declared field: " + Biome.class.getName() + "#GRASS_COLOR_NOISE", e);
        }
    }
}
