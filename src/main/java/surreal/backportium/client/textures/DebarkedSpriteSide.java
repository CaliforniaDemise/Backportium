package surreal.backportium.client.textures;


import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.util.fromdebark.SpriteUtils;
import surreal.backportium.util.fromdebark.UCWColorSpaceUtils;

import java.util.Collection;
import java.util.function.Function;

// Stolen from https://github.com/asiekierka/Debark/blob/master/src/main/java/pl/asie/debark/messy/StrippedBarkColoredSprite.java
public class DebarkedSpriteSide extends SpriteDef {

    private final ResourceLocation logTop;
    private final ResourceLocation logSide;

    public DebarkedSpriteSide(String spriteName, ResourceLocation logTop, ResourceLocation logSide) {
        super(spriteName);
        this.logTop = logTop;
        this.logSide = logSide;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.of(logTop, logSide);
    }

    private float[] getGammaCorrectedLumaRange(TextureAtlasSprite baseTex, int offset16) {
        float minL = Float.MAX_VALUE;
        float maxL = Float.MIN_VALUE;

        double A = 0;
        double B = 0;
        int count = 0;

        int offsetX = offset16 * baseTex.getIconWidth() / 16;
        int offsetY = offset16 * baseTex.getIconHeight() / 16;
        int[] baseData = SpriteUtils.getFrameDataOrWarn(baseTex);

        for (int iy = offsetY; iy < baseTex.getIconHeight() - offsetY; iy++) {
            for (int ix = offsetX; ix < baseTex.getIconWidth() - offsetX; ix++) {
                int pixel = baseData[iy * baseTex.getIconWidth() + ix];
                float[] lab = UCWColorSpaceUtils.XYZtoLAB(UCWColorSpaceUtils.sRGBtoXYZ(UCWColorSpaceUtils.fromInt(pixel)));
                if (lab[0] < minL) minL = lab[0];
                if (lab[0] > maxL) maxL = lab[0];
                A += lab[1]; B += lab[2];
                count++;
            }
        }

        assert count >= 1;
        A /= count;
        B /= count;

        minL = (float) Math.pow(minL / 100f, 2.2) * 100f;
        maxL = (float) Math.pow(maxL / 100f, 2.2) * 100f;

        return new float[] { minL, maxL, (float) A, (float) B };
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        TextureAtlasSprite baseTex = SpriteUtils.loadSpriteOrWarn(logSide, textureGetter);
        float[] gcrMiddle = getGammaCorrectedLumaRange(SpriteUtils.loadSpriteOrWarn(logTop, textureGetter), 2);
        float[] gcrSide = getGammaCorrectedLumaRange(baseTex, 0);
        float[] gcrLeft = new float[] { -0.28125f, -0.234375f };

        gcrLeft[0] = (gcrLeft[0] * (gcrMiddle[1] - gcrMiddle[0])) + gcrMiddle[0];
        gcrLeft[1] = (gcrLeft[1] * (gcrMiddle[1] - gcrMiddle[0])) + gcrMiddle[0];
        if (gcrLeft[0] < 0f) gcrLeft[0] = 0f;
        if (gcrLeft[1] < 0f) gcrLeft[1] = 0f;

        // recolor template texture
        int[] templateData = new int[baseTex.getIconWidth() * baseTex.getIconHeight()];
        int[] templateInput = SpriteUtils.getFrameDataOrWarn(baseTex);
        for (int ix = 0; ix < baseTex.getIconWidth(); ix++) {
            // adapt luma
            // the range is from ~92 to ~98 on the leftmost side, turning into the log top range on the 1/4th
            // we also want to make the middle color less sensitive
            float offset = 1F;
            float minL = gcrLeft[0] * (1 - offset) + (gcrMiddle[0] * 0.75f + gcrMiddle[1] * 0.25f) * offset;
            float maxL = gcrLeft[1] * (1 - offset) + (gcrMiddle[1] * 0.75f + gcrMiddle[0] * 0.25f) * offset;

            minL /= 100f;
            maxL /= 100f;

            int lastPixel = -1;
            for (int iy = 0; iy < baseTex.getIconHeight(); iy++) {
                int ip = iy * baseTex.getIconWidth() + ix;
                int pixel = templateInput[ip];
                float[] fromInt = UCWColorSpaceUtils.fromInt(pixel);
                if (lastPixel != -1) {
                    float[] lastInt = UCWColorSpaceUtils.fromInt(lastPixel);
                    float change = 0.2F;
                    if (!this.insideScope(this.highestChange(fromInt), this.highestChange(lastInt), change)) {
                        templateData[ip] = pixel;
                        continue;
                    }
                }
                float[] lab = UCWColorSpaceUtils.XYZtoLAB(UCWColorSpaceUtils.sRGBtoXYZ(fromInt));
                float lum = (float) Math.pow(lab[0] / 100f, 2.2F) * 100f;
                // luma is in the gcrSide range
                lum = ((lum - gcrSide[0]) / (gcrSide[1] - gcrSide[0]));
                // luma is in the 0..1 range
                lum = (lum * (maxL - minL)) + minL;
                // luma is in the minL..maxL range (still 0..1)
                lum = (float) Math.pow(lum, 1 / 2.2F) * 100f;
                // luma is now proper, i think?
                lab[0] = lum;
                lab[1] = gcrMiddle[2];
                lab[2] = gcrMiddle[3];
                templateData[ip] = UCWColorSpaceUtils.asInt(UCWColorSpaceUtils.XYZtosRGB(UCWColorSpaceUtils.LABtoXYZ(lab))) | 0xFF000000;
                lastPixel = pixel;
            }
        }

        setIconWidth(baseTex.getIconWidth());
        setIconHeight(baseTex.getIconHeight());
        addFrameTextureData(templateData);

        return false;
    }

    private float highestChange(float[] floats) {
        float highestFloat = floats[0];
        float smallestFloat = floats[0];
        for (int i = 1; i < floats.length; i++) {
            float f = floats[i];
            if (highestFloat < f) highestFloat = f;
            if (smallestFloat > f) smallestFloat = f;
        }
        return highestFloat - smallestFloat;
    }

    private boolean insideScope(float v1, float v2, float val) {
        return v1 < v2 + val && v1 > v2 - val;
    }
}