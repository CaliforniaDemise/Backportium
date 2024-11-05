package surreal.backportium.client.textures;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class AnimatedSpriteStill extends SpriteDef {

    private final ResourceLocation animatedIcon;

    public AnimatedSpriteStill(ResourceLocation animatedIcon, String textureName) {
        super(textureName);
        this.animatedIcon = animatedIcon;
    }

    @Override
    public void updateAnimation() {}

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true;
    }

    @Override
    public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        BufferedImage baseImage;
        try {
            List<IResource> baseResource = manager.getAllResources(new ResourceLocation(this.animatedIcon.getNamespace(), "textures/" + this.animatedIcon.getPath() + ".png"));
            try (InputStream stream = baseResource.get(0).getInputStream()) {
                baseImage = TextureUtil.readBufferedImage(stream);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.setIconHeight(baseImage.getWidth());
        this.setIconWidth(baseImage.getWidth());

        int[] data = new int[this.getIconWidth() * this.getIconHeight()];

        int ip = 0;
        for (int iy = 0; iy < this.getIconHeight(); iy++) {
            for (int ix = 0; ix < this.getIconWidth(); ix++, ip++) {
                int pixel = baseImage.getRGB(ix, iy);
                data[ip] = pixel;
            }
        }

        this.addFrameTextureData(data);
        return false;
    }

    @Override
    public boolean hasAnimationMetadata() {
        return false;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.singletonList(this.animatedIcon);
    }
}
