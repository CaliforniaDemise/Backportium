package surreal.backportium.potion.v13;

import net.minecraft.util.ResourceLocation;
import surreal.backportium.Tags;
import surreal.backportium.potion.PotionBasic;

public class PotionSlowFalling extends PotionBasic {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/mob_effect/slow_falling.png");

    public PotionSlowFalling(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        this.setBeneficial();
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return TEXTURE;
    }
}
