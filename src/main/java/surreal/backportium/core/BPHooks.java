package surreal.backportium.core;

import appeng.core.AEConfig;
import appeng.core.features.AEFeature;
import appeng.entity.EntityGrowingCrystal;
import net.minecraft.block.Block;
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
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
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
import surreal.backportium.api.block.StrippableLog;
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
import surreal.backportium.util.Tuple;

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
     * Used in {@link DebarkingTransformer#transformBlock(byte[])}
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
     * Used in {@link DebarkingTransformer#transformItem(byte[])}
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

    // Buoyancy
    public static double Buoyancy$addY(EntityItem entity) {
        if (Loader.isModLoaded("appliedenergistics2") && AEConfig.instance().isFeatureEnabled(AEFeature.IN_WORLD_PURIFICATION) && entity instanceof EntityGrowingCrystal) {
            return 0.25D;
        }
        return 0D;
    }

    // Bubble Column
    public static int BubbleColumn$inBubbleColumn(Entity entity) {
        BlockPos pos = new BlockPos(entity).add(0D, entity.getEyeHeight(), 0D);
        IBlockState state = entity.world.getBlockState(pos);
        if (state.getBlock() == ModBlocks.BUBBLE_COLUMN && entity.world.isAirBlock(pos.up())) {
            boolean downwards = !state.getValue(BlockBubbleColumn.DRAG);
            if (downwards) return 2;
            else return 1;
        }
        return 0;
    }

    public static boolean BubbleColumn$isPlayerRiding(EntityBoat boat) {
        for (Entity entity : boat.getPassengers()) {
            if (entity instanceof EntityPlayer) return true;
        }
        return false;
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
            if (false && player.isElytraFlying() && inRiptide) {
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
