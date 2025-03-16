package surreal.backportium.world.biome;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.extension.BiomePropertiesExtension;
import surreal.backportium.util.Registry;

import static net.minecraftforge.common.BiomeDictionary.Type.*;

public class ModBiomes extends Registry<Biome> {

    @ObjectHolder("backportium:frozen_ocean") public static final BiomeOceanFrozen FROZEN_OCEAN = null;
    @ObjectHolder("backportium:deep_frozen_ocean") public static final BiomeOceanFrozen DEEP_FROZEN_OCEAN = null;
    @ObjectHolder("backportium:warm_ocean") public static final BiomeOceanWarm WARM_OCEAN = null;
    @ObjectHolder("backportium:deep_warm_ocean") public static final BiomeOceanWarm DEEP_WARM_OCEAN = null;
    @ObjectHolder("backportium:lukewarm_ocean") public static final BiomeOceanLukewarm LUKEWARM_OCEAN = null;
    @ObjectHolder("backportium:deep_lukewarm_ocean") public static final BiomeOceanLukewarm DEEP_LUKEWARM_OCEAN = null;
    @ObjectHolder("backportium:cold_ocean") public static final BiomeOceanCold COLD_OCEAN = null;
    @ObjectHolder("backportium:deep_cold_ocean") public static final BiomeOceanCold DEEP_COLD_OCEAN = null;

    public ModBiomes() {
        super(8);
    }

    @Override
    protected Biome register(@NotNull Biome entry, @NotNull ResourceLocation location) {
        return super.register(entry, location).setRegistryName(location);
    }

    @Override
    public void registerEntries(RegistryEvent.Register<Biome> event) {
        Biome frozenOcean = this.register(new BiomeOceanFrozen(new Biome.BiomeProperties("Frozen Ocean").setBaseHeight(-1.0F).setHeightVariation(0.1F).setTemperature(0.0F).setRainfall(0.5F).setSnowEnabled()), "frozen_ocean");
        Biome deepFrozenOcean = this.register(new BiomeOceanFrozen(new Biome.BiomeProperties("Deep Frozen Ocean").setBaseHeight(-1.8F).setHeightVariation(0.1F).setRainfall(0.5F).setSnowEnabled()), "deep_frozen_ocean");
        Biome warmOcean = this.register(new BiomeOceanWarm(new BiomePropertiesExt("Warm Ocean").setActualWaterColor(0x43D5EE).setWaterFogColor(0x041F33).setBaseHeight(-1.0F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F)), "warm_ocean");
        Biome deepWarmOcean = this.register(new BiomeOceanWarm(new BiomePropertiesExt("Deep Warm Ocean").setActualWaterColor(0x43D5EE).setWaterFogColor(0x041F33).setBaseHeight(-1.8F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F)), "deep_warm_ocean");
        Biome lukewarmOcean = this.register(new BiomeOceanLukewarm(new BiomePropertiesExt("Lukewarm Ocean").setActualWaterColor(0x45ADF2).setWaterFogColor(0x041633).setBaseHeight(-1.0F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F)), "lukewarm_ocean");
        Biome deepLukewarmOcean = this.register(new BiomeOceanLukewarm(new BiomePropertiesExt("Deep Lukewarm Ocean").setActualWaterColor(0x45ADF2).setWaterFogColor(0x041633).setBaseHeight(-1.8F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F)), "deep_lukewarm_ocean");
        Biome coldOcean = this.register(new BiomeOceanCold(new BiomePropertiesExt("Cold Ocean").setActualWaterColor(0x3D57D6).setWaterFogColor(0x050533).setBaseHeight(-1.0F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F)), "cold_ocean");
        Biome deepColdOcean = this.register(new BiomeOceanCold(new BiomePropertiesExt("Deep Cold Ocean").setActualWaterColor(0x3D57D6).setWaterFogColor(0x050533).setBaseHeight(-1.8F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F)), "deep_cold_ocean");
        super.registerEntries(event);
        BiomeManager.oceanBiomes.add(frozenOcean);
        BiomeManager.oceanBiomes.add(deepFrozenOcean);
        BiomeManager.oceanBiomes.add(warmOcean);
        BiomeManager.oceanBiomes.add(deepWarmOcean);
        BiomeManager.oceanBiomes.add(lukewarmOcean);
        BiomeManager.oceanBiomes.add(deepLukewarmOcean);
        BiomeManager.oceanBiomes.add(coldOcean);
        BiomeManager.oceanBiomes.add(deepColdOcean);
        BiomeDictionary.makeBestGuess(frozenOcean);
        BiomeDictionary.makeBestGuess(deepFrozenOcean);
        BiomeDictionary.makeBestGuess(warmOcean);
        BiomeDictionary.makeBestGuess(deepWarmOcean);
        BiomeDictionary.makeBestGuess(lukewarmOcean);
        BiomeDictionary.makeBestGuess(deepLukewarmOcean);
        BiomeDictionary.makeBestGuess(coldOcean);
        BiomeDictionary.makeBestGuess(deepColdOcean);
    }

    private static class BiomePropertiesExt extends Biome.BiomeProperties implements BiomePropertiesExtension<BiomePropertiesExt> {
        public BiomePropertiesExt(String nameIn) {
            super(nameIn);
        }
    }
}
