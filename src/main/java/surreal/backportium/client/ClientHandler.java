package surreal.backportium.client;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import surreal.backportium.Backportium;
import surreal.backportium.Tags;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.api.helper.RiptideHelper;
import surreal.backportium.client.model.entity.ModelPhantom;
import surreal.backportium.client.model.entity.ModelTrident;
import surreal.backportium.client.renderer.entity.RenderPhantom;
import surreal.backportium.client.renderer.entity.RenderTrident;
import surreal.backportium.client.renderer.tile.TESRConduit;
import surreal.backportium.client.resource.Models;
import surreal.backportium.client.resource.Sounds;
import surreal.backportium.client.resource.Textures;
import surreal.backportium.client.textures.DebarkedSpriteSide;
import surreal.backportium.client.textures.DebarkedSpriteTop;
import surreal.backportium.client.textures.DebarkedSpriteTopDumb;
import surreal.backportium.core.BPHooks;
import surreal.backportium.core.BPPlugin;
import surreal.backportium.entity.v1_13.EntityPhantom;
import surreal.backportium.entity.v1_13.EntityTrident;
import surreal.backportium.item.ModItems;
import surreal.backportium.tile.v1_13.TileConduit;
import surreal.backportium.util.RandomHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;

import static net.minecraftforge.fml.client.registry.RenderingRegistry.registerEntityRenderingHandler;

@SuppressWarnings("unused")
public class ClientHandler {

    private static final Gson vanillaGson;

    public static void construction(FMLConstructionEvent event) {
        if (FMLLaunchHandler.side() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(ClientHandler.class);

            Models.initModels();
            Textures.initTextures();
            Sounds.initSounds();
        }
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (FMLLaunchHandler.side() == Side.CLIENT) {
            registerEntityRenderers();
        }
    }

    private static void registerEntityRenderers() {
        registerEntityRenderingHandler(EntityTrident.class, m -> new RenderTrident<>(m, new ModelTrident()));
        registerEntityRenderingHandler(EntityPhantom.class, m -> new RenderPhantom(m, new ModelPhantom(), 0.6F));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModItems.registerModels(event);
        ClientRegistry.bindTileEntitySpecialRenderer(TileConduit.class, new TESRConduit());
    }

    private static String getValueName(Object obj) {
        if (obj instanceof IStringSerializable) return ((IStringSerializable) obj).getName();
        else return obj.toString();
    }

    @SubscribeEvent
    public static void bakeModels(ModelBakeEvent event) {
        try {
            IModel model = ModelLoaderRegistry.getModel(new ResourceLocation("block/cube_column"));
            for (Map.Entry<Block, Block> entry : BPHooks.DEBARKED_LOG_BLOCKS.entrySet()) {
                IProperty<?> property = entry.getValue().getBlockState().getProperty("axis");
                Object x = null, y = null, z = null;
                if (property == null) {
                    System.out.println("Could not find property 'axis' on block " + entry.getValue().getRegistryName());
                    continue;
                }
                if (property.getValueClass() == EnumFacing.Axis.class) {
                    x = EnumFacing.Axis.X;
                    y = EnumFacing.Axis.Y;
                    z = EnumFacing.Axis.Z;
                }
                else if (property.getValueClass() == BlockLog.EnumAxis.class) {
                    x = BlockLog.EnumAxis.X;
                    y = BlockLog.EnumAxis.Y;
                    z = BlockLog.EnumAxis.Z;
                }
                else {
                    System.out.println("'Axis' property type " + property.getValueClass() + " does not match for block " + entry.getValue().getRegistryName());
                }
                Map<IBlockState, ModelResourceLocation> modelLocations = event.getModelManager().getBlockModelShapes().getBlockStateMapper().getVariants(entry.getKey());
                Map<IBlockState, ModelResourceLocation> dModelLocations = event.getModelManager().getBlockModelShapes().getBlockStateMapper().getVariants(entry.getValue());

                for (Map.Entry<IBlockState, ModelResourceLocation> entry1 : modelLocations.entrySet()) {
                    List<String> ass = null;
                    {
                        ResourceLocation loc = modelLocations.get(entry1.getKey());
                        ResourceLocation bsLoc = new ResourceLocation(loc.getNamespace(), "blockstates/" + loc.getPath() + ".json");
                        IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(bsLoc);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                        ModelBlockDefinition definition = BlockStateLoader.load(reader, bsLoc, vanillaGson);
                        for (VariantList list : definition.getMultipartVariants()) {
                            for (Variant variant : list.getVariantList()) {
                                String varStr = variant.toString();
                                if (varStr.startsWith("TexturedVariant")) {
                                    ass = new ArrayList<>(2);
                                    StringBuilder builder = new StringBuilder();
                                    for (int i = 17; i < varStr.length(); i++) {
                                        char c = varStr.charAt(i);
                                        if (c == ' ') {
                                            if (varStr.charAt(i + 1) != '=' && varStr.charAt(i - 1) != '=') {
                                                ass.add(builder.toString());
                                                builder = new StringBuilder();
                                            }
                                            continue;
                                        }
                                        builder.append(c);
                                        if (i == varStr.length() - 1) {
                                            ass.add(builder.toString());
                                        }
                                    }
                                }
                            }
                        }
                        reader.close();
                    }
                    IModel oModel = ModelLoaderRegistry.getModel(entry1.getValue());
                    for (ResourceLocation loc : oModel.getDependencies()) {
                        IModel origModel = ModelLoaderRegistry.getModel(loc);
                        Optional<ModelBlock> origModelBlockOpt = origModel.asVanillaModel();
                        if (origModelBlockOpt.isPresent()) {
                            ModelBlock origModelBlock = origModelBlockOpt.get();
                            ImmutableMap<String, String> map;
                            {
                                ImmutableMap.Builder<String, String> textureMapBuilder = new ImmutableMap.Builder<>();
                                if (ass == null) {
                                    for (Map.Entry<String, String> texEntry : origModelBlock.textures.entrySet()) {
                                        textureMapBuilder.put(texEntry.getKey(), texEntry.getValue() + "_debarked");
                                    }
                                }
                                else {
                                    for (String assType : ass) {
                                        String[] split = assType.split("=");
                                        textureMapBuilder.put(split[0], split[1] + "_debarked");
                                    }
                                }
                                map = textureMapBuilder.build();
                            }
                            IModel debarkedModel = origModel.retexture(map);
                            int meta = entry.getKey().getMetaFromState(entry1.getKey());
                            IBlockState debarkedState = entry.getValue().getStateFromMeta(meta);

                            if (debarkedState.getValue(property) == y) {
                                IBakedModel m = debarkedModel.bake(debarkedModel.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
                                event.getModelRegistry().putObject(new ModelResourceLocation(Objects.requireNonNull(entry.getValue().getRegistryName()), "normal"), m);
                                event.getModelRegistry().putObject(new ModelResourceLocation(Objects.requireNonNull(entry.getValue().getRegistryName()), RandomHelper.getVariantFromState(debarkedState)), m);
                            }
                            ModelRotation rotation = ModelRotation.X0_Y0;
                            if (debarkedState.getValue(property) == x) rotation = ModelRotation.X90_Y90;
                            else if (debarkedState.getValue(property) == z) rotation = ModelRotation.X90_Y0;
                            event.getModelRegistry().putObject(dModelLocations.get(debarkedState), debarkedModel.bake(rotation, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()));
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public static void registerTextures(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        map.registerSprite(new ResourceLocation("entity/conduit/wind"));
        map.registerSprite(new ResourceLocation("entity/conduit/wind_vertical"));
        map.registerSprite(new ResourceLocation("particle/nautilus"));
        map.registerSprite(new ResourceLocation("mob_effect/conduit_power"));
        map.registerSprite(new ResourceLocation("mob_effect/dolphins_grace"));
        map.registerSprite(new ResourceLocation("mob_effect/slow_falling"));

        map.setTextureEntry(new DebarkedSpriteTopDumb("backportium:blocks/log_debarked", new ResourceLocation("blocks/log_oak_top")));
        ResourceLocation debarkedSprite = new ResourceLocation(Tags.MOD_ID, "blocks/log_debarked");

        for (Map.Entry<Block, Block> entry : BPHooks.DEBARKED_LOG_BLOCKS.entrySet()) {
            boolean vanilla = Objects.requireNonNull(entry.getKey().getRegistryName()).getNamespace().equals("minecraft");
            Map<IBlockState, ModelResourceLocation> modelLocations = Minecraft.getMinecraft().modelManager.getBlockModelShapes().getBlockStateMapper().getVariants(entry.getKey());
            try {
                for (Map.Entry<IBlockState, ModelResourceLocation> entry1 : modelLocations.entrySet()) {
                    List<String> ass = null;
                    {
                        ResourceLocation loc = modelLocations.get(entry1.getKey());
                        ResourceLocation bsLoc = new ResourceLocation(loc.getNamespace(), "blockstates/" + loc.getPath() + ".json");
                        IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(bsLoc);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                        ModelBlockDefinition definition = BlockStateLoader.load(reader, bsLoc, vanillaGson);
                        for (VariantList list : definition.getMultipartVariants()) {
                            for (Variant variant : list.getVariantList()) {
                                String varStr = variant.toString();
                                if (varStr.startsWith("TexturedVariant")) {
                                    ass = new ArrayList<>(2);
                                    StringBuilder builder = new StringBuilder();
                                    for (int i = 17; i < varStr.length(); i++) {
                                        char c = varStr.charAt(i);
                                        if (c == ' ') {
                                            if (varStr.charAt(i + 1) != '=' && varStr.charAt(i - 1) != '=') {
                                                ass.add(builder.toString());
                                                builder = new StringBuilder();
                                            }
                                            continue;
                                        }
                                        builder.append(c);
                                        if (i == varStr.length() - 1) {
                                            ass.add(builder.toString());
                                        }
                                    }
                                }
                            }
                        }
                        reader.close();
                    }
                    IModel oModel = ModelLoaderRegistry.getModel(entry1.getValue());
                    for (ResourceLocation loc : oModel.getDependencies()) {
                        IModel origModel = ModelLoaderRegistry.getModel(loc);
                        Optional<ModelBlock> origModelBlockOpt = origModel.asVanillaModel();
                        if (origModelBlockOpt.isPresent()) {
                            ModelBlock origModelBlock = origModelBlockOpt.get();
                            String end = null, side = null;
                            if (ass == null) {
                                for (Map.Entry<String, String> texEntry : origModelBlock.textures.entrySet()) {
                                    if (texEntry.getKey().equals("end")) end = texEntry.getValue();
                                    else if (texEntry.getKey().equals("side")) side = texEntry.getValue();
                                }
                            }
                            else {
                                for (String assType : ass) {
                                    String[] split = assType.split("=");
                                    if (split[0].equals("end")) end = split[1];
                                    else if (split[0].equals("side")) side = split[1];
                                }
                            }
                            if (end == null || side == null) {
                                continue;
                            }
                            if (vanilla) {
                                side = "minecraft:" + side;
                                end = "minecraft:" + end;
                            }
                            ResourceLocation endSprite = new ResourceLocation(end);
                            map.setTextureEntry(new DebarkedSpriteSide(side + "_debarked", endSprite, new ResourceLocation(side)));
                            map.setTextureEntry(new DebarkedSpriteTop(end + "_debarked", endSprite, debarkedSprite));
                        }
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
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

    static {
        Field f_gson = ObfuscationReflectionHelper.findField(ModelBlockDefinition.class, "field_178333_a");
        f_gson.setAccessible(true);
        try {
            vanillaGson = (Gson) f_gson.get(null);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
