package surreal.backportium.api.extension;

import net.minecraft.world.biome.Biome;

public interface BiomePropertiesExtension<T extends Biome.BiomeProperties & BiomePropertiesExtension<T>> {
    default T setWaterFogColor(int color) { return (T) this; }
    default T setActualWaterColor(int color) { return (T) this; }
}
