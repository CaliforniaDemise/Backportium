package surreal.backportium.client.textures;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.util.fromdebark.UCWColorSpaceUtils;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class DebarkedSpriteTopDumb extends SpriteDef {

    private final ResourceLocation base;

    public DebarkedSpriteTopDumb(String spriteName, ResourceLocation base) {
        super(spriteName);
        this.base = base;
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.of(base);
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        // we want specifically the *vanilla* texture
        BufferedImage baseImage = null;
        try {
            List<IResource> baseResource = manager.getAllResources(new ResourceLocation(this.base.getNamespace(), "textures/" + this.base.getPath() + ".png"));
            if (!baseResource.isEmpty()) {
                try (InputStream stream = baseResource.get(0).getInputStream()) {
                    baseImage = TextureUtil.readBufferedImage(stream);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (baseImage == null) {
            throw new RuntimeException("Could not load " + this.base + "!");
        }

        int[] templateData = new int[baseImage.getWidth() * baseImage.getHeight()];

        float minL = Float.MAX_VALUE;
        float maxL = Float.MIN_VALUE;

        for (int iy = 1; iy < baseImage.getHeight() - 1; iy++) {
            for (int ix = 1; ix < baseImage.getWidth() - 1; ix++) {
                int pixel = baseImage.getRGB(ix, iy);
                float lum = UCWColorSpaceUtils.sRGBtoLuma(UCWColorSpaceUtils.fromInt(pixel));
                if (lum < minL) minL = lum;
                if (lum > maxL) maxL = lum;
            }
        }

        minL = (float) Math.pow(minL / 100f, 2.2) * 100f;
        maxL = (float) Math.pow(maxL / 100f, 2.2) * 100f;

        int ip = 0;
        Random predictableRandom = new Random(1337);
        for (int iy = 0; iy < baseImage.getHeight(); iy++) {
            for (int ix = 0; ix < baseImage.getWidth(); ix++, ip++) {
                boolean isBorder = ix == 0 || iy == 0 || ix == (baseImage.getWidth() - 1) || iy == (baseImage.getHeight() - 1);
                if (isBorder) {
                    int luma = 96 + predictableRandom.nextInt(7);
                    templateData[ip] = (luma * 0x10101) | 0xFF000000;
                } else {
                    // grayscale and rescale
                    int pixel = baseImage.getRGB(ix, iy);
                    float lum = UCWColorSpaceUtils.sRGBtoLuma(UCWColorSpaceUtils.fromInt(pixel));
                    // luma is in range minL..maxL
                    lum = (float) Math.pow(lum / 100f, 2.2) * 100f;
                    lum = ((lum - minL) / (maxL - minL)) * 100f;
                    // luma is in range 0..100
                    lum = (lum / 2f) + 50f;
                    lum = (float) Math.pow(lum / 100f, 1 / 2.2) * 100f;
                    templateData[ip] = UCWColorSpaceUtils.asInt(UCWColorSpaceUtils.XYZtosRGB(UCWColorSpaceUtils.LABtoXYZ(new float[] { lum, 0, 0 }))) | 0xFF000000;
                }
            }
        }

        setIconWidth(baseImage.getWidth());
        setIconHeight(baseImage.getHeight());
        addFrameTextureData(templateData);

        return false;
    }
}
