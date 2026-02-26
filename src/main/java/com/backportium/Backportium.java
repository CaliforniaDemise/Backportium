package com.backportium;

import com.backportium.client.Client;
import com.backportium.init.BackportBlocks;
import com.backportium.init.BackportItems;
import com.backportium.registry.RegistryManager;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
@SuppressWarnings("unused")
public final class Backportium {

    @SideOnly(Side.CLIENT)
    private Client client;

    private RegistryManager registry;

    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        client = registry.createClient();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        cleanup();
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        registry.registerBlocks(event);
        BackportBlocks.init();
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        registry.registerItems(event);
        BackportItems.init();
    }

    @SubscribeEvent
    public void registerPotions(RegistryEvent.Register<Potion> event) {
        registry.registerPotions(event);
    }

    @SubscribeEvent
    public void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        registry.registerPotionTypes(event);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        registry.registerModels(event);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void stitchTextures(TextureStitchEvent.Pre event) {
        client.stitchTextures(event);
    }

    private void cleanup() {
        this.registry = null;
        System.gc();
    }
}
