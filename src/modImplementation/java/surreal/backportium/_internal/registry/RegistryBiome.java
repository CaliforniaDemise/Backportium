package surreal.backportium._internal.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeOcean;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import surreal.backportium._internal.world.biome.BiomeTypeProvider;

import java.util.function.Consumer;
import java.util.function.Function;

public class RegistryBiome extends Registry<Biome> implements Biomes {

    protected RegistryBiome(RegistryManager manager) {
        super(manager);
    }

    @Override
    public <T extends Biome> T register(Function<Biome.BiomeProperties, T> function, String name, String visibleName, Consumer<Biome.BiomeProperties> consumer) {
        return register(function, new ResourceLocation(manager.getModId(), name), visibleName, consumer);
    }

    @Override
    public <T extends Biome> T register(Function<Biome.BiomeProperties, T> function, ResourceLocation name, String visibleName, Consumer<Biome.BiomeProperties> consumer) {
        Biome.BiomeProperties properties = new Biome.BiomeProperties(visibleName);
        consumer.accept(properties);
        T biome = function.apply(properties);
        biome.setRegistryName(name);
        return this.register(biome);
    }

    @Override
    public void registerAll(RegistryEvent.Register<Biome> event) {
        this.list.forEach(biome -> {
            event.getRegistry().register(biome);
            if (biome instanceof BiomeOcean) BiomeManager.oceanBiomes.add(biome);
            if (biome instanceof BiomeTypeProvider) ((BiomeTypeProvider) biome).addTypes();
            else BiomeDictionary.makeBestGuess(biome);
        });
    }
}
