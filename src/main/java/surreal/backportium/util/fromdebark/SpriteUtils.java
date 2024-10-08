package surreal.backportium.util.fromdebark;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

// Yes, it's stolen from https://github.com/asiekierka/Debark/blob/master/src/main/java/pl/asie/debark/util/SpriteUtils.java
public class SpriteUtils {

    private static final int[] MISSINGNO_DATA = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            MISSINGNO_DATA[i] = ((((i >> 3) ^ (i >> 7)) & 1) != 0) ? 0xFFFF00FF : 0xFF000000;
        }
    }

    private SpriteUtils() {

    }

    public static boolean isMissingno(TextureAtlasSprite sprite) {
        return "missingno".equals(sprite.getIconName()) || "minecraft:missingno".equals(sprite.getIconName());
    }

    public static TextureAtlasSprite loadSpriteOrWarn(ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> getter) {
        TextureAtlasSprite sprite = getter.apply(location);
        if (sprite == null) {
            sprite = getter.apply(TextureMap.LOCATION_MISSING_TEXTURE);
            if (sprite == null) {
                throw new RuntimeException("Could not load " + location + " or fallback!");
            }
        }
        if (isMissingno(sprite)) {
            System.out.println("Couldn't found sprite " + sprite.getIconName());
        }
        return sprite;
    }

    public static int[] getFrameDataOrWarn(TextureAtlasSprite sprite) {
        if (isMissingno(sprite)) {
            return MISSINGNO_DATA;
        }
        int[][] data;
        if (sprite.getFrameCount() <= 0) {
            System.out.println("Could not read texture data for " + sprite.getIconName() + "! - invalid frame count " + sprite.getFrameCount() + "!");
            return MISSINGNO_DATA;
        }
        try {
            data = sprite.getFrameTextureData(0);
        } catch (Exception e) {
            System.err.println("Could not read texture data for " + sprite.getIconName() + "!" + e);
            return MISSINGNO_DATA;
        }
        if (data == null || data.length <= 0 || data[0] == null || data[0].length <= 0) {
            System.err.println("Could not read texture data for " + sprite.getIconName() + " - frame 0 array missing!");
            return MISSINGNO_DATA;
        }
        return data[0];
    }
}
