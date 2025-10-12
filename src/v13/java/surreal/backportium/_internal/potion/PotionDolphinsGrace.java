package surreal.backportium._internal.potion;

import net.minecraft.util.ResourceLocation;
import surreal.backportium.Tags;

public class PotionDolphinsGrace extends PotionBasic {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/mob_effect/dolphins_grace.png");

    public PotionDolphinsGrace() {
        super(true, 0x88A3BE);
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return TEXTURE;
    }
}
