package surreal.backportium.potion.v1_13;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.potion.PotionBasic;

import javax.annotation.Nonnull;

public class PotionConduitPower extends PotionBasic {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/mob_effect/conduit_power.png");

    public PotionConduitPower(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        this.setBeneficial();
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int amplifier) {
        MobEffects.NIGHT_VISION.performEffect(entityLivingBaseIn, amplifier);
        MobEffects.WATER_BREATHING.performEffect(entityLivingBaseIn, amplifier);
        MobEffects.HASTE.performEffect(entityLivingBaseIn, amplifier);
    }
}
