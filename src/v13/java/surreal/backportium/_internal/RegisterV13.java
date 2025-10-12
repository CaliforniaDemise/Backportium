package surreal.backportium._internal;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium._internal.block.*;
import surreal.backportium._internal.client.model.ModelBipedSwimming;
import surreal.backportium._internal.enchantment.EnchantmentChanneling;
import surreal.backportium._internal.enchantment.EnchantmentImpaling;
import surreal.backportium._internal.enchantment.EnchantmentLoyalty;
import surreal.backportium._internal.enchantment.EnchantmentRiptide;
import surreal.backportium._internal.entity.EntityTrident;
import surreal.backportium._internal.item.ItemBlockDriedKelp;
import surreal.backportium._internal.item.ItemConduit;
import surreal.backportium._internal.item.ItemDebugStick;
import surreal.backportium._internal.item.ItemTrident;
import surreal.backportium._internal.potion.PotionConduitPower;
import surreal.backportium._internal.potion.PotionDolphinsGrace;
import surreal.backportium._internal.potion.PotionSlowFalling;
import surreal.backportium._internal.recipe.OreIngredientPredicate;
import surreal.backportium._internal.registry.*;
import surreal.backportium.api.entity.EntityState;
import surreal.backportium.api.entity.RiptideEntity;
import surreal.backportium.api.entity.SwimmingEntity;
import surreal.backportium.api.item.UseAction;
import surreal.backportium.init.*;
import surreal.backportium.util.BlockUtil;
import surreal.backportium.world.biome.BiomeOceanCold;
import surreal.backportium.world.biome.BiomeOceanFrozen;
import surreal.backportium.world.biome.BiomeOceanLukewarm;
import surreal.backportium.world.biome.BiomeOceanWarm;
import surreal.backportium.block.*;
import surreal.backportium.item.ItemBlockClustered;
import surreal.backportium.item.ItemBlockKelp;
import surreal.backportium.util.NewMathHelper;

import java.util.Objects;

public class RegisterV13 {

    protected static void registerEntityStates() {
        ModEntityStates.RIPTIDE = new EntityState() {

            @Override
            public float getHeight(EntityLivingBase entity, float defaultValue) {
                return 0.6F;
            }

            @Override
            public float getWidth(EntityLivingBase entity, float defaultValue) {
                return 0.6F;
            }

            @Override
            public float getEyeHeight(EntityLivingBase entity, float defaultValue) {
                return 0.4F;
            }

            @Override
            public boolean renderHand(World world, EntityPlayer player, ItemStack stack, EnumHand hand, ItemRenderer renderer, float equipProgress, float useTime, float partialTicks, boolean rightArm) {
                if (player.getActiveHand() != hand) return false;
                int i = rightArm ? 1 : -1;
                RiptideEntity riptide = RiptideEntity.cast(player);
                if (riptide.getRiptideTimeLeft() > 0) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float) i * 0.21F, -0.34F + equipProgress * -0.6F, -0.46F);
                    GlStateManager.rotate(i * 95, 0, 0, 1);
                    GlStateManager.rotate(i * -5, 0, 1, 0);
                    GlStateManager.rotate(-65F, 1, 0, 0);
                    Minecraft.getMinecraft().getItemRenderer().renderItemSide(player, stack, rightArm ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightArm);
                    GlStateManager.popMatrix();
                    return true;
                }
                return false;
            }

            @Override
            public void applyModelRotations(ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {}

            @Override
            public void applyRenderRotations(RenderLivingBase<? extends EntityLivingBase> render, EntityLivingBase entity, float ageInTicks, float rotationYaw, float partialTicks, boolean player) {
                RiptideEntity riptide = RiptideEntity.cast(entity);
                if (!player) {
                    if (riptide.inRiptide()) {
                        float yRotation = 72F * (riptide.getRiptideTimeLeft() - partialTicks + 1.0F);
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
                else {
                    if (entity.isElytraFlying() && riptide.inRiptide()) {
                        float rotate = 72F * (riptide.getRiptideTimeLeft() - partialTicks + 1.0F);
                        GlStateManager.rotate(rotate, 0.0F, 1.0F, 0.0F);
                    }
                }
            }
        };
        ModEntityStates.SWIMMING = new EntityState() {

            @Override
            public float getHeight(EntityLivingBase entity, float defaultValue) {
                return 0.6F;
            }

            @Override
            public float getWidth(EntityLivingBase entity, float defaultValue) {
                return 0.6F;
            }

            @Override
            public float getEyeHeight(EntityLivingBase entity, float defaultValue) {
                return 0.4F;
            }

            @Override
            public boolean renderHand(World world, EntityPlayer player, ItemStack stack, EnumHand hand, ItemRenderer renderer, float equipProgress, float useTime, float partialTicks, boolean rightArm) {
                return false;
            }

            @Override
            public void applyModelRotations(ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {
                if (entity instanceof SwimmingEntity) {
                    SwimmingEntity swimming = SwimmingEntity.cast((EntityLivingBase) entity);
                    float swimAnim = ModelBipedSwimming.cast(model).getSwimAnimation();
                    boolean elytra = ((EntityLivingBase) entity).getTicksElytraFlying() > 4;
                    if (!elytra && swimAnim > 0.0F) {
                        if (swimming.isSwimming()) {
                            float f1 = limbSwing % 26.0F;
                            EnumHandSide handside = ((EntityLivingBase) entity).getPrimaryHand();
                            if (((EntityLivingBase) entity).swingingHand != EnumHand.MAIN_HAND) {
                                handside = handside.opposite();
                            }
                            float f2 = handside == EnumHandSide.RIGHT && model.swingProgress > 0.0F ? 0.0F : swimAnim;
                            float f3 = handside == EnumHandSide.LEFT && model.swingProgress > 0.0F ? 0.0F : swimAnim;
                            if (f1 < 14.0F) {
                                model.bipedLeftArm.rotateAngleX = NewMathHelper.rotLerpRad(f3, model.bipedLeftArm.rotateAngleX, 0.0F);
                                model.bipedRightArm.rotateAngleX = NewMathHelper.lerp(f2, model.bipedRightArm.rotateAngleX, 0.0F);
                                model.bipedLeftArm.rotateAngleY = NewMathHelper.rotLerpRad(f3, model.bipedLeftArm.rotateAngleY, (float) Math.PI);
                                model.bipedRightArm.rotateAngleY = NewMathHelper.lerp(f2, model.bipedRightArm.rotateAngleY, (float) Math.PI);
                                float anglef1 = -65.0F * f1 + f1 * f1;
                                float angle14 = -65.0F * 14.0F + 14.0F * 14.0F;
                                model.bipedLeftArm.rotateAngleZ = NewMathHelper.rotLerpRad(f3, model.bipedLeftArm.rotateAngleZ, (float) Math.PI + 1.8707964F * anglef1 / angle14);
                                model.bipedRightArm.rotateAngleZ = NewMathHelper.lerp(f2, model.bipedRightArm.rotateAngleZ, (float) Math.PI - 1.8707964F * anglef1 / angle14);
                            } else if (f1 >= 14.0F && f1 < 22.0F) {
                                float f10 = (f1 - 14.0F) / 8.0F;
                                model.bipedLeftArm.rotateAngleX = NewMathHelper.rotLerpRad(f3, model.bipedLeftArm.rotateAngleX, ((float) Math.PI / 2F) * f10);
                                model.bipedRightArm.rotateAngleX = NewMathHelper.lerp(f2, model.bipedRightArm.rotateAngleX, ((float) Math.PI / 2F) * f10);
                                model.bipedLeftArm.rotateAngleY = NewMathHelper.rotLerpRad(f3, model.bipedLeftArm.rotateAngleY, (float) Math.PI);
                                model.bipedRightArm.rotateAngleY = NewMathHelper.lerp(f2, model.bipedRightArm.rotateAngleY, (float) Math.PI);
                                model.bipedLeftArm.rotateAngleZ = NewMathHelper.rotLerpRad(f3, model.bipedLeftArm.rotateAngleZ, 5.012389F - 1.8707964F * f10);
                                model.bipedRightArm.rotateAngleZ = NewMathHelper.lerp(f2, model.bipedRightArm.rotateAngleZ, 1.2707963F + 1.8707964F * f10);
                            } else if (f1 >= 22.0F && f1 < 26.0F) {
                                float f9 = (f1 - 22.0F) / 4.0F;
                                model.bipedLeftArm.rotateAngleX = NewMathHelper.rotLerpRad(f3, model.bipedLeftArm.rotateAngleX, ((float) Math.PI / 2F) - ((float) Math.PI / 2F) * f9);
                                model.bipedRightArm.rotateAngleX = NewMathHelper.lerp(f2, model.bipedRightArm.rotateAngleX, ((float) Math.PI / 2F) - ((float) Math.PI / 2F) * f9);
                                model.bipedLeftArm.rotateAngleY = NewMathHelper.rotLerpRad(f3, model.bipedLeftArm.rotateAngleY, (float) Math.PI);
                                model.bipedRightArm.rotateAngleY = NewMathHelper.lerp(f2, model.bipedRightArm.rotateAngleY, (float) Math.PI);
                                model.bipedLeftArm.rotateAngleZ = NewMathHelper.rotLerpRad(f3, model.bipedLeftArm.rotateAngleZ, (float) Math.PI);
                                model.bipedRightArm.rotateAngleZ = NewMathHelper.lerp(f2, model.bipedRightArm.rotateAngleZ, (float) Math.PI);
                            }

                            model.bipedLeftLeg.rotateAngleX = NewMathHelper.lerp(swimAnim, model.bipedLeftLeg.rotateAngleX, 0.3F * MathHelper.cos(limbSwing * 0.33333334F + (float) Math.PI));
                            model.bipedRightLeg.rotateAngleX = NewMathHelper.lerp(swimAnim, model.bipedRightLeg.rotateAngleX, 0.3F * MathHelper.cos(limbSwing * 0.33333334F));
                        }
                    }
                }
            }

            @Override
            public void applyRenderRotations(RenderLivingBase<? extends EntityLivingBase> render, EntityLivingBase entity, float ageInTicks, float rotationYaw, float partialTicks, boolean player) {
                if (!player && !entity.isElytraFlying()) {
                    SwimmingEntity swimming = SwimmingEntity.cast(entity);
                    float f = swimming.getSwimAnimation(partialTicks);
                    float f3 = entity.isInWater() ? -90.0F - entity.rotationPitch : -90.0F;
                    float f4 = NewMathHelper.lerp(f, 0.0F, f3);
                    GlStateManager.rotate(f4, 1.0F, 0.0F, 0.0F);
                    if (swimming.isSwimming()) {
                        GlStateManager.translate(0.0F, -1.0, 0.3F);
                    }
                }
            }
        };
    }

    protected static void registerActions() {
        ModActions.register(ModActions.TRIDENT, new UseAction() {

            @SideOnly(Side.CLIENT)
            @Override
            public boolean renderHand(World world, EntityPlayer player, ItemStack stack, EnumHand hand, ItemRenderer renderer, float equipProgress, float useTime, float partialTicks, boolean rightArm) {
                if (player.isHandActive() && player.getActiveHand() == hand) {
                    int i = rightArm ? 1 : -1;
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float) i * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
                    GlStateManager.translate(i * -0.25F, 0.8F, -0.125F);
                    float useTranslate = Math.min(useTime / 60F, 0.175F);
                    float useRotate = Math.min(useTranslate / 10F, 1.0F);
                    GlStateManager.translate(0, useTranslate, 0);
                    GlStateManager.translate(0, 0, useTranslate);
                    GlStateManager.rotate(i * -8.5F, 0, 1, 0);
                    GlStateManager.rotate(useRotate, 0, 1, 0);
                    GlStateManager.rotate(-61.5F, 1, 0, 0);
                    GlStateManager.rotate(i * -1.1F, 0, 0, 1);
                    if (useTranslate > 0.1F) {
                        float f7 = MathHelper.sin((useTime - 0.1F) * 1.3F) * 0.0011F;
                        GlStateManager.translate(f7, f7, f7);
                    }
                    renderer.renderItemSide(player, stack, rightArm ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightArm);
                    GlStateManager.popMatrix();
                    return true;
                }
                return false;
            }

            @SideOnly(Side.CLIENT)
            @Override
            public void setRotationAngles(ItemStack stack, EnumHand hand, ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, boolean rightArm) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase living = (EntityLivingBase) entity;
                    EnumHand activeHand = living.getActiveHand();
                    ItemStack activeStack = living.getActiveItemStack();
                    EnumHandSide handSide = activeHand == EnumHand.MAIN_HAND ? living.getPrimaryHand() : living.getPrimaryHand().opposite();
                    if (living.isHandActive()) {
                        int riptide = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.RIPTIDE, activeStack);
                        if (riptide != 0 && !EnchantmentRiptide.canRiptide(living.world, living)) return;
                        if (handSide == EnumHandSide.RIGHT) {
                            model.bipedRightArm.rotateAngleX = model.bipedRightArm.rotateAngleX * 0.5F - ((float) Math.PI);
                            model.bipedRightArm.rotateAngleZ = model.bipedRightArm.rotateAngleZ - 0.15F; // Not one to one because default looks like shit
                        } else {
                            model.bipedLeftArm.rotateAngleX = model.bipedLeftArm.rotateAngleX * 0.5F - ((float) Math.PI);
                            model.bipedLeftArm.rotateAngleZ = model.bipedLeftArm.rotateAngleZ + 0.15F; // Not one to one because default looks like shit
                        }
                    }
                }
            }
        });
    }

    protected static void registerSoundTypes() {
        ModSoundTypes.CORAL = SoundType.get(() -> ModSounds.BLOCK_CORAL_BLOCK_BREAK, () -> ModSounds.BLOCK_CORAL_BLOCK_STEP, () -> ModSounds.BLOCK_CORAL_BLOCK_PLACE, () -> ModSounds.BLOCK_CORAL_BLOCK_HIT, () -> ModSounds.BLOCK_CORAL_BLOCK_FALL);
        ModSoundTypes.WET_GRASS = SoundType.get(() -> ModSounds.BLOCK_WET_GRASS_BREAK, () -> ModSounds.BLOCK_WET_GRASS_STEP, () -> ModSounds.BLOCK_WET_GRASS_PLACE, () -> ModSounds.BLOCK_WET_GRASS_HIT, () -> ModSounds.BLOCK_WET_GRASS_FALL);
    }

    protected static void registerBlocks(Blocks registry) {
        registry.register(new BlockBlueIce(), "blue_ice");
        registry.registerItem(new BlockBubbleColumn(), null, "bubble_column");
        registry.register(new BlockDefault.ButtonWood(), "spruce_button");
        registry.register(new BlockDefault.ButtonWood(), "birch_button");
        registry.register(new BlockDefault.ButtonWood(), "jungle_button");
        registry.register(new BlockDefault.ButtonWood(), "acacia_button");
        registry.register(new BlockDefault.ButtonWood(), "dark_oak_button");
        registry.register(new BlockDefault.PressurePlate(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING), "spruce_pressure_plate");
        registry.register(new BlockDefault.PressurePlate(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING), "birch_pressure_plate");
        registry.register(new BlockDefault.PressurePlate(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING), "jungle_pressure_plate");
        registry.register(new BlockDefault.PressurePlate(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING), "acacia_pressure_plate");
        registry.register(new BlockDefault.PressurePlate(Material.WOOD, BlockPressurePlate.Sensitivity.EVERYTHING), "dark_oak_pressure_plate");
        registry.register(new BlockDefault.Trapdoor(Material.WOOD), "spruce_trapdoor");
        registry.register(new BlockDefault.Trapdoor(Material.WOOD), "birch_trapdoor");
        registry.register(new BlockDefault.Trapdoor(Material.WOOD), "jungle_trapdoor");
        registry.register(new BlockDefault.Trapdoor(Material.WOOD), "acacia_trapdoor");
        registry.register(new BlockDefault.Trapdoor(Material.WOOD), "dark_oak_trapdoor");
        registry.register(new BlockPumpkin(), "pumpkin");
        registry.registerItem(new BlockConduit(), ItemConduit::new, "conduit");
        registry.register(new BlockCoral(Material.CORAL, MapColor.BLUE), "tube_coral");
        registry.register(new BlockCoral(Material.CORAL, MapColor.STONE), "dead_tube_coral");
        registry.register(new BlockCoral(Material.CORAL, MapColor.PINK), "brain_coral");
        registry.register(new BlockCoral(Material.CORAL, MapColor.STONE), "dead_brain_coral");
        registry.register(new BlockCoral(Material.CORAL, MapColor.PURPLE), "bubble_coral");
        registry.register(new BlockCoral(Material.CORAL, MapColor.STONE), "dead_bubble_coral");
        registry.register(new BlockCoral(Material.CORAL, MapColor.RED), "fire_coral");
        registry.register(new BlockCoral(Material.CORAL, MapColor.STONE), "dead_fire_coral");
        registry.register(new BlockCoral(Material.CORAL, MapColor.YELLOW), "horn_coral");
        registry.register(new BlockCoral(Material.CORAL, MapColor.STONE), "dead_horn_coral");
        registry.register(new BlockCoralBlock(Material.CORAL, MapColor.BLUE), "tube_coral_block");
        registry.register(new BlockCoralBlock(Material.CORAL, MapColor.STONE), "dead_tube_coral_block");
        registry.register(new BlockCoralBlock(Material.CORAL, MapColor.PINK), "brain_coral_block");
        registry.register(new BlockCoralBlock(Material.CORAL, MapColor.STONE), "dead_brain_coral_block");
        registry.register(new BlockCoralBlock(Material.CORAL, MapColor.PURPLE), "bubble_coral_block");
        registry.register(new BlockCoralBlock(Material.CORAL, MapColor.STONE), "dead_bubble_coral_block");
        registry.register(new BlockCoralBlock(Material.CORAL, MapColor.RED), "fire_coral_block");
        registry.register(new BlockCoralBlock(Material.CORAL, MapColor.STONE), "dead_fire_coral_block");
        registry.register(new BlockCoralBlock(Material.CORAL, MapColor.YELLOW), "horn_coral_block");
        registry.register(new BlockCoralBlock(Material.CORAL, MapColor.STONE), "dead_horn_coral_block");
        registry.registerItem(new BlockCoralFan(Material.CORAL, MapColor.BLUE), "tube_coral_fan");
        registry.registerItem(new BlockCoralFan(Material.CORAL, MapColor.STONE), "dead_tube_coral_fan");
        registry.registerItem(new BlockCoralFan(Material.CORAL, MapColor.PINK), "brain_coral_fan");
        registry.registerItem(new BlockCoralFan(Material.CORAL, MapColor.STONE), "dead_brain_coral_fan");
        registry.registerItem(new BlockCoralFan(Material.CORAL, MapColor.PURPLE), "bubble_coral_fan");
        registry.registerItem(new BlockCoralFan(Material.CORAL, MapColor.STONE), "dead_bubble_coral_fan");
        registry.registerItem(new BlockCoralFan(Material.CORAL, MapColor.RED), "fire_coral_fan");
        registry.registerItem(new BlockCoralFan(Material.CORAL, MapColor.STONE), "dead_fire_coral_fan");
        registry.registerItem(new BlockCoralFan(Material.CORAL, MapColor.YELLOW), "horn_coral_fan");
        registry.registerItem(new BlockCoralFan(Material.CORAL, MapColor.STONE), "dead_horn_coral_fan");
        registry.registerItem(new BlockDriedKelp(), ItemBlockDriedKelp::new, "dried_kelp_block");
        registry.registerItem(new BlockKelp(Material.PLANTS), ItemBlockKelp::new, "kelp");
        registry.register(new BlockDefault.Stairs(net.minecraft.init.Blocks.PRISMARINE, 0), "rough_prismarine_stairs");
        registry.register(new BlockDefault.Stairs(net.minecraft.init.Blocks.PRISMARINE, 1), "prismarine_bricks_stairs");
        registry.register(new BlockDefault.Stairs(net.minecraft.init.Blocks.PRISMARINE, 2), "dark_prismarine_stairs");
        registry.registerItem(new BlockPrismarineSlab(), null, "rough_prismarine_slab");
        registry.registerItem(new BlockPrismarineSlab(), null, "prismarine_bricks_slab");
        registry.registerItem(new BlockPrismarineSlab(), null, "dark_prismarine_slab");
        Block tallSeagrass = registry.registerItem(new BlockTallSeagrass(), null, "tall_seagrass");
        registry.register(new BlockSeagrass(tallSeagrass), "seagrass");
        registry.registerItem(new BlockSeaPickle(), ItemBlockClustered::new, "sea_pickle");
//        registry.register("oak_bark");
//        registry.register("spruce_bark");
//        registry.register("birch_bark");
//        registry.register("jungle_bark");
//        registry.register("acacia_bark");
//        registry.register("dark_oak_bark");
//        registry.register("spruce_stripped_log");
//        registry.register("birch_stripped_log");
//        registry.register("jungle_stripped_log");
//        registry.register("acacia_stripped_log");
//        registry.register("dark_oak_stripped_log");
//        registry.register("spruce_stripped_bark");
//        registry.register("birch_stripped_bark");
//        registry.register("jungle_stripped_bark");
//        registry.register("acacia_stripped_bark");
//        registry.register("dark_oak_stripped_bark");
        registry.registerItem(new BlockTurtleEgg(), ItemBlockClustered::new, "turtle_egg");
        registry.register(new BlockHugeMushroom(Material.WOOD, MapColor.RED), "red_mushroom_block");
        registry.register(new BlockHugeMushroom(Material.WOOD, MapColor.DIRT), "brown_mushroom_block");
        registry.register(new BlockHugeMushroom(Material.WOOD, MapColor.CLOTH), "mushroom_stem");
        registry.register(new BlockSmooth(MapColor.QUARTZ), "smooth_quartz");
        registry.register(new BlockSmooth(MapColor.SAND), "smooth_sandstone");
        registry.register(new BlockSmooth(MapColor.ADOBE), "smooth_red_sandstone");
        registry.register(new BlockSmooth(MapColor.STONE), "smooth_stone");
    }

    protected static void registerItems(Items registry) {
//        registry.item("explorer_map");
        registry.register(new ItemDebugStick(), "debug_stick");
        registry.register(new ItemFood(1, 0.0F, false), "dried_kelp");
        registry.register(new Item(), "mob_bucket");
        registry.register(new Item().setCreativeTab(CreativeTabs.MATERIALS), "heart_of_the_sea");
        registry.register(new Item().setCreativeTab(CreativeTabs.MATERIALS), "nautilus_shell");
        registry.register(new Item().setCreativeTab(CreativeTabs.MATERIALS), "phantom_membrane");
        registry.register(new Item().setCreativeTab(CreativeTabs.MATERIALS), "turtle_scute");
        registry.register(new ItemTrident().setCreativeTab(CreativeTabs.COMBAT), "trident");
        registry.register(new Item().setCreativeTab(CreativeTabs.COMBAT), "turtle_helmet");
    }

    protected static void registerBiomes(Biomes registry) {
//        registry.register("small_end_islands");
//        registry.register("end_midlands");
//        registry.register("end_highlands");
        registry.register(BiomeOceanWarm::new, "warm_ocean", "Warm Ocean", properties -> properties.setBaseHeight(-1.0F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F));
        registry.register(BiomeOceanLukewarm::new, "lukewarm_ocean", "Lukewarm Ocean", properties -> properties.setBaseHeight(-1.0F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F));
        registry.register(BiomeOceanCold::new, "cold_ocean", "Cold Ocean", properties -> properties.setBaseHeight(-1.0F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F));
        registry.register(BiomeOceanFrozen::new, new ResourceLocation("minecraft", "frozen_ocean"), "Frozen Ocean", properties -> properties.setBaseHeight(-1.0F).setHeightVariation(0.1F).setTemperature(0.0F).setRainfall(0.5F).setSnowEnabled());
        registry.register(BiomeOceanWarm::new, "deep_warm_ocean", "Deep Warm Ocean", properties -> properties.setBaseHeight(-1.8F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F));
        registry.register(BiomeOceanLukewarm::new, "deep_lukewarm_ocean", "Deep Lukewarm Ocean", properties -> properties.setBaseHeight(-1.8F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F));
        registry.register(BiomeOceanCold::new, "deep_cold_ocean", "Deep Cold Ocean", properties -> properties.setBaseHeight(-1.8F).setHeightVariation(0.1F).setTemperature(0.5F).setRainfall(0.5F));
        registry.register(BiomeOceanFrozen::new, "deep_frozen_ocean", "Deep Frozen Ocean", properties -> properties.setBaseHeight(-1.8F).setHeightVariation(0.1F).setTemperature(0.0F).setRainfall(0.5F).setSnowEnabled());
    }

    protected static void registerSounds(Sounds registry) {
        registry.register("ambient.underwater.enter");
        registry.register("ambient.underwater.exit");
        registry.register("ambient.underwater.loop");
        registry.register("ambient.underwater.loop.additions");
        registry.register("ambient.underwater.loop.additions.rare");
        registry.register("ambient.underwater.loop.additions.ultra_rare");
        registry.register("block.bubble_column.bubble_pop");
        registry.register("block.bubble_column.upwards_ambient");
        registry.register("block.bubble_column.upwards_inside");
        registry.register("block.bubble_column.whirlpool_ambient");
        registry.register("block.bubble_column.whirlpool_inside");
        registry.register("block.conduit.ambient");
        registry.register("block.conduit.ambient.short");
        registry.register("block.conduit.attack.target");
        registry.register("block.conduit.activate");
        registry.register("block.conduit.deactivate");
        registry.register("block.coral_block.break");
        registry.register("block.coral_block.fall");
        registry.register("block.coral_block.hit");
        registry.register("block.coral_block.place");
        registry.register("block.coral_block.step");
        registry.register("block.wet_grass.break");
        registry.register("block.wet_grass.fall");
        registry.register("block.wet_grass.hit");
        registry.register("block.wet_grass.place");
        registry.register("block.wet_grass.step");
        registry.register("block.pumpkin.carve");
        registry.register("entity.cod.ambient");
        registry.register("entity.cod.death");
        registry.register("entity.cod.flop");
        registry.register("entity.cod.hurt");
        registry.register("entity.dolphin.ambient");
        registry.register("entity.dolphin.ambient_water");
        registry.register("entity.dolphin.attack");
        registry.register("entity.dolphin.death");
        registry.register("entity.dolphin.eat");
        registry.register("entity.dolphin.hurt");
        registry.register("entity.dolphin.jump");
        registry.register("entity.dolphin.play");
        registry.register("entity.dolphin.splash");
        registry.register("entity.dolphin.swim");
        registry.register("entity.drowned.ambient");
        registry.register("entity.drowned.death");
        registry.register("entity.drowned.death_water");
        registry.register("entity.drowned.hurt");
        registry.register("entity.drowned.hurt_water");
        registry.register("entity.drowned.shoot");
        registry.register("entity.drowned.step");
        registry.register("entity.drowned.swim");
        registry.register("entity.phantom.ambient");
        registry.register("entity.phantom.bite");
        registry.register("entity.phantom.death");
        registry.register("entity.phantom.flap");
        registry.register("entity.phantom.hurt");
        registry.register("entity.phantom.swoop");
        registry.register("entity.puffer_fish.ambient");
        registry.register("entity.puffer_fish.blow_out");
        registry.register("entity.puffer_fish.blow_up");
        registry.register("entity.puffer_fish.death");
        registry.register("entity.puffer_fish.flop");
        registry.register("entity.puffer_fish.hurt");
        registry.register("entity.puffer_fish.sting");
        registry.register("entity.salmon.ambient");
        registry.register("entity.salmon.death");
        registry.register("entity.salmon.flop");
        registry.register("entity.salmon.hurt");
        registry.register("entity.turtle.ambient_land");
        registry.register("entity.turtle.death");
        registry.register("entity.turtle.death_baby");
        registry.register("entity.turtle.egg_break");
        registry.register("entity.turtle.egg_crack");
        registry.register("entity.turtle.egg_hatch");
        registry.register("entity.turtle.hurt");
        registry.register("entity.turtle.hurt_baby");
        registry.register("entity.turtle.lay_egg");
        registry.register("entity.turtle.shamble");
        registry.register("entity.turtle.shamble_baby");
        registry.register("entity.turtle.swim");
        registry.register("entity.squid.squirt");
        registry.register("entity.husk.converted_to_zombie");
        registry.register("item.axe.strip");
        registry.register("item.trident.hit");
        registry.register("item.trident.hit_ground");
        registry.register("item.trident.return");
        registry.register("item.trident.riptide_1");
        registry.register("item.trident.riptide_2");
        registry.register("item.trident.riptide_3");
        registry.register("item.trident.throw");
        registry.register("item.trident.thunder");
    }

    protected static void registerEntities(Entities registry) {
        registry.register(EntityTrident.class, "trident");
//        registry.entity("dolphin");
//        registry.entity("drowned");
//        registry.entity("cod");
//        registry.entity("salmon");
//        registry.entity("pufferfish");
//        registry.entity("tropical_fish");
//        registry.entity("phantom");
//        registry.entity("turtle");
    }

    protected static void registerEnchantments(Enchantments registry) {
        registry.register(new EnchantmentChanneling(), "channeling");
        registry.register(new EnchantmentImpaling(), "impaling");
        registry.register(new EnchantmentLoyalty(), "loyalty");
        registry.register(new EnchantmentRiptide(), "riptide");
    }

    protected static void registerPotions(Potions registry) {
        registry.register(new PotionConduitPower(), "conduit_power");
        registry.register(new PotionDolphinsGrace(), "dolphins_grace");
        registry.register(new PotionSlowFalling(), "slow_falling");
    }

    protected static void registerPotionTypes(PotionTypes registry) {
        if (ModPotions.SLOW_FALLING != null) {
            registry.register("slow_falling", b -> b.effect(ModPotions.SLOW_FALLING, 1800, 0));
            registry.register("long_slow_falling", b -> b.effect(ModPotions.SLOW_FALLING, 4800, 0));
        }
        registry.register("turtle_master", b -> b.effect(MobEffects.SLOWNESS, 1200, 3).effect(MobEffects.RESISTANCE, 1200, 2));
        registry.register("long_turtle_master", b -> b.effect(MobEffects.SLOWNESS, 3600, 3).effect(MobEffects.RESISTANCE, 3600, 2));
        registry.register("strong_turtle_master", b -> b.effect(MobEffects.SLOWNESS, 1200, 5).effect(MobEffects.RESISTANCE, 1200, 3));
    }

    protected static void registerRecipes(Recipes registry) {
        registry.shaped("blue_ice", new ItemStack(ModBlocks.BLUE_ICE), "AAA", "AAA", "AAA", 'A', net.minecraft.init.Blocks.PACKED_ICE);
        registry.shaped("packed_ice", new ItemStack(net.minecraft.init.Blocks.PACKED_ICE), "AAA", "AAA", "AAA", 'A', net.minecraft.init.Blocks.ICE);
        registry.shaped("conduit", new ItemStack(ModBlocks.CONDUIT), "AAA", "ABA", "AAA", 'A', ModItems.NAUTILUS_SHELL, 'B', ModItems.HEART_OF_THE_SEA);
        registry.shaped("turtle_shell", new ItemStack(ModItems.TURTLE_SHELL), "AAA", "A A", 'A', ModItems.TURTLE_SCUTE);
        registry.shaped("dried_kelp_block", new ItemStack(ModBlocks.DRIED_KELP_BLOCK), "AAA", "AAA", "AAA", 'A', ModItems.DRIED_KELP);
        registry.shapeless("dried_kelp_unpacking", new ItemStack(ModItems.DRIED_KELP, 9), ModBlocks.DRIED_KELP_BLOCK);
        registry.shapeless("spruce_button", new ItemStack(ModBlocks.SPRUCE_BUTTON), new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 1));
        registry.shapeless("birch_button", new ItemStack(ModBlocks.BIRCH_BUTTON), new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 2));
        registry.shapeless("jungle_button", new ItemStack(ModBlocks.JUNGLE_BUTTON), new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 3));
        registry.shapeless("acacia_button", new ItemStack(ModBlocks.ACACIA_BUTTON), new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 4));
        registry.shapeless("dark_oak_button", new ItemStack(ModBlocks.DARK_OAK_BUTTON), new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 5));
        registry.shaped("spruce_pressure_plate", new ItemStack(ModBlocks.SPRUCE_PRESSURE_PLATE), "AA", 'A', new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 1));
        registry.shaped("birch_pressure_plate", new ItemStack(ModBlocks.BIRCH_PRESSURE_PLATE), "AA", 'A', new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 2));
        registry.shaped("jungle_pressure_plate", new ItemStack(ModBlocks.JUNGLE_PRESSURE_PLATE), "AA", 'A', new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 3));
        registry.shaped("acacia_pressure_plate", new ItemStack(ModBlocks.ACACIA_PRESSURE_PLATE), "AA", 'A', new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 4));
        registry.shaped("dark_oak_pressure_plate", new ItemStack(ModBlocks.DARK_OAK_PRESSURE_PLATE), "AA", 'A', new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 5));
        registry.shaped("spruce_trapdoor", new ItemStack(ModBlocks.SPRUCE_TRAPDOOR), "AAA", "AAA", 'A', new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 1));
        registry.shaped("birch_trapdoor", new ItemStack(ModBlocks.BIRCH_TRAPDOOR), "AAA", "AAA", 'A', new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 2));
        registry.shaped("jungle_trapdoor", new ItemStack(ModBlocks.JUNGLE_TRAPDOOR), "AAA", "AAA", 'A', new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 3));
        registry.shaped("acacia_trapdoor", new ItemStack(ModBlocks.ACACIA_TRAPDOOR), "AAA", "AAA", 'A', new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 4));
        registry.shaped("dark_oak_trapdoor", new ItemStack(ModBlocks.DARK_OAK_TRAPDOOR), "AAA", "AAA", 'A', new ItemStack(net.minecraft.init.Blocks.PLANKS, 1, 5));
        OreIngredientPredicate ingredient = new OreIngredientPredicate("plankWood", s -> s.getMetadata() == 0 || !Objects.requireNonNull(s.getItem().getRegistryName()).getNamespace().equals("minecraft"));
        registry.shapeless(net.minecraft.init.Blocks.WOODEN_BUTTON.getRegistryName(), new ItemStack(net.minecraft.init.Blocks.WOODEN_BUTTON), ingredient);
        registry.shaped(net.minecraft.init.Blocks.WOODEN_PRESSURE_PLATE.getRegistryName(), new ItemStack(net.minecraft.init.Blocks.WOODEN_PRESSURE_PLATE), "AA", 'A', ingredient);
        registry.shaped(net.minecraft.init.Blocks.TRAPDOOR.getRegistryName(), new ItemStack(net.minecraft.init.Blocks.TRAPDOOR), "AAA", "AAA", 'A', ingredient);

        // Furnace
        GameRegistry.addSmelting(BlockUtil.getItemFromBlock(ModBlocks.KELP), new ItemStack(ModItems.DRIED_KELP), 0.1F);

        // Potion Mixing
        Ingredient redstoneIng = Ingredient.fromItem(net.minecraft.init.Items.REDSTONE);
        PotionHelper.addMix(net.minecraft.init.PotionTypes.AWKWARD, ModItems.PHANTOM_MEMBRANE, ModPotionTypes.SLOW_FALLING);
        PotionHelper.addMix(ModPotionTypes.SLOW_FALLING, redstoneIng, ModPotionTypes.LONG_SLOW_FALLING);
        PotionHelper.addMix(ModPotionTypes.TURTLE_MASTER, redstoneIng, ModPotionTypes.LONG_TURTLE_MASTER);
        PotionHelper.addMix(ModPotionTypes.TURTLE_MASTER, net.minecraft.init.Items.GLOWSTONE_DUST, ModPotionTypes.STRONG_TURTLE_MASTER);
    }
}
