package surreal.backportium.potion.v1_13;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.potion.PotionBasic;

import javax.annotation.Nonnull;

public class PotionSlowFalling extends PotionBasic {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/mob_effect/slow_falling.png");

    public PotionSlowFalling(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        this.setBeneficial();
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entity, int amplifier) {
        if (entity.motionY < 0F) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.capabilities.isFlying) return;
            }
            entity.motionY += 0.065F;
        }
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return TEXTURE;
    }
}
