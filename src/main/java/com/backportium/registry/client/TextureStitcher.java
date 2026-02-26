package com.backportium.registry.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface TextureStitcher {

    void stitchTextures(Map map);

    interface Map {

        void register(ResourceLocation location);

        @SideOnly(Side.CLIENT)
        void register(TextureAtlasSprite sprite);

        @SideOnly(Side.CLIENT)
        TextureAtlasSprite getSprite(String location);
    }
}
