package surreal.backportium.client.textures;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.util.fromdebark.SpriteUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * Generates and registers gray scaled version of the given sprite.
 * Used for water texture.
 **/
// TODO Might be better to do it for purple shulker box too
public class GrayScaleSprite extends SpriteDef {

    private final ResourceLocation sprite;

    public GrayScaleSprite(String spriteName, ResourceLocation sprite) {
        super(spriteName);
        this.sprite = sprite;
    }

    @NotNull
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.singletonList(this.sprite);
    }

    @Override
    public boolean load(@NotNull IResourceManager manager, @NotNull ResourceLocation location, @NotNull Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
        TextureAtlasSprite colored = SpriteUtils.loadSpriteOrWarn(this.sprite, textureGetter);
        this.animationMetadata = colored.animationMetadata; // This shit so ass
        for (int f = 0; f < colored.getFrameCount(); f++) {
            int[] coloredData = colored.getFrameTextureData(f)[0];
            int[] outData = new int[colored.getIconWidth() * colored.getIconHeight()];
            for (int y = 0; y < colored.getIconHeight(); y++) {
                for (int x = 0; x < colored.getIconWidth(); x++) {
                    int pixel = coloredData[y * colored.getIconWidth() + x];
                    double r = ((pixel >> 16) & 0xFF) / 255.0;
                    double g = ((pixel >> 8) & 0xFF) / 255.0;
                    double b = (pixel & 0xFF) / 255.0;
//                int gs = (int) (MathHelper.clamp(((r + g + b) / 3.0F), 0.0F, 1.0F) * 255F); // Average color
//                int gs = (int) (MathHelper.clamp((0.299 * r) + (0.587 * g) + (0.114 * b), 0.0, 1.0) * 255.0); // Luminosity
                    int gs = (int) ((Math.max(Math.max(r, g), b) + Math.min(Math.min(r, g), b)) / 2.0 * 255.0); // Desaturation
                    outData[y * colored.getIconWidth() + x] = (pixel & 0xFF000000) | (gs << 16) | (gs << 8) | gs;
                }
            }
            this.addFrameTextureData(outData);
        }
        this.setIconWidth(colored.getIconWidth());
        this.setIconHeight(colored.getIconHeight());
        return false;
    }
}
