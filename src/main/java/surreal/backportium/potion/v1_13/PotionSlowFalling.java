package surreal.backportium.potion.v1_13;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import surreal.backportium.potion.PotionBasic;

import javax.annotation.Nonnull;

public class PotionSlowFalling extends PotionBasic {

    public PotionSlowFalling(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        setBeneficial();
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
    public boolean isReady(int duration, int amplifier) {
        return true;
    }
}
