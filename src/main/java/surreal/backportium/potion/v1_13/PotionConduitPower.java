package surreal.backportium.potion.v1_13;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import surreal.backportium.potion.PotionBasic;

import javax.annotation.Nonnull;

public class PotionConduitPower extends PotionBasic {

    public PotionConduitPower(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        setBeneficial();
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int amplifier) {
        MobEffects.NIGHT_VISION.performEffect(entityLivingBaseIn, amplifier);
        MobEffects.WATER_BREATHING.performEffect(entityLivingBaseIn, amplifier);
        MobEffects.HASTE.performEffect(entityLivingBaseIn, amplifier);
    }
}
