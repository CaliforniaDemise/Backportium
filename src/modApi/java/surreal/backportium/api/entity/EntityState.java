package surreal.backportium.api.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.backportium.api.annotations.Final;

/**
 * It's like use action used for riptide and such
 */
public interface EntityState {

    float getHeight(EntityLivingBase entity);

    float getWidth(EntityLivingBase entity);

    float getEyeHeight(EntityLivingBase entity);

    @SideOnly(Side.CLIENT)
    boolean renderHand(World world, EntityPlayer player, ItemStack stack, EnumHand hand, ItemRenderer renderer, float equipProgress, float useTime, float partialTicks, boolean rightArm);

    @SideOnly(Side.CLIENT)
    void applyModelRotations(ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity);

    @SideOnly(Side.CLIENT)
    void applyRenderRotations(RenderLivingBase<? extends EntityLivingBase> render, EntityLivingBase entity, float ageInTicks, float rotationYaw, float partialTicks, boolean player);

    @Final
    default boolean isStateClear(EntityLivingBase entity) {
        float f = this.getWidth(entity) / 2.0F;
        AxisAlignedBB aabb = new AxisAlignedBB(entity.posX - (double) f, entity.posY, entity.posZ - (double) f, entity.posX + (double) f, entity.posY + this.getHeight(entity), entity.posZ + (double) f);
        return entity.world.getCollisionBoxes(entity, aabb).isEmpty();
    }
}
