package surreal.backportium._internal.client.renderer.sprite;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

// Stolen from Debark: https://github.com/asiekierka/Debark/blob/master/src/main/java/pl/asie/debark/util/CustomSprite.java
public class SpriteDef extends TextureAtlasSprite {

    protected SpriteDef(String spriteName) {
        super(spriteName);
    }

    @Override
    public boolean hasCustomLoader(@NotNull IResourceManager manager, @NotNull ResourceLocation location) {
        return true;
    }

    protected void addFrameTextureData(int[] data) {
        int[][] templateData = new int[Minecraft.getMinecraft().getTextureMapBlocks().getMipmapLevels() + 1][];
        templateData[0] = data;
        framesTextureData.add(templateData);
    }
}