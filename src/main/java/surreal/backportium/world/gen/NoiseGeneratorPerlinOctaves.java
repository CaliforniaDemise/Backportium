package surreal.backportium.world.gen;

import net.minecraft.world.gen.NoiseGeneratorSimplex;

import java.util.Random;

public class NoiseGeneratorPerlinOctaves {

    private final NoiseGeneratorSimplex[] generators;

    private final double persistence;
    private final double lacunarity;
    private final double maxValue;

    public NoiseGeneratorPerlinOctaves(Random random, int octave, int amount) {
        this.generators = new NoiseGeneratorSimplex[amount];
        for (int i = 0; i < amount; i++) {
            this.generators[i] = new NoiseGeneratorSimplex(random);
        }
        this.lacunarity = Math.pow(2.0D, octave);
        this.persistence = Math.pow(2.0D, amount - 1) / (Math.pow(2.0D, amount)) - 1.0D;
        this.maxValue = this.getTotalAmplitude(2.0D);
    }

    public double getValue(double x, double z) {
        double value = 0.0D;
        double lacunarity = this.lacunarity;
        double persistence = this.persistence;
        for (NoiseGeneratorSimplex generator : this.generators) {
            value += generator.getValue(x * lacunarity, z * lacunarity) * persistence;
            lacunarity *= 2.0D;
            persistence /= 2.0D;
        }
        return value;
    }

    public double getMaxValue() {
        return maxValue;
    }

    private double getTotalAmplitude(double scale) {
        double out = 0.0D;
        double p = this.persistence;
        for (int i = 0; i < this.generators.length; i++) {
            out += scale * p;
            p /= 2.0D;
        }
        return out;
    }
}
