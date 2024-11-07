package surreal.backportium.potion.v1_13;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.Tags;
import surreal.backportium.potion.PotionBasic;

import javax.annotation.Nonnull;

public class PotionSlowFalling extends PotionBasic {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/mob_effect/slow_falling.png");

    public PotionSlowFalling(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        this.setBeneficial();
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entity, int amplifier) {
        if (entity.motionY < 0.065F) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.capabilities.isFlying) return;
            }
            double motion = 0.065F * (amplifier + 1);
            if (entity.isInLava()) motion /= 8;
            if (entity.isInWater()) motion /= 4;
            entity.motionY += motion;
        }
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return TEXTURE;
    }
}
