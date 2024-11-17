package surreal.backportium.world.biome;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import surreal.backportium.Tags;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.common.BiomeDictionary.Type.*;

public class ModBiomes {

    private static final List<Biome> BIOMES = new ArrayList<>();

    public static final BiomeOceanFrozen FROZEN_OCEAN = register(new BiomeOceanFrozen(new Biome.BiomeProperties("Frozen Ocean 2").setBaseHeight(-1.0F).setHeightVariation(0.1F).setTemperature(0.0F).setRainfall(0.5F).setSnowEnabled()), "frozen_ocean");
    public static final BiomeOceanFrozen DEEP_FROZEN_OCEAN = register(new BiomeOceanFrozen(new Biome.BiomeProperties("Deep Frozen Ocean").setBaseHeight(-1.8F).setHeightVariation(0.1F).setRainfall(0.5F).setSnowEnabled()), "deep_frozen_ocean");

    public static <T extends Biome> T register(T biome, String name) {
        biome.setRegistryName(new ResourceLocation(Tags.MOD_ID, name));
        BIOMES.add(biome);
        return biome;
    }

    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        IForgeRegistry<Biome> registry = event.getRegistry();
        BIOMES.forEach(registry::register);

        BiomeManager.addBiome(BiomeManager.BiomeType.ICY, new BiomeManager.BiomeEntry(FROZEN_OCEAN, 10));
        BiomeManager.addBiome(BiomeManager.BiomeType.ICY, new BiomeManager.BiomeEntry(DEEP_FROZEN_OCEAN, 10));
        BiomeManager.oceanBiomes.add(FROZEN_OCEAN);
        BiomeManager.oceanBiomes.add(DEEP_FROZEN_OCEAN);
        BiomeDictionary.addTypes(FROZEN_OCEAN, COLD, OCEAN, SNOWY);
        BiomeDictionary.addTypes(DEEP_FROZEN_OCEAN, COLD, OCEAN, SNOWY);
    }
}
