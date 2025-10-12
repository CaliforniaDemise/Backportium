package surreal.backportium.api.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * It's like use action used for riptide and such
 */
public interface EntityState {

    float getHeight(EntityLivingBase entity, float defaultValue);

    float getWidth(EntityLivingBase entity, float defaultValue);

    float getEyeHeight(EntityLivingBase entity, float defaultValue);

    @SideOnly(Side.CLIENT)
    boolean renderHand(World world, EntityPlayer player, ItemStack stack, EnumHand hand, ItemRenderer renderer, float equipProgress, float useTime, float partialTicks, boolean rightArm);

    @SideOnly(Side.CLIENT)
    void applyModelRotations(ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity);

    @SideOnly(Side.CLIENT)
    void applyRenderRotations(RenderLivingBase<? extends EntityLivingBase> render, EntityLivingBase entity, float ageInTicks, float rotationYaw, float partialTicks, boolean player);
}
