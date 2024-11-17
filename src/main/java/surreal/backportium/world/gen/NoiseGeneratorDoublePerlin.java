package surreal.backportium.world.gen;

import java.util.Random;

public class NoiseGeneratorDoublePerlin {

    private final NoiseGeneratorPerlinOctaves first, second;
    private final double amplitude;
    private final double maxValue;

    public NoiseGeneratorDoublePerlin(Random random, int octaves, int amount) {
        this.first = new NoiseGeneratorPerlinOctaves(random, octaves, amount);
        this.second = new NoiseGeneratorPerlinOctaves(random, octaves, amount);
        this.amplitude = 0.16666666666666666D / this.createAmplitude(Math.abs(octaves) - amount);
        this.maxValue = (this.first.getMaxValue() + this.second.getMaxValue()) * this.amplitude;
    }

    public double getValue(double x, double z) {
        double d = x * 1.0181268882175227;
        double f = z * 1.0181268882175227;
        return (this.first.getValue(d, f) + this.second.getValue(d, f)) * this.amplitude;
    }

    public double getMaxValue() {
        return maxValue;
    }

    private double createAmplitude(double octaves) {
        return 0.1D * (1.0D + 1.0D / octaves + 1);
    }
}
