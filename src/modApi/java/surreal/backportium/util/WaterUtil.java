package surreal.backportium.util;

public final class WaterUtil {

    public static final int DEFAULT_WATER_FOG_COLOR = 329011;
    public static final int DEFAULT_WATER_COLOR = 4159204;

    private static final int DEFAULT_WATER_COLOR_112 = 16777215;
    private static final int PERCEIVED_WATER_COLOR_112 = 0x2b3bf4;

    public static int emulateLegacyColor(int modColor) {
        if (modColor == DEFAULT_WATER_COLOR_112) {
            return DEFAULT_WATER_COLOR;
        }
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

    private WaterUtil() {}
}
