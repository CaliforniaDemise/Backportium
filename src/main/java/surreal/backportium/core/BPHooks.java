package surreal.backportium.core;

import forestry.arboriculture.blocks.BlockForestryLog;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import surreal.backportium.Backportium;
import surreal.backportium.api.block.DebarkedLog;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.api.helper.RiptideHelper;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.item.v1_13.ItemBlockDebarkedLog;
import surreal.backportium.item.v1_13.ItemTrident;
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

    // Debarking
    public static final Map<Block, Block> DEBARKED_LOG_BLOCKS = new LinkedHashMap<>(); // originalLog, debarkedLog
    public static final List<ItemBlock> DEBARKED_LOG_ITEMS = new ArrayList<>(); // debarkedLogItem

    /**
     * Used in {@link DebarkingTransformer#transformBlockLogEx(byte[])}
     **/
    public static void Debarking$registerBlock(Block block, Block log) {
        DEBARKED_LOG_BLOCKS.put(log, block);
    }

    /**
     * Used in {@link DebarkingTransformer#transformItemBlock(byte[])}
     **/
    public static void Debarking$registerItem(Block block, ItemBlock itemBlock) {
        if (block instanceof DebarkedLog) {
            DEBARKED_LOG_ITEMS.add(itemBlock);
        }
    }

    /**
     * Used in {@link DebarkingTransformer#transformItemBlock(byte[])}
     **/
    public static String Debarking$getItemStackDisplayName(String def, ItemStack stack) {
        if (((ItemBlock) stack.getItem()).getBlock() instanceof DebarkedLog)
            return I18n.translateToLocalFormatted("tile.backportium.debarked_log", def);
        else return def;
    }

    /**
     * Used in {@link DebarkingTransformer#transformBlockStateMapper(byte[])}
     **/
    public static void Debarking$registerBlockStateMapper(BlockStateMapper mapper, Block origLog, IStateMapper mapperIface) {
        if (Debarking$isOriginal(origLog)) {
            Block debarkedLog = DEBARKED_LOG_BLOCKS.get(origLog);
            if (debarkedLog == null) return;
            Map<IBlockState, ModelResourceLocation> m = mapperIface.putStateModelLocations(origLog);
            mapper.registerBlockStateMapper(debarkedLog, new StateMapperBase() {
                @Nonnull
                @Override
                @SuppressWarnings("deprecation")
                protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
                    IBlockState origState = origLog.getStateFromMeta(state.getBlock().getMetaFromState(state));
                    ModelResourceLocation origLoc = m.get(origState);
                    return new ModelResourceLocation(new ResourceLocation(origLoc.getNamespace(), origLoc.getPath() + "_debarked"), origLoc.getVariant());
                }
            });
        }
    }

    /**
     * Used in registering debarked log blocks for vanilla log types.
     **/
    private static int nextId = 253;

    /**
     * Used in {@link DebarkingTransformer#transformBlock(byte[])}
     **/
    public static void Debarking$tryRegisteringDebarkedLogVanilla(RegistryNamespacedDefaultedByKey<ResourceLocation, Block> registry, String registryName, Block origLog) {
        if (Debarking$isOriginal(origLog)) {
            Block debarkedLog = DEBARKED_LOG_BLOCKS.get(origLog);
            if (debarkedLog == null) return;
            ResourceLocation location = new ResourceLocation(registryName);
            debarkedLog.setRegistryName(registryName);
            registry.register(nextId, location, debarkedLog);
            nextId++;
        }
    }

    /**
     * Used in {@link DebarkingTransformer#transformItem(byte[])}
     **/
    public static void Debarking$tryRegisteringDebarkedLogVanilla(RegistryNamespaced<ResourceLocation, Item> registry, Map<Block, Item> blockToItem, Block origLog, Item origItem) {
        if (Debarking$isOriginal(origLog)) {
            Block debarkedLog = DEBARKED_LOG_BLOCKS.get(origLog);
            ItemBlockDebarkedLog debarkedLogItem = new ItemBlockDebarkedLog(debarkedLog, origLog);
            debarkedLogItem.setRegistryName(Objects.requireNonNull(debarkedLog.getRegistryName()));
            registry.register(Block.getIdFromBlock(debarkedLog), Block.REGISTRY.getNameForObject(debarkedLog), debarkedLogItem);
            blockToItem.put(debarkedLog, debarkedLogItem);
        }
    }

    // TODO Use ForgeRegistry#add instead of hooks
    // TODO Mod might be adding multiple instances of debarked block and item, check and find ways if some of them actually are
    /**
     * Used in {@link DebarkingTransformer#transformForgeRegistry(byte[])}
     **/
    public static boolean Debarking$tryRegisteringDebarkedLog(IForgeRegistry<Block> registry, Block origLog) {
        if (origLog instanceof DebarkedLog && registry.containsValue(origLog)) {
            return false;
        }
        Block debarkedLog = DEBARKED_LOG_BLOCKS.get(origLog);
        if (debarkedLog == null) return true;
        if (debarkedLog.getRegistryName() == null) {
            debarkedLog.setRegistryName(Objects.requireNonNull(origLog.getRegistryName()));
        }
        // TODO Copy over variables
        if (!registry.containsValue(debarkedLog)) {
            registry.register(debarkedLog);
        }
        return true;
    }

    // TODO Use ForgeRegistry#add instead of hooks
    /**
     * Used in {@link DebarkingTransformer#transformForgeRegistry(byte[])}
     **/
    public static void Debarking$tryRegisteringDebarkedLog(IForgeRegistry<Item> registry, Item item) {
        if (!(item instanceof ItemBlock)) return;
        if (((ItemBlock) item).getBlock() instanceof DebarkedLog && registry.containsValue(item)) {
            return;
        }
        Block origLog = ((ItemBlock) item).getBlock();
        Block debLog = DEBARKED_LOG_BLOCKS.get(origLog);
        if (debLog != null) {
            for (ItemBlock itemBlock : DEBARKED_LOG_ITEMS) {
                if (itemBlock.getBlock() == debLog) return;
            }
            boolean forestryLoaded = Loader.isModLoaded("forestry");
            if (forestryLoaded && origLog instanceof BlockForestryLog) {
                registry.register(new ItemBlockDebarkedLog.ItemBlockDebarkedForestryLog(debLog, origLog).setRegistryName(Objects.requireNonNull(origLog.getRegistryName())));
            }
            else registry.register(new ItemBlockDebarkedLog(debLog, origLog).setRegistryName(Objects.requireNonNull(origLog.getRegistryName())));
        }
    }

    /**
     * Used in createDebarkedLogClass in {@link DebarkingTransformer}.
     * Some mods like BoP uses variant values that are hardcoded to the specific block.
     * Just fetch the metadata and states from original block right?
     **/
    public static int Debarking$getMetaFromState(Block debarkedLog, Block origLog, IBlockState state) {
        return origLog.getMetaFromState(RandomHelper.copyState(state, origLog));
    }

    /**
     * Used in createDebarkedLogClass in {@link DebarkingTransformer}.
     * Some mods like BoP uses variant values that are hardcoded to the specific block.
     * Just fetch the metadata and states from original block right?
     **/
    public static IBlockState Debarking$getStateFromMeta(Block debarkedLog, Block origLog, int meta) {
        IBlockState ass = origLog.getStateFromMeta(meta);
        return RandomHelper.copyState(ass, debarkedLog);
    }

    // TODO Change position of this when creating an API
    /**
     * Used outside {@link DebarkingTransformer}
     **/
    public static boolean Debarking$isOriginal(Block block) {
        return block instanceof BlockLog && !(block instanceof DebarkedLog);
    }

    /**
     * Used in {@link DebarkingTransformer#transformForgeRegistryEntry$Impl(byte[])}
     * Why care about the existence of registry name when I can just change it before name check happens.
     **/
    public static String Debarking$setRegistryNameDeep(IForgeRegistryEntry.Impl<?> entry, String name) {
        if (entry instanceof Block && entry instanceof DebarkedLog) {
            return name + "_debarked";
        }
        else if (entry instanceof ItemBlock) {
            ItemBlock ass = (ItemBlock) entry;
            if (ass.getBlock() instanceof DebarkedLog) {
                if (!name.endsWith("_debarked")) return name + "_debarked";
            }
        }
        return name;
    }

    // Button Placement
    public static IBlockState button$getStateFromMeta(Block block, int meta) {
        return null;
    }

    public static int button$getMetaFromState(Block block, IBlockState state) {
        return 0;
    }
}
