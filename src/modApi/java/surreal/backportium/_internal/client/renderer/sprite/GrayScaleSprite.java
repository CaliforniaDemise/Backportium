package surreal.backportium._internal.client.renderer.sprite;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.util.SpriteUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * Generates and registers gray scaled version of the given sprite.
 * Used for water texture.
 **/
public class GrayScaleSprite extends SpriteDef {

    private final ResourceLocation sprite;

    static final Field f_animationMetadata;

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
        TextureAtlasSprite colored = SpriteUtil.loadSpriteOrWarn(this.sprite, textureGetter);
        try { this.initAnimationMetadata(colored); }
        catch (Exception e) { throw new RuntimeException(e); }
        for (int f = 0; f < colored.getFrameCount(); f++) {
            int[] coloredData = colored.getFrameTextureData(f)[0];
            int[] outData = new int[colored.getIconWidth() * colored.getIconHeight()];
            for (int y = 0; y < colored.getIconHeight(); y++) {
                for (int x = 0; x < colored.getIconWidth(); x++) {
                    int pixel = coloredData[y * colored.getIconWidth() + x];
                    double r = ((pixel >> 16) & 0xFF) / 255.0;
                    double g = ((pixel >> 8) & 0xFF) / 255.0;
                    double b = (pixel & 0xFF) / 255.0;
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

    private void initAnimationMetadata(TextureAtlasSprite colored) throws Exception {
        AnimationMetadataSection coloredAnimationMetadata = (AnimationMetadataSection) f_animationMetadata.get(colored);
        if (coloredAnimationMetadata != null) {
            f_animationMetadata.set(this, coloredAnimationMetadata);
        }
    }

    static {
        try {
            f_animationMetadata = TextureAtlasSprite.class.getDeclaredField(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "animationMetadata" : "field_110982_k");
            f_animationMetadata.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
            throw new RuntimeException("Problem occurred while trying to reach to TextureAtlasSprite#animationMetadata", e);
        }
    }
}