package surreal.backportium.core;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import surreal.backportium.Backportium;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.api.helper.RiptideHelper;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.block.v1_13.BlockBubbleColumn;
import surreal.backportium.core.util.LogSystem;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.item.v1_13.ItemBlockAddLog;
import surreal.backportium.item.v1_13.ItemTrident;
import surreal.backportium.sound.ModSounds;
import surreal.backportium.util.RandomHelper;
import surreal.backportium.core.transformers.DebarkingTransformer;

import javax.annotation.Nonnull;
import java.util.*;

@SuppressWarnings("unused")
public class BPHooks {

    public static void debugPrint(Object obj) {
        System.out.println(obj);
    }

    // PumpkinTransformer
    public static Block BlockStem$getCrop(Block original) {
        return original == Blocks.MELON_BLOCK ? original : ModBlocks.UNCARVED_PUMPKIN;
    }

    public static boolean WorldGenPumpkin$generate(World worldIn, Random rand, BlockPos position) {
        for (int i = 0; i < 64; ++i) {
            BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).getBlock() == Blocks.GRASS && ModBlocks.UNCARVED_PUMPKIN.canPlaceBlockAt(worldIn, blockpos)) {
                worldIn.setBlockState(blockpos, ModBlocks.UNCARVED_PUMPKIN.getDefaultState(), 2);
            }
        }
        return true;
    }

    // TridentTransformer
    public static boolean EntityLivingBase$handleRiptide(EntityLivingBase entity, int riptideTime) {
        World world = entity.world;
        boolean collided = entity.collidedHorizontally;
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox(), EntityLivingBase::canBeCollidedWith);
        if (entities.size() > 1) {
            ItemStack stack = entity.getActiveItemStack();
            float add = 0F;
            if (!stack.isEmpty()) {
                int impaling = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.IMPALING, stack);
                add += 2.5F * impaling;
            }
            collided = true;
            EntityLivingBase e = entities.get(0);
            if (entity == e) e = entities.get(1);
            e.attackEntityFrom(DamageSource.GENERIC, 8.0F + add);
        }

        if (collided) {
            entity.motionX = -entity.motionX / 2F;
            entity.motionY = -entity.motionY / 2F;
            entity.motionZ = -entity.motionZ / 2F;
            return false;
        }

        return true;
    }

    public static DamageSource EntityPlayer$getDamageSource(EntityPlayer player) {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem() instanceof ItemTrident) {
            return new EntityDamageSource("trident", player);
        }
        return DamageSource.causePlayerDamage(player);
    }

    // Fluidlogging
    public static boolean BlockFluidBase$renderSide(boolean original, IBlockState neighbor) {
        return original && !(neighbor.getBlock() instanceof FluidLogged);
    }

    public static boolean BlockLiquid$renderSide(boolean original, IBlockAccess world, BlockPos pos, EnumFacing facing) {
        IBlockState state = world.getBlockState(pos.offset(facing));
        return original && !(state.getBlock() instanceof FluidLogged);
    }

    // Logs
    /**
     * Used in {@link DebarkingTransformer#transformBlockLogEx(byte[])}
     **/
    public static void Logs$registerBlocks(Block origLog, Block stripped, Block bark, Block strippedBark) {
        LogSystem system = LogSystem.INSTANCE;
        system.register(origLog, stripped, bark, strippedBark);
    }

    /**
     * Used in {@link DebarkingTransformer#transformItemBlock(byte[])}
     **/
    public static void Logs$registerItem(Block addLog, ItemBlock item) {
        if (Logs$isNonOriginal(addLog)) LogSystem.INSTANCE.registerItem(addLog, item);
    }

    /**
     * Used in {@link DebarkingTransformer#transformItemBlock(byte[])}
     **/
    public static String Logs$getItemStackDisplayName(String def, ItemStack stack) {
        Block block = ((ItemBlock) stack.getItem()).getBlock();
        if (Logs$isNonOriginal(block)) {
            ResourceLocation location = Objects.requireNonNull(block.getRegistryName());
            if (location.getPath().endsWith("_stripped")) return I18n.translateToLocalFormatted("tile.backportium.log_stripped", def);
            if (location.getPath().endsWith("_bark")) return I18n.translateToLocalFormatted("tile.backportium.log_bark", def);
            if (location.getPath().endsWith("_stripped_bark")) return I18n.translateToLocalFormatted("tile.backportium.log_stripped_bark", def);
        }
        return def;
    }

    /**
     * Used in registering debarked log blocks for vanilla log types.
     **/
    private static int nextId = 2268;

    /**
     * Used in {@link DebarkingTransformer#transformBlock(byte[])}
     **/
    public static void Logs$registerVanilla(RegistryNamespacedDefaultedByKey<ResourceLocation, Block> registry, String registryName, Block origLog) {
        if (Logs$isOriginal(origLog)) {
            LogSystem system = LogSystem.INSTANCE;
            ResourceLocation location = new ResourceLocation(registryName);
            registry.register(nextId++, location, system.getStripped(origLog).setRegistryName(location));
            registry.register(nextId++, location, system.getBark(origLog).setRegistryName(location));
            registry.register(nextId++, location, system.getStrippedBark(origLog).setRegistryName(location));
        }
    }

    /**
     * Used in {@link DebarkingTransformer#transformItem(byte[])}
     **/
    public static void Logs$registerVanilla(RegistryNamespaced<ResourceLocation, Item> registry, Map<Block, Item> blockToItem, Block origLog, Item origItem) {
        if (Logs$isOriginal(origLog)) {
            LogSystem system = LogSystem.INSTANCE;
            ItemBlockAddLog strippedItem = new ItemBlockAddLog(system.getStripped(origLog), origLog);
            ItemBlockAddLog barkItem = new ItemBlockAddLog(system.getBark(origLog), origLog);
            ItemBlockAddLog strippedBarkItem = new ItemBlockAddLog(system.getStrippedBark(origLog), origLog);
            registry.register(Block.getIdFromBlock(strippedItem.getBlock()), Block.REGISTRY.getNameForObject(strippedItem.getBlock()), strippedItem);
            registry.register(Block.getIdFromBlock(barkItem.getBlock()), Block.REGISTRY.getNameForObject(barkItem.getBlock()), barkItem);
            registry.register(Block.getIdFromBlock(strippedBarkItem.getBlock()), Block.REGISTRY.getNameForObject(strippedBarkItem.getBlock()), strippedBarkItem);
        }
    }

    // TODO Use ForgeRegistry#add instead of hooks
    // TODO Mod might be adding multiple instances of debarked block and item, check and find ways if some of them actually are
    /**
     * Used in {@link DebarkingTransformer#transformForgeRegistry(byte[])}
     * Putted after everything is registered
     **/


//    public static boolean Debarking$tryRegisteringDebarkedLog(IForgeRegistry<Block> registry, Block origLog) {
//        if (origLog instanceof DebarkedLog && registry.containsValue(origLog)) {
//            return false;
//        }
//        Block debarkedLog = DEBARKED_LOG_BLOCKS.get(origLog);
//        if (debarkedLog == null) return true;
//        if (debarkedLog.getRegistryName() == null) {
//            debarkedLog.setRegistryName(Objects.requireNonNull(origLog.getRegistryName()));
//        }
//        // TODO Copy over variables
//        if (!registry.containsValue(debarkedLog)) {
//            registry.register(debarkedLog);
//        }
//        return true;
//    }

    // TODO Use ForgeRegistry#add instead of hooks
    /**
     * Used in {@link DebarkingTransformer#transformForgeRegistry(byte[])}
     **/
//    public static void Debarking$tryRegisteringDebarkedLog(IForgeRegistry<Item> registry, Item item) {
//        if (!(item instanceof ItemBlock)) return;
//        if (((ItemBlock) item).getBlock() instanceof DebarkedLog && registry.containsValue(item)) {
//            return;
//        }
//        Block origLog = ((ItemBlock) item).getBlock();
//        Block debLog = DEBARKED_LOG_BLOCKS.get(origLog);
//        if (debLog != null) {
//            for (ItemBlock itemBlock : DEBARKED_LOG_ITEMS) {
//                if (itemBlock.getBlock() == debLog) return;
//            }
//            boolean forestryLoaded = Loader.isModLoaded("forestry");
//            if (forestryLoaded && origLog instanceof BlockForestryLog) {
//                registry.register(new ItemBlockAddLog.ItemBlockAddLogForestry(debLog, origLog).setRegistryName(Objects.requireNonNull(origLog.getRegistryName())));
//            }
//            else registry.register(new ItemBlockAddLog(debLog, origLog).setRegistryName(Objects.requireNonNull(origLog.getRegistryName())));
//        }
//    }

    /**
     * Used in createDebarkedLogClass in {@link DebarkingTransformer}.
     * Some mods like BoP uses variant values that are hardcoded to the specific block.
     * Just fetch the metadata and states from original block right?
     **/
    public static int Logs$getMetaFromState(Block debarkedLog, Block origLog, IBlockState state) {
        return origLog.getMetaFromState(RandomHelper.copyState(state, origLog));
    }

    /**
     * Used in createDebarkedLogClass in {@link DebarkingTransformer}.
     * Some mods like BoP uses variant values that are hardcoded to the specific block.
     * Just fetch the metadata and states from original block right?
     **/
    public static IBlockState Logs$getStateFromMeta(Block debarkedLog, Block origLog, int meta) {
        IBlockState ass = origLog.getStateFromMeta(meta);
        return RandomHelper.copyState(ass, debarkedLog);
    }

    /**
     * Used in {@link DebarkingTransformer#transformForgeRegistryEntry$Impl(byte[])}
     * Why care about the existence of registry name when I can just change it before name check happens.
     **/
    public static String Logs$setRegistryNameDeep(IForgeRegistryEntry.Impl<?> entry, String name) {
        if (Logs$isNonOriginal(entry)) {
            String clsName = entry.getClass().getName();
            if (clsName.endsWith("$Stripped")) return name + "_stripped";
            if (clsName.endsWith("$Bark")) return name + "_bark";
            if (clsName.endsWith("$StrippedBark")) return name + "_stripped_bark";
        }
        return name;
    }

    public static void Logs$postRegister(Event event) {
        ModContainer container = Loader.instance().activeModContainer();
        if (container == null) return;
        String modId = container.getModId();
        if (event instanceof RegistryEvent.Register) {
            RegistryEvent.Register<?> register = (RegistryEvent.Register<?>) event;
            if (register.getRegistry().getRegistrySuperType() == Block.class) {
                IForgeRegistry<Block> registry = (IForgeRegistry<Block>) register.getRegistry();
                LogSystem system = LogSystem.INSTANCE;
                system.forEachBlock(origLog -> {
                    ResourceLocation origLoc = Objects.requireNonNull(origLog.getRegistryName());
                    if (!origLoc.getNamespace().equals("minecraft") && origLoc.getNamespace().equals(modId)) {
                        Block stripped = system.getStripped(origLog);
                        Block bark = system.getBark(origLog);
                        Block strippedBark = system.getStrippedBark(origLog);
                        if (stripped.getRegistryName() == null) stripped.setRegistryName(origLoc);
                        if (bark.getRegistryName() == null) bark.setRegistryName(origLoc);
                        if (stripped.getRegistryName() == null) strippedBark.setRegistryName(origLoc);
                        if (!registry.containsValue(stripped)) registry.register(stripped);
                        if (!registry.containsValue(bark)) registry.register(bark);
                        if (!registry.containsValue(strippedBark)) registry.register(strippedBark);
                    }
                });
            }
            else if (register.getRegistry().getRegistrySuperType() == Item.class) {
                IForgeRegistry<Item> registry = (IForgeRegistry<Item>) register.getRegistry();
                LogSystem system = LogSystem.INSTANCE;
                system.forEachBlock(origLog -> {
                    ResourceLocation origLoc = Objects.requireNonNull(origLog.getRegistryName());
                    if (!origLoc.getNamespace().equals("minecraft") && origLoc.getNamespace().equals(modId)) {
                        Block stripped = system.getStripped(origLog);
                        Block bark = system.getBark(origLog);
                        Block strippedBark = system.getStrippedBark(origLog);
                        ItemBlock strippedItem = system.getItem(stripped);
                        ItemBlock barkItem = system.getItem(bark);
                        ItemBlock strippedBarkItem = system.getItem(strippedBark);
                        if (strippedItem == null) registry.register(new ItemBlockAddLog(stripped, origLog).setRegistryName(Objects.requireNonNull(stripped.getRegistryName())));
                        else {
                            if (strippedItem.getRegistryName() == null) strippedItem.setRegistryName(Objects.requireNonNull(stripped.getRegistryName()));
                            if (!registry.containsValue(strippedItem)) registry.register(strippedItem);
                        }
                        if (barkItem == null) registry.register(new ItemBlockAddLog(bark, origLog).setRegistryName(Objects.requireNonNull(bark.getRegistryName())));
                        else {
                            if (barkItem.getRegistryName() == null) barkItem.setRegistryName(Objects.requireNonNull(bark.getRegistryName()));
                            if (!registry.containsValue(barkItem)) registry.register(barkItem);
                        }
                        if (strippedBarkItem == null) registry.register(new ItemBlockAddLog(strippedBark, origLog).setRegistryName(Objects.requireNonNull(strippedBark.getRegistryName())));
                        else {
                            if (strippedBarkItem.getRegistryName() == null) strippedBarkItem.setRegistryName(Objects.requireNonNull(strippedBark.getRegistryName()));
                            if (!registry.containsValue(strippedBarkItem)) registry.register(strippedBarkItem);
                        }
                    }
                });
            }
        }
    }

    public static boolean Logs$isOriginal(Object block) {
        return block instanceof BlockLog && !block.getClass().getName().startsWith("backportium.logs");
    }

    private static boolean Logs$isNonOriginal(Object block) {
        return block instanceof BlockLog && block.getClass().getName().startsWith("backportium.logs");
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
        public static void ModelBiped$setRotationAngles(ModelBiped model, EnumHandSide handSide, ModelRenderer mainHandModel, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
            if (entityIn instanceof EntityLivingBase) {
                EntityLivingBase living = (EntityLivingBase) entityIn;
                EnumHand activeHand = living.getActiveHand();
                ItemStack activeStack = living.getActiveItemStack();
                handSide = activeHand == EnumHand.MAIN_HAND ? living.getPrimaryHand() : living.getPrimaryHand().opposite();
                if (activeStack.getItemUseAction() == Backportium.SPEAR && living.isHandActive()) {
                    int riptide = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, activeStack);
                    if (riptide != 0 && !RiptideHelper.canRiptide(living.world, living)) return;
                    if (handSide == EnumHandSide.RIGHT) {
                        model.bipedRightArm.rotateAngleX = model.bipedRightArm.rotateAngleX * 0.5F - ((float) Math.PI);
                        model.bipedRightArm.rotateAngleZ = model.bipedRightArm.rotateAngleZ - 0.15F; // Attention to detail mode
                    } else {
                        model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX * 0.5F - ((float) Math.PI);
                        model.bipedLeftArm.rotateAngleZ = model.bipedLeftArm.rotateAngleZ + 0.15F; // Attention to detail mode
                    }
                }
            }
        }

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
            if (player.isElytraFlying() && inRiptide) {
                float rotate = 72F * (tickLeft - partialTicks + 1.0F);
                GlStateManager.rotate(rotate, 0.0F, 1.0F, 0.0F);
            }
        }

        // Debarking
        /**
         * Used in {@link DebarkingTransformer#transformBlockStateMapper(byte[])}
         **/
        public static void Logs$registerBlockStateMapper(BlockStateMapper mapper, Block origLog, IStateMapper mapperIface) {
            if (Logs$isOriginal(origLog)) {
                LogSystem system = LogSystem.INSTANCE;
                Map<IBlockState, ModelResourceLocation> m = mapperIface.putStateModelLocations(origLog);
                mapper.registerBlockStateMapper(system.getStripped(origLog), new StateMapperBase() {
                    @Nonnull
                    @Override
                    @SuppressWarnings("deprecation")
                    protected ModelResourceLocation getModelResourceLocation(@NotNull IBlockState state) {
                        IBlockState origState = origLog.getStateFromMeta(state.getBlock().getMetaFromState(state));
                        ModelResourceLocation origLoc = m.get(origState);
                        return new ModelResourceLocation(new ResourceLocation(origLoc.getNamespace(), origLoc.getPath() + "_stripped"), origLoc.getVariant());
                    }
                });
                mapper.registerBlockStateMapper(system.getBark(origLog), new StateMapperBase() {
                    @Nonnull
                    @Override
                    @SuppressWarnings("deprecation")
                    protected ModelResourceLocation getModelResourceLocation(@NotNull IBlockState state) {
                        IBlockState origState = origLog.getStateFromMeta(state.getBlock().getMetaFromState(state));
                        ModelResourceLocation origLoc = m.get(origState);
                        return new ModelResourceLocation(new ResourceLocation(origLoc.getNamespace(), origLoc.getPath() + "_bark"), origLoc.getVariant());
                    }
                });
                mapper.registerBlockStateMapper(system.getStrippedBark(origLog), new StateMapperBase() {
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

        // Bubble Column
        public static int EntityPlayerSP$handleBubbleColumn(EntityPlayerSP entity, int i) {
            World world = entity.world;
            BlockPos pos = new BlockPos(entity).add(0, entity.getEyeHeight(), 0);
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() == ModBlocks.BUBBLE_COLUMN) {
                boolean upwards = state.getValue(BlockBubbleColumn.DRAG);
                if (upwards) {
                    if (i != 1) world.playSound(entity.posX, entity.posY, entity.posZ, ModSounds.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, SoundCategory.BLOCKS, 0.7F, 1.0F, false);
                    return 1;
                }
                else {
                    if (i != 2) world.playSound(entity.posX, entity.posY, entity.posZ, ModSounds.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, SoundCategory.BLOCKS, 0.7F, 1.0F, false);
                    return 2;
                }
            }
            return 0;
        }
    }
}
