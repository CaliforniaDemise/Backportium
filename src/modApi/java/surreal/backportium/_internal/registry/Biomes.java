package surreal.backportium._internal.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Biomes {
    <T extends Biome> T register(Function<Biome.BiomeProperties, T> function, String name, String visibleName, Consumer<Biome.BiomeProperties> consumer);
    <T extends Biome> T register(Function<Biome.BiomeProperties, T> function, ResourceLocation name, String visibleName, Consumer<Biome.BiomeProperties> consumer);
}
