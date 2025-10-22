package surreal.backportium.api.world.biome;

import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.annotations.Extension;
import surreal.backportium.api.annotations.Final;

/**
 * Expands {@link Biome} class to allow biome names to be translatable
 */
@Extension(Biome.class)
public interface Translatable {

    /**
     * Gets translation key of the biome. This method gets used in {@link Biome#getBiomeName()}
     * If you're going to override it, make sure to return a field or a fixed value
     * By default, it returns a field named 'translationKey' which has the value of 'biome.@namespace@.@path@.name'
     * @return A fixed translation key
     */
    @NotNull
    default String getTranslationKey() {
        return "";
    }

    /**
     * Sets the translation key of the biome which is a field used in {@link Biome#getBiomeName()}
     * @param translationKey lang key used for translation.
     */
    @Final
    default void setTranslationKey(@NotNull String translationKey) {}

    static Translatable cast(Biome biome) { return (Translatable) biome; }
}
