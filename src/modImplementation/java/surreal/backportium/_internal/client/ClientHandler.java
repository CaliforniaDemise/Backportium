package surreal.backportium._internal.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium.Tags;
import surreal.backportium._internal.client.renderer.TextureStitcher;
import surreal.backportium._internal.client.renderer.model.ModelBaker;
import surreal.backportium._internal.client.renderer.sprite.GrayScaleSprite;
import surreal.backportium._internal.client.resource.FetchAssets;
import surreal.backportium.api.entity.EntityState;
import surreal.backportium.api.entity.EntityWithState;
import surreal.backportium.api.item.UseAction;
import surreal.backportium.init.ModActions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientHandler {

    private final List<ModelBaker> bakers = new ArrayList<>(4);
    private final List<TextureStitcher> textures = new ArrayList<>(8);

    public void addTextureStitcher(TextureStitcher stitcher) {
        textures.add(stitcher);
    }

    public void addModelBaker(ModelBaker baker) {
        bakers.add(baker);
    }

    // Lifecycle
    public void construction(FMLConstructionEvent event) {
        FetchAssets.readAssets();
    }

    // Events
    public void stitchTextures(TextureStitchEvent.Pre event) {
        textures.forEach(stitcher -> stitcher.stitchTextures(event.getMap()));
        TextureMap map = event.getMap();
        stitchWaterTextures(map);
    }

    public void bakeModels(ModelBakeEvent event) {
        bakers.forEach(baker -> baker.bakeModels(event));
    }

    public void renderSpecificHand(RenderSpecificHandEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        UseAction action = ModActions.getUseAction(stack);
        if (action != null && action.renderHand(mc.world, mc.player, stack, event.getHand(), mc.getItemRenderer(), event.getEquipProgress(), stack.getMaxItemUseDuration() - (mc.player.getItemInUseCount() - event.getPartialTicks() + 1.0F), event.getPartialTicks(), (event.getHand() == EnumHand.MAIN_HAND ? mc.player.getPrimaryHand() : mc.player.getPrimaryHand().opposite()) == EnumHandSide.RIGHT)) {
            event.setCanceled(true);
        }
        else {
            EntityState move = EntityWithState.cast(mc.player).getState();
            if (move != null) {
                if (move.renderHand(mc.world, mc.player, stack, event.getHand(), mc.getItemRenderer(), event.getEquipProgress(), stack.getMaxItemUseDuration() - (mc.player.getItemInUseCount() - event.getPartialTicks() + 1.0F), event.getPartialTicks(), (event.getHand() == EnumHand.MAIN_HAND ? mc.player.getPrimaryHand() : mc.player.getPrimaryHand().opposite()) == EnumHandSide.RIGHT)) {
                    event.setCanceled(true);
                }
            }
        }
    }

    private static void stitchWaterTextures(TextureMap map) {
        try {
            IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
            IResource resource = manager.getResource(new ResourceLocation("textures/blocks/water_still.png"));
            if (resource.getResourcePackName().endsWith("Minecraft Forge")) {
                map.registerSprite(new ResourceLocation(Tags.MOD_ID, "blocks/water_still"));
                map.registerSprite(new ResourceLocation(Tags.MOD_ID, "blocks/water_flow"));
            }
            else {
                map.setTextureEntry(new GrayScaleSprite(Tags.MOD_ID + ":blocks/water_still", new ResourceLocation("blocks/water_still")));
                map.setTextureEntry(new GrayScaleSprite(Tags.MOD_ID + ":blocks/water_flow", new ResourceLocation("blocks/water_flow")));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
