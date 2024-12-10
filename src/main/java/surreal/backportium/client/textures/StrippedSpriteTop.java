package surreal.backportium.client.textures;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.util.fromdebark.SpriteUtils;
import surreal.backportium.util.fromdebark.UCWColorSpaceUtils;

import java.util.Collection;
import java.util.function.Function;

public class StrippedSpriteTop extends SpriteDef {
    private final ResourceLocation base;
    private final ResourceLocation template;

    public StrippedSpriteTop(String spriteName, ResourceLocation base, ResourceLocation template) {
        super(spriteName);
        this.base = base;
        this.template = template;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.of(base, template);
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        TextureAtlasSprite baseTex = SpriteUtils.loadSpriteOrWarn(base, textureGetter);
        TextureAtlasSprite templateTex = SpriteUtils.loadSpriteOrWarn(template, textureGetter);

        float minL = Float.MAX_VALUE;
        float maxL = Float.MIN_VALUE;
        double A = 0;
        double B = 0;
        int count = 0;

        int offset = (baseTex.getIconWidth() + 7) / 8;

        int[] baseData = SpriteUtils.getFrameDataOrWarn(baseTex);
        for (int iy = offset; iy < baseTex.getIconHeight() - offset; iy++) {
            for (int ix = offset; ix < baseTex.getIconWidth() - offset; ix++) {
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

        // recolor template texture
        int[] templateData = new int[templateTex.getIconWidth() * templateTex.getIconHeight()];
        int[] templateInput = SpriteUtils.getFrameDataOrWarn(templateTex);
        for (int i = 0; i < templateData.length; i++) {
            int oldPixel = templateInput[i];
            float[] scaledPixel = UCWColorSpaceUtils.fromInt(oldPixel);
            float l = (UCWColorSpaceUtils.sRGBtoLuma(scaledPixel));
            l = (float) Math.pow(l / 100f, 2.2) * 100f;
            l = (((l / 50f) - 1f) * (maxL - minL)) + minL;
            if (l < 0f) l = 0f;
            else if (l > 100f) l = 100f;
            float[] lab = new float[] { l, (float) A, (float) B };
            templateData[i] = UCWColorSpaceUtils.asInt(UCWColorSpaceUtils.XYZtosRGB(UCWColorSpaceUtils.LABtoXYZ(lab))) | 0xFF000000;
        }

        setIconWidth(templateTex.getIconWidth());
        setIconHeight(templateTex.getIconHeight());
        addFrameTextureData(templateData);

        return false;
    }
}
