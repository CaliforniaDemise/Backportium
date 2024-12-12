package surreal.backportium.core.util;

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
import org.jetbrains.annotations.NotNull;
import surreal.backportium.client.textures.StrippedSpriteSide;
import surreal.backportium.client.textures.StrippedSpriteTop;
import surreal.backportium.client.textures.StrippedSpriteTopTemplate;
import surreal.backportium.util.RandomHelper;
import surreal.backportium.util.Tuple;

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

    private final Map<Block, Tuple<Block, Block, Block>> addLogs;
    private final Map<Block, ItemBlock> items; // stripped/bark/sbark, item

    private final Gson vanillaGson;

    public LogSystem() {
        this.addLogs = new HashMap<>();
        this.items = new HashMap<>();
        if (FMLLaunchHandler.side().isClient()) {
            Field f_gson = ObfuscationReflectionHelper.findField(ModelBlockDefinition.class, "field_178333_a");
            f_gson.setAccessible(true);
            try { vanillaGson = (Gson) f_gson.get(null); } catch (IllegalAccessException e) { throw new RuntimeException(e); }
        }
        else this.vanillaGson = null;
    }

    public void register(Block log, @Nullable Block stripped, @Nullable Block bark, @Nullable Block strippedBark) {
        if (stripped == null && bark == null && strippedBark == null) return;
        this.addLogs.put(log, new Tuple<>(stripped, bark, strippedBark));
    }

    public void registerItem(Block addLog, ItemBlock item) {
        this.items.put(addLog, item);
    }

    @NotNull
    public Tuple<Block, Block, Block> getLogs(Block origLog) {
        return this.addLogs.get(origLog);
    }

    @Nullable
    public ItemBlock getItem(Block addLog) {
        return this.items.get(addLog);
    }

    public void forEachBlock(Consumer<Block> consumer) {
        this.addLogs.keySet().forEach(consumer);
    }

    public void registerOres(FMLInitializationEvent event) {
        this.forEachBlock(origLog -> {
            Tuple<Block, Block, Block> tuple = this.getLogs(origLog);
            Block strippedBlock = tuple.getFirst();
            Block barkBlock = tuple.getSecond();
            Block strippedBarkBlock = tuple.getThird();
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
            Tuple<Block, Block, Block> tuple = this.getLogs(origLog);
            Block stripped = tuple.getFirst();
            Block bark = tuple.getSecond();
            Block strippedBark = tuple.getThird();
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
            Tuple<Block, Block, Block> tuple = this.getLogs(origLog);
            if (hasForestry && origLog instanceof BlockForestryLog<?>) {
                Block stripped = tuple.getFirst();
                if (stripped != null) registerForestryTextures(event, origLog, stripped);
            }
            else {
                Block stripped = tuple.getFirst();
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
                                        else {
                                            if (split[0].equals("up")) end = split[1];
                                            else if (split[0].equals("north")) side = split[1];
                                        }
                                    }
                                }
                                else {
                                    for (Map.Entry<String, String> texEntry : actualModelBlock.textures.entrySet()) {
                                        if (texEntry.getKey().equals("end")) end = texEntry.getValue();
                                        else if (texEntry.getKey().equals("side")) side = texEntry.getValue();
                                        else {
                                            if (texEntry.getKey().equals("up")) end = texEntry.getValue();
                                            else if (texEntry.getKey().equals("north")) side = texEntry.getValue();
                                        }
                                    }
                                }
                                if (end != null && side != null) {
                                    if (end.equals(side)) return;
                                    TextureMap map = event.getMap();
                                    ResourceLocation endSprite = new ResourceLocation(end);
                                    ResourceLocation sideSprite = new ResourceLocation(side);
                                    ResourceLocation strippedSprite = new ResourceLocation(endSprite + "_stripped_template");
                                    { // TODO Change how this works
                                        String texLoc = strippedSprite.toString();
                                        if (map.getTextureExtry(texLoc) == null) {
                                            map.setTextureEntry(new StrippedSpriteTopTemplate(texLoc, endSprite));
                                        }
                                    }

                                    {
                                        String location = endSprite + "_stripped";
                                        if (map.getTextureExtry(location) == null) {
                                            if (stripped.getRegistryName().getNamespace().equals("ee")) {
                                                map.setTextureEntry(new StrippedSpriteSide(location, endSprite, endSprite));
                                            }
                                            else {
                                                map.setTextureEntry(new StrippedSpriteTop(location, endSprite, strippedSprite));
                                            }
                                        }
                                    }
                                    {
                                        String location = sideSprite + "_stripped";
                                        if (map.getTextureExtry(location) == null) {
                                            map.setTextureEntry(new StrippedSpriteSide(location, endSprite, sideSprite));
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
                    map.setTextureEntry(new StrippedSpriteTopTemplate(texLoc, endSprite));
                }
            }
            {
                String location = endSprite + "_stripped";
                if (map.getTextureExtry(location) == null) {
                    map.setTextureEntry(new StrippedSpriteTop(endSprite + "_stripped", endSprite, debarkedSprite));
                }
            }
            {
                String location = type.getBarkTexture() + "_stripped";
                if (map.getTextureExtry(location) == null) {
                    map.setTextureEntry(new StrippedSpriteSide(location, endSprite, new ResourceLocation(type.getBarkTexture())));
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
                    com.google.common.base.Optional<?> opt = property.parseValue("y");
                    if (opt.isPresent()) {
                        y = opt.get();
                    }
                    else {
                        System.out.println("'Axis' property type " + property.getValueClass() + " does not match for block " + addLog.getRegistryName());
                        return;
                    }
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
                        if (stupidityMap.isEmpty()) {
                            for (ResourceLocation texture : origModel.getTextures()) {
                                if (isBark) {
                                    if (!texture.getPath().contains("_top")) stupidityMap.put("all", texture + (isStripped ? "_stripped" : ""));
                                    try { repModel = ModelLoaderRegistry.getModel(new ResourceLocation("block/cube_all")); } catch (Exception e) { return; }
                                }
                                else {
                                    if (texture.getPath().contains("_top")) stupidityMap.put("end", texture + (isStripped ? "_stripped" : ""));
                                    else stupidityMap.put("side", texture + (isStripped ? "_stripped" : ""));
                                    try { repModel = ModelLoaderRegistry.getModel(new ResourceLocation("block/cube_column")); } catch (Exception e) { return; }
                                }
                            }
                        }
                        else if (stupidityMap.containsKey("end") || stupidityMap.containsKey("side")) {
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
                        else if (stupidityMap.containsKey("north") && stupidityMap.containsKey("south") && stupidityMap.containsKey("west") && stupidityMap.containsKey("east") && stupidityMap.containsKey("up") && stupidityMap.containsKey("down")) {
                            String side = stupidityMap.get("west");
                            String end = stupidityMap.get("up");
                            if (stupidityMap.get("north").equals(side)) stupidityMap.remove("north");
                            if (stupidityMap.get("south").equals(side)) stupidityMap.remove("south");
                            stupidityMap.remove("west");
                            if (stupidityMap.get("east").equals(side)) stupidityMap.remove("east");
                            stupidityMap.remove("up");
                            if (stupidityMap.get("down").equals(end)) stupidityMap.remove("down");
                            if (isBark) {
                                stupidityMap.put("all", side);
                                try { repModel = ModelLoaderRegistry.getModel(new ResourceLocation("block/cube_all")); } catch (Exception e) { return; }
                            }
                            else {
                                stupidityMap.put("side", side);
                                stupidityMap.put("end", end);
                                try { repModel = ModelLoaderRegistry.getModel(new ResourceLocation("block/cube_column")); } catch (Exception e) { return; }
                            }
                        }
                        try {
                            texturesMap = ImmutableMap.copyOf(stupidityMap);
                        }
                        catch (Exception e) {
                            System.err.println("Error occurred while baking model of " + addLog.getRegistryName());
                            e.printStackTrace();
                            return;
                        }
                    }
                    IModel addModel = repModel.retexture(texturesMap);
                    IBlockState addState = RandomHelper.copyState(entry.getKey(), addLog);
                    IBakedModel m = addModel.bake(modelState, DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
                    event.getModelRegistry().putObject(new ModelResourceLocation(Objects.requireNonNull(addLog.getRegistryName()), "normal"), m);
                    if (property != null) {
                        if (addState.getValue(property) == y) {
                            event.getModelRegistry().putObject(new ModelResourceLocation(Objects.requireNonNull(addLog.getRegistryName()), RandomHelper.getVariantFromState(addState)), m);
                            event.getModelRegistry().putObject(new ModelResourceLocation(Objects.requireNonNull(addLog.getRegistryName()), "inventory"), m);
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
}
