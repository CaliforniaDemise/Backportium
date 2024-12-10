package surreal.backportium.core.util;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import forestry.api.arboriculture.EnumVanillaWoodType;
import forestry.api.arboriculture.IWoodType;
import forestry.arboriculture.blocks.BlockForestryLog;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResource;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.BlockStateLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import surreal.backportium.client.textures.DebarkedSpriteSide;
import surreal.backportium.client.textures.DebarkedSpriteTop;
import surreal.backportium.client.textures.DebarkedSpriteTopDumb;
import surreal.backportium.util.RandomHelper;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

/**
 * Used for registering Stripped and Bark variants of logs without a headache.
 **/
public class LogSystem {

    public static LogSystem INSTANCE = new LogSystem();

    private final Map<Block, Block> stripped; // origLog, stripped
    private final Map<Block, Block> bark; // origLog, bark
    private final Map<Block, Block> sBark; // origLog, strippedBark
    private final Map<Block, ItemBlock> items; // stripped/bark/sbark, item

    private final Gson vanillaGson;

    public LogSystem() {
        this.stripped = new HashMap<>();
        this.bark = new HashMap<>();
        this.sBark = new HashMap<>();
        this.items = new HashMap<>();
        if (FMLLaunchHandler.side().isClient()) {
            Field f_gson = ObfuscationReflectionHelper.findField(ModelBlockDefinition.class, "field_178333_a");
            f_gson.setAccessible(true);
            try { vanillaGson = (Gson) f_gson.get(null); } catch (IllegalAccessException e) { throw new RuntimeException(e); }
        }
        else this.vanillaGson = null;
    }

    public void register(Block log, @Nullable Block stripped, @Nullable Block bark, @Nullable Block strippedBark) {
        if (stripped != null) this.stripped.put(log, stripped);
        if (bark != null) this.bark.put(log, bark);
        if (strippedBark != null) this.sBark.put(log, strippedBark);
    }

    public void registerItem(Block addLog, ItemBlock item) {
        this.items.put(addLog, item);
    }

    @Nullable
    public Block getStripped(Block origLog) {
        return this.stripped.get(origLog);
    }

    @Nullable
    public Block getBark(Block origLog) {
        return this.bark.get(origLog);
    }

    @Nullable
    public Block getStrippedBark(Block origLog) {
        return this.sBark.get(origLog);
    }

    @Nullable
    public ItemBlock getItem(Block addLog) {
        return this.items.get(addLog);
    }

    public void forEachBlock(Consumer<Block> consumer) {
        this.stripped.keySet().forEach(consumer);
    }

    public void registerOres(FMLInitializationEvent event) {
        this.forEachBlock(origLog -> {
            Block strippedBlock = this.getStripped(origLog);
            Block barkBlock = this.getBark(origLog);
            Block strippedBarkBlock = this.getStrippedBark(origLog);
            if (strippedBlock != null || barkBlock != null || strippedBarkBlock != null) {
                int meta = OreDictionary.WILDCARD_VALUE;
                int[] ids = OreDictionary.getOreIDs(new ItemStack(origLog, 1, meta));
                if (ids.length == 0) {
                    ids = OreDictionary.getOreIDs(new ItemStack(origLog));
                    meta = 0;
                }
                ItemStack stripped = ItemStack.EMPTY;
                ItemStack bark = ItemStack.EMPTY;
                ItemStack strippedBark = ItemStack.EMPTY;
                {
                    if (strippedBlock != null) stripped = new ItemStack(strippedBlock, 1, meta);
                    if (barkBlock != null) bark = new ItemStack(barkBlock, 1, meta);
                    if (strippedBarkBlock != null) strippedBark = new ItemStack(strippedBarkBlock, 1, meta);
                }
                for (int i : ids) {
                    if (!stripped.isEmpty()) OreDictionary.registerOre(OreDictionary.getOreName(i), stripped);
                    if (!bark.isEmpty()) OreDictionary.registerOre(OreDictionary.getOreName(i), bark);
                    if (!strippedBark.isEmpty()) OreDictionary.registerOre(OreDictionary.getOreName(i), strippedBark);
                }
                if (!stripped.isEmpty()) OreDictionary.registerOre("logStripped", stripped);
                if (!strippedBark.isEmpty()) OreDictionary.registerOre("logStripped", strippedBark);
            }
        });
    }

    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        this.items.forEach((key, value) -> {
            NonNullList<ItemStack> list = NonNullList.create();
            value.getSubItems(CreativeTabs.SEARCH, list);
            for (ItemStack stack : list) {
                int meta = stack.getMetadata();
                IBlockState state = key.getStateFromMeta(meta);
                IProperty<?> property = key.getBlockState().getProperty("axis");
                if (property != null) {
                    if (property.getValueClass() == EnumFacing.Axis.class) state = state.withProperty((IProperty<EnumFacing.Axis>) property, EnumFacing.Axis.Y);
                    else if (property.getValueClass() == BlockLog.EnumAxis.class) state = state.withProperty((IProperty<BlockLog.EnumAxis>) property, BlockLog.EnumAxis.Y);
                }
                String variantIn = RandomHelper.getVariantFromState(state);
                ModelLoader.setCustomModelResourceLocation(value, meta, new ModelResourceLocation(Objects.requireNonNull(key.getRegistryName()), variantIn));
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public void bakeModels(ModelBakeEvent event) {
        boolean hasForestry = Loader.isModLoaded("forestry");
        this.forEachBlock(origLog -> {
            Block stripped = this.getStripped(origLog);
            Block bark = this.getBark(origLog);
            Block strippedBark = this.getStrippedBark(origLog);
            if (hasForestry && origLog instanceof BlockForestryLog<?>) {
                if (stripped != null) bakeForestryModels(event, origLog, stripped);
                if (bark != null) bakeForestryModels(event, origLog, bark);
                if (strippedBark != null) bakeForestryModels(event, origLog, strippedBark);
            }
            else {
                if (stripped != null) bakeModel(event, origLog, stripped, this.vanillaGson);
                if (bark != null) bakeModel(event, origLog, bark, this.vanillaGson);
                if (strippedBark != null) bakeModel(event, origLog, strippedBark, this.vanillaGson);
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public void registerTextures(TextureStitchEvent.Pre event) {
        boolean hasForestry = Loader.isModLoaded("forestry");
        this.forEachBlock(origLog -> {
            if (hasForestry && origLog instanceof BlockForestryLog<?>) {
                Block stripped = this.getStripped(origLog);
                if (stripped != null) registerForestryTextures(event, origLog, stripped);
            }
            else {
                Block stripped = this.getStripped(origLog);
                if (stripped != null) {
                    Map<IBlockState, ModelResourceLocation> modelLocations = Minecraft.getMinecraft().modelManager.getBlockModelShapes().getBlockStateMapper().getVariants(origLog);
                    for (Map.Entry<IBlockState, ModelResourceLocation> entry : modelLocations.entrySet()) {
                        IModel originalModel;
                        try { originalModel = ModelLoaderRegistry.getModel(entry.getValue()); } catch (Exception e) { return; }
                        int i = 0;
                        VariantList list;
                        try {
                            ResourceLocation stateLoc = entry.getValue();
                            ResourceLocation bsLoc = new ResourceLocation(stateLoc.getNamespace(), "blockstates/" + stateLoc.getPath() + ".json");
                            IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(bsLoc);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                            ModelBlockDefinition definition = BlockStateLoader.load(reader, bsLoc, this.vanillaGson);
                            if (!definition.hasVariant(entry.getValue().getVariant())) continue; // Throws an exception. We should throw errors.
                            list = definition.getVariant(entry.getValue().getVariant());
                            reader.close();
                        }
                        catch (Exception e) { return; }
                        for (ResourceLocation depLoc : originalModel.getDependencies()) {
                            List<String> textureList = null;
                            Variant variant = list.getVariantList().get(i);
                            String varStr = variant.toString();
                            if (varStr.startsWith("TexturedVariant")) {
                                textureList = new ArrayList<>(2);
                                StringBuilder builder = new StringBuilder();
                                for (int a = 17; a < varStr.length(); a++) {
                                    char c = varStr.charAt(a);
                                    if (c == ' ') {
                                        if (varStr.charAt(a + 1) != '=' && varStr.charAt(a - 1) != '=') {
                                            textureList.add(builder.toString());
                                            builder = new StringBuilder();
                                        }
                                        continue;
                                    }
                                    builder.append(c);
                                    if (a == varStr.length() - 1) {
                                        textureList.add(builder.toString());
                                    }
                                }
                            }
                            IModel actualModel;
                            try { actualModel = ModelLoaderRegistry.getModel(depLoc); } catch (Exception e) { return; }
                            Optional<ModelBlock> actualModelBlockOpt = actualModel.asVanillaModel();
                            if (actualModelBlockOpt.isPresent()) {
                                ModelBlock actualModelBlock = actualModelBlockOpt.get();
                                String end = null;
                                String side = null;
                                if (textureList != null) {
                                    for (String texture : textureList) {
                                        String[] split = texture.split("=");
                                        if (split[0].equals("end")) end = split[1];
                                        else if (split[0].equals("side")) side = split[1];
                                    }
                                }
                                else {
                                    for (Map.Entry<String, String> texEntry : actualModelBlock.textures.entrySet()) {
                                        if (texEntry.getKey().equals("end")) end = texEntry.getValue();
                                        else if (texEntry.getKey().equals("side")) side = texEntry.getValue();
                                    }
                                }
                                if (end != null && side != null) {
                                    TextureMap map = event.getMap();
                                    ResourceLocation endSprite = new ResourceLocation(end);
                                    ResourceLocation sideSprite = new ResourceLocation(side);
                                    ResourceLocation strippedSprite = new ResourceLocation(endSprite + "_stripped_template");
                                    { // TODO Change how this works
                                        String texLoc = strippedSprite.toString();
                                        if (map.getTextureExtry(texLoc) == null) {
                                            map.setTextureEntry(new DebarkedSpriteTopDumb(texLoc, endSprite));
                                        }
                                    }

                                    {
                                        String location = endSprite + "_stripped";
                                        if (map.getTextureExtry(location) == null) {
                                            map.setTextureEntry(new DebarkedSpriteTop(location, endSprite, strippedSprite));
                                        }
                                    }
                                    {
                                        String location = sideSprite + "_stripped";
                                        if (map.getTextureExtry(location) == null) {
                                            map.setTextureEntry(new DebarkedSpriteSide(location, endSprite, sideSprite));
                                        }
                                    }
                                }
                            }
                            i++;
                        }
                    }
                }
            }
        });
    }

    @SideOnly(Side.CLIENT)
    private void registerTextures(TextureStitchEvent.Pre event, Block origLog, Block addLog) {

        //        for (Map.Entry<Block, Block> entry : BPHooks.DEBARKED_LOG_BLOCKS.entrySet()) {
//                    IModel origModel;
//                    try {
//                        origModel = ModelLoaderRegistry.getModel(loc);
//                    }
//                    catch (Exception e) {
//                        continue;
//                        // throw new RuntimeException(e);
//                    }
//                    Optional<ModelBlock> origModelBlockOpt = origModel.asVanillaModel();
//                    if (origModelBlockOpt.isPresent()) {
//                        ModelBlock origModelBlock = origModelBlockOpt.get();
//                        String end = null;
//                        List<String> otherSprites = new ArrayList<>(1);
//                        if (ass == null) {
//                            for (Map.Entry<String, String> texEntry : origModelBlock.textures.entrySet()) {
//                                if (texEntry.getKey().equals("end")) end = texEntry.getValue();
//                                else otherSprites.add(texEntry.getValue());
//                            }
//                        }
//                        else {
//                            for (String assType : ass) {
//                                String[] split = assType.split("=");
//                                if (split[0].equals("end")) end = split[1];
//                                else otherSprites.add(split[1]);
//                            }
//                        }
//                        if (end == null) {
//                            continue;
//                        }
//                        ResourceLocation endSprite = new ResourceLocation(end);
//                        ResourceLocation debarkedSprite = new ResourceLocation(endSprite + "_debarked_template");
//
//                        { // TODO Change how this works
//                            String texLoc = debarkedSprite.toString();
//                            if (map.getTextureExtry(texLoc) == null) {
//                                map.setTextureEntry(new DebarkedSpriteTopDumb(texLoc, endSprite));
//                            }
//                        }
//
//                        {
//                            String location = endSprite + "_debarked";
//                            if (map.getTextureExtry(location) == null) {
//                                map.setTextureEntry(new DebarkedSpriteTop(endSprite + "_debarked", endSprite, debarkedSprite));
//                            }
//                        }
//                        for (String sprite : otherSprites) {
//                            String location = new ResourceLocation(sprite + "_debarked").toString();
//                            if (map.getTextureExtry(location) == null) {
//                                map.setTextureEntry(new DebarkedSpriteSide(location, endSprite, new ResourceLocation(sprite)));
//                            }
//                        }
//                    }
//                    i++;
//                }
//            }
//        }
    }

    private static void registerForestryTextures(TextureStitchEvent.Pre event, Block origLog, Block addLog) {
        TextureMap map = event.getMap();
        for (IBlockState state : origLog.getBlockState().getValidStates()) {
            IWoodType type = ((BlockForestryLog<?>) origLog).getWoodType(origLog.getMetaFromState(state));
            if (type instanceof EnumVanillaWoodType) return;
            ResourceLocation endSprite = new ResourceLocation(type.getHeartTexture());
            ResourceLocation debarkedSprite = new ResourceLocation(type.getHeartTexture() + "_stripped_template");
            { // TODO Change how this works
                String texLoc = debarkedSprite.toString();
                if (map.getTextureExtry(texLoc) == null) {
                    map.setTextureEntry(new DebarkedSpriteTopDumb(texLoc, endSprite));
                }
            }
            {
                String location = endSprite + "_stripped";
                if (map.getTextureExtry(location) == null) {
                    map.setTextureEntry(new DebarkedSpriteTop(endSprite + "_stripped", endSprite, debarkedSprite));
                }
            }
            {
                String location = type.getBarkTexture() + "_stripped";
                if (map.getTextureExtry(location) == null) {
                    map.setTextureEntry(new DebarkedSpriteSide(location, endSprite, new ResourceLocation(type.getBarkTexture())));
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static void bakeModel(ModelBakeEvent event, Block origLog, Block addLog, Gson vanillaGson) {
        boolean isStripped = false, isBark = false;
        {
            ResourceLocation loc = Objects.requireNonNull(addLog.getRegistryName());
            if (loc.getPath().contains("_stripped")) isStripped = true;
            if (loc.getPath().endsWith("_bark")) isBark = true;
        }
        IProperty<?> property = addLog.getBlockState().getProperty("axis");
        Object y = null;
        {
            if (property != null) {
                if (property.getValueClass() == EnumFacing.Axis.class) y = EnumFacing.Axis.Y;
                else if (property.getValueClass() == BlockLog.EnumAxis.class) y = BlockLog.EnumAxis.Y;
                else {
                    System.out.println("'Axis' property type " + property.getValueClass() + " does not match for block " + addLog.getRegistryName());
                    return;
                }
            }
        }
        Map<IBlockState, ModelResourceLocation> modelLocations = event.getModelManager().getBlockModelShapes().getBlockStateMapper().getVariants(origLog);
        Map<IBlockState, ModelResourceLocation> addModelLocations = event.getModelManager().getBlockModelShapes().getBlockStateMapper().getVariants(addLog);
        for (Map.Entry<IBlockState, ModelResourceLocation> entry : modelLocations.entrySet()) {
            IModel origModel, repModel;
            try { origModel = ModelLoaderRegistry.getModel(entry.getValue()); } catch (Exception e) { return; }
            repModel = origModel;
            int i = 0;
            VariantList varList;
            try {
                ResourceLocation stateLoc = entry.getValue();
                ResourceLocation bsLoc = new ResourceLocation(stateLoc.getNamespace(), "blockstates/" + stateLoc.getPath() + ".json");
                IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(bsLoc);
                BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                ModelBlockDefinition definition = BlockStateLoader.load(reader, bsLoc, vanillaGson);
                varList = definition.getVariant(entry.getValue().getVariant());
                reader.close();
            }
            catch (IOException e) { return; }
            for (ResourceLocation depLoc : origModel.getDependencies()) {
                IModel actualModel;
                try { actualModel = ModelLoaderRegistry.getModel(depLoc); }
                catch (Exception e) { return; }
                Optional<ModelBlock> actualModelBlockOpt = actualModel.asVanillaModel();
                if (actualModelBlockOpt.isPresent()) {
                    List<String> textureList = null;
                    IModelState modelState;
                    {
                        Variant variant = varList.getVariantList().get(i);
                        modelState = variant.getState();
                        String varStr = variant.toString();
                        if (varStr.startsWith("TexturedVariant")) {
                            textureList = new ArrayList<>(2);
                            StringBuilder builder = new StringBuilder();
                            for (int a = 17; a < varStr.length(); a++) {
                                char c = varStr.charAt(a);
                                if (c == ' ') {
                                    if (varStr.charAt(a + 1) != '=' && varStr.charAt(a - 1) != '=') {
                                        textureList.add(builder.toString());
                                        builder = new StringBuilder();
                                    }
                                    continue;
                                }
                                builder.append(c);
                                if (a == varStr.length() - 1) {
                                    textureList.add(builder.toString());
                                }
                            }
                        }
                    }
                    ModelBlock actualModelBlock = actualModelBlockOpt.get();
                    ImmutableMap<String, String> texturesMap;
                    {
                        Map<String, String> stupidityMap = new HashMap<>();
                        ImmutableMap.Builder<String, String> mapBuilder = new ImmutableBiMap.Builder<>();
                        if (textureList != null) {
                           for (String texture : textureList) {
                               String[] split = texture.split("=");
                               String key = split[0];
                               String value;
                               {
                                   StringBuilder valBuilder = new StringBuilder(split[1]);
                                   if (isStripped) valBuilder.append("_stripped");
                                   value = valBuilder.toString();
                               }
                               stupidityMap.put(key, value);
                           }
                        }
                        else {
                            for (Map.Entry<String, String> texEntry : actualModelBlock.textures.entrySet()) {
                                String key = texEntry.getKey();
                                String value = texEntry.getValue();
                                if (isStripped) value += "_stripped";
                                stupidityMap.put(key, value);
                            }
                        }
                        if (stupidityMap.containsKey("end")) {
                            String side = stupidityMap.get("side");
                            if (isBark || stupidityMap.get("end").equals(side)) {
                                stupidityMap.remove("end");
                                stupidityMap.remove("side");
                                stupidityMap.put("all", side);
                                try { repModel = ModelLoaderRegistry.getModel(new ResourceLocation("block/cube_all")); } catch (Exception e) { return; }
                            }
                            else {
                                try { repModel = ModelLoaderRegistry.getModel(new ResourceLocation("block/cube_column")); } catch (Exception e) { return; }
                            }
                        }
                        mapBuilder.putAll(stupidityMap);
                        texturesMap = mapBuilder.build();
                    }
                    IModel addModel = repModel.retexture(texturesMap);
                    IBlockState addState = RandomHelper.copyState(entry.getKey(), addLog);
                    IBakedModel m = addModel.bake(modelState, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
                    event.getModelRegistry().putObject(new ModelResourceLocation(Objects.requireNonNull(addLog.getRegistryName()), "normal"), m);
                    if (property != null) {
                        if (addState.getValue(property) == y) {
                            event.getModelRegistry().putObject(new ModelResourceLocation(Objects.requireNonNull(addLog.getRegistryName()), RandomHelper.getVariantFromState(addState)), m);
                            ModelResourceLocation modelLoc = addModelLocations.get(addState);
                            event.getModelRegistry().putObject(modelLoc, m);
                        }
                        else {
                            ModelResourceLocation modelLoc = addModelLocations.get(addState);
                            event.getModelRegistry().putObject(modelLoc, addModel.bake(modelState, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()));
                        }
                    }
                    else {
                        ModelResourceLocation modelLoc = addModelLocations.get(addState);
                        event.getModelRegistry().putObject(new ModelResourceLocation(new ResourceLocation(modelLoc.getNamespace(), modelLoc.getPath()), entry.getValue().getVariant()), addModel.bake(modelState, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()));
                    }
                }
                i++;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static void bakeForestryModels(ModelBakeEvent event, Block origLog, Block addLog) {
        boolean isStripped = false, isBark = false;
        {
            ResourceLocation loc = Objects.requireNonNull(addLog.getRegistryName());
            if (loc.getPath().contains("_stripped")) isStripped = true;
            if (loc.getPath().endsWith("_bark")) isBark = true;
        }
        IModel model;
        try {
            if (isBark) model = ModelLoaderRegistry.getModel(new ResourceLocation("block/cube_all"));
            else model = ModelLoaderRegistry.getModel(new ResourceLocation("block/cube_column"));
        }
        catch (Exception e) {
            throw  new RuntimeException("Problem occurred while trying to get cube_column block model.", e);
        }
        Map<IBlockState, ModelResourceLocation> modelLocations = event.getModelManager().getBlockModelShapes().getBlockStateMapper().getVariants(origLog);
        Map<IBlockState, ModelResourceLocation> addModelLocs = event.getModelManager().getBlockModelShapes().getBlockStateMapper().getVariants(addLog);
        for (Map.Entry<IBlockState, ModelResourceLocation> entry : modelLocations.entrySet()) {
            IBlockState state = entry.getKey();
            IWoodType type = ((BlockForestryLog<?>) origLog).getWoodType(origLog.getMetaFromState(state));
            String barkTexture = type.getBarkTexture().contains(":") ? type.getBarkTexture() : "minecraft:" + type.getBarkTexture();
            String heartTexture = type.getHeartTexture().contains(":") ? type.getHeartTexture() : "minecraft:" + type.getHeartTexture();
            ImmutableMap<String, String> textureMap;
            {
                ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
                if (isBark) {
                    if (isStripped) builder.put("all", barkTexture + "_stripped");
                    else builder.put("all", barkTexture);
                }
                else {
                    builder.put("side", barkTexture + "_stripped");
                    builder.put("end", heartTexture + "_stripped");
                }
                textureMap = builder.build();
            }
            IModel inModel = model.retexture(textureMap);
            IBlockState addState = RandomHelper.copyState(state, addLog);
            BlockLog.EnumAxis axis = addState.getValue(BlockLog.LOG_AXIS);
            IModelState modelState = ModelRotation.X0_Y0;
            switch (axis) {
                case X: modelState = ModelRotation.X90_Y90; break;
                case Z: modelState = ModelRotation.X90_Y0; break;
            }
            IBakedModel bakedModel = inModel.bake(modelState, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
            event.getModelRegistry().putObject(addModelLocs.get(addState), bakedModel);
            event.getModelRegistry().putObject(new ModelResourceLocation(Objects.requireNonNull(addLog.getRegistryName()), RandomHelper.getVariantFromState(addState)), bakedModel);
        }
    }

    public static void cleanup() {
        INSTANCE = null;
    }
}
