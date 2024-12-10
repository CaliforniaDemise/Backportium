package surreal.backportium.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium.Backportium;
import surreal.backportium.Tags;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.api.helper.RiptideHelper;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.client.model.entity.ModelPhantom;
import surreal.backportium.client.model.entity.ModelTrident;
import surreal.backportium.client.renderer.entity.RenderPhantom;
import surreal.backportium.client.renderer.entity.RenderTrident;
import surreal.backportium.client.resource.Models;
import surreal.backportium.client.resource.Sounds;
import surreal.backportium.client.resource.Textures;
import surreal.backportium.client.textures.AnimatedSpriteStill;
import surreal.backportium.core.BPPlugin;
import surreal.backportium.core.util.LogSystem;
import surreal.backportium.entity.v1_13.EntityPhantom;
import surreal.backportium.entity.v1_13.EntityTrident;

import static net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientHandler {

    public static void construction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(ClientHandler.class);
        Models.initModels();
        Textures.initTextures();
        Sounds.initSounds();
    }

    public static void preInit(FMLPreInitializationEvent event) {
        registerEntityRenderers();
    }

    private static void registerEntityRenderers() {
        registerEntityRenderingHandler(EntityTrident.class, m -> new RenderTrident<>(m, new ModelTrident()));
        registerEntityRenderingHandler(EntityPhantom.class, m -> new RenderPhantom(m, new ModelPhantom(), 0.6F));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModBlocks.registerStateMappers();
    }

    @SubscribeEvent
    public static void colorBlocks(ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        colors.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> worldIn != null && pos != null ? BiomeColorHelper.getWaterColorAtPos(worldIn, pos) : -1, ModBlocks.BUBBLE_COLUMN);
    }

    private static String getValueName(Object obj) {
        if (obj instanceof IStringSerializable) return ((IStringSerializable) obj).getName();
        else return obj.toString();
    }

    private static void bakeLogModels(ModelBakeEvent event, Block origLog, Block addLog) {
        LogSystem system = LogSystem.INSTANCE;
    }

    @SubscribeEvent
    public static void bakeModels(ModelBakeEvent event) {
        LogSystem system = LogSystem.INSTANCE;
        system.bakeModels(event);
    }

    @SubscribeEvent
    public static void registerTextures(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "entity/conduit/wind"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "entity/conduit/wind_vertical"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "particle/nautilus"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "mob_effect/conduit_power"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "mob_effect/dolphins_grace"));
        map.registerSprite(new ResourceLocation(Tags.MOD_ID, "mob_effect/slow_falling"));

        {
            map.setTextureEntry(new AnimatedSpriteStill(new ResourceLocation(Tags.MOD_ID, "blocks/seagrass"), Tags.MOD_ID + ":items/seagrass"));
        }

        LogSystem.INSTANCE.registerTextures(event);
    }

    // Trident
    @SubscribeEvent
    public static void renderSpecificHand(RenderSpecificHandEvent event) {

        EntityPlayer player = Minecraft.getMinecraft().player;
        EnumHand hand = event.getHand();
        ItemStack stack = event.getItemStack();

        EnumHandSide handSide = hand == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();

        if (stack.getItemUseAction() == Backportium.SPEAR && player.isHandActive() && player.getActiveHand() == hand) {
            event.setCanceled(true);

            World world = Minecraft.getMinecraft().world;

            float partialTicks = event.getPartialTicks();
            float equipProgress = event.getEquipProgress();

            float useTime = stack.getMaxItemUseDuration() - (player.getItemInUseCount() - partialTicks + 1.0F);

            boolean rightArm = handSide == EnumHandSide.RIGHT;

            GlStateManager.pushMatrix();

            int i = rightArm ? 1 : -1;

            GlStateManager.translate((float) i * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);

            GlStateManager.translate(i * -0.25F, 0.8F, -0.125F);

            float useTranslate = useTime / 60F;
            float useRotate = useTranslate / 10F;

            if (useTranslate > 0.175F) useTranslate = 0.175F;
            if (useRotate > 1F) useRotate = 1F;

            GlStateManager.translate(0, useTranslate, 0);
            GlStateManager.translate(0, 0, useTranslate);

            GlStateManager.rotate(i * -8.5F, 0, 1, 0);
            GlStateManager.rotate(useRotate, 0, 1, 0);

            GlStateManager.rotate(-61.5F, 1, 0, 0);
            GlStateManager.rotate(i * -1.1F, 0, 0, 1);

            if (useTranslate > 0.1F) {
                float f7 = MathHelper.sin((useTime - 0.1F) * 1.3F) * 0.0011F;
                GlStateManager.translate(f7, f7, f7);
            }

            Minecraft.getMinecraft().getItemRenderer().renderItemSide(player, stack, rightArm ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightArm);
            GlStateManager.popMatrix();
        }
        else if (stack.getItemUseAction() == Backportium.SPEAR && RiptideHelper.isInRiptide(player)) {
            event.setCanceled(true);

            World world = Minecraft.getMinecraft().world;

            float partialTicks = event.getPartialTicks();

            boolean rightArm = handSide == EnumHandSide.RIGHT;
            int i = rightArm ? 1 : -1;

            float equipProgress = event.getEquipProgress();

            GlStateManager.pushMatrix();
            GlStateManager.translate((float) i * 0.21F, -0.34F + equipProgress * -0.6F, -0.46F);

            GlStateManager.rotate(i * 95, 0, 0, 1);
            GlStateManager.rotate(i * -5, 0, 1, 0);
            GlStateManager.rotate(-65F, 1, 0, 0);

            Minecraft.getMinecraft().getItemRenderer().renderItemSide(player, stack, rightArm ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightArm);
            GlStateManager.popMatrix();
        }
    }

    // Fluidlogging
    @SubscribeEvent
    public static void modifyFov(EntityViewRenderEvent.FOVModifier event) {
        if (BPPlugin.FLUIDLOGGED) return;
        IBlockState state = ActiveRenderInfo.getBlockStateAtEntityViewpoint(event.getEntity().world, event.getEntity(), Minecraft.getMinecraft().getRenderPartialTicks());
        if (state.getBlock() instanceof FluidLogged) {
            event.setFOV(event.getFOV() * 60.0F / 70.0F);
        }
    }
}
