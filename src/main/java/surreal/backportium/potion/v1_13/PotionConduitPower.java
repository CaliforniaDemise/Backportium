package surreal.backportium.potion.v1_13;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.Tags;
import surreal.backportium.potion.PotionBasic;
import surreal.backportium.tile.v1_13.TileConduit;

import javax.annotation.Nonnull;

public class PotionConduitPower extends PotionBasic {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/mob_effect/conduit_power.png");

    public PotionConduitPower(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        this.setBeneficial();
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entity, int amplifier) {
        if (TileConduit.shouldApplyToEntity(entity)) {
            MobEffects.NIGHT_VISION.performEffect(entity, amplifier);
            MobEffects.WATER_BREATHING.performEffect(entity, amplifier);
            MobEffects.HASTE.performEffect(entity, amplifier);
        }
    }
}
