package surreal.backportium.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.world.gen.NoiseGeneratorDoublePerlin;
import surreal.backportium.world.gen.WorldGenIceberg;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

public class BiomeOceanFrozen extends BiomeOcean {

    protected final WorldGenIceberg ICEBERG_PACKED = new WorldGenIceberg(false, Blocks.PACKED_ICE.getDefaultState());
    protected final WorldGenIceberg ICEBERG_BLUE = new WorldGenIceberg(false, ModBlocks.BLUE_ICE.getDefaultState());

    private static final NoiseGeneratorSimplex FROZEN_NOISE = new NoiseGeneratorSimplex(new Random(3456L));
    private static NoiseGeneratorDoublePerlin ICEBERG_SURFACE_NOISE = null;
    private static NoiseGeneratorDoublePerlin ICEBERG_PILLAR_NOISE = null;
    private static NoiseGeneratorDoublePerlin ICEBERG_PILLAR_ROOF_NOISE = null;

    public BiomeOceanFrozen(BiomeProperties properties) {
        super(properties);
    }

    @Override
    public int getModdedBiomeGrassColor(int original) {
        return 0x80B497;
    }

    @Override
    public int getModdedBiomeFoliageColor(int original) {
        return 0x60A17B;
    }

    // TODO Handle underwater fog, maybe find a way to change the whole water color
    @Override
    public int getWaterColorMultiplier() {
        return 0x3938C9;
    }

    @Nonnull
    @Override
    public BiomeDecorator createBiomeDecorator() {
        return new BiomeDecoratorOceanFrozen();
    }

    @Override
    @ParametersAreNonnullByDefault
    public void decorate(World worldIn, Random rand, BlockPos pos) {
        if (rand.nextInt(16) == 0) {
            if (rand.nextInt(200) == 0) ICEBERG_BLUE.generate(worldIn, rand, pos);
            else ICEBERG_PACKED.generate(worldIn, rand, pos);
        }
        super.decorate(worldIn, rand, pos);
    }

    // @Override
    @SuppressWarnings("unused")
    public void generateTerrain(World worldIn, Random rand, ChunkPrimer primer, BlockPos.MutableBlockPos pos, int x, int z, double noiseVal) {
        if (ICEBERG_PILLAR_NOISE == null) ICEBERG_PILLAR_NOISE = new NoiseGeneratorDoublePerlin(new Random(rand.nextLong()), -6, 4);
        if (ICEBERG_PILLAR_ROOF_NOISE == null) ICEBERG_PILLAR_ROOF_NOISE = new NoiseGeneratorDoublePerlin(new Random(rand.nextLong()), -3, 1);
        if (ICEBERG_SURFACE_NOISE == null) ICEBERG_SURFACE_NOISE = new NoiseGeneratorDoublePerlin(new Random(rand.nextLong()), -6, 3);
        double e = Math.min(Math.abs(ICEBERG_SURFACE_NOISE.getValue(x, z) * 8.25D), ICEBERG_PILLAR_NOISE.getValue((double) x * 1.28D, (double) z * 1.28D) * 15.0D);
        if (e > 1.8D) {
            double h = Math.abs(ICEBERG_PILLAR_ROOF_NOISE.getValue((double) x * 1.17D, (double) z * 1.17D) * 1.5D);
            double i = Math.min(e * e * 1.2, Math.ceil(h * 40.0) + 14.0);
            if (this.getTemperature(pos.setPos(x, worldIn.getSeaLevel(), z)) < 0.15F) i -= 2.0D;
            double j;
            if (i > 2.0) {
                j = (double) worldIn.getSeaLevel() - i - 7.0;
                i += worldIn.getSeaLevel();
            } else {
                i = 0.0;
                j = 0.0;
            }
            double k = i;
            Random random;
            {
                long l = Objects.hash(x, z);
                long m = l ^ worldIn.getSeed();
                random = new Random(m);
            }
            int l = 2 + random.nextInt(4);
            int m = worldIn.getSeaLevel() + 18 + random.nextInt(10);
            int n = 0;

            x &= 15;
            z &= 15;
            for (int o = Math.max(m, (int) i + 1); o >= j; o--) {
                if (primer.getBlockState(x, o, z).getBlock() == Blocks.AIR && o < (int) k && random.nextDouble() > 0.01
                        || primer.getBlockState(x, o, z).getBlock() == Blocks.WATER && o > (int) j && o < worldIn.getSeaLevel() && j != 0.0 && random.nextDouble() > 0.15) {
                    if (n <= l && o > m) {
                        primer.setBlockState(x, o, z, Blocks.SNOW.getDefaultState());
                        n++;
                    } else {
                        primer.setBlockState(x, o, z, Blocks.PACKED_ICE.getDefaultState());
                    }
                }
            }
        }
    }

    // TODO Handle this properly
    // @Override
    @SuppressWarnings("unused")
    public float getTheTemperature(BlockPos pos) {
        double frozenVal = 0.0D;
        double folVal = GRASS_COLOR_NOISE.getValue((double) pos.getX() * 0.2, (double) pos.getZ() * 0.2D);
        {
            double lacunarity = 1.0D;
            double persistence = 1.0D / 7.0D;
            for (int i = 0; i < 3; i++) {
                frozenVal += FROZEN_NOISE.getValue((double) pos.getX() * 0.05D * lacunarity, (double) pos.getZ() * 0.05D * lacunarity) * persistence * 7.0D;
                lacunarity /= 2.0D;
                persistence *= 2.0D;
            }
        }
        double f = frozenVal + folVal;
        if (f < 0.3D) {
            double g = GRASS_COLOR_NOISE.getValue((double) pos.getX() * 0.09D, (double) pos.getZ() * 0.09D);
            if (g < 0.8D) {
                return 0.2F;
            }
        }
        return this.getDefaultTemperature();
    }

    // @Override
//    public float getTheTemperature(BlockPos pos) {
//        double temp = TEMPERATURE_NOISE.getValue(pos.getX() / 128.0D, pos.getZ() / 128.0D) * 4.0D;
//        double temp2 = TEMPERATURE_NOISE.getValue(pos.getX() / 64.0D, pos.getZ() / 64.0D) * 4.0D;
//        double temp3 = TEMPERATURE_NOISE.getValue(pos.getX() / 48.0D, pos.getZ() / 48.0D) * 4.0D;
//        double temp4 = TEMPERATURE_NOISE.getValue(pos.getX() / 8.0D, pos.getZ() / 8.0D) * 4.0D;
//        float a = FROZEN_RANDOM.nextFloat();
//        if (a < 0.2F) temp -= 0.1D;
//        else if (a > 0.7F) temp += 0.1D;
//        float out = temp < 0.0D ? 0.0F : 0.2F;
//        if (temp3 < -2.5D) out = 0.0F;
//        if (temp2 > 1.5D) out = 0.2F;
//        if (temp4 < -3.5D) out = 0.0F;
//        return out;
//    }
}
