package surreal.backportium.core;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.api.block.StrippableLog;
import surreal.backportium.api.helper.TridentHelper;
import surreal.backportium.core.util.LogSystem;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.item.v13.ItemBlockAddLog;
import surreal.backportium.util.RandomHelper;
import surreal.backportium.util.Tuple;

import javax.annotation.Nonnull;
import java.util.*;

@SuppressWarnings("unused")
public class BPHooks {

    public static void debugPrint(Object obj) {
        System.out.println(obj);
    }

    // TridentTransformer
    public static boolean EntityLivingBase$handleRiptide(EntityLivingBase entity, int riptideTime) {
        World world = entity.world;
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox(), EntityLivingBase::canBeCollidedWith);
        if (entities.size() > 1) {
            ItemStack stack = entity.getActiveItemStack();
            float add = 0F;
            EntityLivingBase e = entities.get(0);
            if (entity == e) e = entities.get(1);
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

    public static void Logs$postRegister(ModContainer container, Event event) {
        if (LogSystem.INSTANCE == null) return;
        if (event instanceof RegistryEvent.Register) {
            String modId = container.getModId();
            RegistryEvent.Register<?> register = (RegistryEvent.Register<?>) event;
            if (register.getRegistry().getRegistrySuperType() == Block.class) {
                IForgeRegistry<Block> registry = (IForgeRegistry<Block>) register.getRegistry();
                LogSystem system = LogSystem.INSTANCE;
                system.forEachBlock(origLog -> {
                    ResourceLocation origLoc = origLog.getRegistryName();
                    if (origLoc != null && !origLoc.getNamespace().equals("minecraft") && origLoc.getNamespace().equals(modId)) {
                        Tuple<Block, Block, Block> tuple = system.getLogs(origLog);
                        Block stripped = tuple.getFirst();
                        Block bark = tuple.getSecond();
                        Block strippedBark = tuple.getThird();
                        if (stripped != null) {
                            if (stripped.getRegistryName() == null) stripped.setRegistryName(origLoc);
                            stripped.setCreativeTab(origLog.getCreativeTab());
                            if (!registry.containsValue(stripped)) registry.register(stripped);
                        }
                        if (bark != null) {
                            if (bark.getRegistryName() == null) bark.setRegistryName(origLoc);
                            bark.setCreativeTab(origLog.getCreativeTab());
                            if (!registry.containsValue(bark)) registry.register(bark);
                        }
                        if (strippedBark != null) {
                            if (strippedBark.getRegistryName() == null) strippedBark.setRegistryName(origLoc);
                            strippedBark.setCreativeTab(origLog.getCreativeTab());
                            if (!registry.containsValue(strippedBark)) registry.register(strippedBark);
                        }
                    }
                });
            }
            else if (register.getRegistry().getRegistrySuperType() == Item.class) {
                if (modId.equals("mm") || modId.equals("ee")) return; // I am not sure why this happens
                IForgeRegistry<Item> registry = (IForgeRegistry<Item>) register.getRegistry();
                LogSystem system = LogSystem.INSTANCE;
                system.forEachBlock(origLog -> {
                    ResourceLocation origLoc = Objects.requireNonNull(origLog.getRegistryName());
                    if (!origLoc.getNamespace().equals("minecraft") && origLoc.getNamespace().equals(modId)) {
                        Tuple<Block, Block, Block> tuple = system.getLogs(origLog);
                        Block stripped = tuple.getFirst();
                        Block bark = tuple.getSecond();
                        Block strippedBark = tuple.getThird();
                        if (stripped != null) {
                            ItemBlock strippedItem = system.getItem(stripped);
                            if (strippedItem == null) registry.register(new ItemBlockAddLog(stripped, origLog).setRegistryName(origLoc));
                            else {
                                if (strippedItem.getRegistryName() == null) strippedItem.setRegistryName(origLoc);
                                if (!registry.containsValue(strippedItem)) registry.register(strippedItem);
                            }
                        }
                        if (bark != null) {
                            ItemBlock barkItem = system.getItem(bark);
                            if (barkItem == null) registry.register(new ItemBlockAddLog(bark, origLog).setRegistryName(origLoc));
                            else {
                                if (barkItem.getRegistryName() == null) barkItem.setRegistryName(origLoc);
                                if (!registry.containsValue(barkItem)) registry.register(barkItem);
                            }
                        }
                        if (strippedBark != null) {
                            ItemBlock strippedBarkItem = system.getItem(strippedBark);
                            if (strippedBarkItem == null) registry.register(new ItemBlockAddLog(strippedBark, origLog).setRegistryName(origLoc));
                            else {
                                if (strippedBarkItem.getRegistryName() == null) strippedBarkItem.setRegistryName(origLoc);
                                if (!registry.containsValue(strippedBarkItem)) registry.register(strippedBarkItem);
                            }
                        }
                    }
                });
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
        // TridentTransformer
        public static void RenderLivingBase$applyRotations(EntityLivingBase entity, boolean inRiptide, int tickLeft, float partialTicks) {
            if (inRiptide) {
                float yRotation = 72F * (tickLeft - partialTicks + 1.0F);

                if (!entity.isElytraFlying()) {
                    GlStateManager.rotate(-90.0F - entity.rotationPitch, 1.0F, 0.0F, 0.0F);

                    Vec3d vec3d = entity.getLook(partialTicks);
                    double d0 = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
                    double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;

                    if (d0 > 0.0D && d1 > 0.0D) {
                        double d2 = (entity.motionX * vec3d.x + entity.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                        double d3 = entity.motionX * vec3d.z - entity.motionZ * vec3d.x;
                        GlStateManager.rotate((float) (Math.signum(d3) * Math.acos(d2)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
                    }

                    GlStateManager.rotate(yRotation, 0, 1, 0);
                }
            }
        }

        public static void RenderPlayer$fixElytraRotations(EntityPlayer player, boolean inRiptide, int tickLeft, float partialTicks) {
            if (false && player.isElytraFlying() && inRiptide) {
                float rotate = 72F * (tickLeft - partialTicks + 1.0F);
                GlStateManager.rotate(rotate, 0.0F, 1.0F, 0.0F);
            }
        }

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
