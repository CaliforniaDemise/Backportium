package surreal.backportium.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.init.ModBiomes;

import java.lang.reflect.Field;
import java.util.Random;

// Not the best implementation of ocean generation but oh well.
public class GenLayerOceans extends GenLayer {

    private static final Field f_parent;

    private static final PerlinNoise NOISE = new PerlinNoise(new Random(2L));

    public GenLayerOceans(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int @NotNull [] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] aint = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] aint1 = GenLayerZoom.magnify(2001L, new GenLayerOcean(2L), 6).getInts(areaX, areaY, areaWidth, areaHeight);
        for (int i = 0; i < areaHeight; i++) {
            for (int j = 0; j < areaWidth; j++) {
                this.initChunkSeed(j + areaX, i + areaY);
                int index = j + i * areaWidth;
                int k = aint[index];
                int k1 = aint1[index];
                int deepOcean = Biome.getIdForBiome(Biomes.DEEP_OCEAN);
                int warmOcean = Biome.getIdForBiome(ModBiomes.WARM_OCEAN);
                int deepWarmOcean = Biome.getIdForBiome(ModBiomes.DEEP_WARM_OCEAN);
                int lukewarmOcean = Biome.getIdForBiome(ModBiomes.LUKEWARM_OCEAN);
                int deepLukewarmOcean = Biome.getIdForBiome(ModBiomes.DEEP_LUKEWARM_OCEAN);
                int coldOcean = Biome.getIdForBiome(ModBiomes.COLD_OCEAN);
                int deepColdOcean = Biome.getIdForBiome(ModBiomes.DEEP_COLD_OCEAN);
                int frozenOcean = Biome.getIdForBiome(ModBiomes.FROZEN_OCEAN);
                int deepFrozenOcean = Biome.getIdForBiome(ModBiomes.DEEP_FROZEN_OCEAN);
                if (k == 0 || k == deepOcean) {
                    if (k == deepOcean) {
                        if (k1 == warmOcean) k1 = deepWarmOcean;
                        else if (k1 == lukewarmOcean) k1 = deepLukewarmOcean;
                        else if (k1 == coldOcean) k1 = deepColdOcean;
                        else if (k1 == frozenOcean) k1 = deepFrozenOcean;
                        else k1 = deepOcean;
                    }
                    k = k1;
                }
                else aint1[index] = k;
                aint1[index] = k;
            }
        }
        return aint1;
    }

    private static class GenLayerOcean extends GenLayer {
        public GenLayerOcean(long p_i2125_1_) {
            super(p_i2125_1_);
        }

        @Override
        public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
            int[] aint = IntCache.getIntCache(areaWidth * areaHeight);
            for (int i = 0; i < areaHeight; i++) {
                for (int j = 0; j < areaWidth; j++) {
                    this.initChunkSeed(j + areaX, i + areaY);
                    int index = j + i * areaWidth;
                    int k = 0;
                    double temp = NOISE.sample((j + areaX) / 8.0, (i + areaY) / 8.0, 0.0, 0.0, 0.0);
                    if (temp > 0.4) k = Biome.getIdForBiome(ModBiomes.WARM_OCEAN);
                    else if (temp > 0.2) k = Biome.getIdForBiome(ModBiomes.LUKEWARM_OCEAN);
                    else if (temp < -0.4) k = Biome.getIdForBiome(ModBiomes.FROZEN_OCEAN);
                    else if (temp < -0.2) k = Biome.getIdForBiome(ModBiomes.COLD_OCEAN);
                    aint[index] = k;
                }
            }
            return aint;
        }
    }

    private static class PerlinNoise {
        private final byte[] permutations;
        public final double originX;
        public final double originY;
        public final double originZ;

        public PerlinNoise(Random random) {
            this.originX = random.nextDouble() * 256.0;
            this.originY = random.nextDouble() * 256.0;
            this.originZ = random.nextDouble() * 256.0;
            this.permutations = new byte[256];

            for (int i = 0; i < 256; i++) {
                this.permutations[i] = (byte)i;
            }

            for (int i = 0; i < 256; i++) {
                int j = random.nextInt(256 - i);
                byte b = this.permutations[i];
                this.permutations[i] = this.permutations[i + j];
                this.permutations[i + j] = b;
            }
        }

        public double sample(double x, double y, double z, double d, double e) {
            double f = x + this.originX;
            double g = y + this.originY;
            double h = z + this.originZ;
            int i = MathHelper.floor(f);
            int j = MathHelper.floor(g);
            int k = MathHelper.floor(h);
            double l = f - i;
            double m = g - j;
            double n = h - k;
            double o = perlinFade(l);
            double p = perlinFade(m);
            double q = perlinFade(n);
            double s;
            if (d != 0.0) {
                double r = Math.min(e, m);
                s = MathHelper.floor(r / d) * d;
            } else {
                s = 0.0;
            }

            return this.sample(i, j, k, l, m - s, n, o, p, q);
        }

        protected static final int[][] gradients = new int[][]{
            {1, 1, 0},
            {-1, 1, 0},
            {1, -1, 0},
            {-1, -1, 0},
            {1, 0, 1},
            {-1, 0, 1},
            {1, 0, -1},
            {-1, 0, -1},
            {0, 1, 1},
            {0, -1, 1},
            {0, 1, -1},
            {0, -1, -1},
            {1, 1, 0},
            {0, -1, 1},
            {-1, 1, 0},
            {0, -1, -1}
        };

        private static double grad(int hash, double x, double y, double z) {
            int i = hash & 15;
            return dot(gradients[i], x, y, z);
        }

        protected static double dot(int[] gArr, double x, double y, double z) {
            return gArr[0] * x + gArr[1] * y + gArr[2] * z;
        }

        public static double perlinFade(double d) {
            return d * d * d * (d * (d * 6.0 - 15.0) + 10.0);
        }

        private int getGradient(int hash) {
            return this.permutations[hash & 0xFF] & 0xFF;
        }

        public double sample(
            int sectionX, int sectionY, int sectionZ, double localX, double localY, double localZ, double fadeLocalX, double fadeLocalY, double fadeLocalZ
        ) {
            int i = this.getGradient(sectionX) + sectionY;
            int j = this.getGradient(i) + sectionZ;
            int k = this.getGradient(i + 1) + sectionZ;
            int l = this.getGradient(sectionX + 1) + sectionY;
            int m = this.getGradient(l) + sectionZ;
            int n = this.getGradient(l + 1) + sectionZ;
            double d = grad(this.getGradient(j), localX, localY, localZ);
            double e = grad(this.getGradient(m), localX - 1.0, localY, localZ);
            double f = grad(this.getGradient(k), localX, localY - 1.0, localZ);
            double g = grad(this.getGradient(n), localX - 1.0, localY - 1.0, localZ);
            double h = grad(this.getGradient(j + 1), localX, localY, localZ - 1.0);
            double o = grad(this.getGradient(m + 1), localX - 1.0, localY, localZ - 1.0);
            double p = grad(this.getGradient(k + 1), localX, localY - 1.0, localZ - 1.0);
            double q = grad(this.getGradient(n + 1), localX - 1.0, localY - 1.0, localZ - 1.0);
            return lerp3(fadeLocalX, fadeLocalY, fadeLocalZ, d, e, f, g, h, o, p, q);
        }

        public static double lerp3(double deltaX, double deltaY, double deltaZ, double d, double e, double f, double g, double h, double i, double j, double k) {
            return lerp(deltaZ, lerp2(deltaX, deltaY, d, e, f, g), lerp2(deltaX, deltaY, h, i, j, k));
        }

        public static double lerp(double delta, double first, double second) {
            return first + delta * (second - first);
        }

        public static double lerp2(double deltaX, double deltaY, double d, double e, double f, double g) {
            return lerp(deltaY, lerp(deltaX, d, e), lerp(deltaX, f, g));
        }
    }

    private static GenLayer getParent(GenLayer layer) {
        try { return (GenLayer) f_parent.get(layer); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    private static void setParent(GenLayer layer, GenLayer parent) {
        try { f_parent.set(layer, parent); }
        catch (IllegalAccessException e) { throw new RuntimeException(e); }
    }

    static {
        try {
            f_parent = GenLayer.class.getDeclaredField(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "parent" : "field_75909_a");
            f_parent.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}