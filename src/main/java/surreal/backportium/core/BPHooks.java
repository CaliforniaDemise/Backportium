package surreal.backportium.core;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelBlockDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import surreal.backportium.Backportium;
import surreal.backportium.Tags;
import surreal.backportium.api.block.FluidLogged;
import surreal.backportium.api.helper.RiptideHelper;
import surreal.backportium.block.ModBlocks;
import surreal.backportium.enchantment.ModEnchantments;
import surreal.backportium.item.v1_13.ItemBlockDebarkedLog;

import java.util.*;

@SuppressWarnings("unused")
public class BPHooks {

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
    // TODO change map to <debarkedLog, origLog>
    public static final Map<Block, Block> DEBARKED_LOG_BLOCKS = new LinkedHashMap<>();
    public static final Map<Block, ItemBlock> DEBARKED_LOG_ITEMS = new LinkedHashMap<>();
    //    public static final Map<Item, Item> DEBARKED_LOG_ITEMS = new LinkedHashMap<>();

    public static void Debarking$registerBlock(Block block, Block log) {
        DEBARKED_LOG_BLOCKS.put(log, block);
        DEBARKED_LOG_ITEMS.put(log, new ItemBlockDebarkedLog(block, log));
    }

    // TODO Create the item with same class instead of using normal ItemBlock
    public static void Debarking$registerItem(Item logItem) {
    }

    public static Object BlockLog$setRegistryName(String location, Block block, boolean debarked) {
        if (!debarked) {
            return block.setRegistryName(location);
        }
        return block;
    }

    public static Object BlockLog$setRegistryName(ResourceLocation location, Block block, boolean debarked) {
        return BlockLog$setRegistryName(location.toString(), block, debarked);
    }

    public static Object BlockLog$setRegistryName(String modId, String name, Block block, boolean debarked) {
        return BlockLog$setRegistryName(new ResourceLocation(modId, name), block, debarked);
    }

    public static void ModelBakery$log(Map<ResourceLocation, ModelBlockDefinition> map) {
        for (Map.Entry<ResourceLocation, ModelBlockDefinition> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "   " + entry.getValue());
        }
    }

    // TODO Implement debarked log blockstates and models.
    public static ModelBlockDefinition ModelBakery$getModelBlockDefinition(ModelBakery bakery, BlockStateMapper mapper, Map<IBlockState, ModelResourceLocation> stateMap, Block log) {
        ResourceLocation regName = Objects.requireNonNull(log.getRegistryName());
        if (regName.getNamespace().equals(Tags.MOD_ID) && regName.getPath().endsWith("_debarked")) {
            Block origLog = null;
            for (Map.Entry<Block, Block> entry : DEBARKED_LOG_BLOCKS.entrySet()) {
                if (entry.getValue().equals(log)) {
                    origLog = entry.getKey();
                    break;
                }
            }
            if (origLog == null) return new ModelBlockDefinition(new ArrayList<>());
            Set<ResourceLocation> origStateMap = mapper.getBlockstateLocations(origLog);
        }
        return null;
    }

    // Button Placement
    public static IBlockState button$getStateFromMeta(Block block, int meta) {
        return null;
    }

    public static int button$getMetaFromState(Block block, IBlockState state) {
        return 0;
    }
}
