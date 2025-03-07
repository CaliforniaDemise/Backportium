package surreal.backportium.core;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.api.block.StrippableLog;
import surreal.backportium.api.extension.BiomePropertiesExtension;
import surreal.backportium.api.helper.TridentHelper;
import surreal.backportium.core.util.LogSystem;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.item.v13.ItemBlockAddLog;
import surreal.backportium.util.RandomHelper;
import surreal.backportium.util.Tuple;
import surreal.backportium.world.BiomeColorHandler;
import surreal.backportium.world.biome.ModBiomes;

import javax.annotation.Nonnull;
import java.util.*;

// TODO Change Hooks for reaching to other classes so when I remove method, field or class, I know a transformer uses it.
@SuppressWarnings("unused")
public class BPHooks {

    public static void debugPrint(Object obj) {
        System.out.println(obj);
    }

    public static void debugPrint(int i) {
        System.out.println(i);
    }

    public static boolean WorldEntitySpawner$fluidLoggedSpawning(boolean def, IBlockState state) {
        boolean check = state.getBlock() instanceof FluidLogged;
        if (!check || Loader.isModLoaded("fluidlogged_api")) return def;
        else return false;
    }

    public static int GenLayerBiomeEdge$getBiomeId(int def, int i, int j, int areaWidth, int areaHeight, int[] aint, int[] aint1) {
        if (def == 0) return Biome.getIdForBiome(ModBiomes.WARM_OCEAN);
        return def;
    }

    private static int $getTemp(Biome.TempCategory category) {
        switch (category) {
            case COLD: return -1;
            case WARM: return 1;
            case OCEAN:
            case MEDIUM:
            default: return 0;
        }
    }

    // Water Color
    public static int BiomeColorHander$getWaterColor(Biome biome, int oldColor) {
        return BiomeColorHandler.getWaterColor(biome, oldColor);
    }

    public static Vec3d BiomeColorHander$getWaterFogColor(Vec3d oldColor, World world, BlockPos pos, IBlockState state) {
        if (state.getMaterial() == Material.WATER) {
            int fogColor = BiomeColorHandler.getWaterFogColor(world.getBiome(pos));
            int r = (fogColor & 0xff0000) >> 16;
            int g = (fogColor & 0x00ff00) >> 8;
            int b = (fogColor & 0x0000ff);
            return new Vec3d((double) r / 255D, (double) g / 255D, (double) b / 255D);
        }
        return oldColor;
    }

    public static int BiomeColorHander$emulateLegacyColor(int color) {
        return BiomeColorHandler.emulateLegacyColor(color);
    }

    public static void BiomeColor$defaultFogColors(Biome.BiomeProperties properties, String name) {
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
                    case "SwamplandM": ext.setActualWaterColor(6388580); ext.setWaterFogColor(2302743); break;
                    case "Frozen River":
                    case "Legacy Frozen Ocean": ext.setActualWaterColor(3750089); break;
                    case "Cold Beach":
                    case "Cold Taiga":
                    case "Cold Taiga Hills":
                    case "Cold Taiga M": ext.setActualWaterColor(4020182); break;
                }
                return;
            case "integrateddynamcis": ext.setWaterFogColor(5613789); return;
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

    // TridentTransformer
    public static boolean EntityLivingBase$handleRiptide(EntityLivingBase entity, int riptideTime) {
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

    // Logs
    /**
     * Used in {@link LogTransformer#transformBlockLogEx(byte[])}
     **/
    public static void Logs$registerBlocks(Block origLog, Block stripped, Block bark, Block strippedBark) {
        LogSystem system = LogSystem.INSTANCE;
        system.register(origLog, stripped, bark, strippedBark);
    }

    /**
     * Used in {@link LogTransformer#transformItemBlock(byte[])}
     **/
    public static void Logs$registerItem(Block addLog, ItemBlock item) {
        if (Logs$isNonOriginal(addLog)) LogSystem.INSTANCE.registerItem(addLog, item);
    }

    /**
     * Used in {@link LogTransformer#transformItemBlock(byte[])}
     **/
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
     * Used in registering debarked log blocks for vanilla log types.
     **/
    private static int nextId = 2268;

    /**
     * Used in {@link LogTransformer#transformBlock(byte[])}
     **/
    public static void Logs$registerVanilla(RegistryNamespacedDefaultedByKey<ResourceLocation, Block> registry, String registryName, Block origLog) {
        if (Logs$isOriginal(origLog)) {
            LogSystem system = LogSystem.INSTANCE;
            ResourceLocation location = new ResourceLocation(registryName);
            Tuple<Block, Block, Block> tuple = system.getLogs(origLog);
            Block stripped = tuple.getFirst();
            Block bark = tuple.getSecond();
            Block strippedBark = tuple.getThird();
            if (stripped != null) registry.register(nextId++, location, stripped.setRegistryName(location));
            if (bark != null) registry.register(nextId++, location, bark.setRegistryName(location));
            if (strippedBark != null) registry.register(nextId++, location, strippedBark.setRegistryName(location));
        }
    }

    /**
     * Used in {@link LogTransformer#transformItem(byte[])}
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
     * Used in createDebarkedLogClass in {@link LogTransformer}.
     * Some mods like BoP uses variant values that are hardcoded to the specific block.
     * Just fetch the metadata and states from original block right?
     **/
    public static int Logs$getMetaFromState(Block debarkedLog, Block origLog, IBlockState state) {
        return origLog.getMetaFromState(RandomHelper.copyState(state, origLog));
    }

    /**
     * Used in createDebarkedLogClass in {@link LogTransformer}.
     * Some mods like BoP uses variant values that are hardcoded to the specific block.
     * Just fetch the metadata and states from original block right?
     **/
    public static IBlockState Logs$getStateFromMeta(Block debarkedLog, Block origLog, int meta) {
        IBlockState ass = origLog.getStateFromMeta(meta);
        return RandomHelper.copyState(ass, debarkedLog);
    }

    /**
     * Used in {@link LogTransformer#transformForgeRegistryEntry$Impl(byte[])}
     * Why care about the existence of registry name when I can just change it before name check happens.
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

    public static boolean Logs$isOriginal(Object block) {
        return block instanceof StrippableLog && !block.getClass().getName().startsWith("backportium.logs");
    }

    private static boolean Logs$isNonOriginal(Object block) {
        return block instanceof StrippableLog && block.getClass().getName().startsWith("backportium.logs");
    }

    // Button Placement
    public static IBlockState button$getStateFromMeta(Block block, int meta) {
        return null;
    }

    public static int button$getMetaFromState(Block block, IBlockState state) {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public static class Client {
        // Debarking
        /**
         * Used in {@link LogTransformer#transformBlockStateMapper(byte[])}
         **/
        public static void Logs$registerBlockStateMapper(BlockStateMapper mapper, Block origLog, IStateMapper mapperIface) {
            if (Logs$isOriginal(origLog)) {
                LogSystem system = LogSystem.INSTANCE;
                Map<IBlockState, ModelResourceLocation> m = mapperIface.putStateModelLocations(origLog);
                Tuple<Block, Block, Block> tuple = system.getLogs(origLog);
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
    }
}
