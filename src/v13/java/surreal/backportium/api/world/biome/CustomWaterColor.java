package surreal.backportium.api.world.biome;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import surreal.backportium.api.annotations.Extension;
import surreal.backportium.api.annotations.Final;
import surreal.backportium.util.WaterUtil;

/**
 * Expands {@link Biome} class to allow changing water color more accurately and changing the color of fog when you get inside water in the biome.
 * Allows warm oceans to have teal colored waters and of course, changing water fog color.
 */
@Extension(Biome.class)
public interface CustomWaterColor {

    /**
     * An extension of {@link Biome#getWaterColorMultiplier()}
     * Change the actual water color of biomes
     * Deprecates {@link Biome#getWaterColorMultiplier()}, use {@link Biome#getWaterColor()} for getting water color.
     * @return RGB value of the color. -1 if it should use legacy colors.
     */
    default int getActualWaterColor(IBlockAccess world, BlockPos pos) { return WaterUtil.emulateLegacyColor(((Biome) this).getWaterColorMultiplier()); }

    @Final
    default void setActualWaterColor(int waterColor) {}

    /**
     * A 1.13 feature that allows you to have custom water fog color
     * "Fog color" is the color inside the water
     * @return RGB value of the fog color
     */
    default int getWaterFogColor(IBlockAccess world, BlockPos pos) { return WaterUtil.DEFAULT_WATER_FOG_COLOR; }

    default float getWaterFogDensity(EntityLivingBase entity, float defaultDensity) {
        Biome biome = (Biome) this;
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)) return defaultDensity + 0.005F;
        return defaultDensity;
    }

    @Final
    default void setWaterFogColor(int fogColor) {}

    static CustomWaterColor cast(Biome biome) { return (CustomWaterColor) biome; }
}
