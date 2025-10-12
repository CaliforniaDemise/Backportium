package surreal.backportium._internal.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import surreal.backportium.Tags;
import surreal.backportium._internal.tile.TileConduit;

public class PotionConduitPower extends PotionBasic {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Tags.MOD_ID, "textures/mob_effect/conduit_power.png");

    public PotionConduitPower() {
        super(false, 0x1DC2D1);
        this.setBeneficial();
    }

    @Override
    protected ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

    @Override
    public boolean shouldApply(EntityLivingBase entity) {
        return TileConduit.shouldApplyToEntity(entity);
    }
}
