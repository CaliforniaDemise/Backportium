package surreal.backportium.api.item;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface UseAction {

    @SideOnly(Side.CLIENT)
    boolean renderHand(World world, EntityPlayer player, ItemStack stack, EnumHand hand, ItemRenderer renderer, float equipProgress, float useTime, float partialTicks, boolean rightArm);

    @SideOnly(Side.CLIENT)
    void setRotationAngles(ItemStack stack, EnumHand hand, ModelBiped model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, boolean rightArm);
}
