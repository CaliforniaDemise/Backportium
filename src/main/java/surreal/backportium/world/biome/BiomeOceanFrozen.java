package surreal.backportium.world.biome;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.world.gen.WorldGenIceberg;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class BiomeOceanFrozen extends BiomeOcean {

    protected static final WorldGenIceberg ICEBERG_PACKED = new WorldGenIceberg(false, Blocks.PACKED_ICE.getDefaultState());
    protected static final WorldGenIceberg ICEBERG_BLUE = new WorldGenIceberg(false, ModBlocks.BLUE_ICE.getDefaultState());

    public static void generate(World world, Random random, BlockPos pos) {
        if (!world.isRemote) {
            if (world.rand.nextBoolean()) {
                ICEBERG_BLUE.generate(world, random, pos);
            }
            else ICEBERG_PACKED.generate(world, random, pos);
        }
    }

    private static final NoiseGeneratorSimplex FROZEN_NOISE = new NoiseGeneratorSimplex(new Random(3456L));

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

    @Override
    @ParametersAreNonnullByDefault
    public void decorate(World worldIn, Random rand, BlockPos pos) {
        if (rand.nextInt(4) == 0) {
            if (rand.nextInt(200) == 0) ICEBERG_BLUE.generate(worldIn, rand, pos);
            else ICEBERG_PACKED.generate(worldIn, rand, pos);
        }
        super.decorate(worldIn, rand, pos);
    }

    // @Override
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
