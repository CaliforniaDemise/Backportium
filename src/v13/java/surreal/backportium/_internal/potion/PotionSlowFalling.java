package surreal.backportium._internal.potion;

import net.minecraft.util.ResourceLocation;
import surreal.backportium.Tags;

public class PotionSlowFalling extends PotionBasic {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/mob_effect/slow_falling.png");

    public PotionSlowFalling() {
        super(false, 0xF3CFB9);
        this.setBeneficial();
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return TEXTURE;
    }
}
