package surreal.backportium.world.biome;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.util.Registry;

import static net.minecraftforge.common.BiomeDictionary.Type.*;

public class ModBiomes extends Registry<Biome> {

    @ObjectHolder("backportium:frozen_ocean") public static final BiomeOceanFrozen FROZEN_OCEAN = null;
    @ObjectHolder("backportium:deep_frozen_ocean") public static final BiomeOceanFrozen DEEP_FROZEN_OCEAN = null;

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
        super.registerEntries(event);
        BiomeManager.addBiome(BiomeManager.BiomeType.ICY, new BiomeManager.BiomeEntry(frozenOcean, 10));
        BiomeManager.addBiome(BiomeManager.BiomeType.ICY, new BiomeManager.BiomeEntry(deepFrozenOcean, 10));
        BiomeManager.oceanBiomes.add(frozenOcean);
        BiomeManager.oceanBiomes.add(deepFrozenOcean);
        BiomeDictionary.addTypes(frozenOcean, COLD, OCEAN, SNOWY);
        BiomeDictionary.addTypes(deepFrozenOcean, COLD, OCEAN, SNOWY);
    }
}
