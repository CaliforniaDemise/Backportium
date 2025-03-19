package surreal.backportium.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import surreal.backportium.Backportium;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.api.block.StrippableLog;
import surreal.backportium.api.extension.BiomePropertiesExtension;
import surreal.backportium.api.helper.TridentHelper;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.block.v13.BlockBubbleColumn;
import surreal.backportium.core.util.LogSystem;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.item.v13.ItemBlockAddLog;
import surreal.backportium.potion.ModPotions;
import surreal.backportium.sound.ModSounds;
import surreal.backportium.util.IntegrationHelper;
import surreal.backportium.util.RandomHelper;
import surreal.backportium.util.Tuple;
import surreal.backportium.world.BiomeColorHandler;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Used for reaching fields and methods, so I know a transformer uses them before deleting them.
 **/
@SuppressWarnings("unused")
public class BPHooks {

    // Testing
    public static void debugPrint(Object obj) { System.out.println(obj); }
    public static void debugPrint(int i) { System.out.println(i); }

    // Breathing
    /**
     * Handles air replenishing when entity is inside Bubble Column.
     * See BreathingTransformer#transformEntityLivingBase(byte[])
     **/
    public static void Breathing$handleBubbleColumn(EntityLivingBase entity) {
        World world = entity.world;
        BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        if (world.getBlockState(pos).getBlock() == ModBlocks.BUBBLE_COLUMN) {
            entity.setAir(Math.min(entity.getAir() + 4, 300));
        }
    }

    // Bubble Column
    @SideOnly(Side.CLIENT)
    public static int BubbleColumn$handle(Entity entity, int i) {
        World world = entity.world;
        BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == ModBlocks.BUBBLE_COLUMN) {
            if (state.getValue(BlockBubbleColumn.DRAG)) {
                if (i != 1) {
                    world.playSound(entity.posX, entity.posY, entity.posZ, ModSounds.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, SoundCategory.BLOCKS, 0.7F, 1.0F, false);
                    return 1;
                }
            }
            else {
                if (i != 2) {
                    world.playSound(entity.posX, entity.posY, entity.posZ, ModSounds.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, SoundCategory.BLOCKS, 0.7F, 1.0F, false);
                    return 2;
                }
            }
        }
        else return 0;
        return i;
    }

    public static void BubbleColumn$setupRotation(float rockingAngle) {
        if (!MathHelper.epsilonEquals(rockingAngle, 0.0F)) {
            GlStateManager.rotate(rockingAngle, 1F, 0F, 1F);
        }
    }

    public static Block BubbleColumn$block() { return ModBlocks.BUBBLE_COLUMN; }
    public static boolean BubbleColumn$dragValue(IBlockState state) { return state.getValue(BlockBubbleColumn.DRAG); }

    // Buoyancy
    /**
     * Fixes an issue that occurs with AE2 crystals and buoyancy.
     * See BuoyancyTransformer#transformEntityItem(byte[])
     **/
    public static boolean Buoyancy$isAE2Loaded() {
        return IntegrationHelper.APPLIED_ENERGISTICS;
    }

    // Logs
    /**
     * For automatically registering new log types in BlockLog extending class.
     * See LogTransformer#transformBlockLogEx(byte[])
     **/
    public static void Logs$registerBlocks(Block origLog, Block stripped, Block bark, Block strippedBark) {
        LogSystem system = LogSystem.INSTANCE;
        system.register(origLog, stripped, bark, strippedBark);
    }

    /**
     * Used for registering item blocks of new log types in ItemBlock class.
     * See LogTransformer#transformItemBlock(byte[])
     **/
    public static void Logs$registerItem(Block addLog, ItemBlock item) {
        if (Logs$isNonOriginal(addLog)) LogSystem.INSTANCE.registerItem(addLog, item);
    }

    /**
     * Used for adding 'Stripped' adjective to stripped types and 'Bark' name to bark types.
     * See LogTransformer#transformItemBlock(byte[])
     **/
    @SuppressWarnings("deprecation")
    public static String Logs$getItemStackDisplayName(String def, ItemStack stack) {
        Block block = ((ItemBlock) stack.getItem()).getBlock();
        if (Logs$isNonOriginal(block)) {
            ResourceLocation location = Objects.requireNonNull(block.getRegistryName());
            if (location.getPath().endsWith("_stripped")) return I18n.translateToLocalFormatted("tile.backportium.log_stripped", def);
            if (location.getPath().endsWith("_stripped_bark")) return I18n.translateToLocalFormatted("tile.backportium.log_stripped_bark", def);
            if (location.getPath().endsWith("_bark")) return I18n.translateToLocalFormatted("tile.backportium.log_bark", def);
        }
        return def;
    }

    /**
     * Used for registering new log types of vanilla logs.
     * See below.
     **/
    private static int nextId = 2268;

    /**
     * Used for registering blocks of vanilla stripped logs and barks.
     * See LogTransformer#transformBlock(byte[])
     **/
    public static void Logs$registerVanilla(RegistryNamespacedDefaultedByKey<ResourceLocation, Block> registry, String registryName, Block origLog) {
        if (Logs$isOriginal(origLog)) {
            LogSystem system = LogSystem.INSTANCE;
            ResourceLocation location = new ResourceLocation(registryName);
            Tuple<Block, Block, Block> tuple = Objects.requireNonNull(system.getLogs(origLog));
            Block stripped = tuple.getFirst();
            Block bark = tuple.getSecond();
            Block strippedBark = tuple.getThird();
            if (stripped != null) registry.register(nextId++, location, stripped.setRegistryName(location));
            if (bark != null) registry.register(nextId++, location, bark.setRegistryName(location));
            if (strippedBark != null) registry.register(nextId++, location, strippedBark.setRegistryName(location));
        }
    }

    /**
     * Used for registering items of vanilla stripped logs and barks.
     * See LogTransformer#transformItem(byte[])
     **/
    public static void Logs$registerVanilla(RegistryNamespaced<ResourceLocation, Item> registry, Map<Block, Item> blockToItem, Block origLog, Item origItem) {
        if (Logs$isOriginal(origLog)) {
            LogSystem system = LogSystem.INSTANCE;
            Tuple<Block, Block, Block> tuple = system.getLogs(origLog);
            Block stripped = tuple.getFirst();
            Block bark = tuple.getSecond();
            Block strippedBark = tuple.getThird();
            if (stripped != null) {
                ItemBlockAddLog strippedItem = new ItemBlockAddLog(stripped, origLog);
                registry.register(Block.getIdFromBlock(strippedItem.getBlock()), Block.REGISTRY.getNameForObject(strippedItem.getBlock()), strippedItem);
            }
            if (bark != null) {
                ItemBlockAddLog barkItem = new ItemBlockAddLog(bark, origLog);
                registry.register(Block.getIdFromBlock(barkItem.getBlock()), Block.REGISTRY.getNameForObject(barkItem.getBlock()), barkItem);
            }
            if (strippedBark != null) {
                ItemBlockAddLog strippedBarkItem = new ItemBlockAddLog(strippedBark, origLog);
                registry.register(Block.getIdFromBlock(strippedBarkItem.getBlock()), Block.REGISTRY.getNameForObject(strippedBarkItem.getBlock()), strippedBarkItem);
            }
        }
    }

    /**
     * Some mods like BoP uses variant values that are hardcoded to the specific block.
     * Workaround that by fetching the metadata from the original log.
     * Used at createDebarkedLogClass in LogTransformer.
     **/
    public static int Logs$getMetaFromState(Block debarkedLog, Block origLog, IBlockState state) {
        return origLog.getMetaFromState(RandomHelper.copyState(state, origLog));
    }

    /**
     * Some mods like BoP uses variant values that are hardcoded to the specific block.
     * Workaround that by fetching the states from the original log.
     * Used at createDebarkedLogClass in LogTransformer.
     **/
    @SuppressWarnings("deprecation")
    public static IBlockState Logs$getStateFromMeta(Block debarkedLog, Block origLog, int meta) {
        IBlockState ass = origLog.getStateFromMeta(meta);
        return RandomHelper.copyState(ass, debarkedLog);
    }

    /**
     * Why should I care about the existence of registry name when I can set inside IForgeRegistryEntry#setRegistryName()?
     * See LogTransformer#transformForgeRegistryEntry$Impl(byte[])
     **/
    public static String Logs$setRegistryNameDeep(IForgeRegistryEntry.Impl<?> entry, String name) {
        Block block = null;
        if (entry instanceof Block) block = (Block) entry;
        else if (entry instanceof ItemBlock) block = ((ItemBlock) entry).getBlock();
        if (block == null) return name;
        if (Logs$isNonOriginal(block)) {
            String clsName = block.getClass().getName();
            if (!name.endsWith("_stripped_bark") && clsName.endsWith("$StrippedBark")) return name + "_stripped_bark";
            if (!name.endsWith("_stripped") && clsName.endsWith("$Stripped")) return name + "_stripped";
            if (!name.endsWith("_bark") && clsName.endsWith("$Bark")) return name + "_bark";
        }
        return name;
    }

    /**
     * For checking if any log types is properly registered.
     * See LogTransformer#transformForgeRegistry(byte[])
     **/
    @SuppressWarnings("unchecked")
    public static <T extends IForgeRegistryEntry<T>> void Logs$postRegister(IForgeRegistry<T> registry, Object entry) {
        if (LogSystem.INSTANCE == null) return;
        if (registry.getRegistrySuperType() == Block.class) {
            ResourceLocation location = Objects.requireNonNull(((T) entry).getRegistryName());
            if (!location.getNamespace().equals("minecraft") && Logs$isOriginal(entry)) {
                Block origLog = (Block) entry;
                Tuple<Block, Block, Block> tuple = LogSystem.INSTANCE.getLogs(origLog);
                if (tuple == null) return;
                Block stripped = tuple.getFirst();
                Block bark = tuple.getSecond();
                Block strippedBark = tuple.getThird();
                if (stripped != null) {
                    if (stripped.getRegistryName() == null) stripped.setRegistryName(location);
                    stripped.setCreativeTab(origLog.getCreativeTab());
                    if (!registry.containsValue((T) stripped)) registry.register((T) stripped);
                }
                if (bark != null) {
                    if (bark.getRegistryName() == null) bark.setRegistryName(location);
                    bark.setCreativeTab(origLog.getCreativeTab());
                    if (!registry.containsValue((T) bark)) registry.register((T) bark);
                }
                if (strippedBark != null) {
                    if (strippedBark.getRegistryName() == null) strippedBark.setRegistryName(location);
                    strippedBark.setCreativeTab(origLog.getCreativeTab());
                    if (!registry.containsValue((T) strippedBark)) registry.register((T) strippedBark);
                }
            }
        }
        else if (registry.getRegistrySuperType() == Item.class) {
            ResourceLocation location = Objects.requireNonNull(((T) entry).getRegistryName());
            String modId = location.getNamespace();
            Block origLog = Block.getBlockFromItem((Item) entry);
            if (origLog == Blocks.AIR) return;
            if (!location.getNamespace().equals("minecraft") && Logs$isOriginal(origLog)) {
                LogSystem system = LogSystem.INSTANCE;
                Tuple<Block, Block, Block> tuple = system.getLogs(origLog);
                if (tuple == null) return;
                Block stripped = tuple.getFirst();
                Block bark = tuple.getSecond();
                Block strippedBark = tuple.getThird();
                if (stripped != null) {
                    Item strippedItem = system.getItem(stripped);
                    if (strippedItem == null) registry.register((T) new ItemBlockAddLog(stripped, origLog).setRegistryName(location));
                    else {
                        if (strippedItem.getRegistryName() == null) strippedItem.setRegistryName(location);
                        if (!registry.containsValue((T) strippedItem)) registry.register((T) strippedItem);
                    }
                }
                if (bark != null) {
                    Item barkItem = system.getItem(bark);
                    if (barkItem == null) registry.register((T) new ItemBlockAddLog(bark, origLog).setRegistryName(location));
                    else {
                        if (barkItem.getRegistryName() == null) barkItem.setRegistryName(location);
                        if (!registry.containsValue((T) barkItem)) registry.register((T) barkItem);
                    }
                }
                if (strippedBark != null) {
                    Item strippedBarkItem = system.getItem(strippedBark);
                    if (strippedBarkItem == null) registry.register((T) new ItemBlockAddLog(strippedBark, origLog).setRegistryName(location));
                    else {
                        if (strippedBarkItem.getRegistryName() == null) strippedBarkItem.setRegistryName(location);
                        if (!registry.containsValue((T) strippedBarkItem)) registry.register((T) strippedBarkItem);
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void Logs$registerBlockStateMapper(BlockStateMapper mapper, Block origLog, IStateMapper mapperIface) {
        if (Logs$isOriginal(origLog)) {
            LogSystem system = LogSystem.INSTANCE;
            Map<IBlockState, ModelResourceLocation> m = mapperIface.putStateModelLocations(origLog);
            Tuple<Block, Block, Block> tuple = Objects.requireNonNull(system.getLogs(origLog));
            Block stripped = tuple.getFirst();
            Block bark = tuple.getSecond();
            Block strippedBark = tuple.getThird();
            if (stripped != null) {
                mapper.registerBlockStateMapper(stripped, new StateMapperBase() {
                    @Nonnull
                    @Override
                    @SuppressWarnings("deprecation")
                    protected ModelResourceLocation getModelResourceLocation(@NotNull IBlockState state) {
                        IBlockState origState = origLog.getStateFromMeta(state.getBlock().getMetaFromState(state));
                        ModelResourceLocation origLoc = m.get(origState);
                        return new ModelResourceLocation(new ResourceLocation(origLoc.getNamespace(), origLoc.getPath() + "_stripped"), origLoc.getVariant());
                    }
                });
            }
            if (bark != null) {
                mapper.registerBlockStateMapper(bark, new StateMapperBase() {
                    @Nonnull
                    @Override
                    @SuppressWarnings("deprecation")
                    protected ModelResourceLocation getModelResourceLocation(@NotNull IBlockState state) {
                        IBlockState origState = origLog.getStateFromMeta(state.getBlock().getMetaFromState(state));
                        ModelResourceLocation origLoc = m.get(origState);
                        return new ModelResourceLocation(new ResourceLocation(origLoc.getNamespace(), origLoc.getPath() + "_bark"), origLoc.getVariant());
                    }
                });
            }
            if (strippedBark != null) {
                mapper.registerBlockStateMapper(strippedBark, new StateMapperBase() {
                    @Nonnull
                    @Override
                    @SuppressWarnings("deprecation")
                    protected ModelResourceLocation getModelResourceLocation(@NotNull IBlockState state) {
                        IBlockState origState = origLog.getStateFromMeta(state.getBlock().getMetaFromState(state));
                        ModelResourceLocation origLoc = m.get(origState);
                        return new ModelResourceLocation(new ResourceLocation(origLoc.getNamespace(), origLoc.getPath() + "_stripped_bark"), origLoc.getVariant());
                    }
                });
            }
        }
    }

    /**
     * For checking if the block is the original log block.
     **/
    public static boolean Logs$isOriginal(Object block) {
        return block instanceof StrippableLog && !block.getClass().getName().startsWith("backportium.logs");
    }

    /**
     * Opposite of {@link BPHooks#Logs$isOriginal(Object)}
     **/
    private static boolean Logs$isNonOriginal(Object block) {
        return block instanceof StrippableLog && block.getClass().getName().startsWith("backportium.logs");
    }

    // Slow Falling
    public static double SlowFalling$fallingSpeed(double original, EntityLivingBase entity) {
        if (entity.motionY <= 0.0 && entity.isPotionActive(ModPotions.SLOW_FALLING)) {
            entity.fallDistance = 0.0F;
            return original / (8.0 + Objects.requireNonNull(entity.getActivePotionEffect(ModPotions.SLOW_FALLING)).getAmplifier());
        }
        return original;
    }

    // Conduit Power
    public static PotionEffect ConduitPower$getAmplifier(@Nullable PotionEffect original, EntityLivingBase entity) {
        PotionEffect conduitPower = entity.getActivePotionEffect(ModPotions.CONDUIT_POWER);
        if (original == null) return conduitPower;
        else if (conduitPower == null) return original;
        else return original.getAmplifier() > conduitPower.getAmplifier() ? original : conduitPower;
    }

    /**
     *
     **/
    public static boolean ConduitPower$isActive(boolean original, EntityLivingBase entity) {
        return original || (entity.isPotionActive(ModPotions.CONDUIT_POWER) && ModPotions.CONDUIT_POWER.shouldApply(entity));
    }

    /**
     * Night vision
     **/
    @SideOnly(Side.CLIENT)
    public static boolean ConduitPower$isActive(boolean original) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        return original || (player.isPotionActive(ModPotions.CONDUIT_POWER) && ModPotions.CONDUIT_POWER.shouldApply(player));
    }

    /**
     * Night vision
     **/
    @SideOnly(Side.CLIENT)
    public static PotionEffect ConduitPower$getPotionEffect(@Nullable PotionEffect effect, EntityLivingBase entity) {
        PotionEffect effect1 = entity.getActivePotionEffect(ModPotions.CONDUIT_POWER);
        if (effect == null) return effect1;
        else if (effect1 == null) return effect;
        else return effect.getDuration() > effect1.getDuration() ? effect : effect1;
    }

    /**
     * Night vision
     **/
    @SideOnly(Side.CLIENT)
    public static int ConduitPower$getNightVisionTime(EntityLivingBase entity, int original) {
        if (entity.isPotionActive(ModPotions.CONDUIT_POWER)) return 201;
        return original;
    }

    // Biome | Mostly for custom water colors
    public static int Buffet$getBiomeId(int fixedBiome) {
        if (fixedBiome < 0) fixedBiome = -1;
        else {
            Biome biome = Biome.getBiomeForId(fixedBiome);
            if (biome == null) fixedBiome = -1;
        }
        return fixedBiome;
    }

    public static String Biome$getTranslationKey(String name) {
        StringBuilder builder = new StringBuilder("biome.");
        {
            ModContainer container = Loader.instance().activeModContainer();
            if (container != null && container != Loader.instance().getMinecraftModContainer()) builder.append(container.getModId()).append('.');
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (c == ' ') builder.append('_');
            else builder.append(Character.toLowerCase(c));
        }
        builder.append(".name");
        return builder.toString();
    }

    @SideOnly(Side.CLIENT)
    public static String Biome$getLocalizedName(String original, String key) {
        String localizedName = net.minecraft.client.resources.I18n.format(key);
        if (localizedName.equals(key)) return original;
        return localizedName;
    }

    /**
     * Hook for reaching {@link BiomeColorHandler#getWaterColor(Biome, int)} from class transformers.
     **/
    public static int WaterColor$getWaterColor(Biome biome, int oldColor) {
        return BiomeColorHandler.getWaterColor(biome, oldColor);
    }

    @SideOnly(Side.CLIENT)
    public static String getBlockTexture(IBlockState state, String defTexture) {
        Block block = state.getBlock();
        if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) return "backportium:blocks/water_still";
        return defTexture;
    }

    /**
     * Hook for reaching {@link BiomeColorHandler#getWaterFogColor(Biome)} from class transformers.
     * It additionally turns the given int value to Vec3d.
     * See
     **/
    private static int targetFogColor = -1;
    private static int prevFogColor = -1;
    private static long fogAdjustTime = -1;
    public static Vec3d WaterColor$getWaterFogColor(Vec3d oldColor, World world, BlockPos pos, IBlockState state, float f6) {
        if (state.getMaterial() == Material.WATER) {
            long i = System.nanoTime() / 1000000L;
            int fogColor = BiomeColorHandler.getWaterFogColor(world.getBiome(pos));
            if (fogAdjustTime < 0) {
                targetFogColor = fogColor;
                prevFogColor = fogColor;
                fogAdjustTime = i;
            }
            int k = targetFogColor >> 16 & 255;
            int l = targetFogColor >> 8 & 255;
            int i1 = targetFogColor & 255;
            int j1 = prevFogColor >> 16 & 255;
            int k1 = prevFogColor >> 8 & 255;
            int l1 = prevFogColor & 255;
            float f = MathHelper.clamp((float)(i - fogAdjustTime) / 5000.0F, 0.0F, 1.0F);
            float f1 = RandomHelper.lerp(f, (float)j1, (float)k);
            float f2 = RandomHelper.lerp(f, (float)k1, (float)l);
            float f3 = RandomHelper.lerp(f, (float)l1, (float)i1);
            double r = f1 / 255.0;
            double g = f2 / 255.0;
            double b = f3 / 255.0;
            if (targetFogColor != fogColor) {
                targetFogColor = fogColor;
                prevFogColor = MathHelper.floor(f1) << 16 | MathHelper.floor(f2) << 8 | MathHelper.floor(f3);
                fogAdjustTime = i;
            }
            double f9 = Math.min(1.0 / r, Math.min(1.0 / g, 1.0 / b));
            r = r * (1.0 - f6) + r * f9 * f6;
            g = g * (1.0 - f6) + g * f9 * f6;
            b = b * (1.0 - f6) + b * f9 * f6;
            return new Vec3d(r, g, b);
        }
        return oldColor;
    }

    public static float WaterColor$getFogDensity(float density, Entity entity, float waterVision) {
        GlStateManager.setFog(GlStateManager.FogMode.EXP2);
        density -= 0.05F;
        Biome biome = entity.world.getBiome(new BlockPos(entity));
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)) density += 0.005F;
        density -= waterVision * waterVision * 0.03F;
        return density;
    }

    public static int WaterColor$emulateLegacyColor(int color) {
        return BiomeColorHandler.emulateLegacyColor(color);
    }

    public static void WaterColor$defaultWaterColors(Biome.BiomeProperties properties, String name) {
        String modId;
        {
            ModContainer container = Loader.instance().activeModContainer();
            modId = container != null ? container.getModId() : "minecraft";
        }
        BiomePropertiesExtension<?> ext = (BiomePropertiesExtension<?>) properties;
        switch (modId) {
            case "minecraft":
                switch (name) {
                    case "Swampland":
                    case "Swampland M":
                        ext.setActualWaterColor(6388580); ext.setWaterFogColor(2302743); break;
                    case "Frozen River":
                    case "Legacy Frozen Ocean":
                        ext.setActualWaterColor(3750089); break;
                    case "Cold Beach":
                    case "Cold Taiga":
                    case "Cold Taiga Hills":
                    case "Cold Taiga M":
                        ext.setActualWaterColor(4020182); break;
                }
                break;
            case "integrateddynamcis": ext.setWaterFogColor(5613789); break;
            case "biomesoplenty":
                switch (name) {
                    case "Bayou": ext.setActualWaterColor(0x62AF84); ext.setWaterFogColor(0x0C211C); break;
                    case "Dead Swamp": ext.setActualWaterColor(0x354762); ext.setWaterFogColor(0x040511); break;
                    case "Mangrove": ext.setActualWaterColor(0x448FBD); ext.setWaterFogColor(0x061326); break;
                    case "Mystic Grove": ext.setActualWaterColor(0x9C3FE4); ext.setWaterFogColor(0x2E0533); break;
                    case "Ominous Woods": ext.setActualWaterColor(0x312346); ext.setWaterFogColor(0x0A030C); break;
                    case "Tropical Rainforest": ext.setActualWaterColor(0x1FA14A); ext.setWaterFogColor(0x02271A); break;
                    case "Quagmire": ext.setActualWaterColor(0x433721); ext.setWaterFogColor(0x0C0C03); break;
                    case "Wetland": ext.setActualWaterColor(0x272179); ext.setWaterFogColor(0x0C031B); break;
                }
                return;
            case "thebetweenlands":
                switch (name) {
                    case "Swamplands":
                    case "Swamplands Clearing": ext.setActualWaterColor(1589792); ext.setWaterFogColor(1589792); break;
                    case "Coarse Islands":
                    case "Raised Isles":
                    case "Deep Waters": ext.setActualWaterColor(1784132); ext.setWaterFogColor(1784132); break;
                    case "Marsh 0":
                    case "Marsh 1": ext.setActualWaterColor(4742680); ext.setWaterFogColor(4742680); break;
                    case "Sludge Plains":
                    case "Sludge Plains Clearing": ext.setActualWaterColor(3813131); ext.setWaterFogColor(3813131); break;
                }
                return;
            case "traverse": ext.setActualWaterColor(0x3F76E4); ext.setWaterFogColor(0x50533); return;
            case "thaumcraft": ext.setActualWaterColor(3035999); break;
        }
    }

    // Trident
    /**
     * Handles the riptide collision.
     * See TridentTransformer#transformEntityLivingBase(byte[])
     **/
    public static boolean Trident$riptideCollision(EntityLivingBase entity, int riptideTime) {
        World world = entity.world;
        List<Entity> entities = world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox(), e -> e instanceof EntityLivingBase && e.canBeCollidedWith() && EntitySelectors.NOT_SPECTATING.apply(e));
        if (!entities.isEmpty()) {
            ItemStack stack = entity.getActiveItemStack();
            float add = 0F;
            Entity e = entities.get(0);
            if (!stack.isEmpty() && TridentHelper.canImpale(e)) {
                int impaling = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.IMPALING, stack);
                add = TridentHelper.handleImpaling(add, impaling);
            }
            e.attackEntityFrom(DamageSource.GENERIC, 8.0F + add);
            entity.motionX = -entity.motionX / 2F;
            entity.motionY = -entity.motionY / 2F;
            entity.motionZ = -entity.motionZ / 2F;
            return false;
        }
        return true;
    }

    /**
     * Gets sound to play when entity gets into riptide state.
     * See TridentTransformer#transformEntityLivingBase(byte[]) - handleRiptide method.
     **/
    public static SoundEvent Trident$getRiptideSound(int level) {
        return TridentHelper.getRiptideSound(level);
    }

    /**
     * For reaching {@link Backportium#SPEAR}, {@link ModEnchantments#RIPTIDE}, {@link TridentHelper#canRiptide(World, EntityLivingBase)}. Also, I don't want to mess with cursed frames.
     * See TridentTransformer$transformModelBiped(byte[])
     **/
    @SideOnly(Side.CLIENT)
    public static void Trident$setRotationAngles(ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase) entityIn;
            ItemStack stack = living.getActiveItemStack();
            EnumHandSide handSide = living.getPrimaryHand();
            EnumHand hand = living.getActiveHand();
            if (hand != EnumHand.MAIN_HAND) handSide = handSide.opposite();
            if (stack.getItemUseAction() == Backportium.SPEAR) {
                if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, stack) != 0 && !TridentHelper.canRiptide(living.world, living)) return;
                if (handSide == EnumHandSide.RIGHT) {
                    model.bipedRightArm.rotateAngleX = model.bipedRightArm.rotateAngleX * 0.5F - (float) Math.PI;
                    model.bipedRightArm.rotateAngleZ -= 0.15F;
                }
                else {
                    model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX * 0.5F - (float) Math.PI;
                    model.bipedLeftArm.rotateAngleZ += 0.15F;
                }
            }
        }
    }

    // Button Placement
    public static IBlockState button$getStateFromMeta(Block block, int meta) {
        return null;
    }

    public static int button$getMetaFromState(Block block, IBlockState state) {
        return 0;
    }

    // Water Logging
    /**
     * Fixes non-water creatures spawning inside fluidlogged blocks.
     * See WaterLoggingTransformer#transformWorldEntitySpawner(byte[])
     **/
    public static boolean fluidLogging$entitySpawning(boolean def, IBlockState state) {
        if (!(state.getBlock() instanceof FluidLogged) || IntegrationHelper.FLUIDLOGGED) return def;
        else return false;
    }

    // Player Move
    public static float playerMove$lerp(float pct, float start, float end) {
        return RandomHelper.lerp(pct, start, end);
    }

    @SideOnly(Side.CLIENT)
    public static boolean playerMove$isPlayerMoving(EntityPlayerSP player, boolean verticalSpeed) {
        return RandomHelper.isPlayerMoving(player, verticalSpeed);
    }
}
