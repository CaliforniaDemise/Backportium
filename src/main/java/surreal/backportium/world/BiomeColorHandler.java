package surreal.backportium.world;

import net.minecraft.world.biome.Biome;
import surreal.backportium.api.extension.BiomeExtension;

// BiomeProperties color defaults to default water color
// setWaterColor method does the emulateLegacy color setActualWaterColor sets the color.
public class BiomeColorHandler {

    public static final int DEFAULT_WATER_FOG_COLOR = 329011;
    public static final int DEFAULT_WATER_COLOR = 4159204;

    private static final int DEFAULT_WATER_COLOR_112 = 16777215;
    private static final int PERCEIVED_WATER_COLOR_112 = 0x2b3bf4;

    public static int getWaterColor(Biome biome, int oldColor) {
        if (oldColor == DEFAULT_WATER_COLOR_112) return DEFAULT_WATER_COLOR;
        return oldColor;
    }

    public static int getWaterFogColor(Biome biome) {
        if (biome == null) return DEFAULT_WATER_FOG_COLOR;
        int color = ((BiomeExtension) biome).getWaterFogColor();
        if (color != -1) return color;
        return DEFAULT_WATER_FOG_COLOR;
    }

    public static int emulateLegacyColor(int modColor) {
        int modR = (modColor & 0xff0000) >> 16;
        int modG = (modColor & 0x00ff00) >> 8;
        int modB = (modColor & 0x0000ff);
        int legacyR = (PERCEIVED_WATER_COLOR_112 & 0xff0000) >> 16;
        int legacyG = (PERCEIVED_WATER_COLOR_112 & 0x00ff00) >> 8;
        int legacyB = (PERCEIVED_WATER_COLOR_112 & 0x0000ff);
        int displayedR = (modR * legacyR) / 255;
        int displayedG = (modG * legacyG) / 255;
        int displayedB = (modB * legacyB) / 255;
        return (displayedR << 16) | (displayedG << 8) | displayedB;
    }
}
