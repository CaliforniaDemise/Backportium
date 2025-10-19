package surreal.backportium.init;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import surreal.backportium.api.entity.EntityState;

public class ModEntityStates {

    public static EntityState STANDING;
    public static EntityState SNEAKING;
    public static EntityState SLEEPING;
    public static EntityState FLYING;
    public static EntityState RIPTIDE;
    public static EntityState CRAWLING;

    static {
        STANDING = new EntityState() {
            @Override
            public float getHeight(EntityLivingBase entity, float defaultValue) {
                return defaultValue;
            }

            @Override
            public float getWidth(EntityLivingBase entity, float defaultValue) {
                return defaultValue;
            }

            @Override
            public float getEyeHeight(EntityLivingBase entity, float defaultValue) {
                return defaultValue;
            }

            @Override
            public boolean renderHand(World world, EntityPlayer player, ItemStack stack, EnumHand hand, ItemRenderer renderer, float equipProgress, float useTime, float partialTicks, boolean rightArm) {
                return false;
            }

            @Override
            public void applyModelRotations(ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {

            }

            @Override
            public void applyRenderRotations(RenderLivingBase<? extends EntityLivingBase> render, EntityLivingBase entity, float ageInTicks, float rotationYaw, float partialTicks, boolean player) {

            }
        };
        SNEAKING = new EntityState() {
            @Override
            public float getHeight(EntityLivingBase entity, float defaultValue) {
                return 1.5F;
            }

            @Override
            public float getWidth(EntityLivingBase entity, float defaultValue) {
                return 0.6F;
            }

            @Override
            public float getEyeHeight(EntityLivingBase entity, float defaultValue) {
                return defaultValue - 0.35F; // - 0.08F old
            }

            @Override
            public boolean renderHand(World world, EntityPlayer player, ItemStack stack, EnumHand hand, ItemRenderer renderer, float equipProgress, float useTime, float partialTicks, boolean rightArm) {
                return false;
            }

            @Override
            public void applyModelRotations(ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {}

            @Override
            public void applyRenderRotations(RenderLivingBase<? extends EntityLivingBase> render, EntityLivingBase entity, float ageInTicks, float rotationYaw, float partialTicks, boolean player) {}
        };
        SLEEPING = new EntityState() {
            @Override
            public float getHeight(EntityLivingBase entity, float defaultValue) {
                return 0.2F;
            }

            @Override
            public float getWidth(EntityLivingBase entity, float defaultValue) {
                return 0.2F;
            }

            @Override
            public float getEyeHeight(EntityLivingBase entity, float defaultValue) {
                return 0.2F;
            }

            @Override
            public boolean renderHand(World world, EntityPlayer player, ItemStack stack, EnumHand hand, ItemRenderer renderer, float equipProgress, float useTime, float partialTicks, boolean rightArm) {
                return false;
            }

            @Override
            public void applyModelRotations(ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {}

            @Override
            public void applyRenderRotations(RenderLivingBase<? extends EntityLivingBase> render, EntityLivingBase entity, float ageInTicks, float rotationYaw, float partialTicks, boolean player) {}
        };
        FLYING = new EntityState() {
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
            public void applyModelRotations(ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity) {}

            @Override
            public void applyRenderRotations(RenderLivingBase<? extends EntityLivingBase> render, EntityLivingBase entity, float ageInTicks, float rotationYaw, float partialTicks, boolean player) {}
        };
    }
}
