package surreal.backportium.core;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import net.minecraft.init.Items;
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
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import surreal.backportium.Backportium;
import surreal.backportium.Tags;
import surreal.backportium.api.block.DebarkedLog;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.api.helper.RiptideHelper;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.item.v1_13.ItemBlockDebarkedLog;

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

    public static void Debarking$registerBlock(Block block, Block log) {
        DEBARKED_LOG_BLOCKS.put(log, block);
    }

    public static void Debarking$registerItem(Block block, ItemBlock itemBlock) {
        if (block instanceof DebarkedLog) {
            DEBARKED_LOG_ITEMS.add(itemBlock);
        }
    }

    public static String Debarking$getItemStackDisplayName(String def, ItemStack stack) {
        if (((ItemBlock) stack.getItem()).getBlock() instanceof DebarkedLog)
            return I18n.translateToLocalFormatted("tile.backportium.debarked_log", def);
        else return def;
    }

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
                    return new ModelResourceLocation(new ResourceLocation(Tags.MOD_ID, origLoc.getPath() + "_debarked"), origLoc.getVariant());
                }
            });
        }
    }

    // Load it to loadVariantList this.model.put value line
    public static ModelBlock Debarking$registerModelBlock(ModelBlock original, ResourceLocation originalLoc, Map<ResourceLocation, ModelBlock> models) {
        ResourceLocation debarkedLoc = originalLoc instanceof ModelResourceLocation ? new ModelResourceLocation(new ResourceLocation(Tags.MOD_ID, originalLoc.getPath() + "_debarked"), ((ModelResourceLocation) originalLoc).getVariant()) : new ResourceLocation(Tags.MOD_ID, originalLoc.getPath() + "_debarked");
        ModelBlock debarkedModel = models.get(debarkedLoc);
        if (debarkedModel != null) return original;
        Map<String, String> textures = new Object2ObjectOpenHashMap<>();
        for (String key : original.textures.keySet()) {
            textures.put(key, original.textures.get(key) + "_debarked");
        }
        debarkedModel = new ModelBlock(original.getParentLocation(), original.getElements(), textures, original.isAmbientOcclusion(), original.isGui3d(), original.getAllTransforms(), original.getOverrides());
        models.put(debarkedLoc, debarkedModel);
        return original;
    }

    public static void Debarking$registerModelBlockDefinition(BlockStateMapper mapper, Block origLog, ResourceLocation location, Map<ModelResourceLocation, VariantList> variants, Map<String, VariantList> mapVariants, Map<ResourceLocation, ModelBlockDefinition> blockDefinitions) {
        if (Debarking$isOriginal(origLog)) {
            Block debarkedLog = DEBARKED_LOG_BLOCKS.get(origLog);
            Map<String, VariantList> debarkedVariantList = createDebarkedVariantList(mapVariants);
            ModelBlockDefinition definition = new ModelBlockDefinition(debarkedVariantList, null);
            Map<IBlockState, ModelResourceLocation> stateVariants= mapper.getVariants(debarkedLog);
            for (Map.Entry<IBlockState, ModelResourceLocation> entry : stateVariants.entrySet()) {
                ModelResourceLocation modelresourcelocation = entry.getValue();
                if (location.equals(modelresourcelocation)) {
                    variants.put(modelresourcelocation, definition.getVariant(modelresourcelocation.getVariant()));
                }
            }
        }
    }

    private static Map<String, VariantList> createDebarkedVariantList(Map<String, VariantList> mapVariants) {
        Map<String, VariantList> map = new Object2ObjectOpenHashMap<>();
        for (Map.Entry<String, VariantList> entry : mapVariants.entrySet()) {
            VariantList variants = entry.getValue();
            List<Variant> list = new ArrayList<>(variants.getVariantList().size());
            for (Variant variant : variants.getVariantList()) {
                list.add(new Variant(variant.getModelLocation(), variant.getRotation(), variant.isUvLock(), variant.getWeight()));
            }
            VariantList dList = new VariantList(list);
            map.put(entry.getKey(), dList);
        }
        return map;
    }

    private static int a = 253;
    public static void Debarking$tryRegisteringDebarkedLogVanilla(RegistryNamespacedDefaultedByKey<ResourceLocation, Block> registry, String registryName, Block origLog) {
        if (Debarking$isOriginal(origLog)) {
            Block debarkedLog = DEBARKED_LOG_BLOCKS.get(origLog);
            if (debarkedLog == null) return;
            ResourceLocation location = new ResourceLocation(registryName + "_debarked");
            debarkedLog.setRegistryName(location);
            registry.register(a, location, debarkedLog);
            a++;
        }
    }

    public static void Debarking$tryRegisteringDebarkedLogVanilla(RegistryNamespaced<ResourceLocation, Item> registry, Map<Block, Item> blockToItem, Block origLog, Item origItem) {
        if (Debarking$isOriginal(origLog)) {
            Block debarkedLog = DEBARKED_LOG_BLOCKS.get(origLog);
            ItemBlockDebarkedLog debarkedLogItem = new ItemBlockDebarkedLog(debarkedLog, origLog);
            debarkedLogItem.setRegistryName(Objects.requireNonNull(debarkedLog.getRegistryName()));
            registry.register(Block.getIdFromBlock(debarkedLog), Block.REGISTRY.getNameForObject(debarkedLog), debarkedLogItem);
            blockToItem.put(debarkedLog, debarkedLogItem);
        }
    }

    public static void Debarking$tryRegisteringDebarkedLog(IForgeRegistry<Block> registry, Block origLog) {
        if (origLog instanceof DebarkedLog) {
           DEBARKED_LOG_BLOCKS.put(((DebarkedLog) origLog).getOriginal(), origLog);
        }
        else if (Debarking$isOriginal(origLog) && !DEBARKED_LOG_BLOCKS.containsKey(origLog)) {
            Block debarkedLog = DEBARKED_LOG_BLOCKS.get(origLog);
            if (debarkedLog == null) {
                System.out.println("Debarked log is null for " + origLog.getRegistryName());
                return;
            }
            if (debarkedLog.getRegistryName() == null) {
                debarkedLog.setRegistryName(origLog.getRegistryName() + "_debarked");
                registry.register(debarkedLog);
            }
            else if (!registry.containsValue(debarkedLog)) {
                registry.register(debarkedLog);
            }
        }
    }

    public static void Debarking$tryRegisteringDebarkedLog(IForgeRegistry<Item> registry, Item item) {
//        if (!(item instanceof ItemBlock)) return;
//        Block origLog = ((ItemBlock) item).getBlock();
//        if (origLog instanceof DebarkedLog) {
//            DEBARKED_LOG_ITEMS.put(((DebarkedLog) origLog).getOriginal(), (ItemBlock) item);
//        }
//        else if (Debarking$isOriginal(origLog) && !DEBARKED_LOG_ITEMS.containsKey(origLog)) {
//            Block debarkedLog = DEBARKED_LOG_BLOCKS.get(origLog);
//            if (debarkedLog == null) {
//                System.out.println("vfvwvfewfvewfvewfewfvew " + origLog.getRegistryName());
//                return;
//            }
//            ItemBlock debarkedLogItem = DEBARKED_LOG_ITEMS.get(origLog);
//            if (debarkedLogItem == null) {
//                debarkedLogItem = new ItemBlockDebarkedLog(debarkedLog, origLog);
//                DEBARKED_LOG_ITEMS.put(origLog, debarkedLogItem);
//                return;
//            }
//            if (debarkedLogItem.getRegistryName() == null) {
//                debarkedLogItem.setRegistryName(origLog.getRegistryName() + "_debarked");
//                registry.register(debarkedLogItem);
//            }
//            else if (!registry.containsValue(debarkedLogItem)) {
//                registry.register(debarkedLogItem);
//            }
//        }
    }

    public static boolean Debarking$isOriginal(Block block) {
        return block instanceof BlockLog && !(block instanceof DebarkedLog);
    }

    // TODO Create the item with same class instead of using normal ItemBlock
    public static void Debarking$registerItem(Item logItem) {
    }

    public static IForgeRegistryEntry<Block> BlockLog$setRegistryName(String location, Block block) {
        if (!(block instanceof DebarkedLog)) {
            return block.setRegistryName(location);
        }
        else return block.setRegistryName(location + "_debarked");
    }

    public static IForgeRegistryEntry<Block> BlockLog$setRegistryName(ResourceLocation location, Block block) {
        return BlockLog$setRegistryName(location.toString(), block);
    }

    public static IForgeRegistryEntry<Block> BlockLog$setRegistryName(String modId, String name, Block block) {
        return BlockLog$setRegistryName(new ResourceLocation(modId, name), block);
    }

    public static IForgeRegistryEntry<Item> BlockLog$setRegistryName(Item item, String location, Block block) {
        if (!(block instanceof DebarkedLog)) {
            return item.setRegistryName(location);
        }
        else {
            String toSet;
            if (location.endsWith("_debarked")) toSet = location;
            else toSet = location + "_debarked";
            return item.setRegistryName(toSet);
        }
    }

    public static IForgeRegistryEntry<Item> BlockLog$setRegistryName(Item item, ResourceLocation location, Block block) {
        return BlockLog$setRegistryName(item, location.toString(), block);
    }

    public static IForgeRegistryEntry<Item> BlockLog$setRegistryName(Item item, String modId, String name, Block block) {
        return BlockLog$setRegistryName(item, new ResourceLocation(modId, name), block);
    }

    public static void ModelBakery$log(Map<ResourceLocation, ModelBlockDefinition> map) {
        for (Map.Entry<ResourceLocation, ModelBlockDefinition> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "   " + entry.getValue());
        }
    }

    public static void ModelBakery$registerModelBlockDefinition(Map<ResourceLocation, ModelBlockDefinition> blockDefinitions, BlockStateMapper mapper, Block block, ResourceLocation location) {
        if (Debarking$isOriginal(block)) {
            ResourceLocation modelLoc = new ResourceLocation(location.getNamespace(), "blockstates/" + location.getPath() + ".json"); // for blockDefinitions
            Block debarkedLog = DEBARKED_LOG_BLOCKS.get(block);

        }
    }

    // TODO Implement debarked log blockstates and models.
//    public static ModelBlockDefinition ModelBakery$getModelBlockDefinition(ModelBakery bakery, BlockStateMapper mapper, Map<IBlockState, ModelResourceLocation> stateMap, Block log) {
//        ResourceLocation regName = Objects.requireNonNull(log.getRegistryName());
//        if (regName.getNamespace().equals(Tags.MOD_ID) && regName.getPath().endsWith("_debarked")) {
//            Block origLog = null;
//            for (Map.Entry<Block, Block> entry : DEBARKED_LOG_BLOCKS.entrySet()) {
//                if (entry.getValue().equals(log)) {
//                    origLog = entry.getKey();
//                    break;
//                }
//            }
//            if (origLog == null) return new ModelBlockDefinition(new ArrayList<>());
//            Set<ResourceLocation> origStateMap = mapper.getBlockstateLocations(origLog);
//        }
//        return null;
//    }

    // Button Placement
    public static IBlockState button$getStateFromMeta(Block block, int meta) {
        return null;
    }

    public static int button$getMetaFromState(Block block, IBlockState state) {
        return 0;
    }
}
