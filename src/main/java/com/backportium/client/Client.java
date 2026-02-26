package com.backportium.client;

import com.backportium.registry.client.TextureStitcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class Client {

    private final TextureStitcher[] stitchers;

    public Client(TextureStitcher[] stitchers) {
        this.stitchers = stitchers;
    }

    public void stitchTextures(TextureStitchEvent.Pre event) {
        TexMap map = new TexMap(event.getMap());
        for (TextureStitcher stitcher : stitchers) {
            stitcher.stitchTextures(map);
        }
    }

    @SideOnly(Side.CLIENT)
    private static final class TexMap implements TextureStitcher.Map {

        private final TextureMap map;

        TexMap(TextureMap map) {
            this.map = map;
        }

        @Override
        public void register(ResourceLocation location) {
            this.map.registerSprite(location);
        }

        @Override
        public void register(TextureAtlasSprite sprite) {
            this.map.setTextureEntry(sprite);
        }

        @Override
        public TextureAtlasSprite getSprite(String location) {
            return map.getAtlasSprite(location);
        }
    }
}
